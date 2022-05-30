package ProjectW;

import ProjectW.Utils.DamageType;
import org.bukkit.entity.Player;

public class DamageReason {

    protected String reason = "damage reason";
    protected Player damager;
    protected DamageType damageType;
    protected Integer iFrames = 0;

    public DamageReason(String reason, Player damager, DamageType damageType, int iFrames){
        this.reason = reason;
        this.damager = damager;
        this.damageType = damageType;
        this.iFrames = iFrames;
    }

    public void destroyDamageReason(){
        this.damageType = null;
        this.damager = null;
        this.reason = null;
        this.iFrames = null;
    }

    public String getReason(){return reason;}

    public Player getDamager(){return damager;}

    public DamageType getDamageType(){return damageType;}

    public int getiFrames(){return iFrames;}

    public void setIFrames(int setIFrames){iFrames = setIFrames;}
}
