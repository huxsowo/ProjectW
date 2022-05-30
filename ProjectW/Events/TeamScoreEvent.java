package ProjectW.Events;

import ProjectW.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamScoreEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Team playerTeam;

    public TeamScoreEvent(Team playerTeam){
        this.playerTeam = playerTeam;
    }

    public Team getPlayerTeam(){return playerTeam;}

    public HandlerList getHandlers(){return handlers;}

    public static HandlerList getHandlerList(){return handlers;}
}
