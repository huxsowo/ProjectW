package ProjectW;

import ProjectW.MapPoints.PowerupSpawn;
import ProjectW.MapPoints.TeamSpawn;
import ProjectW.MapPoints.WoolPoint;
import org.bukkit.Location;

import java.util.ArrayList;

public class MapData {

    private String name;
    private WoolPoint woolPoint;
    private ArrayList<Location> spawnLocations;

    public MapData(String name, WoolPoint woolPoint, ArrayList<Location> spawnLocations){
        this.name = name;
        this.woolPoint = woolPoint;
        this.spawnLocations = spawnLocations;
    }

    public String getName(){return name;}

    public WoolPoint getWoolPoint(){return woolPoint;}

    public ArrayList<Location> getSpawnLocations(){return spawnLocations;}

    public void setName(String name){this.name = name;}

    public void setWoolPoint(WoolPoint woolPoint){this.woolPoint = woolPoint;}

    public void setSpawnLocations(ArrayList<Location> spawnLocations){this.spawnLocations = spawnLocations;}

    public void addSpawnLocation(Location location){this.spawnLocations.add(location);}
}
