package ProjectW.MapPoints;

import ProjectW.GameManagers.PowerupManager;
import ProjectW.Powerup;
import ProjectW.Utils.Utils;
import ProjectW.ProjectW;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class PowerupSpawn {

    protected World world; //I know this is redundant because you can get the world from the location. But if the line where the NMSWorld "craftWorld" is created doesn't use a field world variable to get the handle from, it will display a null warning. I have no idea why.
    protected Location location;
    protected ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    protected Powerup activePowerup;
    protected int respawnTime = 10;

    protected Plugin plugin;

    public PowerupSpawn(Location location, ArrayList<Powerup> powerups){
        this.plugin = ProjectW.getInstance();
        this.location = location;
        this.powerups = powerups;
        this.respawnTime *= 20;
        this.world = location.getWorld();
    }

    public void spawnPowerup(Powerup powerup){ //Spawns an invisible spinning armor stand to display the powerup and checks to see if anyone picked it up.
        if (getLocation() == null){
            return;
        }
        net.minecraft.server.v1_16_R3.World craftWorld = ((CraftWorld) getWorld()).getHandle();
        net.minecraft.server.v1_16_R3.EntityArmorStand nmsArmorStand = new net.minecraft.server.v1_16_R3.EntityArmorStand(craftWorld,location.getX(),location.getY(), location.getZ());
        nmsArmorStand.setLocation(location.getX(), location.getY() + 1.0, location.getZ(), 180.0f, 0.0f);
        nmsArmorStand.setMarker(true);
        nmsArmorStand.setInvisible(true);
        ChatComponentText name = new ChatComponentText("§l§6" + powerup.getName());
        nmsArmorStand.setCustomName(name);
        nmsArmorStand.setCustomNameVisible(true);
        ItemStack bukkitHeadItemStack = new ItemStack(powerup.getPowerupMaterial());
        net.minecraft.server.v1_16_R3.ItemStack nmsHeadItemStack = CraftItemStack.asNMSCopy(bukkitHeadItemStack);
        nmsArmorStand.setSlot(EnumItemSlot.MAINHAND, nmsHeadItemStack);
        PowerupManager.getInstance().getPowerupStands().add(nmsArmorStand);
        new BukkitRunnable(){
            @Override
            public void run(){
                Location asLocation = new Location(world, nmsArmorStand.locX(), nmsArmorStand.locY(), nmsArmorStand.locZ(), nmsArmorStand.yaw, nmsArmorStand.pitch);
                nmsArmorStand.setLocation(asLocation.getX(), asLocation.getY(), asLocation.getZ(), asLocation.getYaw() + 1.0f, asLocation.getPitch());
                for (Entity entity : world.getNearbyEntities(asLocation, 1, 1, 1)){
                    if (entity instanceof Player){
                        powerup.checkAndActivate((Player) entity);
                        nmsArmorStand.die();
                        startTimer();
                        cancel();
                    }
                }
            }
        }.runTaskTimer(plugin,1,0);
    }

    public void startTimer(){ //Starts timer for a powerup to respawn at the point after being taken.
        new BukkitRunnable(){
            @Override
            public void run(){
                int rngChoice = Utils.getRandomInteger((powerups.size() - 1), 0);
                spawnPowerup(powerups.get(rngChoice));
            }
        }.runTaskLater(plugin, respawnTime);
    }

    public World getWorld(){return world;}

    public Location getLocation(){return this.location;}
}