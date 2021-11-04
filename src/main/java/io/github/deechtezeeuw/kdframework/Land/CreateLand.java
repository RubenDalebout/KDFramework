package io.github.deechtezeeuw.kdframework.Land;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CreateLand {
    private KDFramework plugin;
    private Land land;

    public CreateLand(KDFramework plugin, CommandSender sender, String KDName) {
        this.plugin = plugin;
        land = new Land(KDName, "&7[&f"+KDName+"&7]", true, 25);

        create_land(sender);
    }

    // Global action
    private void create_land(CommandSender sender) {
        // Check if clan exists
        if (!plugin.SQLSelect.land_exists(land.getName())) {
            // Land does not exists
            plugin.SQLInsert.land_create(land);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aHet land &2&l"+land.getName()+" &ais aangemaakt!"));
            return;
        } else {
            // Land exists
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+land.getName()+" &cbestaat al!"));
            return;
        }
    }
}
