package ProjectW.Kits;

import ProjectW.Abilities.IronGolemSword;
import ProjectW.Kit;
import ProjectW.Kits.KitTypes.KitType;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KitIronGolem extends Kit {

    public KitIronGolem(){
        super();
        this.name = "Iron_Golem";
        this.damage = 4.5;
        this.armor = 3.0;
        this.regen = 0.25;
        this.disguise = DisguiseType.IRON_GOLEM;
        this.displayItem = Material.IRON_BLOCK;
        this.speed = 20;
        this.kbMod = 0.85;
        this.kitType = KitType.TANK;
    }

    public void equipKit(Player player){
        super.equipKit(player);

        setArmor(Material.IRON_CHESTPLATE,2);

        setItem(Material.IRON_SWORD, 0, new IronGolemSword());
    }
}
