package ProjectW.Abilities;

import ProjectW.Ability;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class IronGolemSword extends Ability {

    public boolean inSlam = false;

    public IronGolemSword(){
        super();
        this.name = "Iron Golem Sword";
        this.rightClickActivate = true;
        this.cooldownTime = 12.0;
    }

    public void activate(){
        inSlam = false;
        Vector velocity = owner.getLocation().getDirection();
        velocity.multiply(0.6);
        owner.setVelocity(velocity.setY(0.75));
        Entity entity = owner;
        new BukkitRunnable(){
            @Override
            public void run(){
                if (entity.isOnGround()){
                    Bukkit.broadcastMessage("Player on ground");
                    inSlam = false;
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 10, 1);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        if (e.getEntity() == owner){
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL){
                if (inSlam){
                    e.setCancelled(true);
                }
            }
        }
    }
}
