package ProjectW.Events;

import ProjectW.Team;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class WoolBlockCapturedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player capturer;
    private Block block;
    private Team playerTeam;

    public WoolBlockCapturedEvent(Player capturer, Block block, Team playerTeam){
        this.capturer = capturer;
        this.block = block;
        this.playerTeam = playerTeam;
    }

    public Player getCapturer(){return capturer;}

    public Block getBlock(){return block;}

    public Team getPlayerTeam(){return playerTeam;}

    public HandlerList getHandlers(){return handlers;}

    public static HandlerList getHandlerList(){return handlers;}
}
