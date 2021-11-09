package io.github.deechtezeeuw.kdframework.GUI;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GUIRanks {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public GUIRanks (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;
        this.checkArguments();
    }

    private void checkArguments() {
        if (sender.hasPermission("k.ranks.others") && args.length > 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " <kingdom>"));
            return;
        }

        if (sender.hasPermission("k.ranks.others") && args.length > 1) {
            if (!plugin.SQLSelect.land_exists(args[1])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&cHet land &4&l"+args[1]+" &cbestaat niet!"));
                return;
            }

            Land land = plugin.SQLSelect.land_get_by_name(args[1]);
            this.createGUI((Player) sender, land);
            return;
        }

        if (sender.hasPermission("k.ranks")) {
            // Check if user is in an land
            Player CMDSender = (Player) sender;
            if (!plugin.SQLSelect.player_exists(CMDSender)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan alleen ranks bekijken als je geregistreerd bent in onze database!"));
                return;
            }

            Speler speler = plugin.SQLSelect.player_get_by_name(CMDSender.getName());

            if (speler == null || speler.getLand() == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cU kunt alleen de ranks bekijken als u lid bent van een land!"));
                return;
            }

            Land land = plugin.SQLSelect.land_get_by_player(speler);
            this.createGUI(CMDSender.getPlayer(), land);
        }
    }

    private void createGUI(Player player, Land land) {
        if (player instanceof Player) {
            Integer rankCount = land.getRanks().size();
            double Chestvalue = 9+9*Math.ceil(rankCount.floatValue()/9);
            Integer GUISize = (int) Chestvalue;

            Inventory gui = Bukkit.getServer().createInventory(player, GUISize, land.getName()+" ranks");

            // Create glass panes
            this.createGlasspanes(gui, GUISize);

            // Create rank stacks
            Integer i = 0;
            for (Rank rank : land.getRanks()) {
                // Create shield
                ItemStack Shield = new ItemStack(Material.SHIELD);

                ItemMeta MetaShield = Shield.getItemMeta();
                ArrayList<String> ShieldLore = new ArrayList<String>();
                String Maximum = "Oneindig";
                if (rank.getMaximum() != null) {
                    Maximum = rank.getMaximum().toString();
                }
                ShieldLore.add(ChatColor.translateAlternateColorCodes('&', "&aRank: &2&l"+rank.getName()));
                ShieldLore.add(ChatColor.translateAlternateColorCodes('&', "&aLevel: &2&l"+rank.getLevel()));
                ShieldLore.add(ChatColor.translateAlternateColorCodes('&', "&aMaximum: &2&l"+Maximum));
                ShieldLore.add(ChatColor.translateAlternateColorCodes('&', "&aDefault: &2&l"+rank.getKdDefault()));
                MetaShield.setLore(ShieldLore);
                MetaShield.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

                Shield.setItemMeta(MetaShield);
                MetaShield.setDisplayName(ChatColor.translateAlternateColorCodes('&', rank.getPrefix()));

                Shield.setItemMeta(MetaShield);

                gui.setItem(i, Shield);
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
