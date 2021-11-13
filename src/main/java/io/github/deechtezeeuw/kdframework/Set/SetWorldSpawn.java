package io.github.deechtezeeuw.kdframework.Set;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWorldSpawn {

    public SetWorldSpawn(KDFramework plugin, CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("k.set.worldspawn")) {
            plugin.Config.noPermission(sender);
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe kan alleen vanuit in-game de spawn aanpassen"));
            return;
        }

        Player player = (Player) sender;

        Location location = player.getLocation();
        plugin.getConfig().set("general.settings.world.spawn.x", location.getX());
        plugin.getConfig().set("general.settings.world.spawn.y", location.getY());
        plugin.getConfig().set("general.settings.world.spawn.z", location.getZ());
        plugin.getConfig().set("general.settings.world.spawn.yaw", location.getYaw());
        plugin.getConfig().set("general.settings.world.spawn.pitch", location.getPitch());

        plugin.saveConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + "&aSuccesvol de nieuwe &2&lWereld spawn &aopgeslagen!"));

        plugin.Config.reloadVariables(sender);
    }
}
