package io.github.deechtezeeuw.kdframework.Commands;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KingdomCommand implements CommandExecutor {

    // Global plugin variable
    private KDFramework plugin;

    public KingdomCommand (KDFramework plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("k") || label.equalsIgnoreCase("kingdom")) {
            // If arguments are null
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aVoor hulp gebruik &2&l/"+label+" help&a!"));
                return true;
            }

            // Sub commands

            // Reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("k.reload")) {
                    plugin.Config.reloadVariables(sender);
                } else {
                    plugin.Config.noPermission(sender);
                }
            }
        }

        return true;
    }
}
