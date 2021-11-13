package io.github.deechtezeeuw.kdframework.Relaties;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RelateNeutral {
    private CommandSender sender;
    private String label;
    private String[] args;

    public RelateNeutral(CommandSender sender, String label, String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = args;
        this.checkArgs();
    }

    private void checkArgs() {
        KDFramework plugin = KDFramework.getInstance();
        // Check if sender is player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe kan alleen in-game neutraal zetten."));
            return;
        }
        Player player = (Player) sender;

        // Check if player is in database
        if (!plugin.SQLSelect.player_exists(player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cIn verband met de veiligheid mag je alleen een neutraal aanmaken als je in de database bestaat!"));
            return;
        }
        Speler speler = plugin.SQLSelect.player_get_by_uuid(player.getUniqueId());

        // Check if player is in a land
        if (speler.getLand() == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe moet in een land zitten om deze actie uit te kunnen voeren!"));
            return;
        }

        // Check args
        if (args.length == 2 && sender.hasPermission("k.neutraal")) {
            // Check if other land exists
            if (!plugin.SQLSelect.land_exists(args[1])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[1]+" &cbestaat niet!"));
                return;
            }
            Land land = plugin.SQLSelect.land_get_by_player(speler);
            Land other = plugin.SQLSelect.land_get_by_name(args[1]);

            // Check if already neutraal
            if (land.get_relation_number(other) == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+land.getName()+" &cen &4&l"+other.getName()+" &czijn al neutraal!"));
                return;
            }

            // Ask neutral

            return;
        }
        if (args.length == 3 && sender.hasPermission("k.neutraal.other")) {
            Land land = plugin.SQLSelect.land_get_by_name(args[1]);
            Land other = plugin.SQLSelect.land_get_by_name(args[2]);
            sender.sendMessage("admin");
            return;
        }

        if (sender.hasPermission("k.neutraal.other")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" ally <kingdom> <kingdom>&c!"));
            return;
        }
        if (sender.hasPermission("k.ally")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" ally <kingdom>&c!"));
            return;
        }
    }
}
