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
        MapData mapData = new MapData();
        String mapName = (String) ymlData.get("name");
        mapData.setName(mapName);
        double horizontalBoost = (double) ymlData.get("horizontalBoost");
        mapData.setHorizontalBoost(horizontalBoost);
        double verticalBoost = (double) ymlData.get("verticalBoost");
        mapData.setVerticalBoost(verticalBoost);
        String[] locs1 = ((String) ymlData.get("corner1")).split(", ");
        String[] locs2 = ((String) ymlData.get("corner2")).split(", ");
        Location corner1 = new Location(map, Float.parseFloat(locs1[0]), Float.parseFloat(locs1[1]), Float.parseFloat(locs1[2]));
        Location corner2 = new Location(map, Float.parseFloat(locs2[0]), Float.parseFloat(locs2[1]), Float.parseFloat(locs2[2]));
        Cuboid mapCuboid = new Cuboid(corner1, corner2);
        for (Block block : mapCuboid) {
            if (!(block.getState() instanceof Sign) && block.getType() != Material.EMERALD_BLOCK){
                continue;
            }
            if (block.getState() instanceof Sign){
                Sign sign = (Sign) block.getState();
                for (String line : sign.getLines()){
                    if (line.equalsIgnoreCase("RED")){
                        mapData.setRedTeamSpawn(new TeamSpawn(block.getLocation().add(0.5,0,0.5), TeamColor.RED));
                        block.setType(Material.AIR);
                        break;
                    }
                    if (line.equalsIgnoreCase("BLUE")){
                        mapData.setBlueTeamSpawn(new TeamSpawn(block.getLocation().add(0.5,0,0.5), TeamColor.BLUE));
                        block.setType(Material.AIR);
                        break;
                    }
                }
            }
            if (block.getType() == Material.EMERALD_BLOCK && block.getRelative(BlockFace.UP).getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE){
                Location woolLocation = Utils.getMiddleOfBlock(block.getLocation().subtract(0,1,0).getBlock());
                mapData.setWoolPoint(new WoolPoint(woolLocation));
                block.setType(Material.AIR);
                block.getRelative(BlockFace.UP).setType(Material.AIR);
            }
            if (mapData.getWoolPoint() != null && mapData.getBlueTeamSpawn() != null && mapData.getRedTeamSpawn() != null){
                break;
            }
        }
        ArrayList<PowerupSpawn> powerupSpawns = new ArrayList<PowerupSpawn>();
        ArrayList<Powerup> powerups = new ArrayList<Powerup>();
        for (int i = 1; ; i++){
            String powData = (String) ymlData.get("pow" + i);
            if (powData == null){
                break;
            } else {
                String[] splitPowData = powData.split(", ");
                Location location = new Location(map, Float.parseFloat(splitPowData[0]), Float.parseFloat(splitPowData[1]), Float.parseFloat(splitPowData[2]));
                if (splitPowData.length >= 4) {
                    for (int c = 3; ; c++) {
                        if (splitPowData[c] != null) {
                            powerups.add(getPowerupFromYml(splitPowData[c]));
                        } else {
                            break;
                        }
                    }
                } else {
                    powerups.addAll(Arrays.asList(PowerupManager.getInstance().getAllPowerups()));
                }
                powerupSpawns.add(new PowerupSpawn(location, powerups));
            }
        }
        mapData.setPowerupSpawns(powerupSpawns);
        return mapData;
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
        ProjectW.getMultiverseCore().getMVWorldManager().cloneWorld(mapName, newMapName);
        World world = Bukkit.getWorld(newMapName);
        MapData mapData = createMapData(world, getMapDatapoints(world));
        for (Block block : mapData.getWoolPoint().getBlocks()){
            block.setType(Material.RED_WOOL);
        }
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
        //return mapList.get(Utils.getRandomInteger(mapList.size() -1, 0));
        return mapList.get(1);
    }

    public static void deleteMap(World world){
        ProjectW.getMultiverseCore().getMVWorldManager().deleteWorld("CLONED-" + GameManager.getInstance().getMapData().getName());
    }
}
