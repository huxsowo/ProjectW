package ProjectW.Kits;

import ProjectW.Abilities.SpewBile;
import ProjectW.Abilities.BileSpout;
import ProjectW.Abilities.TestAbility;
import ProjectW.Abilities.ZombieSword;
import ProjectW.Attributes.Reaper;
import ProjectW.Attributes.Regeneration;
import ProjectW.Kit;
import ProjectW.Kits.KitTypes.KitType;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KitZombie extends Kit {

    public KitZombie(){
        super();

        this.name = "Zombie";
        this.damage = 4.0;
        this.armor = 1.5;
        this.health = 20.0;
        this.kbMod = 1.0;
        this.speed = 22;
        this.regen = 0.25;
        this.disguise = DisguiseType.ZOMBIE;
        this.displayItem = Material.ROTTEN_FLESH;
        this.kitType = KitType.BRUTE;
    }

    public void equipKit(Player player){
        super.equipKit(player);

        setArmor(Material.LEATHER_CHESTPLATE, 2);

        setItem(Material.IRON_SWORD, 0, new ZombieSword());
        setItem(Material.ROTTEN_FLESH, 1, new SpewBile());
        setItem(Material.ZOMBIE_HEAD, 2, new BileSpout());
        setItem(Material.BARRIER, 3, new TestAbility());

        addAttribute(new Regeneration(regen, 1));
        addAttribute(new Reaper(7));
    }
}
