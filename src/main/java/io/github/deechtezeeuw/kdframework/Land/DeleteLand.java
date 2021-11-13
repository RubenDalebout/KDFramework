package io.github.deechtezeeuw.kdframework.Land;

import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DeleteLand {
    private KDFramework plugin;
    private String KDName;

    public DeleteLand(KDFramework plugin, CommandSender sender, String KDName) {
        this.plugin = plugin;
        this.KDName = KDName;

        delete_land(sender);
    }

    // Global action
    private void delete_land(CommandSender sender) {
        // Check if clan exists
        if (plugin.SQLSelect.land_exists(KDName)) {
            // Land exists
            Land land = plugin.SQLSelect.land_get_by_name(KDName);
            for (Speler speler : land.getLeden()) {
                plugin.SQLUpdate.update_player_land(speler.getUuid(), null);
                plugin.SQLUpdate.update_player_rank(speler.getUuid(), null);
            }

            for (Rank rank : land.getRanks()) {
                plugin.SQLDelete.rank_delete(rank);
            }

            for (Invite invite : land.getInvites()) {
                plugin.SQLDelete.invite_delete(invite);
            }

            plugin.SQLDelete.table_relations_delete_column(land);

            plugin.SQLDelete.land_delete(land.getName());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aHet land &2&l"+KDName+" &ais verwijderd!"));
            return;
        } else {
            // Land does not exists
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+KDName+" &cbestaat niet!"));
            return;
        }
    }
}
