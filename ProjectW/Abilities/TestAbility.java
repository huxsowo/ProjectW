package ProjectW.Abilities;

import ProjectW.Ability;
import ProjectW.GameManagers.GameManager;
import ProjectW.MapData;
import ProjectW.MapPoints.PowerupSpawn;
import ProjectW.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class TestAbility extends Ability {

    public TestAbility(){
        super();
        this.name = "Test Ability";
        this.cooldownTime = 4;
        this.swapSlotActivate = true;
    }

    public void activate(){
        Location location = Utils.getMiddleOfBlock(owner.getLocation().getBlock());
        Bukkit.broadcastMessage(location.toString());
    }
}
