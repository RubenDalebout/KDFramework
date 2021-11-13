package io.github.deechtezeeuw.kdframework.Relaties;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RelateEnemy {
    private CommandSender sender;
    private String label;
    private String[] args;

    public RelateEnemy(CommandSender sender, String label, String[] args) {
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
                    plugin.Config.getGeneralPrefix() + "&cJe kan alleen in-game vijandig zetten."));
            return;
        }
        Player player = (Player) sender;

        // Check if player is in database
        if (!plugin.SQLSelect.player_exists(player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cIn verband met de veiligheid mag je alleen een vijand aanmaken als je in de database bestaat!"));
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
        if (args.length == 2 && sender.hasPermission("k.enemy")) {
            // Check if other land exists
            if (!plugin.SQLSelect.land_exists(args[1])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[1]+" &cbestaat niet!"));
                return;
            }
            Land land = plugin.SQLSelect.land_get_by_player(speler);
            Land other = plugin.SQLSelect.land_get_by_name(args[1]);

            // Check if not own kingdom
            if (land.getUuid().equals(other.getUuid())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan niet je eigen kingdom tot vijand verklaren!"));
                return;
            }

            // Check if already enemy
            if (land.get_relation_number(other) == 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+land.getName()+" &cen &4&l"+other.getName()+" &czijn al vijanden!"));
                return;
            }

            // Set both on enemy
            plugin.SQLUpdate.update_relationship(land, other, 2);
            plugin.SQLUpdate.update_relationship(other, land, 2);

            for (Speler spelers : land.getLeden()) {
                if (Bukkit.getServer().getPlayer(spelers.getUuid()) != null) {
                    Bukkit.getServer().getPlayer(spelers.getUuid()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+land.getName()+" &cen &4&l"+other.getName()+" &czijn nu vijanden!"));
                }
            }

            for (Speler spelers : other.getLeden()) {
                if (Bukkit.getServer().getPlayer(spelers.getUuid()) != null) {
                    Bukkit.getServer().getPlayer(spelers.getUuid()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+other.getName()+" &cen &4&l"+land.getName()+" &czijn nu vijanden!"));
                }
            }
            return;
        }
        if (args.length == 3 && sender.hasPermission("k.enemy.other")) {
            if (!plugin.SQLSelect.land_exists(args[1])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[1]+" &cbestaat niet!"));
                return;
            }
            Land land = plugin.SQLSelect.land_get_by_name(args[1]);
            if (!plugin.SQLSelect.land_exists(args[2])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cbestaat niet!"));
                return;
            }
            Land other = plugin.SQLSelect.land_get_by_name(args[2]);

            // Check if the kingdoms are not the same
            if (land.getUuid().equals(other.getUuid())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan niet een kingdom vijand maken van zichzelf!"));
                return;
            }

            // Check if already enemy
            if (land.get_relation_number(other) == 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+land.getName()+" &cen &4&l"+other.getName()+" &czijn al vijanden!"));
                return;
            }

            // Set both on enemy
            plugin.SQLUpdate.update_relationship(land, other, 2);
            plugin.SQLUpdate.update_relationship(other, land, 2);

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aSuccesvol &2&l"+land.getName()+" &aen &2&l"+other.getName()+" &avijanden gemaakt!"));

            for (Speler spelers : land.getLeden()) {
                if (Bukkit.getServer().getPlayer(spelers.getUuid()) != null) {
                    Bukkit.getServer().getPlayer(spelers.getUuid()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+land.getName()+" &cen &4&l"+other.getName()+" &czijn nu vijanden!"));
                }
            }

            for (Speler spelers : other.getLeden()) {
                if (Bukkit.getServer().getPlayer(spelers.getUuid()) != null) {
                    Bukkit.getServer().getPlayer(spelers.getUuid()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+other.getName()+" &cen &4&l"+land.getName()+" &czijn nu vijanden!"));
                }
            }

            return;
        }

        if (sender.hasPermission("k.enemy.other")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" vijand <kingdom> <kingdom>&c!"));
            return;
        }
        if (sender.hasPermission("k.enemy")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" vijand <kingdom>&c!"));
            return;
        }
    }
}
