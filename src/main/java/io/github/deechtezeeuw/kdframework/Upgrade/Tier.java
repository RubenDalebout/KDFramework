package io.github.deechtezeeuw.kdframework.Upgrade;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
        if (land.getTier() < HighestTier && land.getTier() >= lowestTier) {
            Integer newTier = land.getTier()+1;
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&aUpgrade &2&l"+land.getName()+"&atier naar &2&l"+newTier+"&a."));
            return;
        }
    }

    // Downgrade
    public void downgrade(String landString) {

    }
}
