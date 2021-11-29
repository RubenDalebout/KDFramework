package io.github.deechtezeeuw.kdframework.Upgrade;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tier {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public Tier(KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;
    }

    // Arguments check
    public Boolean arguments() {
        if (args.length == 1 && args[0].equalsIgnoreCase("tier"))
            return true;
        if (args.length != 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" "+args[0]+" <upgrade/downgrade> <kingdom>&c!"));
            return false;
        }
        if (!args[1].equalsIgnoreCase("upgrade") && !args[1].equalsIgnoreCase("downgrade")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" "+args[0]+" <upgrade/downgrade> <kingdom>&c!"));
            return false;
        }
        return true;
    }

    // Upgrade
    public void upgrade(String landString) {
        // Check if landString is kd
        if (!plugin.SQLSelect.land_exists(landString)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+landString+" &cbestaat niet!"));
            return;
        }

        // Get land
        Land land = plugin.SQLSelect.land_get_by_name(landString);

        Integer lowestTier = 0;
        Integer HighestTier = 0;
        for (String Tier : plugin.Config.getTierConfig().getConfigurationSection("tiers").getKeys(false)) {
            if (StringUtils.isNumeric(Tier))
                if (Integer.parseInt(Tier) > HighestTier) HighestTier = Integer.parseInt(Tier);
        }

        // Check if possible
        if (land.getTier() >= HighestTier || land.getTier() < lowestTier) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+land.getName()+" &ckan niet meer upgraden."));
            return;
        }

        Integer newTier = land.getTier()+1;

        List<Rank> oldRanks = land.getRanks();

        // Get all new ranks
        List<Rank> newRanks = new ArrayList<>();
        for (String key : plugin.Config.getTierConfig().getConfigurationSection("tiers."+newTier+".ranks").getKeys(false)) {
            String rank = key;
            Integer level = plugin.Config.getTierConfig().getInt("tiers."+newTier+".ranks."+rank+".level");
            Integer maximum = plugin.Config.getTierConfig().getInt("tiers."+newTier+".ranks."+rank+".maximum");
            String prefix = plugin.Config.getTierConfig().getString("tiers."+newTier+".ranks."+rank+".prefix");
            String tab = plugin.Config.getTierConfig().getString("tiers."+newTier+".ranks."+rank+".tab");
            Boolean rankDefault = plugin.Config.getTierConfig().getBoolean("tiers."+newTier+".ranks."+rank+".default");
            if (rankDefault)
                maximum = null;

            // Create rank
            UUID uuid = UUID.randomUUID();
            Rank newRank = new Rank(uuid, rank, level, maximum, prefix,tab, rankDefault);
            plugin.SQLInsert.rank_create(land.getUuid(), newRank);
            newRanks.add(newRank);
        }

        // Give all players new rank with same lever or lower
        for (Speler kdplayer : land.getLeden()) {
            if (kdplayer.getRank() == null) break;
            Rank kdpRank = plugin.SQLSelect.get_rank(kdplayer.getRank());
            // Loop through new ranks
            for (Rank newRank : newRanks) {
                if (newRank.getLevel() == kdpRank.getLevel() || newRank.getKdDefault()) {
                    // Set user as rank
                    plugin.SQLUpdate.update_player_rank(kdplayer.getUuid(), newRank.getUuid());
                    // Check if user is online to send him the message
                    if (Bukkit.getPlayer(kdplayer.getName()) != null) {
                        plugin.SpelerPerms.reload_permissions(Bukkit.getPlayer(kdplayer.getName()));
                        Bukkit.getPlayer(kdplayer.getName()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&aJe rank is verandert in &2&l"+newRank.getName()+"&a."));
                    }
                    break;
                }
            }
        }

        // Delete old ranks
        for (Rank oldRank : oldRanks) {
            plugin.SQLDelete.rank_delete(oldRank);
        }

        // Update maximum
        Integer max = plugin.Config.getTierConfig().getInt("tiers."+newTier+".maximum");
        plugin.SQLUpdate.update_land(land.getName(), "maximum", max.toString());
        // Update dynmap icon
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String command = "dmarker update id:kd-"+land.getName().toLowerCase()+" icon:"+plugin.Config.getTierConfig().getString("tiers."+newTier+".icon");
        Bukkit.dispatchCommand(console, command);
        // Update tier
        plugin.SQLUpdate.update_land(land.getName(), "tier", newTier.toString());

        // Send message to all people that the land is upgraded
        for (Speler kdplayer : land.getLeden()) {
            if (Bukkit.getPlayer(kdplayer.getName()) != null) {
                plugin.SpelerPerms.reload_permissions(Bukkit.getPlayer(kdplayer.getName()));
                Bukkit.getPlayer(kdplayer.getName()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aJe land is nu &2&ltier "+newTier+" &ageworden!"));
            }
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + "&aHet land &2&l"+land.getName()+" &ais nu &2&ltier "+newTier+"&a!"));
        return;
    }

    // Downgrade
    public void downgrade(String landString) {
        // Check if landString is kd
        if (!plugin.SQLSelect.land_exists(landString)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+landString+" &cbestaat niet!"));
            return;
        }

        // Get land
        Land land = plugin.SQLSelect.land_get_by_name(landString);

        Integer lowestTier = 0;
        Integer HighestTier = 0;
        for (String Tier : plugin.Config.getTierConfig().getConfigurationSection("tiers").getKeys(false)) {
            if (StringUtils.isNumeric(Tier))
                if (Integer.parseInt(Tier) > HighestTier) HighestTier = Integer.parseInt(Tier);
        }

        // Check if possible
        if (land.getTier() <= lowestTier) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+land.getName()+" &ckan niet meer downgraden."));
            return;
        }

        Integer newTier = land.getTier()-1;

        List<Rank> oldRanks = land.getRanks();

        // Get all new ranks
        List<Rank> newRanks = new ArrayList<>();
        if (newTier != 0) {
            for (String key : plugin.Config.getTierConfig().getConfigurationSection("tiers." + newTier + ".ranks").getKeys(false)) {
                String rank = key;
                Integer level = plugin.Config.getTierConfig().getInt("tiers." + newTier + ".ranks." + rank + ".level");
                Integer maximum = plugin.Config.getTierConfig().getInt("tiers." + newTier + ".ranks." + rank + ".maximum");
                String prefix = plugin.Config.getTierConfig().getString("tiers." + newTier + ".ranks." + rank + ".prefix");
                String tab = plugin.Config.getTierConfig().getString("tiers." + newTier + ".ranks." + rank + ".tab");
                Boolean rankDefault = plugin.Config.getTierConfig().getBoolean("tiers." + newTier + ".ranks." + rank + ".default");
                if (rankDefault)
                    maximum = null;

                // Create rank
                UUID uuid = UUID.randomUUID();
                Rank newRank = new Rank(uuid, rank, level, maximum, prefix, tab, rankDefault);
                plugin.SQLInsert.rank_create(land.getUuid(), newRank);
                newRanks.add(newRank);
            }
        } else {
            for (String key : plugin.Config.getLandConfig().getConfigurationSection("land.ranks").getKeys(false)) {
                String rank = key;
                Integer level = plugin.Config.getLandConfig().getInt("land.ranks." + rank + ".level");
                Integer maximum = plugin.Config.getLandConfig().getInt("land.ranks." + rank + ".maximum");
                String prefix = plugin.Config.getLandConfig().getString("land.ranks." + rank + ".prefix");
                String tab = plugin.Config.getLandConfig().getString("land.ranks." + rank + ".tab");
                Boolean rankDefault = plugin.Config.getLandConfig().getBoolean("land.ranks." + rank + ".default");
                if (rankDefault)
                    maximum = null;

                // Create rank
                UUID uuid = UUID.randomUUID();
                Rank newRank = new Rank(uuid, rank, level, maximum, prefix, tab, rankDefault);
                plugin.SQLInsert.rank_create(land.getUuid(), newRank);
                newRanks.add(newRank);
            }
        }

        // Give all players new rank with same lever or lower
        for (Speler kdplayer : land.getLeden()) {
            if (kdplayer.getRank() == null) break;
            Rank kdpRank = plugin.SQLSelect.get_rank(kdplayer.getRank());
            // Loop through new ranks
            for (Rank newRank : newRanks) {
                if (newRank.getLevel() == kdpRank.getLevel() || newRank.getKdDefault()) {
                    // Set user as rank
                    plugin.SQLUpdate.update_player_rank(kdplayer.getUuid(), newRank.getUuid());
                    // Check if user is online to send him the message
                    if (Bukkit.getPlayer(kdplayer.getName()) != null) {
                        plugin.SpelerPerms.reload_permissions(Bukkit.getPlayer(kdplayer.getName()));
                        Bukkit.getPlayer(kdplayer.getName()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&aJe rank is verandert in &2&l"+newRank.getName()+"&a."));
                    }
                    break;
                }
            }
        }

        // Delete old ranks
        for (Rank oldRank : oldRanks) {
            plugin.SQLDelete.rank_delete(oldRank);
        }

        // Update maximum
        if (newTier != 0) {
            // Update maximum
            Integer max = plugin.Config.getTierConfig().getInt("tiers."+newTier+".maximum");
            plugin.SQLUpdate.update_land(land.getName(), "maximum", max.toString());
            // Update dynmap icon
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            String command = "dmarker update id:kd-"+land.getName().toLowerCase()+" icon:"+plugin.Config.getTierConfig().getString("tiers."+newTier+".icon");
            Bukkit.dispatchCommand(console, command);
        } else {
            plugin.SQLUpdate.update_land(land.getName(), "maximum", plugin.Config.getLandConfig().getString("land.maximum"));
            // Update dynmap icon
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            String command = "dmarker update id:kd-"+land.getName().toLowerCase()+" icon:"+plugin.Config.getRegionConfig().getString("kingdom-region.dynmap.icon");
            Bukkit.dispatchCommand(console, command);
        }
        // Update tier
        plugin.SQLUpdate.update_land(land.getName(), "tier", newTier.toString());

        // Send message to all people that the land is upgraded
        for (Speler kdplayer : land.getLeden()) {
            if (Bukkit.getPlayer(kdplayer.getName()) != null) {
                plugin.SpelerPerms.reload_permissions(Bukkit.getPlayer(kdplayer.getName()));
                Bukkit.getPlayer(kdplayer.getName()).sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aJe land is nu &2&ltier "+newTier+" &ageworden!"));
            }
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + "&aHet land &2&l"+land.getName()+" &ais nu &2&ltier "+newTier+"&a!"));
        return;
    }

    public void gui() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe moet in-game zijn om de GUI te openen!"));
            return;
        }

        Integer tierCount = 1+plugin.Config.getTierConfig().getConfigurationSection("tiers").getKeys(false).size();
        double Chestvalue = 9+9*Math.ceil(tierCount.floatValue()/9);
        Integer GUISize = (int) Chestvalue;

        Inventory gui = Bukkit.getServer().createInventory((Player) sender, GUISize, "Tiers");

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

        Integer i = 0;

        // Create tier stacks
        ItemStack Sunflower = new ItemStack(Material.DOUBLE_PLANT, 1);

        ItemMeta MetaSunflower = Sunflower.getItemMeta();
        ArrayList<String> SunflowerLore = new ArrayList<String>();
        SunflowerLore.add(ChatColor.translateAlternateColorCodes('&',
                "&2&lCondities:"));
        SunflowerLore.add(ChatColor.translateAlternateColorCodes('&',
                "&cGeen condities."));
        MetaSunflower.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        MetaSunflower.setLore(SunflowerLore);
        Sunflower.setItemMeta(MetaSunflower);
        MetaSunflower.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8Tier 0"));

        Sunflower.setItemMeta(MetaSunflower);

        gui.setItem(i, Sunflower);
        i++;

        // Iterate through tiers
        for (String Tier : plugin.Config.getTierConfig().getConfigurationSection("tiers").getKeys(false)) {
            if (StringUtils.isNumeric(Tier)) {
                Integer number = Integer.parseInt(Tier);
                // Create tier stacks
                Sunflower = new ItemStack(Material.DOUBLE_PLANT, 1);

                MetaSunflower = Sunflower.getItemMeta();
                SunflowerLore = new ArrayList<String>();
                SunflowerLore.add(ChatColor.translateAlternateColorCodes('&',
                        "&2&lCondities:"));
                if (plugin.Config.getTierConfig().getStringList("tiers." + number + ".conditions").size() > 0) {
                    for (String conditie : plugin.Config.getTierConfig().getStringList("tiers." + number + ".conditions")) {
                        SunflowerLore.add(ChatColor.translateAlternateColorCodes('&',
                                "&a- "+conditie));
                    }
                } else {
                    SunflowerLore.add(ChatColor.translateAlternateColorCodes('&',
                            "&cGeen condities."));
                }
                MetaSunflower.setLore(SunflowerLore);
                MetaSunflower.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

                Sunflower.setItemMeta(MetaSunflower);
                MetaSunflower.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8Tier "+number));

                Sunflower.setItemMeta(MetaSunflower);

                gui.setItem(i, Sunflower);
                i++;
            }
        }

        ((Player) sender).getPlayer().openInventory(gui);

        // Create close button
        GlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

        MetaGlassPane = GlassPane.getItemMeta();
        GlassLore = new ArrayList<String>();
        MetaGlassPane.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        GlassPane.setItemMeta(MetaGlassPane);
        MetaGlassPane.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lSluiten"));

        GlassPane.setItemMeta(MetaGlassPane);

        gui.setItem(GUISize-5, GlassPane);

        ((Player) sender).getPlayer().openInventory(gui);
    }
}
