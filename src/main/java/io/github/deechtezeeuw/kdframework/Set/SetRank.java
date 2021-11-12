package io.github.deechtezeeuw.kdframework.Set;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRank {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public SetRank(KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        check_args();
    }

    private void check_args() {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" "+args[0]+" "+args[1]+" <speler> <value>"));
            return;
        }

        if (args.length == 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" "+args[0]+" "+args[1]+" "+args[2]+" <value>"));
            return;
        }

        if (args.length > 4) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" "+args[0]+" "+args[1]+" "+args[2]+" "+args[3]+" &cen niet meer!"));
            return;
        }

        // Check if user exists in db
        if (!plugin.SQLSelect.player_exists_name(args[2])) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cSpeler &4&l"+args[2]+" &cis nog nooit online geweest."));
            return;
        }

        Speler ChangeRankOf = plugin.SQLSelect.player_get_by_name(args[2]);
        // Check if user in in a land
        if (ChangeRankOf.getLand() == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cSpeler &4&l"+args[2]+" &czit niet in een land."));
            return;
        }

        // Check if rank exists in user kd
        Land land = plugin.SQLSelect.land_get_by_player(ChangeRankOf);
        Rank newRank = null;
        boolean found = false;
        for (Rank rank : land.getRanks()) {
            if (rank.getName().equalsIgnoreCase(args[3])) {
                found = true;
                newRank = rank;
            }
        }
        if (!found) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cRank &4&l"+args[3]+" &cbestaat niet in dit land."));
            return;
        }

        // Check if user is already that ranks
        if (ChangeRankOf.getRank().equals(newRank.getUuid())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&4&l"+ChangeRankOf.getName()+" &cheeft al de rank &4&l"+newRank.getName()+"&c!"));
            return;
        }

        // Check if user has permission to edit people from all kd
        if (sender.hasPermission("k.set.rank.other")) {
            // Check if rank is not at its maximum
            Integer peopleWithRank = 0;
            for (Speler speler : land.getLeden()) {
                if (speler.getRank().equals(newRank.getUuid()))
                    peopleWithRank = peopleWithRank+1;
            }

            if (peopleWithRank >= newRank.getMaximum() && newRank.getKdDefault() != true) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cde rank &4&l"+newRank.getName()+" &cheeft al het maximum aan spelers van &4&l"+newRank.getMaximum()+"&c!"));
                return;
            }

            // Set user as rank
            plugin.SQLUpdate.update_player_rank(ChangeRankOf.getUuid(), newRank.getUuid());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aSuccesvol &2&l"+ChangeRankOf.getName()+" &azijn rank verandert in &2&l"+newRank.getName()+"&a."));

            // Check if user is online to send him the message
            if (Bukkit.getPlayer(ChangeRankOf.getName()) != null) {
                plugin.SpelerPerms.reload_permissions(Bukkit.getPlayer(ChangeRankOf.getName()));
                Bukkit.getPlayer(ChangeRankOf.getName()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aJe rank is verandert in &2&l"+newRank.getName()+"&a."));
            }
            return;
        }

        // User can edit only from there own clan and not higher or equel to them self

        // Check if they are from the same KD
        Player player = (Player) sender;
        Speler SenderSpeler = plugin.SQLSelect.player_get_by_name(player.getName());

        if (!SenderSpeler.getLand().equals(ChangeRankOf.getLand())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJij en &4&l"+ChangeRankOf.getName()+" &czitten niet in hetzelfde land!"));
            return;
        }

        // check if other user has not a higher level or equal
        Rank ChangeRankOfOldRank = plugin.SQLSelect.get_rank(ChangeRankOf.getRank());
        Rank SenderRank = plugin.SQLSelect.get_rank(SenderSpeler.getRank());
        if (ChangeRankOfOldRank.getLevel() >= SenderRank.getLevel()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&4&l"+ChangeRankOf.getName()+" &czijn level is hoger of gelijk aan jouw level!"));
            return;
        }

        // check if new rank has higher or equal level to your level
        if (newRank.getLevel() >= SenderRank.getLevel()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe kan iemand niet de rol &4&l"+newRank.getName()+" &cgeven, omdat jouw rol level gelijk of lager is!"));
            return;
        }

        // Set user as rank
        plugin.SQLUpdate.update_player_rank(ChangeRankOf.getUuid(), newRank.getUuid());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + "&aSuccesvol &2&l"+ChangeRankOf.getName()+" &azijn rank verandert in &2&l"+newRank.getName()+"&a."));

        // Check if user is online to send him the message
        if (Bukkit.getPlayer(ChangeRankOf.getName()) != null) {
            plugin.SpelerPerms.reload_permissions(Bukkit.getPlayer(ChangeRankOf.getName()));
            Bukkit.getPlayer(ChangeRankOf.getName()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aJe rank is verandert in &2&l"+newRank.getName()+"&a."));
        }
        return;
    }
}
