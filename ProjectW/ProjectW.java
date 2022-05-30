package ProjectW;

import ProjectW.Events.WoolBlockCapturedEvent;
import ProjectW.GameManagers.*;
import ProjectW.Utils.*;
import com.onarandombox.MultiverseCore.MultiverseCore;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ProjectW extends JavaPlugin implements Listener {

    private static Plugin ourInstance;
    private static Location hubSpawnLocation;

    public static void main(String[] args){

    }

    public static Plugin getInstance(){return ourInstance;}

    @Override
    public void onEnable(){
        ourInstance = this;
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new DamageUtil(), this);

        hubSpawnLocation = Bukkit.getWorld("hub").getSpawnLocation();

        KitManager.getInstance().start();
        PowerupManager.getInstance().start();
        CooldownManager.getInstance().start(this);
        GameManager.getInstance().start(this);
    }

    @Override
    public void onDisable(){
        for (EntityArmorStand armorStand : PowerupManager.getInstance().getPowerupStands()){
            armorStand.die();
        }
        if (GameManager.getInstance().getClonedMap() == null){
            return;
        }
        String clonedMapName = GameManager.getInstance().getClonedMap().getName();
        if (getMultiverseCore().getMVWorldManager().getMVWorld(clonedMapName) != null){
            getMultiverseCore().getMVWorldManager().deleteWorld(clonedMapName);
        }
        PowerupManager.getInstance().getPowerupStands().clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        String uuid = ((Player) sender).getUniqueId().toString();
        switch (cmd.getName().toLowerCase()) {
            case "kit":
                if (args.length > 1){ //If there is either nothing after /kit or more than one argument, command function is aborted and the player is told how to properly use the command.
                    ServerMessageUtil.sendServerMessageToPlayer("Incorrect arguments. Correct usage: /kit [kitName]", player, ServerMessageType.SERVER, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                    return false;
                } else {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("none")){
                            if (KitManager.getPlayerKitHashMap().containsKey(player.getUniqueId())){
                                KitManager.getPlayerKitHashMap().get(player.getUniqueId()).destroyKit();
                                KitManager.getPlayerKitHashMap().remove(player.getUniqueId());
                                return true;
                            }
                        }
                        for (Kit check : KitManager.getInstance().getAllKits()) {
                            if (args[0].equalsIgnoreCase(check.getName())) {
                                KitManager.getInstance().equipPlayer(player, check);
                                return true;
                            }
                        }
                    }
                    if (args.length == 0) {
                        Kit equippedKit = KitManager.getInstance().getPlayerKit(player);
                        if (equippedKit != null){
                            ServerMessageUtil.sendServerMessageToPlayer("You are currently using " + equippedKit.getName() + " kit", player, ServerMessageType.SERVER, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                        } else {
                            int i = 1;
                            StringBuilder kitList = new StringBuilder("Kits: ");
                            for (Kit kit : KitManager.getInstance().getAllKits()){
                                if (i < KitManager.getInstance().getAllKits().length) {
                                    kitList.append(kit.getName()).append(", ");
                                } else {
                                    kitList.append(kit.getName());
                                }
                            }
                            ServerMessageUtil.sendServerMessageToPlayer(kitList.toString(), player, ServerMessageType.SERVER, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

                        }
                        return true;
                    }
                }
            case "forcestart":
                if (uuid.equalsIgnoreCase("40974325-4802-4aba-92c2-037c3a085306")){
                    GameManager.getInstance().setForceStart(true);
                    return true;
                }
                return false;
        }
        return false;
    }

    public static MultiverseCore getMultiverseCore() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");

        if (plugin instanceof MultiverseCore) {
            return (MultiverseCore) plugin;
        }

        System.out.println("Multiverse not found!");
        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
        return null;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.sendMessage("Welcome to Wool Wars!");
        player.setFoodLevel(20);
        player.setHealth(20.0);
        player.setExp(0.0f);
        player.setLevel(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(Bukkit.getWorld("hub").getSpawnLocation());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if (KitManager.getPlayerKitHashMap().containsKey(player.getUniqueId())) {
            KitManager.getInstance().getPlayerKit(player).destroyKit();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player player = e.getEntity();
        if (DamageManager.getEntityDamageReasons(player) != null) {
            DamageManager.getEntityDamageReasons(player).clear();
        }
        player.setVelocity(new Vector(0, 0, 0));
        KitManager.getInstance().equipPlayer(player, KitManager.getInstance().getPlayerKit(player));
        if (GameManager.getInstance().getGameStatus() == GameStatus.WAITING_FOR_PLAYERS) {
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    public void stopHungerLoss(FoodLevelChangeEvent e){e.setCancelled(true);}

    @EventHandler
    public void stopHealthRegen(EntityRegainHealthEvent e){e.setCancelled(true);}

    @EventHandler
    public void cancelSwapOffHand(PlayerSwapHandItemsEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        Block block = e.getBlock();
        if (player.getGameMode() == GameMode.CREATIVE){
            return;
        }
        e.setCancelled(true);
        for (Location check : GameManager.getInstance().getMapData().getWoolPoint().getLocations()){
            if (block.getLocation() == check || block.getLocation().equals(check)){
                WoolBlockCapturedEvent event = new WoolBlockCapturedEvent(player, block, GameManager.getInstance().getPlayerTeamHashMap().get(player));
                Bukkit.getServer().getPluginManager().callEvent(event);
                break;
            }
        }
    }

    @EventHandler
    public void onReload(ServerLoadEvent e){
        for (Player player : getServer().getOnlinePlayers()) {
            GameManager.getInstance().getPlayers().add(player);
        }
    }

    public static Location getHubSpawnLocation(){return hubSpawnLocation;}
}
