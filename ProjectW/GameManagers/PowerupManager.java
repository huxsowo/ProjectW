package ProjectW.GameManagers;

import ProjectW.Powerup;
import ProjectW.Powerups.CooldownReset;
import ProjectW.Powerups.DoubleJump;
import ProjectW.Powerups.MeterCharge;
import net.minecraft.server.v1_16_R3.EntityArmorStand;

import java.util.ArrayList;

public class PowerupManager {

    public static PowerupManager ourInstance = new PowerupManager();

    private Powerup[] allPowerups;
    private ArrayList<EntityArmorStand> powerupStands = new ArrayList<EntityArmorStand>();

    public void start(){
        allPowerups = new Powerup[]{
                new DoubleJump(),
                new CooldownReset(),
                new MeterCharge(),
        };
    }

    public static PowerupManager getInstance(){return ourInstance;}

    public Powerup[] getAllPowerups(){return allPowerups;}

    public ArrayList<EntityArmorStand> getPowerupStands(){return powerupStands;}
}
