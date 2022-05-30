package ProjectW.Abilities;

import ProjectW.Ability;
import ProjectW.Utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieSword extends Ability {

    public ZombieSword(){
        super();
        this.name = "Zombie Sword";
        this.rightClickActivate = true;
        this.cooldownTime = 20.0;
    }

    public void activate(){
        owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(8.0);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ZOMBIE_HURT,1,1.5f);
        for (int i = 0; i <= 5; i++){
            Location particleLoc = owner.getEyeLocation();
            particleLoc.add(Utils.getRandomDouble(1.0, -1.0), Utils.getRandomDouble(1.0, -1.0), Utils.getRandomDouble(1.0, -1.0));
            owner.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, particleLoc,0,0,0,0,0,null,true);
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0);
                owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1, 0.5f);
                for (int i = 0; i <= 10; i++){
                    Location particleLoc = owner.getEyeLocation();
                    particleLoc.add(Utils.getRandomDouble(1.0, -1.0), Utils.getRandomDouble(1.0, -1.0), Utils.getRandomDouble(1.0, -1.0));
                    owner.getWorld().spawnParticle(Particle.TOTEM, particleLoc,0,0,0,0,0,null,true);
                }
            }
        }.runTaskLater(plugin, 50);
    }
}
