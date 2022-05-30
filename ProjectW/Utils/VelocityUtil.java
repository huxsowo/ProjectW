package ProjectW.Utils;

import ProjectW.GameManagers.KitManager;
import ProjectW.Kit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VelocityUtil {

    public static void addMeleeKnockbackToPlayer(Player owner, Player target, double amount, double ylimit){
        if (target.isDead()){
            return;
        }
        Vector enemy = target.getLocation().toVector();
        Vector player = owner.getLocation().toVector();
        Kit damageeKit = KitManager.getInstance().getPlayerKit(target);
        if (damageeKit.getName().equalsIgnoreCase("Choose")) {
            return;
        }
        Kit kit = KitManager.getInstance().getPlayerKit(target);
        for (int x = 0; x <= 20 - target.getHealth(); x++) {
            amount = amount + 0.0285 - (target.getHealth() / 500);
        }
        double kbToApply = (amount * kit.getKbMod());
        Vector difference = enemy.subtract(player);
        Vector finalVel = difference.normalize().multiply(kbToApply);
        double currentY = target.getVelocity().getY();
        target.setVelocity(new Vector(finalVel.getX(), (currentY + ylimit), finalVel.getZ()));
    }

    public static void addAbilityKnockbackToPlayer(Location source, Player target, double amount, double yLimit){
        if (target.isDead()){
            return;
        }
        Vector enemy = target.getLocation().toVector();
        Vector sourceVector = source.toVector();
        Kit damageeKit = KitManager.getInstance().getPlayerKit(target);
        if (damageeKit.getName().equalsIgnoreCase("Choose")) {
            return;
        }
        Kit kit = KitManager.getInstance().getPlayerKit(target);
        for (int x = 0; x <= 20 - target.getHealth(); x++) {
            amount = amount + 0.0285 - (target.getHealth() / 500);
        }
        double kbToApply = (amount * kit.getKbMod());
        Vector difference = enemy.subtract(sourceVector);
        Vector finalVel = difference.normalize().multiply(kbToApply);
        double currentY = target.getVelocity().getY();
        target.setVelocity(new Vector(finalVel.getX(), (yLimit), finalVel.getZ()).normalize());
    }
}
