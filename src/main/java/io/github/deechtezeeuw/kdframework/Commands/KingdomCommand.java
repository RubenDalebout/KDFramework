package io.github.deechtezeeuw.kdframework.Commands;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.CreateLand;
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

            // Land
            if (args[0].equalsIgnoreCase("land")) {
                // Check if has more then 1 argument
                if (args.length > 1) {
                    // Create land
                    if (args[1].equalsIgnoreCase("create")) {
                        // Check if there is an kingdom name
                        if (args.length == 3) {
                            new CreateLand(sender, args[2]);
                            return true;
                        }
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&cFoutieve argumentatie: &4&l/"+label+ " "+args[0]+" "+args[1]+" <kd-naam>"));
                        return true;
                    }
                    // Edit land
                    if (args[1].equalsIgnoreCase("edit")) {
                        return true;
                    }
                    // Delete land
                    if (args[1].equalsIgnoreCase("delete")) {
                        return true;
                    }

                    // Send message that arguments can have [create/edit/delete]
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cFoutieve argumentatie: &4&l/"+label+" "+args[0]+" [create/edit/delete]"));
                    return true;
                } else {
                    // Send message that arguments can have [create/edit/delete]
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cFoutieve argumentatie: &4&l/"+label+" "+args[0]+" [create/edit/delete]"));
                    return true;
                }
            }

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
