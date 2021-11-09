package io.github.deechtezeeuw.kdframework.GUI;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
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

public class GUILands {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public GUILands (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            this.createGUI(player);
        }
    }

    private void createGUI(Player player) {
        Integer landCount = plugin.SQLSelect.land_list().size();
        double Chestvalue = 9+9*Math.ceil(landCount.floatValue()/9);
        Integer GUISize = (int) Chestvalue;

        Inventory gui = Bukkit.getServer().createInventory(player, GUISize, " Landen");

        // Create glass panes
        this.createGlasspanes(gui, GUISize);

        // Create rank stacks
        Integer i = 0;
        for (Land land : plugin.SQLSelect.land_list()) {
            // Create shield
            ItemStack Flag = new ItemStack(Material.BANNER);

            ItemMeta MetaFlag = Flag.getItemMeta();
            ArrayList<String> FlagLore = new ArrayList<String>();
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aLand: &2&l"+land.getName()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aLeiding: &2&l"+land.getLeiding()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aLeden: &2&l"+land.getLeden().size()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aMaximum: &2&l"+land.getMaximum()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aInvite: &2&l"+land.getInvite()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aDefault: &2&l"+land.get_defaultRank().getName()));
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
