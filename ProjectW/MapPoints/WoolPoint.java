package ProjectW.MapPoints;

import ProjectW.ProjectW;
import ProjectW.Utils.TeamColor;
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
    protected ArrayList<Location> capturingLocations;
    protected ArrayList<Location> capturedLocations;

    protected Plugin plugin;

    public WoolPoint(Location location){
        this.plugin = ProjectW.getInstance();
        this.location = location;
        blocks = getWoolPointBlocks(location.getBlock());
        locations = new ArrayList<Location>();
        capturedLocations = new ArrayList<Location>();
        for (Block block : blocks){
            locations.add(block.getLocation());
        }
        capturingLocations = (ArrayList<Location>) locations.clone();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public ArrayList<Block> getWoolPointBlocks(Block centerBlock){
        Location centerLocation = Utils.getMiddleOfBlock(centerBlock);
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(centerLocation.clone().add(0,0,5).getBlock());
        blocks.add(centerLocation.clone().add(0,0,4).getBlock());
        blocks.add(centerLocation.clone().add(0,0,3).getBlock());
        blocks.add(centerLocation.clone().add(0,0,2).getBlock());
        blocks.add(centerLocation.clone().add(0,0,1).getBlock());
        blocks.add(centerBlock);
        blocks.add(centerLocation.clone().add(0,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(0,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(0,0,-3).getBlock());
        blocks.add(centerLocation.clone().add(0,0,-4).getBlock());
        blocks.add(centerLocation.clone().add(0,0,-5).getBlock());

        blocks.add(centerLocation.clone().add(1,0,5).getBlock());
        blocks.add(centerLocation.clone().add(1,0,4).getBlock());
        blocks.add(centerLocation.clone().add(1,0,3).getBlock());
        blocks.add(centerLocation.clone().add(1,0,2).getBlock());
        blocks.add(centerLocation.clone().add(1,0,1).getBlock());
        blocks.add(centerLocation.clone().add(1,0,0).getBlock());
        blocks.add(centerLocation.clone().add(1,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(1,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(1,0,-3).getBlock());
        blocks.add(centerLocation.clone().add(1,0,-4).getBlock());
        blocks.add(centerLocation.clone().add(1,0,-5).getBlock());

        blocks.add(centerLocation.clone().add(2,0,4).getBlock());
        blocks.add(centerLocation.clone().add(2,0,3).getBlock());
        blocks.add(centerLocation.clone().add(2,0,2).getBlock());
        blocks.add(centerLocation.clone().add(2,0,1).getBlock());
        blocks.add(centerLocation.clone().add(2,0,0).getBlock());
        blocks.add(centerLocation.clone().add(2,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(2,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(2,0,-3).getBlock());
        blocks.add(centerLocation.clone().add(2,0,-4).getBlock());

        blocks.add(centerLocation.clone().add(3,0,4).getBlock());
        blocks.add(centerLocation.clone().add(3,0,3).getBlock());
        blocks.add(centerLocation.clone().add(3,0,2).getBlock());
        blocks.add(centerLocation.clone().add(3,0,1).getBlock());
        blocks.add(centerLocation.clone().add(3,0,0).getBlock());
        blocks.add(centerLocation.clone().add(3,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(3,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(3,0,-3).getBlock());
        blocks.add(centerLocation.clone().add(3,0,-4).getBlock());

        blocks.add(centerLocation.clone().add(4,0,3).getBlock());
        blocks.add(centerLocation.clone().add(4,0,2).getBlock());
        blocks.add(centerLocation.clone().add(4,0,1).getBlock());
        blocks.add(centerLocation.clone().add(4,0,0).getBlock());
        blocks.add(centerLocation.clone().add(4,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(4,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(4,0,-3).getBlock());

        blocks.add(centerLocation.clone().add(5,0,1).getBlock());
        blocks.add(centerLocation.clone().add(5,0,0).getBlock());
        blocks.add(centerLocation.clone().add(5,0,-1).getBlock());
        //HALFWAY
        blocks.add(centerLocation.clone().add(-1,0,5).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,4).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,3).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,2).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,1).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,0).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,-3).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,-4).getBlock());
        blocks.add(centerLocation.clone().add(-1,0,-5).getBlock());

        blocks.add(centerLocation.clone().add(-2,0,4).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,3).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,2).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,1).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,0).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,-3).getBlock());
        blocks.add(centerLocation.clone().add(-2,0,-4).getBlock());

        blocks.add(centerLocation.clone().add(-3,0,4).getBlock());
        blocks.add(centerLocation.clone().add(-3,0,3).getBlock());
        blocks.add(centerLocation.clone().add(-3,0,2).getBlock());
        blocks.add(centerLocation.clone().add(-3,0,1).getBlock());
        blocks.add(centerLocation.clone().add(-3,0,0).getBlock());
        blocks.add(centerLocation.clone().add(-3,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(-3,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(-3,0,-3).getBlock());
        blocks.add(centerLocation.clone().add(-3,0,-4).getBlock());

        blocks.add(centerLocation.clone().add(-4,0,3).getBlock());
        blocks.add(centerLocation.clone().add(-4,0,2).getBlock());
        blocks.add(centerLocation.clone().add(-4,0,1).getBlock());
        blocks.add(centerLocation.clone().add(-4,0,0).getBlock());
        blocks.add(centerLocation.clone().add(-4,0,-1).getBlock());
        blocks.add(centerLocation.clone().add(-4,0,-2).getBlock());
        blocks.add(centerLocation.clone().add(-4,0,-3).getBlock());

        blocks.add(centerLocation.clone().add(-5,0,1).getBlock());
        blocks.add(centerLocation.clone().add(-5,0,0).getBlock());
        blocks.add(centerLocation.clone().add(-5,0,-1).getBlock());
        return blocks;
    }

    public void capture(TeamColor color, int captureForce){
        for (int i = 1 ; i <= captureForce ; i++){
            if (capturedLocations.size() > 0){
                if (capturedLocations.get(0).getBlock().getType() != color.getBlockMaterial()){
                    setBack(captureForce);
                    return;
                }
            }
            for (int a = 0 ; a <= 5 ; a++){
                if (capturingLocations.size() <= 0) {
                    return;
                }
                int rng = Utils.getRandomInteger(capturingLocations.size() - 1, 0);
                Location location = capturingLocations.get(rng);
                capturingLocations.remove(rng);
                capturedLocations.add(location);
                location.getBlock().setType(color.getBlockMaterial());
            }
        }
    }

    public void setBack(int captureForce) {
        for (int i = 0 ; i <= captureForce; i++){
            for (int a = 0; a <= 5; a++) {
                if (capturedLocations.size() <= 0) {
                    return;
                }
                int rng = Utils.getRandomInteger(capturedLocations.size() - 1, 0);
                Location location = capturedLocations.get(rng);
                capturedLocations.remove(rng);
                capturingLocations.add(location);
                location.getBlock().setType(Material.WHITE_WOOL);
            }
        }
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
