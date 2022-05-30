package ProjectW.Abilities;

import ProjectW.Ability;
import ProjectW.Utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class SearingSweep extends Ability {

    boolean activated = false;

    public SearingSweep(){
        this.name = "Searing Sweep";
        this.rightClickActivate = true;
        this.cooldownTime = 8.0;
    }

    public void activate(){
        activated = true;
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_PIGLIN_ANGRY,1,1);
        for (int i = 0; i <= 5; i++){
            Location particleLoc = owner.getEyeLocation();
            particleLoc.add(Utils.getRandomDouble(1.0, -0.65), Utils.getRandomDouble(1.0, -1.0), Utils.getRandomDouble(1.0, -1.0));
            owner.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, particleLoc,0,0,0,0,0,null,true);
        }
    }

    @EventHandler
    public void onAttack(PlayerInteractEvent e){
        if (!activated){
            return;
        }
        if (e.getPlayer() != owner){
            return;
        }
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
            doSearingAttack();
        }
    }

    public void doSearingAttack(){
        activated = false;
        Location sweepLocation = owner.getLocation().add(0, 1.2, 0);
        Vector sweepDirection = sweepLocation.getDirection().clone().multiply(1.75);
        sweepLocation.add(sweepDirection);
        owner.getWorld().spawnParticle(Particle.SWEEP_ATTACK, sweepLocation,0,0,0,0,0, null, true);
        owner.getWorld().playSound(sweepLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,1);
        owner.getWorld().playSound(sweepLocation, Sound.BLOCK_FIRE_EXTINGUISH,1,1);
        for (int i = 0; i<=15 ; i++){
            owner.getWorld().spawnParticle(Particle.LAVA, sweepLocation.clone().add(Utils.getRandomDouble(0.75, -0.75), Utils.getRandomDouble(0.2, -0.2), Utils.getRandomDouble(0.75, -0.75)),0,0,0,0,0,null,true);
        }
        for (Entity hitEntity : owner.getWorld().getNearbyEntities(sweepLocation, 1.5, 0.5, 1.5)){
            if (hitEntity instanceof Player && hitEntity != owner){
                hitEntity.setFireTicks(100);
            }
        }
    }
}
