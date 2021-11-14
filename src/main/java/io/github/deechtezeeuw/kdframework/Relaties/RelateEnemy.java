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

        // Check if admin is doing it
        if (args.length == 3 && sender.hasPermission("k.enemy.other")) {
            // Check if args 1 is an existing land
            if (!plugin.SQLSelect.land_exists(args[1])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[1]+" &cbestaat niet!"));
                return;
            }
            // Check if args 2 is an existing land
            if (!plugin.SQLSelect.land_exists(args[2])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cbestaat niet!"));
                return;
            }
            Land land = plugin.SQLSelect.land_get_by_name(args[1]);
            Land other = plugin.SQLSelect.land_get_by_name(args[2]);

            // Check of ze al enemy zijn
            if (land.get_relation_number(other) == 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[1]+" &cen &4&l"+args[2]+" &czijn al vijanden!"));
                return;
            }

            plugin.SQLUpdate.update_relationship(land, other, 2);
            plugin.SQLUpdate.update_relationship(other, land, 2);

            // Delete relationship request
            plugin.SQLDelete.table_relationship_request_delete(land, other);
            plugin.SQLDelete.table_relationship_request_delete(other, land);

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aHet land &2&l"+args[1]+" &aen &2&l"+args[2]+" &azijn nu vijanden!"));

            for (Speler kdSpeler : land.getLeden()) {
                if (Bukkit.getServer().getPlayer(kdSpeler.getUuid()) != null) {
                    Player kdPlayer = Bukkit.getServer().getPlayer(kdSpeler.getUuid());
                    kdPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&aJouw land en het land &2&l"+other.getName()+" &ahebben nu een vijandige relatie!"));
                }
            }

            for (Speler kdSpeler : other.getLeden()) {
                if (Bukkit.getServer().getPlayer(kdSpeler.getUuid()) != null) {
                    Player kdPlayer = Bukkit.getServer().getPlayer(kdSpeler.getUuid());
                    kdPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&aJouw land en het land &2&l"+land.getName()+" &ahebben nu een vijandige relatie!"));
                }
            }
            return;
        }
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

        if (args.length == 2 && sender.hasPermission("k.enemy")) {
            // Check if other land exists
            if (!plugin.SQLSelect.land_exists(args[1])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[1]+" &cbestaat niet!"));
                return;
            }
            Land land = plugin.SQLSelect.land_get_by_player(speler);
            Land other = plugin.SQLSelect.land_get_by_name(args[1]);

            // Check if own land
            if (other.getUuid().equals(speler.getLand())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan relatie met je eigen land aanmaken!"));
                return;
            }

            // Check if already enemy
            if (land.get_relation_number(other) == 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+land.getName()+" &cen &4&l"+other.getName()+" &czijn al vijanden!"));
                return;
            }

            // Check if relation is neutral
            if (land.get_relation_number(other) == 0) {
                // Other land wants to be neutral already so set both too neutral and delete requests
                plugin.SQLUpdate.update_relationship(land, other, 2);
                plugin.SQLUpdate.update_relationship(other, land, 2);

                // Delete relationship request
                plugin.SQLDelete.table_relationship_request_delete(land, other);
                plugin.SQLDelete.table_relationship_request_delete(other, land);

                for (Speler kdSpeler : land.getLeden()) {
                    if (Bukkit.getServer().getPlayer(kdSpeler.getUuid()) != null) {
                        Player kdPlayer = Bukkit.getServer().getPlayer(kdSpeler.getUuid());
                        kdPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&aJouw land en het land &2&l"+other.getName()+" &ahebben nu een vijandige relatie!"));
                    }
                }

                for (Speler kdSpeler : other.getLeden()) {
                    if (Bukkit.getServer().getPlayer(kdSpeler.getUuid()) != null) {
                        Player kdPlayer = Bukkit.getServer().getPlayer(kdSpeler.getUuid());
                        kdPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&aJouw land en het land &2&l"+land.getName()+" &ahebben nu een vijandige relatie!"));
                    }
                }
                return;
            }

            // Check if relation is ally
            if (land.get_relation_number(other) == 1) {
                // You are enemies from each other and you want to be neutral
                // Check if you already did an request
                if (plugin.SQLSelect.relationships_request_exists(land, other, "Vijand")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&aJe hebt al een aanvraag liggen bij dat kingdom voor een vijandige relatie!"));
                    return;
                }

                // Check if their is an request from the other land to be neutral
                if (plugin.SQLSelect.relationships_request_exists(other, land, "Vijand")) {
                    // Other land wants to be neutral already so set both too neutral and delete requests
                    plugin.SQLUpdate.update_relationship(land, other, 2);
                    plugin.SQLUpdate.update_relationship(other, land, 2);

                    // Delete relationship request
                    plugin.SQLDelete.table_relationship_request_delete(land, other);
                    plugin.SQLDelete.table_relationship_request_delete(other, land);

                    for (Speler kdSpeler : land.getLeden()) {
                        if (Bukkit.getServer().getPlayer(kdSpeler.getUuid()) != null) {
                            Player kdPlayer = Bukkit.getServer().getPlayer(kdSpeler.getUuid());
                            kdPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.Config.getGeneralPrefix() + "&aJouw land en het land &2&l"+other.getName()+" &ahebben nu een vijandige relatie!"));
                        }
                    }

                    for (Speler kdSpeler : other.getLeden()) {
                        if (Bukkit.getServer().getPlayer(kdSpeler.getUuid()) != null) {
                            Player kdPlayer = Bukkit.getServer().getPlayer(kdSpeler.getUuid());
                            kdPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.Config.getGeneralPrefix() + "&aJouw land en het land &2&l"+land.getName()+" &ahebben nu een vijandige relatie!"));
                        }
                    }
                    return;
                } else {
                    // Send request to other land
                    plugin.SQLInsert.relationship_request_create(land, other, "Vijand");
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&2&lVijandige &arelatie aangevraagd tussen &2&l"+land.getName()+" &aen &2&l"+other.getName()+"&a!"));

                    // Send to people of that land the request
                    for (Speler spelers : other.getLeden()) {
                        if (Bukkit.getServer().getPlayer(spelers.getUuid()) != null) {
                            Player otherPlayer = Bukkit.getServer().getPlayer(spelers.getUuid());
                            if (otherPlayer.hasPermission("k.enemy")) {
                                otherPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.Config.getGeneralPrefix() + "&aHet land &2&l"+land.getName()+" &awilt een vijandige relatie met uw land. Accepteer dit door &2&l/k vijand "+land.getName()+" &ate typen."));
                            }
                        }
                    }
                    return;
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
