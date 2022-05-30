package ProjectW.GameManagers;

import ProjectW.Utils.ServerMessageType;
import ProjectW.Utils.ServerMessageUtil;
import ProjectW.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;

public class CooldownManager extends BukkitRunnable {
    public static CooldownManager ourInstance = new CooldownManager();
    public static ArrayList<CooldownData> cooldownData = new ArrayList<>();

    private boolean isRunning = false;

    public void start(Plugin plugin) throws ManagerAlreadyRunningException {
        if (isRunning) {
            throw new ManagerAlreadyRunningException();
        }

        this.runTaskTimer(plugin, 1, 1);
        isRunning = true;
    }

    @Override
    public void run() {
        for (Iterator<CooldownData> cdDataIterator = cooldownData.iterator(); cdDataIterator.hasNext(); ) {
            CooldownData currData = cdDataIterator.next();

            if (currData.getRemainingTimeMs() <= 0) {
                //currData.abilityUser.sendMessage("You can use + currData.abilityName");
                cdDataIterator.remove();

                if (Utils.holdingItemWithName(currData.abilityUser, currData.abilityName)) {
                    ArrayList<String> lore = (ArrayList<String>) currData.abilityUser.getInventory().getItemInMainHand().getItemMeta().getLore();
                    ServerMessageUtil.sendServerMessageToPlayer("§7You can use §a" + lore.get(0) + "§7.", currData.abilityUser, ServerMessageType.RECHARGE);
                    currData.abilityUser.playSound(currData.abilityUser.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 24.0f);
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Utils.holdingItemWithLore(player, currData.abilityName) && player.equals(currData.abilityUser)) {
                    displayCooldownTo(player, currData);
                }
            }
        }
    }

    public long getRemainingTimeFor(String abilityName, Player abilityUser) {
        for (CooldownData cd : cooldownData) {
            if (StringUtils.containsIgnoreCase(cd.abilityName, abilityName) && cd.abilityUser.equals(abilityUser)) {
                return cd.getRemainingTimeMs();
            }
        }
        return 0;
    }

    public void addCooldown(String abilityName, long duration, Player abilityUser) {
        if (duration <= 1000){
            return;
        } else {
            cooldownData.add(new CooldownData(abilityName, duration, abilityUser));
        }
    }

    private void displayCooldownTo(Player player, CooldownData cd) {
        int barLength = 24;
        int startRedBarInterval = barLength - (int) ((cd.getRemainingTimeMs() / (double) cd.duration) * barLength); //Val between 0 - 1
        ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInMainHand().getItemMeta().getLore();
        StringBuilder sb = new StringBuilder("§f§l" + lore.get(0) + " ");
        for (int i = 0; i < barLength; i++){
            if (i < startRedBarInterval){
                sb.append("§a▌");
            } else {
                sb.append("§c▌");
            }
        }

        sb.append("  §f" + Utils.msToSeconds(cd.getRemainingTimeMs()) + " Seconds");
        Utils.sendActionBarMessage(sb.toString(), player);
    }

    public static CooldownManager getInstance(){return ourInstance;}

    public static class CooldownData {
        public Player abilityUser;
        public String abilityName;
        public long duration;
        public long startTime;

        public CooldownData(String abilityName, long duration, Player abilityUser) {
            this.abilityName = abilityName;
            this.duration = duration;
            startTime = System.currentTimeMillis();
            this.abilityUser = abilityUser;
        }

        public long getRemainingTimeMs() {return  startTime + duration - System.currentTimeMillis();}
    }
}
