package ProjectW.GameManagers;

import ProjectW.DamageReason;
import ProjectW.ProjectW;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;

public class DamageManager {

    public static DamageManager ourInstance = new DamageManager();
    public static HashMap<LivingEntity, HashSet<DamageReason>> playerLastDamageReasons = new HashMap<LivingEntity, HashSet<DamageReason>>();
    private static int meleeIFrames = 8;

    private boolean isRunning = false;

    public void start(Plugin plugin) throws ManagerAlreadyRunningException {
        if (isRunning){
            throw new ManagerAlreadyRunningException();
        }
        isRunning = true;
    }

    public static void giveDamageReason(LivingEntity damagee, DamageReason damageReason){
        if (!playerLastDamageReasons.containsKey(damagee)){
            HashSet<DamageReason> reasons = new HashSet<DamageReason>();
            reasons.add(damageReason);
            playerLastDamageReasons.put(damagee, reasons);
        } else {
            HashSet<DamageReason> reasons = getEntityDamageReasons(damagee);
            reasons.add(damageReason);
        }
        int delayTicks = damageReason.getiFrames();
        new BukkitRunnable(){
            @Override
            public void run(){
                HashSet<DamageReason> reasons = getEntityDamageReasons(damagee);
                reasons.remove(damageReason);
                if (reasons.isEmpty()){
                    clearEntityDamageHistory(damagee);
                }
                damageReason.destroyDamageReason();
            }
        }.runTaskLater(ProjectW.getInstance(), delayTicks);
    }

    public static void clearEntityDamageHistory(LivingEntity entity){
        if (playerLastDamageReasons.containsKey(entity)){
            HashSet<DamageReason> reasons = playerLastDamageReasons.get(entity);
            reasons.clear();
            playerLastDamageReasons.remove(entity);
        }
    }

    public static HashSet<DamageReason> getEntityDamageReasons(LivingEntity entity){
        return playerLastDamageReasons.get(entity);
    }

    public static boolean entityAlreadyHasReason(LivingEntity entity, DamageReason reason){
        if (playerLastDamageReasons.containsKey(entity)) {
            String reasonString1 = "" + reason.getReason() + reason.getiFrames() + reason.getDamager().getUniqueId() + reason.getDamageType();
            for (DamageReason existingReasons : playerLastDamageReasons.get(entity)) {
                String reasonString2 = "" + existingReasons.getReason() + existingReasons.getiFrames() + existingReasons.getDamager().getUniqueId() + existingReasons.getDamageType();
                if (reasonString1.equalsIgnoreCase(reasonString2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getMeleeIFrames(){return meleeIFrames;}
}
