package ProjectW.MapPoints;

import ProjectW.ProjectW;
import ProjectW.Utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class WoolPoint implements Listener {

    protected Location location;
    protected ArrayList<Block> blocks;
    protected ArrayList<Location> locations;

    protected Plugin plugin;

    public WoolPoint(Location location){
        this.plugin = ProjectW.getInstance();
        this.location = location;
        blocks = getWoolPointBlocks(location.getBlock());
        locations = new ArrayList<Location>();
        for (Block block : blocks){
            locations.add(block.getLocation());
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public ArrayList<Block> getWoolPointBlocks(Block centerBlock){
        Location centerLocation = Utils.getMiddleOfBlock(centerBlock);
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(centerLocation.clone().add(2,0,2).getBlock());
        blocks.add(centerLocation.clone().add(1,0,2).getBlock());
        blocks.add(centerLocation.clone().add(0,0,2).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,2).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,2).getBlock());
        blocks.add(centerLocation.clone().add(2,0,1).getBlock());
        blocks.add(centerLocation.clone().add(1,0,1).getBlock());
        blocks.add(centerLocation.clone().add(0,0,1).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,1).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,1).getBlock());
        blocks.add(centerBlock);
        blocks.add(centerLocation.clone().add(0,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(0,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(1,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(2,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(1,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(2,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,0).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,0).getBlock());
        blocks.add(centerLocation.clone().add(1,0,0).getBlock());
        blocks.add(centerLocation.clone().add(2,0,0).getBlock());

        return blocks;
    }

    public boolean isFullyRed(){
        for (Location location : locations){
            if (location.getBlock().getType() != Material.RED_WOOL){
                return false;
            }
        }
        return true;
    }

    public boolean isFullyBlue(){
        for (Location location : locations){
            if (location.getBlock().getType() != Material.BLUE_WOOL){
                return false;
            }
        }
        return true;
    }

    public void reset(){
        for (Location location : locations){
            location.getBlock().setType(Material.WHITE_WOOL);
        }
    }

    public void start(){

    }

    public ArrayList<Block> getBlocks(){return blocks;}

    public Location getLocation(){return location;}

    public ArrayList<Location> getLocations(){return locations;}
}
