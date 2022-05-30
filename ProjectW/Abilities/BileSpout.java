package ProjectW.Abilities;

import ProjectW.Ability;
import ProjectW.EntityProjectile;
import ProjectW.Utils.ServerMessageType;
import ProjectW.Utils.ServerMessageUtil;
import ProjectW.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class BileSpout extends Ability {

    protected int projAmount = 5;
    protected int runTime = 40;
    protected boolean enabled = false;

    public BileSpout(){
        super();
        this.name = "Bile Spout";
        this.cooldownTime = 2;
        this.swapSlotActivate = true;
    }

    public void activate(){
        if (!(owner.getExp() >= 1.0f)){
            ServerMessageUtil.sendServerMessageToPlayer("You need to be fully charged to use this ability.", owner, ServerMessageType.ABILITY, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            return;
        }
        RayTraceResult result = owner.rayTraceBlocks(100, FluidCollisionMode.NEVER);
        Block hitBlock = result.getHitBlock();
        Vector hitPos = result.getHitPosition();
        if (hitBlock == null){
            ServerMessageUtil.sendServerMessageToPlayer("You need to aim at a block to use this ability.", owner, ServerMessageType.ABILITY, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            return;
        }
        owner.setExp(0.0f);
        Location overrideLocation = hitPos.toLocation(owner.getWorld()).add(0,0.4,0);
        overrideLocation.setDirection(new Vector(0,1,0));
        enabled = true;
        endTimer();
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!enabled){
                    cancel();
                }
                owner.getWorld().playSound(overrideLocation, Sound.ENTITY_PLAYER_BURP, 0.7f, (float) Utils.getRandomDouble(2.0, 1.3));
                ItemStack rottenFlesh = new ItemStack(Material.ROTTEN_FLESH, 1);
                for (int i = 0; i < projAmount; i++) {
                    Item firing = owner.getWorld().dropItem(overrideLocation, rottenFlesh);
                    RottenProjectile projectile = new RottenProjectile(plugin, owner, name, firing);
                    projectile.setOverridePosition(overrideLocation);
                    projectile.launchProjectile();
                }
            }

            class RottenProjectile extends EntityProjectile {

                public RottenProjectile(Plugin plugin, Player firer, String name, Entity projectile){
                    super(plugin, firer, name, projectile);
                    this.setDamage(0.25);
                    this.setSpeed(0.75);
                    this.setKnockback(0.4);
                    this.setUpwardKnockback(0.25);
                    this.setHitboxSize(0.4);
                    this.setVariation(30);
                    this.setiFrames(0);
                    this.setAngle(180);
                    this.setFromPlayer(false);
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

    public void endTimer(){
        new BukkitRunnable(){
            @Override
            public void run(){
                enabled = false;
            }
        }.runTaskLater(plugin, runTime);
    }
}
