package me.spigot.hellgast23.autoparkourlite.domein;

import me.spigot.hellgast23.autoparkourlite.MainPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created on: 19/10/2015 at 14:42
 */
public class ParkourUpdater implements Runnable {

    private BukkitTask task;
    private Map<UUID, Parkour> parkours;
    private long updateTimeTicks;

    public ParkourUpdater(Map<UUID, Parkour> parkours, long updateTimeTicks){
        this.parkours = parkours;
        this.updateTimeTicks = updateTimeTicks;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        Iterator<Map.Entry<UUID,Parkour>> it = parkours.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<UUID,Parkour> entry = it.next();
            if (entry.getValue().isDestroyed()){
                it.remove();
            }else{
                entry.getValue().generateNextBlock(time);
            }
        }
    }

    public void start(){
        stop();
        this.task = MainPlugin.runTaskTime(this,updateTimeTicks, updateTimeTicks);
    }

    public void stop(){
        if (task != null){
            MainPlugin.cancelTask(task);
            task = null;
        }
    }

}
