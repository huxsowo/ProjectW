package ProjectW;

import ProjectW.Utils.DamageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntityProjectile extends BukkitRunnable {

    protected Plugin plugin;
    protected Player firer;
    protected String name;
    protected Entity projectile;
    protected Location overridePosition;
    protected double time;
    protected double[] data;
    protected boolean direct = false;
    protected boolean timed = false;
    protected boolean fired;
    protected boolean upwardKnockbackSet = true;
    protected boolean doesKnockback = true;
    protected boolean pierce = false;
    protected boolean fireOpposite = false;
    protected boolean lastsOnGround = false;
    protected boolean clearOnFinish = true;
    protected boolean expAdd = false;
    protected boolean fromPlayer = true;
    protected int iFrames;
    protected int hungerGain;

    public EntityProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
        this.plugin = plugin;
        this.firer = firer;
        this.name = name;
        this.projectile = projectile;
        this.iFrames = 5;
        this.data = new double[]{0, 0, 0, 0, 0, 0, 0};
    }

    public double getRandomVariation() {
        double variation = getVariation();
        double randomAngle = Math.random() * variation / 2;
        if (new Random().nextBoolean()) {
            randomAngle *= -1;
        }
        return randomAngle * Math.PI / 180;
    }

    public void launchProjectile(){
        if (timed) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {projectile.remove();}
            }, (long) (time * 20));
        }
        firer.setLevel(0);
        if (fired) {
            return;
        }
        if (getOverridePosition() == null){
            setOverridePosition(firer.getEyeLocation());
        }
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                projectile.teleport(getOverridePosition());
            }
        }, 2L);
        projectile.setCustomName(name);
        if (projectile instanceof Item){
            Item item = (Item) projectile;
            item.setPickupDelay(10000000);
         }
        double magnitude = getSpeed();
        Vector direction;
        if (fromPlayer) {
            direction = firer.getLocation().getDirection();
        } else {
            direction = getOverridePosition().getDirection();
        }
        if (getFireOpposite()) {
            direction.multiply(-1);
        }
        direction.rotateAroundX(getRandomVariation());
        direction.rotateAroundY(getRandomVariation());
        direction.rotateAroundZ(getRandomVariation());
        double rotation = getAngle() * Math.PI / 180;
        direction.rotateAroundY(getAngle() * Math.PI / 180);
        if (direct) {
            projectile.setVelocity(direction.multiply(magnitude).setY(0).normalize());
        } else {
            projectile.setVelocity(direction.multiply(magnitude));
        }
        this.runTaskTimer(plugin, 0L, 1L);
        fired = true;
    }

    @Override
    public void run() {
        if (projectile.isDead() || !projectile.isDead() && projectile.isOnGround()) {
            if (!lastsOnGround) {
                onBlockHit(projectile.getLocation().subtract(0, 0.4, 0).getBlock());
                this.cancel();
                return;
            }
        }
        ArrayList<Location> checkLocations = new ArrayList<Location>();
        Location loc1 = projectile.getLocation().add(0.3,0,0);
        Location loc2 = projectile.getLocation().add(-0.3,0,0);
        Location loc3 = projectile.getLocation().add(0,0.3,0);
        Location loc4 = projectile.getLocation().add(0,-0.3,0);
        Location loc5 = projectile.getLocation().add(0,0,0.3);
        Location loc6 = projectile.getLocation().add(0,0,-0.3);
        checkLocations.add(loc1);
        checkLocations.add(loc2);
        checkLocations.add(loc3);
        checkLocations.add(loc4);
        checkLocations.add(loc5);
        checkLocations.add(loc6);
        for (Location locations : checkLocations){
            if (locations.getBlock().getType().isSolid() && !lastsOnGround){
                onBlockHit(locations.getBlock());
                this.cancel();
                return;
            }
        }

        double hitboxRange = getHitboxSize();
        List<Entity> canHit = projectile.getNearbyEntities(hitboxRange, hitboxRange, hitboxRange);
        canHit.remove(projectile);
        canHit.remove(firer);
        if (canHit.size() <= 0) {
            return;
        }
        for (Entity entity : canHit) {
            if (!(entity instanceof LivingEntity)) {
                return;
            }
            if (entity.getName().equalsIgnoreCase(projectile.getName())) {
                continue;
            }
            LivingEntity target = (LivingEntity) canHit.get(0);
            if (!pierce) {
                onHit(target);
            }
            break;
        }
    }

    public boolean onHit(LivingEntity target) {
        boolean success = target != null;
        if (success) {
            double damage = getDamage();
            int iFrames = getIFrames();
            if (target instanceof Player) {
                DamageUtil.dealAbilityDamageToPlayer(firer, (Player) target, damage, true, expAdd, name, iFrames, projectile.getLocation(), getKnockback(), getUpwardKnockback());
            } else {
                DamageUtil.dealAbilityDamageToNonPlayer(firer, target, damage, true, expAdd, name, iFrames);
            }
            firer.setFoodLevel(firer.getFoodLevel() + hungerGain);
            double knockback = getKnockback();
            double upwardKnockback = getUpwardKnockback();
            Vector velocity = projectile.getVelocity();
            if (doesKnockback) {
                if (upwardKnockbackSet) {
                 //   VelocityUtil.addKnockback(firer, target, knockback, upwardKnockback);
                } else {
                    velocity = velocity.normalize().multiply(knockback);
                    target.setVelocity(velocity);
                }
            }
        } else {
            if (!lastsOnGround) {
                projectile.remove();
            }
        }
        if (!pierce) {
            clearProjectile();
        }
        return success;
    }

    public boolean onBlockHit(Block landingLocation) {
        projectile.remove();
        clearProjectile();
        return true;
    }

    public boolean clearProjectile() {
        if (getClearOnFinish()) {
            projectile.remove();
            return true;
        }
        return false;
    }

    public void setOverridePosition(Location location){overridePosition = location;}

    public Location getOverridePosition(){return overridePosition;}

    public boolean getFireOpposite(){return fireOpposite;}

    public void setDamage(double damage) {
        data[0] = damage;
    }

    public double getDamage() {
        return data[0];
    }

    public void setSpeed(double speed) {
        data[1] = speed;
    }

    public double getSpeed() {
        return data[1];
    }

    public void setKnockback(double knockback) {
        data[2] = knockback;
    }

    public double getKnockback() {
        return data[2];
    }

    public void setUpwardKnockback(double upwardKnockback) {
        data[3] = upwardKnockback;
        upwardKnockbackSet = true;
    }

    public double getUpwardKnockback() {
        return data[3];
    }

    public void setHitboxSize(double hitboxRange) {
        data[4] = hitboxRange;
    }

    public double getHitboxSize() {
        return data[4];
    }

    public void setVariation(double variation) {
        data[5] = variation;
    }

    public double getVariation() {
        return data[5];
    }

    public void setAngle(double angle) {
        data[6] = angle;
    }

    public double getAngle() {
        return data[6];
    }

    public int getIFrames(){return iFrames;}

    public void setiFrames(int setIFrames){iFrames = setIFrames;}

    public boolean getClearOnFinish(){return clearOnFinish;}

    public void setFromPlayer(boolean isFromPlayer){fromPlayer = isFromPlayer;}
}
