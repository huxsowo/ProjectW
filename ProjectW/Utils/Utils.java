package ProjectW.Utils;

import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Utils {

    public static boolean holdingItemWithLore(Player player, String lore) {
        ItemMeta itemMeta = player.getInventory().getItemInMainHand().getItemMeta();
        if (itemMeta != null){
            return itemMeta.getLore().get(0).equals(lore);
        }

        return false;
    }

    public static boolean holdingItemWithName(Player player, String name) {
        ItemMeta itemMeta = player.getInventory().getItemInMainHand().getItemMeta();
        if (itemMeta != null) {
            return itemMeta.getDisplayName().equals(name);
        }

        return false;
    }

    public static void sendActionBarMessage(String message, Player player) {
        String jsonMessage = "{\"text\": \"" + message + "\"}";
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(jsonMessage);
        PacketPlayOutChat chatPacket = new PacketPlayOutChat(chatBaseComponent, ChatMessageType.GAME_INFO, player.getUniqueId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(chatPacket);
    }

    public static double msToSeconds(long milliseconds) {
        return (long) (milliseconds / (double) 100) / (double) 10;
    }

    public static double getRandomDouble(double max, double min){
        return ((Math.random()*(max - min)) + min);
    }

    public static int getRandomInteger(int max, int min){return (int) ((Math.random()*(max - min)) + min);}

    public static void getBlocksWithinTwoPoints(Location loc1, Location loc2, Player player) {
        Vector v = loc1.toVector();
        Vector p = loc2.toVector();
        Vector d = v.subtract(p);
        BlockIterator BI = new BlockIterator(loc2.getWorld(), p, d, 0, ((Double) loc1.distance(loc2)).intValue());
        while (BI.hasNext()) {
            Block block = BI.next();
        }
    }

    public static boolean isPlayerTouchingGround(Player player){
        Block below = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (!below.isPassable()){
            double blockY = below.getLocation().getY();
            double playerY = player.getLocation().getY();
            Bukkit.broadcastMessage("" + (playerY - blockY));
            return true;
        }
        return false;
    }

    public static Location getMiddleOfBlock(Block block){
        Location blockLocation = block.getLocation();
        return blockLocation.add(0.5, 0.5, 0.5);
    }
}
