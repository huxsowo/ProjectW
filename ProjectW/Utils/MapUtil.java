package ProjectW.Utils;

import ProjectW.GameManagers.GameManager;
import ProjectW.GameManagers.PowerupManager;
import ProjectW.MapData;
import ProjectW.MapPoints.PowerupSpawn;
import ProjectW.MapPoints.TeamSpawn;
import ProjectW.MapPoints.WoolPoint;
import ProjectW.Powerup;
import ProjectW.ProjectW;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class MapUtil {

    public static Location[] getMapCorners(World map){ //Gets the two opposite corners of a map. Usually to scan for datapoints inbetween them.
        File datapoints = new File("./" + map.getName() + "/datapoints.txt");
        StringBuilder rawData = new StringBuilder();
        try {
            Scanner reader = new Scanner(datapoints);
            while (reader.hasNextLine()){
                String data = reader.nextLine();
                rawData.append(data);
                rawData.append("\n");
                Bukkit.broadcastMessage(data);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String locationString = rawData.toString();
        String[] locs = locationString.split(", ");
        Location corner1 = new Location(map, Float.parseFloat(locs[0]), Float.parseFloat(locs[1]), Float.parseFloat(locs[2]));
        Location corner2 = new Location(map, Float.parseFloat(locs[3]), Float.parseFloat(locs[4]), Float.parseFloat(locs[5]));
        return new Location[]{
                corner1,
                corner2,
        };
    }

    public static Map<String, Object> getMapDatapoints(World map){
        Map<String, Object> data = null;
        try {
            InputStream inputStream = new FileInputStream(new File("./" + map.getName() + "/datapoints.yml"));
            Yaml yaml = new Yaml();
            data = yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static MapData createMapData(World map, Map<String, Object> ymlData) {
        Bukkit.broadcastMessage("creating map data");
        String mapName = (String) ymlData.get("name");
        ArrayList<Location> spawnLocations = new ArrayList<Location>();
        WoolPoint woolPoint = null;
        String[] locs1 = ((String) ymlData.get("corner1")).split(", ");
        String[] locs2 = ((String) ymlData.get("corner2")).split(", ");
        Location corner1 = new Location(map, Float.parseFloat(locs1[0]), Float.parseFloat(locs1[1]), Float.parseFloat(locs1[2]));
        Location corner2 = new Location(map, Float.parseFloat(locs2[0]), Float.parseFloat(locs2[1]), Float.parseFloat(locs2[2]));
        Cuboid mapCuboid = new Cuboid(corner1, corner2);
        for (Block block : mapCuboid) {
            if (block.getType() != Material.DIAMOND_BLOCK && block.getType() != Material.EMERALD_BLOCK){
                continue;
            }
            Bukkit.broadcastMessage("datapoint");
            if (block.getType() == Material.DIAMOND_BLOCK && block.getRelative(BlockFace.UP).getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE){
                Bukkit.broadcastMessage("SPAWN POINT");
                spawnLocations.add(Utils.getMiddleOfBlock(block));
                block.getRelative(BlockFace.UP).setType(Material.AIR);
                block.setType(Material.AIR);
            }
            if (block.getType() == Material.EMERALD_BLOCK && block.getRelative(BlockFace.UP).getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE){
                Bukkit.broadcastMessage("CAPTURE POINT");
                Location woolLocation = Utils.getMiddleOfBlock(block.getLocation().subtract(0,1,0).getBlock());
                woolPoint = new WoolPoint(woolLocation);
                block.getRelative(BlockFace.UP).setType(Material.AIR);
                block.setType(Material.AIR);
            }
        }
        return new MapData(mapName, woolPoint, spawnLocations);
    }

    public static Powerup getPowerupFromYml(String powData){
        for (Powerup powerup : PowerupManager.getInstance().getAllPowerups()){
            if (powData.equalsIgnoreCase(powerup.getYmlName())){
                return powerup;
            }
        }
        return null;
    }

    public static String initializeMap(String mapName){
        String newMapName = "CLONED-" + mapName;
        if (ProjectW.getMultiverseCore().getMVWorldManager().getMVWorld(newMapName) != null){
            ProjectW.getMultiverseCore().getMVWorldManager().deleteWorld(newMapName);
        }
        ProjectW.getMultiverseCore().getMVWorldManager().cloneWorld(mapName, newMapName);
        World world = Bukkit.getWorld(newMapName);
        MapData mapData = createMapData(world, getMapDatapoints(world));
        GameManager.getInstance().setMapData(mapData);
        return newMapName;
    }

    public static String getRandomMapName(){
        InputStream inputStream = ProjectW.getInstance().getResource("maps.yml");
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        ArrayList<String> mapList = new ArrayList<String>();
        for (int i = 1; ; i++){
            String mapName = (String) data.get("map" + i);
            if (mapName == null){
                break;
            } else {
                mapList.add(mapName);
                ConsoleMessageUtil.LogDebugMessage("map added");
            }
        }
        return mapList.get(Utils.getRandomInteger(mapList.size() -1, 0));
    }

    public static void deleteMap(World world){
        ProjectW.getMultiverseCore().getMVWorldManager().deleteWorld("CLONED-" + GameManager.getInstance().getMapData().getName());
    }
}
