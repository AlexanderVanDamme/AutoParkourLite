package me.spigot.hellgast23.autoparkourlite;

import me.spigot.hellgast23.autoparkourlite.domein.DomeinController;
import me.spigot.hellgast23.autoparkourlite.ui.ParkourCmdExecutor;
import me.spigot.hellgast23.autoparkourlite.ui.ParkourListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;

/**
 * Created on: 19/10/2015 at 14:35
 */
public class MainPlugin extends JavaPlugin {

    private static MainPlugin instance;
    private DomeinController domCont;

    @Override
    public void onEnable(){
        MainPlugin.instance = this;

        this.domCont = new DomeinController(this);

        getServer().getPluginManager().registerEvents(new ParkourListener(domCont),this);
        getCommand("autoparkourlite").setExecutor(new ParkourCmdExecutor(domCont));

        //Setup metrics
        try {
            Metrics metrics = new  Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

    }

    @Override
    public void onDisable(){
        getServer().getOnlinePlayers().forEach(p -> domCont.stopParkour(p,"AutoParkourLite is disabling."));
    }

    public static BukkitTask runTaskTime(Runnable run, long startDelayTicks, long delayBetweenTicks){
        return Bukkit.getScheduler().runTaskTimer(instance,run,startDelayTicks,delayBetweenTicks);
    }

    public static void cancelTask(BukkitTask task){
        Bukkit.getScheduler().cancelTask(task.getTaskId());
    }

}
