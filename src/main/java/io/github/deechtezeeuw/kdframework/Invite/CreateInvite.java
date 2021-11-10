package io.github.deechtezeeuw.kdframework.Invite;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CreateInvite {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    private UUID uuid = UUID.randomUUID();
    private UUID land;
    private UUID player;

    public CreateInvite (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        this.checkArguments();
    }

    private void checkArguments() {
        // Wrong messages
        if (sender.hasPermission("k.invite.add.other") && args.length == 2 || sender.hasPermission("k.invite.add.other") && args.length > 4 ) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l"+label+" "+args[0]+" "+args[1]+" <kingdom> <speler>&c!"));
            return;
        } else if (sender.hasPermission("k.invite.add") && args.length == 2 || sender.hasPermission("k.invite.add") && args.length > 3 && !(sender.hasPermission("k.invite.add.other")) ) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l"+label+" "+args[0]+" "+args[1]+" <speler>&c!"));
            return;
        }

        // Invite someone for a special kingdom
        if (sender.hasPermission("k.invite.add.other") && args.length > 3) {
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

            // Check if user has already an invite for that land
            List<Invite> invites = plugin.SQLSelect.invite_get_from_user(speler);
            for (Invite invite : invites) {
                if (invite.getLand().equals(land.getUuid())) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+speler.getName()+" &cis al geinvite voor &4&l"+ArgKingdom+"&c!"));
                    return;
                }
            }

            // Check if user is already in that land
            if (speler.getLand().equals(land.getUuid())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&4&l"+speler.getName()+" &czit al in &4&l"+ArgKingdom+"&c!"));
                return;
            }

            Invite invite = new Invite(this.uuid, land.getUuid(), speler.getUuid());
            plugin.SQLInsert.invite_create(invite);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aJe hebt &2&l"+speler.getName()+" &ageinvite voor het land &2&l"+land.getName()+"&a!"));

            if (Bukkit.getPlayer(speler.getName()) != null) {
                Bukkit.getPlayer(speler.getName()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aJe bent geinvite voor &2&l"+land.getName()+"&a!"));
            }
            return;
        }

        // Invite someone for your own kingdom
        if (sender.hasPermission("k.invite.add") && args.length == 3) {
            String InviteUser = args[2];

            // check if user in console or player
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan alleen inviten in-game!"));
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
                        plugin.Config.getGeneralPrefix() + "&cJe kan alleen inviten als je lid bent van een land!"));
                return;
            }

            // check if user is in db
            if (!plugin.SQLSelect.player_exists_name(InviteUser)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes( '&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan &4&l"+InviteUser+" &cniet inviten, omdat hij niet in onze database bestaat!"));
                return;
            }
            Speler InvitedSpeler = plugin.SQLSelect.player_get_by_name(InviteUser);

            // Check if user is in same land
            if (InvitedSpeler.getLand() != null) {
                if (Inviter.getLand().equals(InvitedSpeler.getLand())) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l" + InvitedSpeler.getName() + " &cen jij zitten al in hetzelfde land!"));
                    return;
                }
            }

            // Check if user has already an invite from your land
            List<Invite> invites = plugin.SQLSelect.invite_get_from_user(InvitedSpeler);
            for (Invite invite : invites) {
                if (invite.getLand().equals(Inviter.getLand())) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&4&l"+InvitedSpeler.getName()+" &cis al geinvite voor jouw land!"));
                    return;
                }
            }

            // Create invite
            Land land = plugin.SQLSelect.land_get_by_player(Inviter);
            Invite invite = new Invite(this.uuid, land.getUuid(), InvitedSpeler.getUuid());
            plugin.SQLInsert.invite_create(invite);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aJe hebt &2&l"+InvitedSpeler.getName()+" &ageinvite voor het land &2&l"+land.getName()+"&a!"));

            if (Bukkit.getPlayer(InvitedSpeler.getName()) != null) {
                Bukkit.getPlayer(InvitedSpeler.getName()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aJe bent geinvite voor &2&l"+land.getName()+"&a!"));
            }
            return;
        } else {
            plugin.Config.noPermission(sender);
            return;
        }
    }
}
