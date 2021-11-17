package io.github.deechtezeeuw.kdframework.Region;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class deleteRegion {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public deleteRegion(KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        // Check if there are 3 arguments
        if (args.length != 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" region delete <kingdom>"));
            return;
        }

        // Check if its done by player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe kan alleen in-game een regio verwijderen!"));
            return;
        }
        Player player = (Player) sender;

        // Check if player exists in db
        if (!plugin.SQLSelect.player_exists(player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cIn verband met de veiligheid moet u in de database bestaan!"));
            return;
        }
        Speler speler = plugin.SQLSelect.player_get_by_uuid(player.getUniqueId());

        // Check if args[2] is an kingdom
        if (!plugin.SQLSelect.land_exists(args[2])) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cbestaat niet!"));
            return;
        }
        Land land = plugin.SQLSelect.land_get_by_name(args[2]);

        // Check if region already exists
        if (!plugin.worldGuardPlugin.getRegionManager(player.getWorld()).hasRegion(land.getName())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cheeft geen region!"));
            return;
        }

        plugin.worldGuardPlugin.getRegionManager(player.getWorld()).removeRegion(land.getName());

        // Commands need to be done by console
        List<String> cmds = new ArrayList<>();
        cmds.add("dmarker delete id:kd-"+land.getName().toLowerCase());
        cmds.add("plugman reload dynmap-worldguard");

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        for (String cmd : cmds) {
            Bukkit.dispatchCommand(console, cmd);
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + "&aLand regio succesvol verwijderd!"));
        return;
    }
}
