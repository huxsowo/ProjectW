package ProjectW;

import ProjectW.GameManagers.KitManager;
import ProjectW.Kits.KitTypes.KitType;
import ProjectW.Utils.ConsoleMessageUtil;
import ProjectW.Utils.ServerMessageType;
import ProjectW.Utils.ServerMessageUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Kit {

    // KIT DEFINERS
    protected String name = "Kit Name";
    protected Material displayItem = Material.BARRIER;
    protected DisguiseType disguise = null;
    protected KitType kitType = KitType.UNDEFINED;

    // KIT STATS
    protected double health = 20.0;
    protected double damage = 5.0;
    protected double armor = 5.0;
    protected double kbMod = 1.0;
    protected double regen = 0.25;
    protected float speed = 0f;
    protected float attackSpeedMultiplier = 1.0f;

    // KIT ATTACHMENTS
    protected Player owner;
    protected List<Attribute> attributes = new ArrayList<Attribute>();

    // EXTRA
    protected Plugin plugin;

    public void Kit(){this.plugin = ProjectW.getInstance();}

    public void equipKit(Player player){
        if (KitManager.getInstance().getPlayerKit(player) != null) {
            destroyKit();
        }
        owner = player;
        player.setWalkSpeed(speed * 0.01f);
        player.getInventory().clear();
        player.setExp(0f);
        player.setHealth(20.0);
        player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0 * getAttackSpeedMultiplier());
        if (disguise != null) {
            MobDisguise disg = new MobDisguise(disguise);
            disg.setEntity(player);
            FlagWatcher watcher = disg.getWatcher();
            watcher.setCustomName("§a§l" + owner.getName());
            watcher.setCustomNameVisible(true);
            ItemStack[] armorStack = watcher.getArmor();
            for (int i = 0; i <= 3; i++){
                armorStack[i] = new ItemStack(Material.AIR);
            }
            watcher.setArmor(armorStack);
            disg.startDisguise();
            ServerMessageUtil.sendServerMessageToPlayer("You equipped the " + name + " kit!", player, ServerMessageType.SERVER, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            player.getInventory().setHeldItemSlot(0);
            player.setGameMode(GameMode.SURVIVAL);
        }
        else {
            DisguiseAPI.undisguiseToAll(player);
        }
    }

    public void destroyKit(){
        Player player = owner;
        player.getInventory().clear();
        KitManager.getPlayerKitHashMap().remove(owner.getUniqueId());
        owner = null;
        for (Attribute attribute : attributes){
            if (attribute instanceof Ability){
                Ability.removeCooldown(attribute.name, player);
            }
            attribute.remove();
        }
        attributes.clear();
    }

    public void addAttribute(Attribute attribute){
        attributes.add(attribute);
        attribute.setOwner(owner);
    }

    public void setItem(Material itemMaterial, int inventorySlot, Ability ability){
        if (owner == null){
            return;
        }
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();
        if (ability != null){
            ability.slot = inventorySlot;
            addAttribute(ability);
            if (ability.rightClickActivate) {
                meta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "Right Click " + ChatColor.WHITE + "➝ " + ChatColor.GREEN + ChatColor.BOLD + ability.name);
            } else if (ability.leftClickActivate){
                meta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "Left Click " + ChatColor.WHITE + "➝ " + ChatColor.GREEN + ChatColor.BOLD + ability.name);
            } else if (ability.swapSlotActivate) {
                meta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "Hold " + ChatColor.WHITE + "➝ " + ChatColor.GREEN + ChatColor.BOLD + ability.name);
            }
            if (ability.lore != null){
                meta.setLore(ability.lore);
            } else {
                ArrayList<String> lore = new ArrayList<>();
                lore.clear();
                lore.add(ability.name);
                meta.setLore(lore);
            }
        }
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage((int)damage);
        }
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        owner.getInventory().setItem(inventorySlot, item);
    }

    public void setArmor(Material itemMaterial, int armorSlot){
        if (!hasOwner()){
            return;
        }
        ItemStack item = new ItemStack(itemMaterial);
        if (!(CraftItemStack.asNMSCopy(item).getItem() instanceof ItemArmor) && armorSlot != 3){ //Checks if the item trying to be equipped is a type of armor. Aborts the function if it is not armor.
            ConsoleMessageUtil.LogDebugMessage("Item being equipped to kit is not valid armor. ITEM: " + Objects.requireNonNull(item.getItemMeta()).getDisplayName());
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        ItemStack[] armor = owner.getInventory().getArmorContents();
        armor[armorSlot] = item;
        owner.getInventory().setArmorContents(armor);
    }

    public boolean hasOwner(){
        if (owner == null){
            return false;
        } else {
            return true;
        }
    }

    public Player getOwner(){return owner;}

    public String getName(){return name;}

    public Material getDisplayItem(){return displayItem;}

    public DisguiseType getDisguise(){return disguise;}

    public KitType getKitType(){return kitType;}

    public double getHealth(){return health;}

    public double getDamage(){return damage;}

    public double getArmor(){return armor;}

    public double getKbMod(){return kbMod;}

    public double getRegen(){return regen;}

    public float getSpeed(){return speed;}

    public float getAttackSpeedMultiplier(){return attackSpeedMultiplier;}

}
