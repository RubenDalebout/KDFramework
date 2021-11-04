package io.github.deechtezeeuw.kdframework.Set;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class SetLand {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public SetLand(KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        check_args();
    }

    private void check_args() {
        // Check if arguments has an player
        if (args.length >= 3) {
            // Check if player exists
            if (!plugin.SQLSelect.player_exists_name(args[2])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cSpeler &4&l"+args[2]+" &cis nog nooit online geweest."));
                return;
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" "+args[0]+" "+args[1]+" <speler> <land>"));
            return;
        }

        if (args.length == 4) {
            // Check if land exists or it value is none
            if (!plugin.SQLSelect.land_exists(args[3]) && !args[3].equalsIgnoreCase("none")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[3]+" &cbestaat niet!"));
                return;
            }
        } else {
            // Wrong argumentation
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" "+args[0]+" "+args[1]+" "+args[2]+" <land>"));
            return;
        }

        Speler speler = plugin.SQLSelect.player_get_by_name(args[2]);
        // User going into new kingdom
        if (!args[3].equalsIgnoreCase("none")) {
            Land land = plugin.SQLSelect.land_get_by_name(args[3]);
            // Check if user is in an kingdom
            if (speler.getLand() != null) {
                // Check if user is already into that kingdom
                if (land.getUuid().equals(speler.getLand())) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l" + speler.getName() + " &czit al in &4&l" + land.getName() + "&c!"));
                    return;
                }
            }

            // Update user kingdom
            plugin.SQLUpdate.update_player_land(speler.getUuid(), land.getUuid());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aSuccesvol &2&l"+speler.getName()+" &ain het land &2&l"+land.getName()+ " &agezet!"));

            // Check if user is online to send him the message
            if (Bukkit.getPlayer(speler.getUuid()) != null) {
                Bukkit.getPlayer(speler.getUuid()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aJe bent in het land &2&l"+land.getName()+" &agezet!"));
            }
        }

        // User going into none
        if (args[3].equalsIgnoreCase("none")) {
            Land land = null;

            // Check if user is not in a land
            if (speler.getLand() == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l" + speler.getName() + " &czit al in &4&lgeen kingdom&c!"));
                return;
            }

            // Update user kingdom
            plugin.SQLUpdate.update_player_land(speler.getUuid(), null);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aSuccesvol &2&l"+speler.getName()+" &auit zijn land gezet."));

            // Check if user is online to send him the message
            if (Bukkit.getPlayer(speler.getUuid()) != null) {
                Bukkit.getPlayer(speler.getUuid()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aJe bent uit je land gezet!"));
            }
        }

        return;
    }
}
