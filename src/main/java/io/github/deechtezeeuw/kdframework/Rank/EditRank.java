package io.github.deechtezeeuw.kdframework.Rank;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditRank {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public EditRank (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        this.checkArguments();
    }

    private void checkArguments() {
        if (sender.hasPermission("k.rank.edit.other") && args.length == 6) {
            String ArgKingdom = args[2];
            String ArgRank = args[3];
            String ArgWhat = args[4];
            String ArgValue = args[5];

            // Check if Kingdom exists
            if (!plugin.SQLSelect.land_exists(ArgKingdom)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+ArgKingdom+" &cbestaat niet!"));
                return;
            }
            Land land = plugin.SQLSelect.land_get_by_name(ArgKingdom);
            // Check if rank exists in kingdom
            boolean found = false;
            Rank editRank = null;
            for (Rank rank : land.getRanks()) {
                if (rank.getName().equalsIgnoreCase(ArgRank)) {
                    found = true;
                    editRank = rank;
                }
            }
            if (!found) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+ArgRank+" &cbestaat niet in het land &4&l"+land.getName()+"&c!"));
                return;
            }

            // Check if ArgWhat is prefix/level/maximum/default
            if (!ArgWhat.equalsIgnoreCase("prefix") && !ArgWhat.equalsIgnoreCase("level") && !ArgWhat.equalsIgnoreCase("maximum") && !ArgWhat.equalsIgnoreCase("default")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+ArgWhat+" &cis niet toegestaan, mag alleen: &4&lprefix&c, &4&llevel&c, &4&lmaximum &cof &4&ldefault &czijn!"));
                return;
            }

            // Check if its prefix the length is not longer then 36
            if (ArgWhat.equalsIgnoreCase("prefix")) {
                if (ArgValue.length() > 36) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+ArgValue+" &cis niet toegestaan, je totale karakters mag niet langer zijn dan &4&l36 &ctekens!"));
                    return;
                }

                // Update prefix
                plugin.SQLUpdate.update_rank(editRank, ArgWhat, ArgValue);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+editRank.getName()+" &aheeft nu de prefix &2&l"+ArgValue+" &ain het land &2&l"+land.getName()+"&a!"));
                return;
            }

            // Check if its level is an number
            if (ArgWhat.equalsIgnoreCase("level")) {
                // Check if argnew is number
                if (!(StringUtils.isNumeric(ArgValue))) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+ArgValue+" &cis niet toegestaan, je nieuwe &4&llevel &cmoet een &4&lgetal &czijn!"));
                    return;
                }
                Integer newValue = Integer.parseInt(ArgValue);

                // Update level
                plugin.SQLUpdate.update_rank(editRank, ArgWhat, newValue.toString());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+editRank.getName()+" &aheeft nu een level van &2&l"+newValue+" &ain het land &2&l"+land.getName()+"&a!"));
                return;
            }

            // Check if its maximum is an number
            if (ArgWhat.equalsIgnoreCase("maximum")) {
                // Check if argnew is number
                if (!(StringUtils.isNumeric(ArgValue))) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+ArgValue+" &cis niet toegestaan, je nieuwe &4&lmaximum &cmoet een &4&lgetal &czijn!"));
                    return;
                }
                Integer newValue = Integer.parseInt(ArgValue);
                // Check if this rank is the default rank
                if (editRank.getKdDefault()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cDeze rank mag geen maximum hebben, omdat het de default rank is!"));
                    return;
                }

                // check if Argnew is lower then rank members
                Integer members = 0;
                for (Speler spieler : land.getLeden()) {
                    if (spieler.getRank().equals(editRank.getUuid())) {
                        members = members+1;
                    }
                }

                if (newValue < members) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+ArgValue+" &cis niet toegestaan, je nieuwe maximum moet hoger zijn of gelijk zijn aan &4&l"+members+" &c!"));
                    return;
                }

                // Update maximum
                plugin.SQLUpdate.update_rank(editRank, ArgWhat, newValue.toString());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+editRank.getName()+" &aheeft nu het maximum van &2&l"+newValue+" &ain het land &2&l"+land.getName()+"&a!"));
                return;
            }

            // Check if its default and its true or false
            if (ArgWhat.equalsIgnoreCase("default")) {
                if (!ArgValue.equalsIgnoreCase("true")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+ArgValue+" &cis niet toegestaan, je nieuwe &4&ldefault &cmoet &4&ltrue &czijn!"));
                    return;
                }

                Rank oldDefault = land.get_defaultRank();
                // Check if its the same old rank
                if (ArgWhat.equalsIgnoreCase("true") && editRank.equals(oldDefault)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+editRank.getName()+" &cis al de default rank!"));
                    return;
                }

                // New defaultrank
                plugin.SQLUpdate.update_rank(editRank, ArgWhat, ArgValue);
                // Old defaultrank
                plugin.SQLUpdate.update_rank(oldDefault, ArgWhat, "false");

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+editRank.getName()+" &ais nu de default rank in het land &2&l"+land.getName()+"&a!"));
                return;
            }
            return;
        }

        // Check args 2
        if (args.length == 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " "+args[1]+" <rank> <column> <value>"));
            return;
        }
        // Check args 3
        if (args.length == 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " "+args[1]+" "+args[2]+" <column> <value>"));
            return;
        }
        // Check args 4
        if (args.length == 4) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " "+args[1]+" "+args[2]+" "+args[3]+" <value>"));
            return;
        }
        // Check args 5
        if (args.length > 5) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " "+args[1]+" "+args[2]+" "+args[3]+" "+args[4] + " &cen niet meer!"));
            return;
        }

        // Check if user is in the database
        Player player = (Player) sender;
        if (!plugin.SQLSelect.player_exists(player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cIn verband met veiligheids redenen kunnen wij niet toestaan dat u niet bestaat in onze database!"));
            return;
        }
        Speler speler = plugin.SQLSelect.player_get_by_name(player.getName());
        // Check if player is not null and in a land
        if (speler == null || speler.getLand() == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cU kunt alleen een rol aanpassen als u lid bent van een land!"));
            return;
        }
        Land land = plugin.SQLSelect.land_get_by_player(speler);

        String ArgRank = args[2];
        String ArgWhat = args[3];
        String ArgNew = args[4];
        Rank editRank = null;
        // Check if rank exists in Kingdom
        boolean found = false;
        for (Rank rank : land.getRanks()) {
            if (rank.getName().equalsIgnoreCase(args[2])) {
                found = true;
                editRank = rank;
            }
        }
        if (!found) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+ArgRank+" &cbestaat niet in het land &4&l"+land.getName()+"&c!"));
            return;
        }

        // Check if ArgWhat is prefix/level/maximum/default
        if (!ArgWhat.equalsIgnoreCase("prefix") && !ArgWhat.equalsIgnoreCase("level") && !ArgWhat.equalsIgnoreCase("maximum") && !ArgWhat.equalsIgnoreCase("default")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&4&l"+ArgWhat+" &cis niet toegestaan, mag alleen: &4&lprefix&c, &4&llevel&c, &4&lmaximum &cof &4&ldefault &czijn!"));
            return;
        }

        // Check if its prefix the length is not longer then 36
        if (ArgWhat.equalsIgnoreCase("prefix")) {
            if (ArgNew.length() > 36) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+ArgNew+" &cis niet toegestaan, je totale karakters mag niet langer zijn dan &4&l36 &ctekens!"));
                return;
            }

            // Update prefix
            plugin.SQLUpdate.update_rank(editRank, ArgWhat, ArgNew);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+editRank.getName()+" &aheeft nu de prefix &2&l"+ArgNew+" &a!"));
            return;
        }

        // Check if its level is an number
        if (ArgWhat.equalsIgnoreCase("level")) {
            // Check if argnew is number
            if (!(StringUtils.isNumeric(ArgNew))) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+ArgNew+" &cis niet toegestaan, je nieuwe &4&llevel &cmoet een &4&lgetal &czijn!"));
                return;
            }
            Integer newValue = Integer.parseInt(ArgNew);
            // Check if editrank is higher or equal of yours
            if (editRank.getLevel() >= plugin.SQLSelect.get_rank(speler.getRank()).getLevel()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+editRank.getName()+" &cheeft een hoger of gelijk level, deze rank kan je niet aanpassen!"));
                return;
            }

            // Check if newvalue is higher or equal to your level
            if (newValue >= plugin.SQLSelect.get_rank(speler.getRank()).getLevel()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+newValue+" &cmoet lager zijn dan jouw level!"));
                return;
            }

            // Update level
            plugin.SQLUpdate.update_rank(editRank, ArgWhat, newValue.toString());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+editRank.getName()+" &aheeft nu een level van &2&l"+newValue+" &a!"));
            return;
        }

        // Check if its maximum is an number
        if (ArgWhat.equalsIgnoreCase("maximum")) {
            // Check if argnew is number
            if (!(StringUtils.isNumeric(ArgNew))) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+ArgNew+" &cis niet toegestaan, je nieuwe &4&lmaximum &cmoet een &4&lgetal &czijn!"));
                return;
            }
            Integer newValue = Integer.parseInt(ArgNew);
            // Check if this rank is the default rank
            if (editRank.getKdDefault()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDeze rank mag geen maximum hebben, omdat het de default rank is!"));
                return;
            }

            // check if Argnew is lower then rank members
            Integer members = 0;
            for (Speler spieler : land.getLeden()) {
                if (spieler.getRank().equals(editRank.getUuid())) {
                    members = members+1;
                }
            }

            if (newValue < members) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+ArgNew+" &cis niet toegestaan, je nieuwe maximum moet hoger zijn of gelijk zijn aan &4&l"+members+" &c!"));
                return;
            }

            // Update maximum
            plugin.SQLUpdate.update_rank(editRank, ArgWhat, newValue.toString());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+editRank.getName()+" &aheeft nu het maximum van &2&l"+newValue+" &a!"));
            return;
        }

        // Check if its default and its true or false
        if (ArgWhat.equalsIgnoreCase("default")) {
            if (!ArgNew.equalsIgnoreCase("true")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+ArgNew+" &cis niet toegestaan, je nieuwe &4&ldefault &cmoet &4&ltrue &czijn!"));
                return;
            }

            Rank oldDefault = land.get_defaultRank();
            // Check if its the same old rank
            if (ArgWhat.equalsIgnoreCase("true") && editRank.equals(oldDefault)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+editRank.getName()+" &cis al de default rank!"));
                return;
            }

            // Check if editRank is lower then your rank
            if (editRank.getLevel() >= plugin.SQLSelect.get_rank(speler.getRank()).getLevel()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cDe rank &4&l"+editRank.getName()+" &cis hoger of gelijk aan jouw level en mag dus niet geedit worden!"));
                return;
            }

            // New defaultrank
            plugin.SQLUpdate.update_rank(editRank, ArgWhat, ArgNew);
            // Old defaultrank
            plugin.SQLUpdate.update_rank(oldDefault, ArgWhat, "false");

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aDe rank &2&l"+editRank.getName()+" &ais nu de default rank in het land &2&l"+land.getName()+"&a!"));
            return;
        }
    }
}
