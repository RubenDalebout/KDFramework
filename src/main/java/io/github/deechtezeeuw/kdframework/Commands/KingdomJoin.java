package io.github.deechtezeeuw.kdframework.Commands;

import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KingdomJoin {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public KingdomJoin(KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        this.checkArgs();
    }

    private void checkArgs() {
        // Check if not console
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe kan alleen in-game een land joinen!"));
            return;
        }
        Player player = (Player) sender;
        // Check if player exists
        if (!plugin.SQLSelect.player_exists(player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cIn verband met veiligheid kunnen we niet toestaan dat u niet in onze database bestaat!"));
            return;
        }
        Speler speler = plugin.SQLSelect.player_get_by_uuid(player.getUniqueId());

        // Check if player is already in a land
        if (speler.getLand() != null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe kan geen ander land joinen, zolang je al in een land zit!"));
            return;
        }

        // Check if land exists
        if (!plugin.SQLSelect.land_exists(args[1])) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[1]+" &cbestaat niet!"));
            return;
        }
        Land land = plugin.SQLSelect.land_get_by_name(args[1]);

        // Check if land is full
        if (!(land.getLeden().size() < land.getMaximum())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+land.getName()+" &czit vol!"));
        }

        // Check if land is on invite
        if (land.getInvite()) {
            // on invite

            // Get all invites from user
            boolean found = false;
            List<Invite> invites = plugin.SQLSelect.invite_get_from_user(speler);
            for (Invite invite : invites) {
                if (invite.getLand().equals(land.getUuid()))
                    found = true;
            }

            if (!found) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe hebt geen invite voor &4&l"+land.getName()+"&c!"));
                return;
            }

            for (Speler spelers : land.getLeden()) {
                if (Bukkit.getServer().getPlayer(spelers.getUuid()) != null) {
                    Bukkit.getServer().getPlayer(spelers.getUuid()).sendMessage(
                            ChatColor.translateAlternateColorCodes('&',
                                    plugin.Config.getGeneralPrefix() + "&4&l"+speler.getName()+" &ais het land gejoind!")
                    );
                }
            }
            // you can join
            plugin.SQLUpdate.update_player_land(speler.getUuid(), land.getUuid());
            plugin.SQLDelete.invite_delete_player(speler);
            plugin.SQLUpdate.update_player_rank(speler.getUuid(), land.get_defaultRank().getUuid());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aJe bent het land &2&l"+land.getName()+ " &agejoind!"));
            // check if land has an spawn
            if (land.getSpawn() == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet kingdom &4&l"+land.getName()+" &cheeft nog geen spawn!"));
                return;
            }
            land.goto_spawn(player);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aJe word geteleporteerd naar je kingdom..."));
            return;
        } else {
            for (Speler spelers : land.getLeden()) {
                if (Bukkit.getServer().getPlayer(spelers.getUuid()) != null) {
                    Bukkit.getServer().getPlayer(spelers.getUuid()).sendMessage(
                            ChatColor.translateAlternateColorCodes('&',
                                    plugin.Config.getGeneralPrefix() + "&4&l"+speler.getName()+" &ais het land gejoind!")
                    );
                }
            }
            // you can join
            plugin.SQLUpdate.update_player_land(speler.getUuid(), land.getUuid());
            plugin.SQLDelete.invite_delete_player(speler);
            plugin.SQLUpdate.update_player_rank(speler.getUuid(), land.get_defaultRank().getUuid());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aJe bent het land &2&l"+land.getName()+ " &agejoind!"));
            // check if land has an spawn
            if (land.getSpawn() == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet kingdom &4&l"+land.getName()+" &cheeft nog geen spawn!"));
                return;
            }
            land.goto_spawn(player);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aJe word geteleporteerd naar je kingdom..."));
            return;
        }
    }
}
