package ProjectW.GameManagers;

import ProjectW.Events.TeamScoreEvent;
import ProjectW.Events.WoolBlockCapturedEvent;
import ProjectW.MapData;
import ProjectW.MapPoints.PowerupSpawn;
import ProjectW.Team;
import ProjectW.Utils.*;
import ProjectW.ProjectW;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GameManager extends BukkitRunnable implements Listener {

    private boolean roundGoing;
    private HashMap<Player, Team> playerTeamHashMap;
    private HashMap<Team, Integer> teamScoreHashMap;
    private ArrayList<Team> teams;
    private ArrayList<Player> players;
    private MapData mapData;
    private GameStatus gameStatus;
    private World originalMap;
    private World clonedMap;
    private Team redTeam;
    private Team blueTeam;

    private boolean forceStart = false;

    public static GameManager ourInstance = new GameManager();
    private boolean isRunning = false;
    private Plugin plugin;

    public void start(Plugin plugin) throws ManagerAlreadyRunningException {
        if (isRunning){
            throw new ManagerAlreadyRunningException();
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        playerTeamHashMap = new HashMap<Player, Team>();
        teamScoreHashMap = new HashMap<Team, Integer>();
        players = new ArrayList<Player>();
        teams = new ArrayList<Team>();
        this.plugin = plugin;
        this.runTaskTimer(plugin,1,1);
        isRunning = true;
        gameStatus = GameStatus.WAITING_FOR_PLAYERS;
    }

    @Override
    public void run(){
        switch (gameStatus){
            case WAITING_FOR_PLAYERS:
                if (Objects.requireNonNull(Bukkit.getWorld("hub")).getPlayers().size() >= 8 || forceStart){
                    gameStatus = GameStatus.PREPARING_MATCH;
                    prepareMatch();
                    Bukkit.broadcastMessage("preparing match");
                    break;
                }
                break;
            case PREPARING_MATCH:

            case SPAWNING:

            case IN_ROUND:
                Location checkLocation = mapData.getWoolPoint().getLocation().clone().add(0,5,0);
                for (Entity entity : clonedMap.getNearbyEntities(checkLocation,2.5,5,2.5)){
                    if (entity instanceof Player){
                        Bukkit.broadcastMessage("capturing");
                    }
            }

        }
    }

    public void prepareMatch(){
        String mapName = MapUtil.getRandomMapName();
        ServerMessageUtil.sendServerMessageToWorld("Picking random map...", Objects.requireNonNull(Bukkit.getWorld("hub")), ServerMessageType.SERVER);
        new BukkitRunnable(){
            @Override
            public void run(){
                String displayMapName = mapName;
                displayMapName = displayMapName.replace('-', ' ');
                ServerMessageUtil.sendServerMessageToWorld("Random map §l§a" + displayMapName + "§r was chosen!", Objects.requireNonNull(Bukkit.getWorld("hub")), ServerMessageType.SERVER);
                String newMapName = MapUtil.initializeMap(mapName);
                originalMap = Objects.requireNonNull(Bukkit.getWorld(mapName));
                clonedMap = Objects.requireNonNull(Bukkit.getWorld(newMapName));
            }
        }.runTaskLater(plugin, 100);

        new BukkitRunnable(){
            @Override
            public void run(){
                if (Bukkit.getWorld(clonedMap.getName()) != null){
                    for (PowerupSpawn powerupSpawn : mapData.getPowerupSpawns()){

                        powerupSpawn.startTimer();
                    }
                    assignTeams(Bukkit.getWorld("hub"));
                    for (Player player : Bukkit.getWorld("hub").getPlayers()){
                        if (getInstance().getPlayerTeamHashMap().get(player).getTeamColor() == TeamColor.RED){
                            player.teleport(mapData.getRedTeamSpawn().getLocation());
                        }
                        if (getInstance().getPlayerTeamHashMap().get(player).getTeamColor() == TeamColor.BLUE){
                            player.teleport(mapData.getBlueTeamSpawn().getLocation());
                        }
                    }
                    Bukkit.broadcastMessage("teleported players");
                    gameStatus = GameStatus.IN_ROUND;
                }
            }
        }.runTaskLater(plugin, 150);
    }

    public void startMatch(World world){
        if (world.getPlayers().size() % 2 != 0){
            return;
        }
        assignTeams(world);
    }

    public void endRound(Team scoringTeam) {
        for (Player player : players) {
            player.sendTitle(scoringTeam.getTeamColor().getName() + " scored!", "§c" + teamScoreHashMap.get(redTeam) + "§r - §9" + teamScoreHashMap.get(blueTeam), 8, 40, 8);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayerTeamHashMap().get(player).getTeamColor() == TeamColor.RED) {
                        player.teleport(mapData.getRedTeamSpawn().getLocation());
                    } else {
                        player.teleport(mapData.getBlueTeamSpawn().getLocation());
                    }
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }.runTaskLater(plugin, 60);
        }
    }

    public void endMatch(Team winningTeam){
        for (Player player : players){
            player.sendTitle(winningTeam.getTeamColor().getName() + " wins!", "§c" + teamScoreHashMap.get(redTeam) + "§r - §9" + teamScoreHashMap.get(blueTeam), 8, 40, 8);
            new BukkitRunnable(){
                @Override
                public void run(){
                    player.teleport(ProjectW.getHubSpawnLocation());
                    players.remove(player);
                }
            }.runTaskLater(plugin, 60);
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                MapUtil.deleteMap(clonedMap);
                gameStatus = GameStatus.WAITING_FOR_PLAYERS;
                teams.clear();
                teamScoreHashMap.clear();
                playerTeamHashMap.clear();
                mapData = null;
                forceStart = false;
            }
        }.runTaskLater(plugin, 60);

    }

    public void assignTeams(World world){
        ArrayList<Player> redTeamList = new ArrayList<Player>();
        ArrayList<Player> blueTeamList = new ArrayList<Player>();
        int i = 1;
        for (Player player : world.getPlayers()){
            if (i % 2 == 0){
                redTeamList.add(player);
            } else {
                blueTeamList.add(player);
            }
            i++;
        }
        Team redTeam = new Team(redTeamList, TeamColor.RED);
        Team blueTeam = new Team(blueTeamList, TeamColor.BLUE);
        GameManager.getInstance().initializeTeams(redTeam, blueTeam);
    }

    public void initializeTeams(Team redTeam, Team blueTeam){
        for (Player player : redTeam.getMembers()){
            playerTeamHashMap.put(player, redTeam);
            player.setDisplayName("§c" + player.getName());
            player.setCustomNameVisible(true);
        }
        for (Player player : blueTeam.getMembers()){
            playerTeamHashMap.put(player, blueTeam);
            player.setDisplayName("§9" + player.getName());
            player.setCustomNameVisible(true);
        }
        teams.add(redTeam);
        teams.add(blueTeam);
        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
        teamScoreHashMap.put(redTeam, 0);
        teamScoreHashMap.put(blueTeam, 0);
    }

    @EventHandler
    public void onTeamScore(TeamScoreEvent e){
        e.getPlayerTeam().score();
        mapData.getWoolPoint().reset();
        for (Player player : players){
            player.setGameMode(GameMode.SPECTATOR);
            if (teamScoreHashMap.get(e.getPlayerTeam()) >= 3){
                endMatch(e.getPlayerTeam());
            } else {
                endRound(e.getPlayerTeam());
            }
        }
    }

    @EventHandler
    public void onWoolBlockCapture(WoolBlockCapturedEvent e){
        Location location = e.getBlock().getLocation();
        e.getBlock().setType(e.getPlayerTeam().getTeamColor().getBlockMaterial());
        if (e.getPlayerTeam().getTeamColor().getBlockMaterial() == Material.RED_WOOL){
            if (mapData.getWoolPoint().isFullyRed()){
                TeamScoreEvent event = new TeamScoreEvent(redTeam);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        } else {
            if (mapData.getWoolPoint().isFullyBlue()){
                TeamScoreEvent event = new TeamScoreEvent(blueTeam);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        Block blockBelow = player.getLocation().clone().subtract(0, 0.25,0).getBlock();
        switch (blockBelow.getType()){
            case SLIME_BLOCK:
                if (mapData.getHorizontalBoost() == 0){
                    player.setVelocity(player.getVelocity().setY(mapData.getVerticalBoost()));
                } else {
                    player.setVelocity(player.getLocation().getDirection().multiply(mapData.getHorizontalBoost()).setY(mapData.getVerticalBoost()));
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (getInstance().gameStatus == GameStatus.WAITING_FOR_PLAYERS){
            players.add(player);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        players.add(player);
        getPlayerTeamHashMap().remove(player);
    }

    public static GameManager getInstance(){return ourInstance;}

    public boolean isRoundGoing(){return roundGoing;}

    public HashMap<Player, Team> getPlayerTeamHashMap(){return playerTeamHashMap;}

    public HashMap<Team, Integer> getTeamScoreHashMap(){return teamScoreHashMap;}

    public ArrayList<Team> getTeams(){return teams;}

    public ArrayList<Player> getPlayers(){return players;}

    public MapData getMapData(){return mapData;}

    public GameStatus getGameStatus(){return gameStatus;}

    public World getClonedMap(){return clonedMap;}

    public void setForceStart(boolean setForceStart){this.forceStart = setForceStart;}

    public void setMapData(MapData setMapData){mapData = setMapData;}

    public void setOriginalMap(World originalMap){this.originalMap = originalMap;}

    public void setClonedMap(World clonedMap){this.clonedMap = clonedMap;}
}
