package io.github.deechtezeeuw.kdframework.Land;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CreateLand {
    private KDFramework plugin;
    private Land land;

    public CreateLand(KDFramework plugin, CommandSender sender, String KDName) {
        this.plugin = plugin;
        land = new Land(UUID.randomUUID(),KDName, "&7[&f"+KDName+"&7]", plugin.Config.getLandConfig().getBoolean("land.invite"), plugin.Config.getLandConfig().getInt("land.maximum"), null, null, null, null, null, "&7[&f"+KDName.substring(0,4)+"&7]");

        create_land(sender, KDName);
    }

    // Global action
    private void create_land(CommandSender sender, String KDName) {
        // Check if clan exists
        if (!plugin.SQLSelect.land_exists(land.getName())) {
            // Land does not exists
            plugin.SQLInsert.land_create(land); // create land
            plugin.SQLInstall.table_relations_add_column(land);

            Land newLand = plugin.SQLSelect.land_get_by_name(KDName);
            // Default rank landConfig
            for (String key : plugin.Config.getLandConfig().getConfigurationSection("land.ranks").getKeys(false)) {
                String rank = key;
                Integer level = plugin.Config.getLandConfig().getInt("land.ranks."+rank+".level");
                Integer maximum = plugin.Config.getLandConfig().getInt("land.ranks."+rank+".maximum");
                String prefix = plugin.Config.getLandConfig().getString("land.ranks."+rank+".prefix");
                Boolean rankDefault = plugin.Config.getLandConfig().getBoolean("land.ranks."+rank+".default");
                if (rankDefault)
                    maximum = null;

                // Create rank
                Rank newRank = new Rank(UUID.randomUUID(), rank, level, maximum, prefix,null, rankDefault);
                plugin.SQLInsert.rank_create(newLand.getUuid(), newRank);
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aHet land &2&l"+land.getName()+" &ais aangemaakt!"));
            return;
        } else {
            // Land exists
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+land.getName()+" &cbestaat al!"));
            return;
        }
    }
}
