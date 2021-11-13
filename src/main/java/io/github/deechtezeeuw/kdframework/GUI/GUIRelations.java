package io.github.deechtezeeuw.kdframework.GUI;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
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

public class GUIRelations {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public GUIRelations (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Check if user is in db
            if (!plugin.SQLSelect.player_exists(player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cWe kunnen niet toestaan dat u niet bestaat in onze database!"));
                return;
            }
            Speler speler = plugin.SQLSelect.player_get_by_uuid(player.getUniqueId());

            if (speler.getLand() == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cVoor deze actie moet je in een land zitten!"));
                return;
            }

            Land land = plugin.SQLSelect.land_get_by_player(speler);

            this.createGUI(player, land);
        }
    }

    private void createGUI(Player player, Land playerLand) {
        Integer landCount = plugin.SQLSelect.land_list().size();
        double Chestvalue = 9+9*Math.ceil(landCount.floatValue()/9);
        Integer GUISize = (int) Chestvalue;

        Inventory gui = Bukkit.getServer().createInventory(player, GUISize, "Relaties "+playerLand.getName());

        // Create glass panes
        this.createGlasspanes(gui, GUISize);

        // Create rank stacks
        Integer i = 0;
        for (Land land : plugin.SQLSelect.land_list()) {
            if (land.getName().equalsIgnoreCase(playerLand.getName()))
                continue;
            // Create shield
            ItemStack Flag = new ItemStack(Material.BANNER);

            ItemMeta MetaFlag = Flag.getItemMeta();
            ArrayList<String> FlagLore = new ArrayList<String>();
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aRelatie: &2&l"+land.get_relation_string(playerLand)));
            MetaFlag.setLore(FlagLore);
            MetaFlag.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

            Flag.setItemMeta(MetaFlag);
            MetaFlag.setDisplayName(ChatColor.translateAlternateColorCodes('&', land.getPrefix()));

            Flag.setItemMeta(MetaFlag);

            gui.setItem(i, Flag);
            i++;
        }

        // Create close button
        this.createCloseButton(gui, GUISize);

        player.openInventory(gui);
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
