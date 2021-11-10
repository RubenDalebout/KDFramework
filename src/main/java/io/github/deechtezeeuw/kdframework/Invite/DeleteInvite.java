package io.github.deechtezeeuw.kdframework.Invite;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DeleteInvite {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public DeleteInvite (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        this.checkArguments();
    }

    private void checkArguments() {
        // Wrong messages
        if (sender.hasPermission("k.invite.remove.other") && args.length == 2 || sender.hasPermission("k.invite.remove.other") && args.length > 4 ) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l"+label+" "+args[0]+" "+args[1]+" <kingdom> <speler>&c!"));
            return;
        } else if (sender.hasPermission("k.invite.remove") && args.length == 2 || sender.hasPermission("k.invite.remove") && args.length > 3 && !(sender.hasPermission("k.invite.remove.other")) ) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l"+label+" "+args[0]+" "+args[1]+" <speler>&c!"));
            return;
        }

        // Remove someones invite for a special kingdom
        if (sender.hasPermission("k.invite.remove.other") && args.length > 3) {
            if (args.length != 4) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                        plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l"+label+" "+args[0]+" "+args[1]+" <kingdom> <speler>&c!"));
                return;
            }

            String ArgKingdom = args[2];
            String ArgSpeler = args[3];

            // Check if land exists
            if (!plugin.SQLSelect.land_exists(ArgKingdom)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+ArgKingdom+" &cbestaat niet!"));
                return;
            }
            Land land = plugin.SQLSelect.land_get_by_name(ArgKingdom);

            // Check if user exists
            if (!plugin.SQLSelect.player_exists_name(ArgSpeler)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                        plugin.Config.getGeneralPrefix() + "&cDe gebruiker &4&l"+ArgSpeler+" &cbestaat niet!"));
                return;
            }
            Speler speler = plugin.SQLSelect.player_get_by_name(ArgSpeler);

            // Check if user has already an invite from your land
            List<Invite> invites = plugin.SQLSelect.invite_get_from_user(speler);
            boolean found = false;
            Invite inviteFromUsr = null;
            for (Invite invite : invites) {
                if (invite.getLand().equals(land.getUuid())) {
                    found = true;
                    inviteFromUsr = invite;
                    break;
                }
            }

            if (!found) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l" + speler.getName() + " &cheeft geen invite van het land &4&l"+land.getName()+"&c!"));
                return;
            }

            // Delete invite
            if (found) {
                plugin.SQLDelete.invite_delete(inviteFromUsr);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aDe invite van &2&l"+speler.getName()+" &ais verwijderd voor het land &2&l"+land.getName()+"&a!"));
                if (Bukkit.getPlayer(speler.getUuid()) != null) {
                    Bukkit.getPlayer(speler.getUuid()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&aJouw invite voor het land &2&l"+land.getName()+" &ais verwijderd!"));
                }
            }
            return;
        }

        // Invite remove for your own kingdom
        if (sender.hasPermission("k.invite.remove") && args.length == 3) {
            String InviteUser = args[2];

            // check if user in console or player
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan alleen invites verwijderen in-game!"));
                return;
            }

            Player player = (Player) sender;
            // check if inviter exists in db
            if (!plugin.SQLSelect.player_exists(player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                        plugin.Config.getGeneralPrefix() + "&cIn verband met veiligheids redenen kunnen wij niet toestaan dat u niet bestaat in onze database!"));
                return;
            }
            Speler Inviter = plugin.SQLSelect.player_get_by_name(player.getName());

            // Check if inviter is in a land
            if (Inviter == null || Inviter.getLand() == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan alleen invites verwijderen als je in een land zit!"));
                return;
            }

            // check if user is in db
            if (!plugin.SQLSelect.player_exists_name(InviteUser)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan &4&l"+InviteUser+" &cniet zijn invites verwijderen, omdat hij niet in onze database bestaat!"));
                return;
            }
            Speler InvitedSpeler = plugin.SQLSelect.player_get_by_name(InviteUser);

            // Check if user has already an invite from your land
            List<Invite> invites = plugin.SQLSelect.invite_get_from_user(InvitedSpeler);
            boolean found = false;
            Invite inviteFromUsr = null;
            for (Invite invite : invites) {
                if (invite.getLand().equals(Inviter.getLand())) {
                    found = true;
                    inviteFromUsr = invite;
                    break;
                }
            }

            if (!found) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l" + InvitedSpeler.getName() + " &cheeft geen invite van jouw land!"));
                return;
            }

            Land land = plugin.SQLSelect.land_get_by_player(Inviter);

            // Delete invite
            if (found) {
                plugin.SQLDelete.invite_delete(inviteFromUsr);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aDe invite van &2&l"+InvitedSpeler.getName()+" &ais verwijderd voor het land &2&l"+land.getName()+"&a!"));
                if (Bukkit.getPlayer(InvitedSpeler.getUuid()) != null) {
                    Bukkit.getPlayer(InvitedSpeler.getUuid()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&aJouw invite voor het land &2&l"+land.getName()+" &ais verwijderd!"));
                }
            }
            return;
        } else {
            plugin.Config.noPermission(sender);
            return;
        }
    }
}
