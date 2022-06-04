package ProjectW.GameManagers;

import ProjectW.Kit;
import ProjectW.Kits.KitIronGolem;
import ProjectW.Kits.KitPiglin;
import ProjectW.Kits.KitWitch;
import ProjectW.Kits.KitZombie;
import ProjectW.Utils.ConsoleMessageUtil;
import ProjectW.Utils.ServerMessageType;
import ProjectW.Utils.ServerMessageUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class KitManager {

    public static KitManager ourInstance = new KitManager();
    protected static HashMap<UUID, Kit> playerKitHashMap = new HashMap<UUID, Kit>();
    public Kit[] allKits;

    public void start() {
        allKits = new Kit[]{
                //Put in order of how they should appear in a list
                new KitZombie(),
                new KitIronGolem(),
                new KitPiglin(),
                new KitWitch(),
        };
    }

    public void equipPlayer(Player player, Kit check) {
        UUID playerUUID = player.getUniqueId();
        Kit kit = playerKitHashMap.get(playerUUID);
        if (playerKitHashMap.containsKey(playerUUID)) {
            kit.destroyKit();
        }
        try {
            kit = check.getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (kit != null) {
            kit.equipKit(player);
            playerKitHashMap.put(playerUUID, kit);
        } else {
            ServerMessageUtil.sendServerMessageToPlayer("The kit you are trying to equip does not exist", player, ServerMessageType.DEBUG);
            ConsoleMessageUtil.LogDebugMessage("PLAYER: " + player.getName() + " trying to equip a kit that does not exist");
        }
    }

    public Kit getPlayerKit(Player player){
        return playerKitHashMap.get(player.getUniqueId());
    }

    public static HashMap<UUID, Kit> getPlayerKitHashMap(){return playerKitHashMap;}

    public Kit[] getAllKits(){return allKits;}

    public static KitManager getInstance(){return ourInstance;}
}
