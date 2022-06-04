package ProjectW.GameManagers;

import ProjectW.Events.TeamScoreEvent;
import ProjectW.Events.WoolBlockCapturedEvent;
import ProjectW.MapData;
import ProjectW.MapPoints.PowerupSpawn;
import ProjectW.MapPoints.WoolPoint;
import ProjectW.Team;
import ProjectW.Utils.*;
import ProjectW.ProjectW;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GameManager extends BukkitRunnable implements Listener {

    private boolean roundGoing;
    private HashMap<Player, Team> playerTeamHashMap;
    private ArrayList<Team> teams;
    private ArrayList<Player> players;
    private MapData mapData;
    private GameStatus gameStatus;
    private World originalMap;
    private boolean isPointDown;
    private boolean isPointDropping;
    private World clonedMap;
    private Team redTeam;
    private Team blueTeam;
    private ArrayList<Player> capturing;
    private int timer = 20 * 30;

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
        players = new ArrayList<Player>();
        capturing = new ArrayList<Player>();
        teams = new ArrayList<Team>();
        isPointDown = false;
        isPointDropping = false;
        this.plugin = plugin;
        isRunning = true;
        gameStatus = GameStatus.WAITING_FOR_PLAYERS;
        this.runTaskTimer(plugin,1,1);
    }

    @Override
    public void run(){
        switch (gameStatus){
            case WAITING_FOR_PLAYERS:
                if (Objects.requireNonNull(Bukkit.getWorld("hub")).getPlayers().size() >= 4 || forceStart){
                    prepareMatch();
                    gameStatus = GameStatus.PREPARING_MATCH;
                    Bukkit.broadcastMessage("preparing match");
                    break;
                }
                break;
            case PREPARING_MATCH:
                break;
            case SPAWNING:
                break;
            case IN_ROUND:
                timer -= 1;
                if (timer <= 0){
                    if (!isPointDropping) {
                        dropPoint();
                    }
                } else {
                    Bukkit.broadcastMessage("" + timer);
                }
                if (isPointDown) {
                    Location checkLocation = Utils.getMiddleOfBlock(mapData.getWoolPoint().getLocation().getBlock()).clone().add(0, 20, 0);
                    for (Entity entity : clonedMap.getNearbyEntities(checkLocation, 5.5, 20, 5.5)) {
                        if (entity instanceof Player) {
                            Block belowPlayer = Utils.getClosestBlockBelowPlayer((Player) entity, 40);
                            for (Location woolLocations : mapData.getWoolPoint().getLocations()) {
                                if (belowPlayer.getLocation() == woolLocations || belowPlayer.getLocation().equals(woolLocations)) {
                                    if (!capturing.contains(entity)) {
                                        capturing.add((Player) entity);
                                        break;
                                    }
                                }
                                capturing.remove(entity);
                            }
                        }
                    }
                }
        }
    }

    public void dropPoint(){
        isPointDropping = true;
        Location startLoc = mapData.getWoolPoint().getLocation().clone().add(0, 20, 0);
        startLoc.getBlock().setType(Material.WHITE_WOOL);
        new BukkitRunnable(){
            @Override
            public void run(){
                Firework fw;
                fw = (Firework) startLoc.getWorld().spawnEntity(startLoc, EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.setPower(2);
                fwm.addEffect(FireworkEffect.builder().withColor(Color.GREEN).flicker(true).build());
                fw.setFireworkMeta(fwm);
                fw.detonate();
                startLoc.getBlock().setType(Material.AIR);
                startLoc.subtract(0,1,0);
                if (startLoc.getBlock().getType() != Material.AIR){
                    for (Location location : mapData.getWoolPoint().getLocations()){
                        location.getBlock().setType(Material.WHITE_WOOL);
                    }
                    pointCaptureDetection();
                    cancel();
                    return;
                }
                startLoc.getBlock().setType(Material.WHITE_WOOL);
            }
        }.runTaskTimer(plugin, 1, 15);
    }

    public void pointCaptureDetection(){
        isPointDown = true;
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!isPointDown){
                    cancel();
                }
                int redCaptureForce = 0;
                int blueCaptureForce = 0;
                for (Player player : capturing){
                    if (playerTeamHashMap.get(player) == redTeam){
                        redCaptureForce += 1;
                    } else {
                        blueCaptureForce += 1;
                    }
                }
                if (redCaptureForce - blueCaptureForce == 0){
                    if (capturing.size() > 0){
                        Bukkit.broadcastMessage("Point is contested");
                        return;
                    }
                    Bukkit.broadcastMessage("No one is capturing");
                    mapData.getWoolPoint().setBack(0);
                    return;
                }
                if (redCaptureForce - blueCaptureForce < 0){
                    Bukkit.broadcastMessage("Blue is capturing");
                    mapData.getWoolPoint().capture(TeamColor.BLUE, blueCaptureForce);
                    if (mapData.getWoolPoint().isFullyBlue()){
                        endMatch(blueTeam);
                        cancel();
                    }
                } else {
                    mapData.getWoolPoint().capture(TeamColor.RED, redCaptureForce);
                    Bukkit.broadcastMessage("Red is capturing");
                    if (mapData.getWoolPoint().isFullyRed()){
                        endMatch(redTeam);
                        cancel();
                    }
                }
            }
        }.runTaskTimer(plugin,1,20);
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
                    assignTeams(Bukkit.getWorld("hub"));
                    ArrayList<Location> startLocations = (ArrayList<Location>) mapData.getSpawnLocations().clone();
                    for (Team team : teams){
                        int rng = Utils.getRandomInteger(startLocations.size() - 1,0);
                        Location spawnLocation = startLocations.get(rng);
                        startLocations.remove(rng);
                        for (Player player : team.getMembers()){
                            player.teleport(spawnLocation);
                        }
                    }
                    Bukkit.broadcastMessage("teleported players");
                    gameStatus = GameStatus.IN_ROUND;
                }
            }
        }.runTaskLater(plugin, 150);
    }

    public void endMatch(Team winningTeam){
        isPointDown = false;
        for (Player player : players){
            player.sendTitle(winningTeam.getTeamColor().getName() + " wins!","", 8, 40, 8);
            player.setGameMode(GameMode.SPECTATOR);
            new BukkitRunnable(){
                @Override
                public void run(){
                    player.teleport(Utils.getMiddleOfBlock(ProjectW.getHubSpawnLocation().getBlock()).add(0,0.5,0));
                }
            }.runTaskLater(plugin, 60);
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                MapUtil.deleteMap(clonedMap);
                gameStatus = GameStatus.WAITING_FOR_PLAYERS;
                teams.clear();
                playerTeamHashMap.clear();
                mapData = null;
                isPointDropping = false;
                forceStart = false;
                players.clear();
                players.addAll(ProjectW.getHubWorld().getPlayers());
                for (Player player : players){
                    Bukkit.broadcastMessage(player.getName());
                }
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
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (getInstance().gameStatus == GameStatus.WAITING_FOR_PLAYERS){
            players.add(player);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        players.remove(player);
        getPlayerTeamHashMap().remove(player);
    }

    public static GameManager getInstance(){return ourInstance;}

    public boolean isRoundGoing(){return roundGoing;}

    public HashMap<Player, Team> getPlayerTeamHashMap(){return playerTeamHashMap;}

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
