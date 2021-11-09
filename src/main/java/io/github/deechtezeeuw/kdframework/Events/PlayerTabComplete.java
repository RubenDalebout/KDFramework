package io.github.deechtezeeuw.kdframework.Events;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayerTabComplete implements TabCompleter {
    private KDFramework plugin;

    public PlayerTabComplete(KDFramework plugin) {
        this.plugin = plugin;
    }

    public List<String> subCommands = new ArrayList<String>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> result = new ArrayList<String>();

        // Temp add commands
        subCommands.add("help"); subCommands.add("land");subCommands.add("set");subCommands.add("rank");

        if (args.length == 1) {
            for (String a : subCommands) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        // /k land [create/edit/delete/info]
        if (args.length == 2 && args[0].equalsIgnoreCase("land")) {
            List<String> landCommands = new ArrayList<>();
            landCommands.add("create");landCommands.add("edit");landCommands.add("delete");landCommands.add("info");
            for (String a : landCommands) {
                if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        // /k land edit <kingdom namen>
        if (args.length == 3 && args[0].equalsIgnoreCase("land") && args[1].equalsIgnoreCase("create") ||
            args.length == 3 && args[0].equalsIgnoreCase("land") && args[1].equalsIgnoreCase("edit") ||
                args.length == 3 && args[0].equalsIgnoreCase("land") && args[1].equalsIgnoreCase("delete") ||
                args.length == 3 && args[0].equalsIgnoreCase("land") && args[1].equalsIgnoreCase("info")) {
            List<Land> landCommands = plugin.SQLSelect.land_list();
            for (Land land : landCommands) {
                if (land.getName().toLowerCase().startsWith(args[2].toLowerCase()))
                    result.add(land.getName());
            }
            return result;
        }

        // /k land edit kingdom [prefix/invite/maximum]
        if (args.length == 4 && args[0].equalsIgnoreCase("land") && args[1].equalsIgnoreCase("edit")) {
            List<String> landCommands = new ArrayList<>();
            landCommands.add("prefix");landCommands.add("invite");landCommands.add("maximum");
            for (String a : landCommands) {
                if (a.toLowerCase().startsWith(args[3].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        // /k land edit kingdom prefix <value>
        if (args.length == 5 && args[0].equalsIgnoreCase("land") && args[1].equalsIgnoreCase("edit") && args[3].equalsIgnoreCase("prefix")) {
            List<String> landCommands = new ArrayList<>();
            landCommands.add("&7[&fVoorbeeld&7]");
            for (String a : landCommands) {
                if (a.toLowerCase().startsWith(args[4].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        // /k land edit kingdom invite <value>
        if (args.length == 5 && args[0].equalsIgnoreCase("land") && args[1].equalsIgnoreCase("edit") && args[3].equalsIgnoreCase("invite")) {
            List<String> landCommands = new ArrayList<>();
            landCommands.add("true");landCommands.add("false");
            for (String a : landCommands) {
                if (a.toLowerCase().startsWith(args[4].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        // /k land edit kingdom maximum <value>
        if (args.length == 5 && args[0].equalsIgnoreCase("land") && args[1].equalsIgnoreCase("edit") && args[3].equalsIgnoreCase("maximum")) {
            List<String> landCommands = new ArrayList<>();
            landCommands.add("10");landCommands.add("25");landCommands.add("50");
            for (String a : landCommands) {
                if (a.toLowerCase().startsWith(args[4].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        // /k set [land/rank]
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            List<String> landCommands = new ArrayList<>();
            landCommands.add("land");landCommands.add("rank");
            for (String a : landCommands) {
                if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        // /k set land <speler> [landen]
        if (args.length == 4 && args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("land")) {
            List<Land> landCommands = plugin.SQLSelect.land_list();
            List<String> landNamen = new ArrayList<>();
            landNamen.add("none");
            for (Land land : landCommands) {
                landNamen.add(land.getName());
            }
            for (String a : landNamen) {
                if (a.toLowerCase().startsWith(args[3].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        // /k set rank <speler> [rank]
        if (args.length == 4 && args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("rank")) {
            // Check if player exists in db
            if (plugin.SQLSelect.player_exists_name(args[2])) {
                List<String> ranks = new ArrayList<>();
                // Get land from player
                Speler speler = plugin.SQLSelect.player_get_by_name(args[2]);
                if (speler.getLand() != null) {
                    Land land = plugin.SQLSelect.land_get_by_player(speler);

                    for (Rank rank : land.getRanks()) {
                        ranks.add(rank.getName());
                    }
                } else {
                    ranks.add("none");
                }

                for (String a : ranks) {
                    if (a.toLowerCase().startsWith(args[3].toLowerCase()))
                        result.add(a);
                }

                return result;
            }
        }

        // /k land [create/edit/delete/info]
        if (args.length == 2 && args[0].equalsIgnoreCase("rank")) {
            List<String> landCommands = new ArrayList<>();
            landCommands.add("create");landCommands.add("edit");landCommands.add("delete");
            for (String a : landCommands) {
                if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        // /k ranks [landen]
        if (args.length == 2 && args[0].equalsIgnoreCase("ranks")) {
            List<Land> landList = plugin.SQLSelect.land_list();
            List<String> landNamen = new ArrayList<>();
            for (Land land : landList) {
                landNamen.add(land.getName());
            }

            for (String a : landNamen) {
                if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                    result.add(a);
            }
            return result;
        }

        return null;
    }
}
