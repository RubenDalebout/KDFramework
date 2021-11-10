package io.github.deechtezeeuw.kdframework.GUI;

import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GUIInvites {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public GUIInvites (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        if (sender.hasPermission("k.invites.other") && args.length > 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " <kingdom>"));
            return;
        }

        if (sender.hasPermission("k.invites.other") && args.length > 1) {
            if (!plugin.SQLSelect.land_exists(args[1])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&cHet land &4&l"+args[1]+" &cbestaat niet!"));
                return;
            }

            Land land = plugin.SQLSelect.land_get_by_name(args[1]);
            this.createGUI((Player) sender, land);
            return;
        }

        if (sender.hasPermission("k.invites")) {
            // Check if user is in an land
            Player CMDSender = (Player) sender;
            if (!plugin.SQLSelect.player_exists(CMDSender)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan alleen invites bekijken als je geregistreerd bent in onze database!"));
                return;
            }

            Speler speler = plugin.SQLSelect.player_get_by_name(CMDSender.getName());

            if (speler == null || speler.getLand() == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cU kunt alleen de invites bekijken als u lid bent van een land!"));
                return;
            }

            Land land = plugin.SQLSelect.land_get_by_player(speler);
            this.createGUI(CMDSender.getPlayer(), land);
        }
    }

    private void createGUI(Player player, Land land) {
        if (player instanceof Player) {
            Integer rankCount = land.getInvites().size();
            double Chestvalue = 9+9*Math.ceil(rankCount.floatValue()/9);
            Integer GUISize = (int) Chestvalue;

            Inventory gui = Bukkit.getServer().createInventory(player, GUISize, land.getName()+" invites");

            // Create glass panes
            this.createGlasspanes(gui, GUISize);

            // Create rank stacks
            Integer i = 0;
            for (Invite invite : land.getInvites()) {
                // Create shield
                ItemStack JukeBox = new ItemStack(Material.JUKEBOX);

                Speler speler = plugin.SQLSelect.player_get_by_uuid(invite.getPlayer());

                ItemMeta MetaJukeBox = JukeBox.getItemMeta();
                ArrayList<String> JukeBoxLore = new ArrayList<String>();
                MetaJukeBox.setLore(JukeBoxLore);
                MetaJukeBox.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

                JukeBox.setItemMeta(MetaJukeBox);
                MetaJukeBox.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&lInvite &2&l"+speler.getName()));

                JukeBox.setItemMeta(MetaJukeBox);

                gui.setItem(i, JukeBox);
                i++;
            }

            // Create close button
            this.createCloseButton(gui, GUISize);

            player.openInventory(gui);
        }
    }

    private void createGlasspanes(Inventory gui, Integer GUISize) {
        // Create glass panes
        ItemStack GlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);

        ItemMeta MetaGlassPane = GlassPane.getItemMeta();
        ArrayList<String> GlassLore = new ArrayList<String>();
        MetaGlassPane.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        GlassPane.setItemMeta(MetaGlassPane);
        MetaGlassPane.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));

        GlassPane.setItemMeta(MetaGlassPane);

        for (int i = 0; i < GUISize; i++) {
            gui.setItem(i, GlassPane);
        }
    }

    private void createCloseButton(Inventory gui, Integer GUISize) {
        // Create glass pane
        ItemStack GlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

        ItemMeta MetaGlassPane = GlassPane.getItemMeta();
        ArrayList<String> GlassLore = new ArrayList<String>();
        MetaGlassPane.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        GlassPane.setItemMeta(MetaGlassPane);
        MetaGlassPane.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lSluiten"));

        GlassPane.setItemMeta(MetaGlassPane);

        gui.setItem(GUISize-5, GlassPane);
    }
}
