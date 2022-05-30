package ProjectW.MapPoints;

import ProjectW.Utils.TeamColor;
import org.bukkit.Location;

public class TeamSpawn {

    private Location location;
    private TeamColor teamColor;

    public TeamSpawn(Location location, TeamColor teamColor){
        this.location = location;
        this.teamColor = teamColor;
    }

    public Location getLocation(){return location;}

    public TeamColor getTeamColor(){return teamColor;}
}
