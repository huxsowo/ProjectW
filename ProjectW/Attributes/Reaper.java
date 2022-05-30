package ProjectW.Attributes;

import ProjectW.Attribute;
import ProjectW.DamageReason;
import ProjectW.GameManagers.DamageManager;
import ProjectW.Utils.DamageType;
import ProjectW.Utils.ServerMessageType;
import ProjectW.Utils.ServerMessageUtil;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Reaper extends Attribute {

    int hits = 5;
    boolean charged = false;

    public Reaper(int hits){
        super();
        this.name = "Reaper";
        this.hits = hits;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onMeleeHit(EntityDamageByEntityEvent e){
        if (e.getDamager() != owner){
            return;
        }
        DamageReason reason = new DamageReason("Attack", (Player) e.getDamager(), DamageType.MELEE, DamageManager.getMeleeIFrames());
        if (DamageManager.entityAlreadyHasReason((LivingEntity) e.getEntity(), reason)){
            return;
        }
        if (charged){
            if (owner.getExp() < 1.0f){
                charged = false;
            } else {
                return;
            }
        }
        float expAdd = (float) 1 / hits;
        double expMultiplier = Math.max(0.2f, owner.getAttackCooldown());
        expAdd *= expMultiplier;
        owner.setExp(Math.min(1.0f, owner.getExp() + expAdd));
        if (owner.getExp() >= 1.0f){
            owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,1,2);
            ServerMessageUtil.sendServerMessageToPlayer("You are fully charged.", owner, ServerMessageType.ABILITY);
            charged = true;
        }
    }
}
