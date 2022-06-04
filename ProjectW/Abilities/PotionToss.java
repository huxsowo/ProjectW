package ProjectW.Abilities;

import ProjectW.Ability;
import ProjectW.EntityProjectile;
import ProjectW.Utils.DamageUtil;
import ProjectW.Utils.Utils;
import ProjectW.Utils.VelocityUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PotionToss extends Ability {

    public PotionToss(){
        this.name = "Potion Toss";
        this.rightClickActivate = true;
        this.cooldownTime = 3.0;
    }

    public void activate(){
        ItemStack potionStack = new ItemStack(Material.SPLASH_POTION);
        Item potion = owner.getWorld().dropItem(owner.getEyeLocation(), potionStack);
        PotionProjectile projectile = new PotionProjectile(plugin,owner,name,potion);
        projectile.launchProjectile();
        new BukkitRunnable(){
            @Override
            public void run(){
                if (potion.isDead()){
                    owner.getWorld().playSound(potion.getLocation(), Sound.BLOCK_GLASS_BREAK,1,1);
                    for (int i = 0; i < 40; i++) {
                        owner.getWorld().spawnParticle(Particle.SPELL_WITCH, potion.getLocation().add(Utils.getRandomDouble(1, -1),0,Utils.getRandomDouble(1,-1)), 0, 0, 0, 0, 0, null, true);
                    }
                    for (Entity entity : potion.getNearbyEntities(2,1.25,2)){
                        if (entity instanceof Player){
                            if (entity != owner)
                            DamageUtil.dealAbilityDamageToPlayer(owner, (Player) entity, 4, true,false,name,0, potion.getLocation(), 0.35, 0.3);
                        }
                    }
                    potion.remove();
                    cancel();
                } else{
                    owner.getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, potion.getLocation(),0,0,0,0,0,null,true);
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

    class PotionProjectile extends EntityProjectile {

        public PotionProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(0);
            this.setSpeed(1.25);
            this.setHitboxSize(0.25);
            this.setVariation(0);
            this.setKnockback(0);
            this.setUpwardKnockback(0);
        }
    }
}
