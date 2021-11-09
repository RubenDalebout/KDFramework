package io.github.deechtezeeuw.kdframework.Rank;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeleteRank {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public DeleteRank (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        this.checkArguments();
    }

    private void checkArguments() {
        if (!(args.length > 2)) {
            if (sender.hasPermission("k.rank.delete.others")) {
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
        if (sender.hasPermission("k.rank.delete.others") && args.length == 4) {
            // Check if arg 2 is an kingdom or an new rank
            if (!plugin.SQLSelect.land_exists(args[2])) {
                // Not an kingdom
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cWe kunnen de rank &4&l" + args[3] + " &cniet verwijderen, omdat &4&l" + args[2] + " &cgeen kingdom is!"));
                return;
            }

            // Land exists
            Land land = plugin.SQLSelect.land_get_by_name(args[2]);

            // Check if rank already exists
            Boolean found = false;
            Rank deleteRank = null;
            for (Rank rank : land.getRanks()) {
                if (rank.getName().equalsIgnoreCase(args[3])) {
                    found = true;
                    deleteRank = rank;
                }
            }

            if (!found) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+args[3]+" &cbestaat niet in het land &4&l"+land.getName()+"&c!"));
                return;
            }

            // Check if rank thats going to be deleted is the default rank
            if (land.get_defaultRank().equals(deleteRank)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+args[3]+" &ckan niet worden verwijderd, omdat het de default rank is van &4&l"+land.getName()+"&c!"));
                return;
            }

            // Delete rank in land
            plugin.SQLDelete.rank_delete(deleteRank);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+args[3]+" &ais verwijderd voor het land &2&l"+land.getName()+"&a!"));
            return;
        } else {
            // user can only add rank to hes own kingdom
            if (!sender.hasPermission("k.rank.delete")) {
                plugin.Config.noPermission(sender);
                return;
            }
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
                        plugin.Config.getGeneralPrefix() + "&cU kunt alleen een rol verwijderen als u lid bent van een land!"));
                return;
            }

            // Check if role already exists in land
            Land land = plugin.SQLSelect.land_get_by_player(speler);
            Boolean found = false;
            Rank deleteRank = null;
            for (Rank rank : land.getRanks()) {
                if (rank.getName().equalsIgnoreCase(args[2])) {
                    found = true;
                    deleteRank = rank;
                }
            }

            if (!found) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+args[2]+" &cbestaat niet in het land &4&l"+land.getName()+"&c!"));
                return;
            }

            // Check if rank thats going to be deleted is the default rank
            if (land.get_defaultRank().equals(deleteRank)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+args[2]+" &ckan niet worden verwijderd, omdat het de default rank is van &4&l"+land.getName()+"&c!"));
                return;
            }

            // Check if the rank thats going to be deleted is equal or higher then player level
            Rank spelerRank = plugin.SQLSelect.get_rank(speler.getRank());
            if (deleteRank.getLevel() >= spelerRank.getLevel()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+args[2]+" &ckan niet worden verwijderd, omdat het level gelijk of hoger is dan die van jouw rank!"));
                return;
            }

            // Delete rank in land
            plugin.SQLDelete.rank_delete(deleteRank);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+args[2]+" &ais verwijderd voor het land &2&l"+land.getName()+"&a!"));
            return;
        }
    }
}
