package io.github.deechtezeeuw.kdframework.GUI;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GUI {
    public void information(Player player) {
        Integer landCount = KDFramework.getInstance().SQLSelect.land_list().size();
        double Chestvalue = 9+9*Math.ceil(landCount.floatValue()/9);
        Integer GUISize = (int) Chestvalue;

        Inventory gui = Bukkit.getServer().createInventory(player, GUISize, "Staff informatie");

        // Create glass panes
        this.createGlasspanes(gui);

        // Create rank stacks
        Integer i = 0;
        for (Land land : KDFramework.getInstance().SQLSelect.land_list()) {
            // Create shield
            ItemStack Flag = new ItemStack(Material.BANNER);

            ItemMeta MetaFlag = Flag.getItemMeta();
            ArrayList<String> FlagLore = new ArrayList<String>();
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aLand: &2&l"+land.getName()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aTab: &2&l"+land.getTab()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aPrefix: &2&l"+land.getPrefix()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aTier: &2&l"+land.getTier()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aLeden: &2&l"+land.getLeden().size()+"&a/&2&l"+land.getMaximum()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aInvite: &2&l"+land.getInvite()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aKolonies: &2&l"+land.getColony().size()+"&a/&2&l"+land.getColonyMax()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aLeider: &2&l"+land.getLeiding()));
            FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&aRanks:"));
            for (Rank rank : land.getRanks()) {
                FlagLore.add(ChatColor.translateAlternateColorCodes('&', "&a"+rank.getName()+": &2&l"+rank.getPrefix()));
            }

            MetaFlag.setLore(FlagLore);
            MetaFlag.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

            Flag.setItemMeta(MetaFlag);
            MetaFlag.setDisplayName(ChatColor.translateAlternateColorCodes('&', land.getName()));

            Flag.setItemMeta(MetaFlag);

            gui.setItem(i, Flag);
            i++;
        }

        // Create close button
        this.closeButton(gui);

        player.openInventory(gui);
    }

    private void createGlasspanes(Inventory gui) {
        // Create glass panes
        ItemStack GlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);

        ItemMeta MetaGlassPane = GlassPane.getItemMeta();
        ArrayList<String> GlassLore = new ArrayList<String>();
        MetaGlassPane.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        GlassPane.setItemMeta(MetaGlassPane);
        MetaGlassPane.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));

        GlassPane.setItemMeta(MetaGlassPane);

        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, GlassPane);
        }
    }

    private void closeButton(Inventory gui) {
        // Create glass pane
        ItemStack GlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

        ItemMeta MetaGlassPane = GlassPane.getItemMeta();
        ArrayList<String> GlassLore = new ArrayList<String>();
        MetaGlassPane.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        GlassPane.setItemMeta(MetaGlassPane);
        MetaGlassPane.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lSluiten"));

        GlassPane.setItemMeta(MetaGlassPane);

        gui.setItem(gui.getSize()-5, GlassPane);
    }
}
