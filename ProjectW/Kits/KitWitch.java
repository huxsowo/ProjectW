package ProjectW.Kits;

import ProjectW.Abilities.PotionToss;
import ProjectW.Attributes.Regeneration;
import ProjectW.Kit;
import ProjectW.Kits.KitTypes.KitType;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KitWitch extends Kit {

    public KitWitch(){
        super();
        this.name = "Witch";
        this.disguise = DisguiseType.WITCH;
        this.kitType = KitType.UNDEFINED;
        this.regen = 0.25;
        this.damage = 6;
        this.armor = 5;
        this.displayItem = Material.POTION;
        this.speed = 22;
    }

    public void equipKit(Player player){
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_CHESTPLATE,2);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_BOOTS,0);

        setItem(Material.IRON_AXE,0,new PotionToss());

        addAttribute(new Regeneration(regen, 1));
    }
}
