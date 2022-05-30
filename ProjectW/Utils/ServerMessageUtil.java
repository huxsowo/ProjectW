package ProjectW.Utils;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ServerMessageUtil {

    public static void sendServerMessageToPlayer(String message, Player player, ServerMessageType type) {
        player.sendMessage(type + " " + message);
    }

    public static void sendServerMessageToPlayer(String message, Player player, ServerMessageType type, Sound soundEffect) {
        player.sendMessage(type + " " + message);
        player.playSound(player.getLocation(), soundEffect, 1f, 1f);
    }

    public static void sendServerMessageToWorld(String message, World world, ServerMessageType type) {
        for (Player player : world.getPlayers()){
            player.sendMessage(type + " " + message);
        }
    }

    public static void sendServerMessageToWorld(String message, World world, ServerMessageType type, Sound soundEffect) {
        for (Player player : world.getPlayers()){
            player.sendMessage(type + " " + message);
            player.playSound(player.getLocation(), soundEffect, 1f, 1f);
        }
    }
}
