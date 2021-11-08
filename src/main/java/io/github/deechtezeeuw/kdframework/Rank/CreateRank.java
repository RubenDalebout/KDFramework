package io.github.deechtezeeuw.kdframework.Rank;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CreateRank {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public CreateRank (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        this.checkArguments();
    }

    private void checkArguments() {
        // args 0 = rank
        // args 1 = create

        // Check if there is an args 2
        if (!(args.length > 2)) {
            if (sender.hasPermission("k.rank.create.all")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " "+args[1]+" <rank/kingdom> <rank>"));
                return;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " "+args[1]+" <rank>"));
                return;
            }
        }

        // Check if user has permission to add ranks to all or only hes land
        if (false) {
            // Check if arg 2 is an kingdom or an new rank
            if (plugin.SQLSelect.land_exists(args[2])) {
                // Is an kingdom
                sender.sendMessage(args[2] + " is een kingdom");
                return;
            } else {
                // Not an kingdom
                sender.sendMessage(args[2] + " is geen kingdom");
                return;
            }
        } else {
            // user can only add rank to hes own kingdom
            // Check if user has all arguments good
            if (!(args.length == 3)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " "+args[1]+" "+args[2]+" &cen niet meer!"));
                return;
            }

            // Check if user is in an land
            Player CMDSender = (Player) sender;
            if (!plugin.SQLSelect.player_exists(CMDSender)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cIn verband met veiligheids redenen kunnen wij niet toestaan dat u niet bestaat in onze database!"));
                return;
            }

            Speler speler = plugin.SQLSelect.player_get_by_name(CMDSender.getName());

            if (speler == null || speler.getLand() == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cU kunt alleen een rol toevoegen als u lid bent van een land!"));
                return;
            }

            // Check if role already exists in land
            Land land = plugin.SQLSelect.land_get_by_player(speler);
            Boolean found = false;
            for (Rank rank : land.getRanks()) {
                if (rank.getName().equalsIgnoreCase(args[2])) {
                    found = true;
                }
            }

            if (found) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+args[2]+" &cbestaat al in het land &4&l"+land.getName()+"&c!"));
                return;
            }

            // Create rank in land
            Rank newRank = new Rank(UUID.randomUUID(), args[2], 1, null, "&7[&"+args[2]+"&7]", false);
            plugin.SQLInsert.rank_create(land.getUuid(), newRank);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+args[2]+" &ais ingemaakt voor het land &2&l"+land.getName()+"&a!"));
            return;
        }
    }
}
