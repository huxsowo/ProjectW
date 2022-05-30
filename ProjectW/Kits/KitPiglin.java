package ProjectW.Kits;

import ProjectW.Abilities.SearingSweep;
import ProjectW.Attributes.Regeneration;
import ProjectW.Kit;
import ProjectW.Kits.KitTypes.KitType;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KitPiglin extends Kit {

    public KitPiglin(){
        super();
        this.name = "Piglin";
        this.armor = 2.5;
        this.damage = 5.0;
        this.disguise = DisguiseType.PIGLIN;
        this.speed = 22;
        this.displayItem = Material.GOLD_INGOT;
        this.kitType = KitType.BRUTE;
        this.regen = 0.25;
    }

    public void equipKit(Player player){
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setItem(Material.GOLDEN_SWORD, 0, new SearingSweep());

        addAttribute(new Regeneration(regen,1));
    }
}
