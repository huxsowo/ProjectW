package ProjectW.Utils;

import org.bukkit.Bukkit;

public class ConsoleMessageUtil {

    public static void LogDebugMessage(String message){
        Bukkit.getConsoleSender().sendMessage("[DEBUG] " + message);
    }
}
