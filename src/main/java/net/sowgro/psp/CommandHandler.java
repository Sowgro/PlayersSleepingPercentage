package net.sowgro.psp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

import static net.sowgro.psp.PlayersSleepingPercentage.plugin;

/**
 * Parses and handles commands
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (!sender.hasPermission("playersSleepingPercentage")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
            return true;
        }

        if (args.length < 1 || args[0] == null || args[0].isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Invalid argument!");
            return false;
        }

        int val;
        try {
            val = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Input was not a number!");
            return false;
        }

//        if (val < 0 || val > 100) {
//            sender.sendMessage(ChatColor.RED + "Input must be between 0 and 100 inclusive!");
//            return false;
//        }

        plugin.getConfig().set("PlayersSleepingPercentage", val);
        sender.sendMessage(ChatColor.GREEN + "Set to " + val);
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
