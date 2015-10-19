package me.spigot.hellgast23.autoparkourlite.ui;

import me.spigot.hellgast23.autoparkourlite.domein.DomeinController;
import me.spigot.hellgast23.autoparkourlite.domein.Parkour;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created on: 19/10/2015 at 14:51
 */
public class ParkourListener implements Listener {

    private DomeinController domContr;

    public ParkourListener(DomeinController domContr){
        this.domContr = domContr;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        domContr.stopParkour(event.getPlayer(),"quit");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBreak(BlockBreakEvent event){
        if (event.isCancelled()) return;
        if (domContr.isParkouring(event.getPlayer()) || Parkour.isParkourBlock(event.getBlock())){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event){
        if ( event.isCancelled()) return;
        if (domContr.isParkouring(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}
