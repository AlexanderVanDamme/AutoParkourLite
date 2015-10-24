package me.spigot.hellgast23.autoparkourlite.ui;

import me.spigot.hellgast23.autoparkourlite.domein.DomeinController;
import me.spigot.hellgast23.autoparkourlite.exception.InvalidActionException;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created on: 19/10/2015 at 14:52
 */
public class ParkourCmdExecutor implements CommandExecutor{

    private DomeinController domContr;

    public ParkourCmdExecutor(DomeinController domContr) {
        this.domContr = domContr;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to do this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("autoparkourlite.start")){
            player.sendMessage(ChatColor.RED + "You do not have permission to do this.");
            return true;
        }
        try {
            domContr.startParkour(player);
            player.sendMessage(ChatColor.GREEN + "[AutoParkourLite] " + ChatColor.GRAY + "Your AutoParkourLite parkour has started.");
        } catch (InvalidActionException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

}
