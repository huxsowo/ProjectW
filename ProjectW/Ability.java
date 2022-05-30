package ProjectW;

import ProjectW.GameManagers.CooldownManager;
import ProjectW.Utils.ServerMessageType;
import ProjectW.Utils.ServerMessageUtil;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class Ability extends Attribute {

    protected double cooldownTime = 2.5;
    protected boolean leftClickActivate = false;
    protected boolean rightClickActivate = false;
    protected boolean swapSlotActivate = false;
    protected boolean mustBeOnGround = false;
    protected boolean mustBeInAir = false;
    protected ArrayList<String> lore;
    protected int slot = 0;

    public Ability() {
        super();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public abstract void activate();

    public void activateLeft(Player player){
        if (leftClickActivate) {
            checkAndActivate(player);
        }
    }

    public void activateRight(Player player){
        if (rightClickActivate) {
            checkAndActivate(player);
        }
    }

    public void activateSwap(Player player){
        if (swapSlotActivate){
            checkAndActivate(player);
        }
    }

    public void checkAndActivate(Player player){
        ItemStack item;
        if (!swapSlotActivate) {
            item = player.getInventory().getItemInMainHand();
        } else {
            item = player.getInventory().getItem(slot);
        }
        if (item == null || item.getItemMeta().getLore().get(0) == null){
            return;
        }
        String itemName = item.getItemMeta().getLore().get(0);

        if (CooldownManager.getInstance().getRemainingTimeFor(itemName, player) <= 0){
            if (StringUtils.containsIgnoreCase(itemName, name)){
                if (mustBeOnGround){
                    if (owner.getLocation().subtract(0,0.2,0).getBlock().isPassable()){
                        ServerMessageUtil.sendServerMessageToPlayer("You must be on the ground to use §a§l" + name, owner, ServerMessageType.ABILITY);
                        return;
                    }
                }
                if (mustBeInAir){
                    if (!owner.getLocation().subtract(0,0.2,0).getBlock().isPassable()){
                        ServerMessageUtil.sendServerMessageToPlayer("You must be in the air to use §a§l" + name, owner, ServerMessageType.ABILITY);
                        return;
                    }
                }
            }
            CooldownManager.getInstance().addCooldown(itemName, (long) (cooldownTime * 1000), player);
            if (swapSlotActivate){
                owner.setCooldown(item.getType(), (int) (cooldownTime * 20));
            }
            ServerMessageUtil.sendServerMessageToPlayer("You used " + name, player, ServerMessageType.ABILITY);
            activate();
        }
    }

    public static void setCooldown(String abilityName, Player abilityUser, long duration) {
        for (CooldownManager.CooldownData cd : CooldownManager.cooldownData) {
            if (cd.abilityUser == abilityUser && abilityUser.getInventory().getItemInMainHand().getItemMeta().getLore().contains(abilityName)){
                CooldownManager.cooldownData.remove(cd);
            }
            CooldownManager.cooldownData.add(new CooldownManager.CooldownData(abilityName, duration, abilityUser));
        }
    }

    public static void removeCooldown(String abilityName, Player abilityUser) {
        CooldownManager.cooldownData.removeIf(cd -> cd.abilityName.contains(abilityName) && cd.abilityUser == abilityUser);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if (player != owner){
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getHand() != EquipmentSlot.OFF_HAND) {
                activateRight(player);
                return;
            }
        }
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
            activateLeft(player);
        }
    }

    @EventHandler
    public void onPlayerSwapHeldItem(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        if (player != owner) {
            return;
        }
        if (e.getNewSlot() == slot){
            activateSwap(player);
        }
        e.setCancelled(true);
    }
}
