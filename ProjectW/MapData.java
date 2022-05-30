package ProjectW;

import ProjectW.MapPoints.PowerupSpawn;
import ProjectW.MapPoints.TeamSpawn;
import ProjectW.MapPoints.WoolPoint;

import java.util.ArrayList;

public class MapData {

    private String name = null;
    private WoolPoint woolPoint = null;
    private ArrayList<PowerupSpawn> powerupSpawns = null;
    private TeamSpawn redTeamSpawn = null;
    private TeamSpawn blueTeamSpawn = null;
    private double verticalBoost = 1.0;
    private double horizontalBoost = 0.0;

    public MapData(){}

    public String getName(){return name;}

    public WoolPoint getWoolPoint(){return woolPoint;}

    public ArrayList<PowerupSpawn> getPowerupSpawns(){return powerupSpawns;}

    public TeamSpawn getRedTeamSpawn(){return redTeamSpawn;}

    public TeamSpawn getBlueTeamSpawn(){return blueTeamSpawn;}

    public double getVerticalBoost(){return verticalBoost;}

    public double getHorizontalBoost(){return horizontalBoost;}

    public void setName(String name){this.name = name;}

    public void setWoolPoint(WoolPoint woolPoint){this.woolPoint = woolPoint;}

    public void setPowerupSpawns(ArrayList<PowerupSpawn> powerupSpawns){this.powerupSpawns = powerupSpawns;}

    public void setRedTeamSpawn(TeamSpawn redTeamSpawn){this.redTeamSpawn = redTeamSpawn;}

    public void setBlueTeamSpawn (TeamSpawn blueTeamSpawn){this.blueTeamSpawn = blueTeamSpawn;}

    public void setVerticalBoost(double verticalBoost){this.verticalBoost = verticalBoost;}

    public void setHorizontalBoost(double horizontalBoost){this.horizontalBoost = horizontalBoost;}
}
