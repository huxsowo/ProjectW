package ProjectW.Abilities;

import ProjectW.Ability;
import ProjectW.EntityProjectile;
import ProjectW.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class SpewBile extends Ability {

    protected int projAmount = 30;

    public SpewBile(){
        super();
        this.name = "Spew Bile";
        this.cooldownTime = 6.5;
        this.swapSlotActivate = true;
    }

    public void activate(){
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1.0f, 2.0f);
        ItemStack rottenFlesh = new ItemStack(Material.ROTTEN_FLESH, 1);
        for (int i = 0; i < projAmount; i++) {
            Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), rottenFlesh);
            RottenProjectile projectile = new RottenProjectile(plugin, owner, name, firing);
            projectile.setOverridePosition(owner.getEyeLocation().add(Utils.getRandomDouble(1.5, -1.5), Utils.getRandomDouble(1.25, -0.25), Utils.getRandomDouble(1.5, -1.5)));
            projectile.launchProjectile();
        }
    }

    class RottenProjectile extends EntityProjectile{

        public RottenProjectile(Plugin plugin, Player firer, String name, Entity projectile){
        super(plugin, firer, name, projectile);
        this.setDamage(0.25);
        this.setSpeed(0.75);
        this.setKnockback(0.4);
        this.setUpwardKnockback(0.25);
        this.setHitboxSize(0.4);
        this.setVariation(30);
        this.setiFrames(0);
        }
    }
}
