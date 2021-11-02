package io.github.deechtezeeuw.kdframework.Configuraties;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Configuratie {

    // Global plugin variable
    private KDFramework plugin;

    // Configuration file general
    private String generalPrefix;
    private String generalCReload;
    private String generalNoPerms;

    public Configuratie (KDFramework plugin) {
        this.plugin = plugin;
        loadVariables();
    }

    // get Configuration file General
    public String getGeneralPrefix() {
        return this.generalPrefix + " &f";
    }

    public String getGeneralCReload() {
        return this.generalCReload;
    }

    public String getGeneralNoPerms() {
        return this.generalNoPerms;
    }

    private void loadVariables() {

        // Configuration file general
        this.generalPrefix = plugin.getConfig().getString("general.settings.prefix");
        this.generalCReload = plugin.getConfig().getString("general.messages.config-reload");
        this.generalNoPerms = plugin.getConfig().getString("general.messages.no-permission");
    }

    // Reload command
    public void reloadVariables(CommandSender sender) {
        plugin.reloadConfig();
        this.loadVariables();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + plugin.Config.getGeneralCReload()));
    }

    // No permissions
    public void noPermission(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + plugin.Config.getGeneralNoPerms()));
    }
}
