package ProjectW;

import ProjectW.Events.TeamScoreEvent;
import ProjectW.GameManagers.GameManager;
import ProjectW.Utils.TeamColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Team {

    private ArrayList<Player> members;
    private TeamColor teamColor;

    protected Plugin plugin;

    public Team(ArrayList<Player> members, TeamColor teamColor){
        this.plugin = ProjectW.getInstance();
        this.members = members;
        this.teamColor = teamColor;
    }

    public void addMember(Player player){
        if (members.contains(player)){
            return;
        }
        members.add(player);
    }

    public void removeMember(Player player){
        if (!members.contains(player)){
            return;
        }
        members.remove(player);
    }

    public void score(){
        GameManager.getInstance().getTeamScoreHashMap().replace(this, (getScore() + 1));
        TeamScoreEvent event = new TeamScoreEvent(this);
    }

    public int getScore(){
        return GameManager.getInstance().getTeamScoreHashMap().get(this);
    }

    public ArrayList<Player> getMembers(){return members;}

    public TeamColor getTeamColor(){return teamColor;}
}
