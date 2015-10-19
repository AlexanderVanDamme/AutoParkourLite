package me.spigot.hellgast23.autoparkourlite.domein;

import me.spigot.hellgast23.autoparkourlite.exception.InvalidActionException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created on: 19/10/2015 at 14:38
 */
public class DomeinController {

    private JavaPlugin instance;
    private final Map<UUID, Parkour> playerParkours;
    private ParkourUpdater parkourUpdater;
    private PositionManager posMan;

    public DomeinController(JavaPlugin instance){
        this.instance = instance;
        this.playerParkours = new HashMap<>();
        this.parkourUpdater = new ParkourUpdater(playerParkours,1);
        this.posMan = new PositionManager();
        this.parkourUpdater.start();
    }

    public void startParkour(Player p) throws InvalidActionException {
        if (isInParkour(p))
            throw new InvalidActionException("You can not start a new parkour if you're already in a parkour.");
        Parkour parkour = new Parkour(p,posMan);
        playerParkours.put(p.getUniqueId(), parkour);
        parkour.start();
    }

    public boolean isInParkour(Player p){
        return playerParkours.containsKey(p.getUniqueId());
    }

    public void stopParkour(Player p, String reason){
        if (isInParkour(p)){
            Parkour parkour = playerParkours.get(p.getUniqueId());
            playerParkours.remove(p.getUniqueId());
            parkour.finish(reason);
        }
    }


    public boolean isParkouring(Player player) {
        return playerParkours.containsKey(player.getUniqueId()) && !playerParkours.get(player.getUniqueId()).isDestroyed();
    }
}
