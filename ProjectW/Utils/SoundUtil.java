package ProjectW.Utils;

import ProjectW.ProjectW;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SoundUtil {

    public static void playKillSound(Player killer){
        killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,0.9f);
        new BukkitRunnable() {
            @Override
            public void run(){
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
            }
        }.runTaskLater(ProjectW.getInstance(), 2);
    }
}
