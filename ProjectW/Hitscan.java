package ProjectW;

import ProjectW.Utils.DamageUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;

public class Hitscan {

    protected Plugin plugin;
    protected Player firer;
    protected String name;
    protected Particle particle;
    protected boolean pierces = false;
    protected boolean melee = false;
    protected double spread = 0;
    private Particle particleEffect;
    private double[] data;
    private int iFrames;
    private boolean safety;
    private boolean hit;
    List<Entity> canHit;
    Location loc;

    public Hitscan(Plugin plugin, Player firer, String name) {
        this.plugin = plugin;
        this.firer = firer;
        this.name = name;
        this.iFrames = 0;
        this.data = new double[]{0, 0, 0, 0, 0, 0, 0};
    }

    public float getRandomSpread(float spread) {
        float min = spread * -1;
        float randomSpread = (float) ((Math.random() * (spread - min)) + min) * (float) Math.PI / 180;
        return randomSpread;
    }

    public void shoot() {
        hit = false;
        safety = true;
        float spread = (float) getSpread();
        loc = firer.getLocation().add(0, 1, 0);
        Vector direction = loc.getDirection().multiply(0.2);
        direction.rotateAroundX(getRandomSpread(spread));
        direction.rotateAroundY(getRandomSpread(spread));
        direction.rotateAroundZ(getRandomSpread(spread));
        double length = getLength();
        double hitbox = getHitbox();
        double damage = getDamage();
        double knockback = getKnockback();
        double upwardsKnockback = getUpwardsKnockback();
        int iFrames = getIFrames();
        particleEffect = getParticleEffect();
        direction.normalize();
        for (int i = 0; i <= length; i++) {
            loc.add(direction);
            if (particleEffect != null) {
                firer.getWorld().spawnParticle(particleEffect, loc, 0);
            }
            canHit = (List<Entity>) loc.getWorld().getNearbyEntities(loc, hitbox, hitbox, hitbox);
            canHit.remove(firer);
            for (Entity entity : canHit) {
                if (entity instanceof Player) {
                    Player victim = (Player) entity;
                    DamageUtil.dealAbilityDamageToPlayer(firer, victim, damage, true, false, name, iFrames, loc, knockback, upwardsKnockback);
                    if (!getPierces()) {
                        i = (int) length + 1;
                        if (safety = true) {
                            hit = true;
                            onPlayerHit(victim);
                            safety = false;
                        }
                    }
                    if (getPierces()) {
                        onPiercingHit(victim);
                    }
                }
            }
            Block blockHit = loc.getBlock();
            if (blockHit.getType().isSolid()) {
                hit = true;
                i = (int) length + 1;
                onBlockHit(blockHit);
            }
            if (!hit && i == length) {
                onNothingHit(loc);
            }
        }
    }

    public boolean onPlayerHit(LivingEntity target) {
        safety = false;
        return true;
    }

    public boolean onPiercingHit(LivingEntity target) {
        return true;
    }

    public boolean onNothingHit(Location endLoc) {
        return true;
    }

    public boolean onBlockHit(Block target) {
        return true;
    }

    public void setLength(Integer setLength) {
        data[0] = setLength;
    }

    public double getLength() {
        return data[0];
    }

    public void setHitbox(Float setHitbox) {
        data[1] = setHitbox;
    }

    public double getHitbox() {
        return data[1];
    }

    public void setDamage(Double setDamage) {
        data[2] = setDamage;
    }

    public double getDamage() {
        return data[2];
    }

    public void setKnockback(Double setKnockback) {
        data[3] = setKnockback;
    }

    public double getKnockback() {
        return data[3];
    }

    public void setIFrames(int setIFrames) {
        iFrames = setIFrames;
    }

    public int getIFrames() {
        return iFrames;
    }

    public void setSpread(int setSpread) {
        data[5] = setSpread;
    }

    public double getSpread() {
        return data[5];
    }

    public void setUpwardsKnockback(Double setUpwardsKnockback) {
        data[6] = setUpwardsKnockback;
    }

    public double getUpwardsKnockback() {
        return data[6];
    }

    public Location getLoc() {
        return loc;
    }

    public void setParticleEffect(Particle setParticleEffect) {
        particleEffect = setParticleEffect;
    }

    public Particle getParticleEffect() {
        return particleEffect;
    }

    public void setPierces(Boolean setPierces) {
        pierces = setPierces;
    }

    public Boolean getPierces() {
        return pierces;
    }
}