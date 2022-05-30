package ProjectW.Attributes;

import ProjectW.Attribute;

public class Regeneration extends Attribute {

    double regen;
    double delay;

    public Regeneration(double regen, double delay){
        super();
        this.name = "Regeneration";
        this.regen = regen;
        this.delay = delay;
        task = this.runTaskTimer(plugin, 0, (long) delay * 20);
    }

    @Override
    public void run(){
        if (!owner.isDead()){
            owner.setHealth(Math.min(owner.getHealth() + regen, 20));
        }
    }
}
