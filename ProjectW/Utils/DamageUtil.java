package ProjectW.Utils;

import ProjectW.DamageReason;
import ProjectW.GameManagers.DamageManager;
import ProjectW.GameManagers.KitManager;
import ProjectW.Kit;
import ProjectW.ProjectW;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DamageUtil implements Listener {

    @EventHandler //TODO: Expand upon this to encompass the possibility of non players damage players and vice versa
    public void onMelee(EntityDamageByEntityEvent e) { //Cancels the vanilla melee attack damage and knockback to apply custom damage and knockback based on kit stats
        e.setCancelled(true);
        LivingEntity damagee = (LivingEntity) e.getEntity();
        LivingEntity damager = (LivingEntity) e.getDamager();
        if (damagee instanceof Player && damager instanceof Player) {
            Player playerDamagee = (Player) damagee;
            Player playerDamager = (Player) damager;
            if (KitManager.getInstance().getPlayerKit(playerDamager) != null && KitManager.getInstance().getPlayerKit(playerDamagee) != null) {
                damagee.setNoDamageTicks(0);
                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) { //if the attack that triggered the event was a melee attack, proceed with the function
                    Kit damagerKit = KitManager.getInstance().getPlayerKit(playerDamager);
                    DamageReason damageReason = new DamageReason("Attack", playerDamager, DamageType.MELEE, DamageManager.getMeleeIFrames());
                    if (!hasDamageReason(damagee, damageReason)) {
                        dealMeleeDamageToPlayer(playerDamager, playerDamagee, damagerKit.getDamage(), damageReason);
                    }
                }
            } else {
                ConsoleMessageUtil.LogDebugMessage("Attacking without kit");
            }
        }
    }

    public static boolean hasDamageReason(LivingEntity damagee, DamageReason damageReason){ //checks if the player still has iframes from a duplicate form of damage
        if (DamageManager.entityAlreadyHasReason(damagee, damageReason)) {
            return true;
        } else {
            return false;
        }
    }

    public void dealMeleeDamageToPlayer(Player damager, Player damagee, double damage, DamageReason reason){ //Deals melee damage to a player.
        Kit damageeKit = KitManager.getInstance().getPlayerKit(damagee);
        double damageMultiplier = Math.max(0.2f, damager.getAttackCooldown());
        double armor = (damageeKit.getArmor() * 2) * 4;
        damage = damage * damageMultiplier;
        if (damagee.getFireTicks() > 0){
            damage *= 1.25;
        }
        double totalDamage = (damage - ((armor / 100.0) * damage));
        showDamageNumber(damager, damagee, totalDamage);
        DamageManager.giveDamageReason(damagee, reason);
        damagee.damage(totalDamage);
        ConsoleMessageUtil.LogDebugMessage("Initial Damage: " + damage + ". Defense Percent: " + armor + " Damage after calculations: " + totalDamage);
        for (DamageReason reasons : DamageManager.getEntityDamageReasons(damagee)){
            if (reasons.getDamageType() == DamageType.MELEE){
                VelocityUtil.addMeleeKnockbackToPlayer(damager, damagee, (0.425 * damageMultiplier), 0.35);
                return;
            }
        }
    }

    public static void dealAbilityDamageToPlayer(Player damager, Player damagee, double damage, boolean setExpDamage, boolean expAdd, String reason, int iFrames, Location source, double knockback, double upwardsKnockback) {
        if (KitManager.getInstance().getPlayerKit(damager) != null && KitManager.getInstance().getPlayerKit(damagee) != null) {
            damagee.setNoDamageTicks(0);
            DamageReason damageReason = new DamageReason(reason, damager, DamageType.ABILITY, iFrames);
            if (!hasDamageReason(damagee, damageReason)) {
                Kit damageeKit = KitManager.getInstance().getPlayerKit(damagee);
                double armor = (damageeKit.getArmor() * 2) * 4;
                if (damagee.getFireTicks() > 0){
                    damage *= 1.25;
                }
                double totalDamage = (damage - ((armor / 100.0) * damage));
                showDamageNumber(damager, damagee, totalDamage);
                damagee.damage(totalDamage);
                damagee.setNoDamageTicks(0);
                DamageManager.giveDamageReason(damagee, damageReason);
                if (DamageManager.getEntityDamageReasons(damagee).size() > 0){
                    return;
                }
                VelocityUtil.addAbilityKnockbackToPlayer(source, damagee, knockback, upwardsKnockback);
            }
        } else {
            ConsoleMessageUtil.LogDebugMessage("Attacking without kit");
        }
    }

    public static void dealAbilityDamageToNonPlayer(LivingEntity damager, LivingEntity damagee, double damage, boolean setExpDamage, boolean expAdd, String reason, int iFrames) {

    }

    public static void showDamageNumber(Player damager, LivingEntity damagee, double damage){
        if (damagee instanceof ArmorStand){
            return;
        }
        float spread = 1f;
        Location l = damagee.getEyeLocation();
        net.minecraft.server.v1_16_R3.World world = ((CraftWorld) damager.getWorld()).getHandle();
        net.minecraft.server.v1_16_R3.EntityArmorStand nmsArmorStand = new net.minecraft.server.v1_16_R3.EntityArmorStand(world,getRandomSpread(spread),getRandomSpread(spread),getRandomSpread(spread));
        Location startLoc = new Location (damager.getWorld(), l.getX() + getRandomSpread(spread), l.getY() + getRandomSpread(spread), l.getZ() + getRandomSpread(spread), l.getYaw(), l.getPitch());
        nmsArmorStand.setLocation(startLoc.getX(), startLoc.getY(), startLoc.getZ(), startLoc.getYaw(), startLoc.getPitch());
        nmsArmorStand.setMarker(true);
        nmsArmorStand.setInvisible(true);
        String damageString;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        damageString = decimalFormat.format(damage);
        ChatComponentText name = new ChatComponentText("✰" + damageString + "✰");
        nmsArmorStand.setCustomName(name);
        nmsArmorStand.setCustomNameVisible(true);
        world.addEntity(nmsArmorStand);
        new BukkitRunnable() {
            @Override
            public void run() {
                nmsArmorStand.setLocation(nmsArmorStand.locX(), nmsArmorStand.locY() + 0.1, nmsArmorStand.locZ(), nmsArmorStand.yaw, nmsArmorStand.pitch);
                Location newLocation = new Location(damager.getWorld(), nmsArmorStand.locX(), nmsArmorStand.locY(), nmsArmorStand.locZ(), nmsArmorStand.yaw, nmsArmorStand.pitch);
                if (newLocation.getY() - startLoc.getY() > 1.0 || newLocation.getY() - startLoc.getY() < -1.0){
                    nmsArmorStand.die();
                    cancel();
                }
            }
        }.runTaskTimer(ProjectW.getInstance(), 0, 2);
    }

    public static float getRandomSpread(float spread){
        float min = spread * -1;
        float randomSpread = (float) ((Math.random()*(spread - min)) + min);
        return randomSpread;
    }
}
