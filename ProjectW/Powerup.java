package ProjectW;

import ProjectW.Utils.ServerMessageType;
import ProjectW.Utils.ServerMessageUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class Powerup {

    protected Material powerupMaterial;
    protected String name;
    protected String ymlName;
    protected Player owner;

    protected Plugin plugin;

    public Powerup(){
        plugin = ProjectW.getInstance();
    }

    public void checkAndActivate(Player player){
        owner = player;
        activate();
        ServerMessageUtil.sendServerMessageToPlayer("You picked up the §l§2" + name + "§r powerup!", player, ServerMessageType.POWERUP, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
    }

    public abstract void activate();


    public Material getPowerupMaterial(){return powerupMaterial;}

    public String getName(){return name;}

    public String getYmlName(){return ymlName;}

}
