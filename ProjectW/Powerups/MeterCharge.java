package ProjectW.Powerups;

import ProjectW.Powerup;
import org.bukkit.Material;

public class MeterCharge extends Powerup {

    public MeterCharge(){
        super();
        this.name = "Meter Charge";
        this.ymlName = "mc";
        this.powerupMaterial = Material.EXPERIENCE_BOTTLE;
    }

    public void activate(){
        owner.setExp(Math.min(1.0f, (owner.getExp() + 0.5f)));
    }
}
