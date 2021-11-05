package io.github.deechtezeeuw.kdframework.Configuraties;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Configuratie {

    // Global plugin variable
    private KDFramework plugin;

    // Configuration file general
    private String generalPrefix;
    private String generalChat;
    private String generalOnFirst;
    private String generalOnJoin;
    private String generalOnQuit;
    private String generalCReload;
    private String generalNoPerms;

    private File landConfigFile;
    private FileConfiguration landConfig;

    public Configuratie (KDFramework plugin) {
        this.plugin = plugin;
        createLandConfig();
        loadVariables();
    }

    // get Configuration file General
    public String getGeneralPrefix() {
        return this.generalPrefix + " &f";
    }

    public String getGeneralChat() {
        return this.generalChat;
    }

    public String getGeneralOnFirst() {
        return this.generalOnFirst;
    }

    public String getGeneralOnJoin() {
        return this.generalOnJoin;
    }

    public String getGeneralOnQuit() {
        return this.generalOnQuit;
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
        this.generalChat = plugin.getConfig().getString("general.settings.chat");
        this.generalOnFirst = plugin.getConfig().getString("general.messages.on-first");
        this.generalOnJoin = plugin.getConfig().getString("general.messages.on-join");
        this.generalOnQuit = plugin.getConfig().getString("general.messages.on-quit");
        this.generalCReload = plugin.getConfig().getString("general.messages.config-reload");
        this.generalNoPerms = plugin.getConfig().getString("general.messages.no-permission");
    }

    public FileConfiguration getLandConfig() {
        return this.landConfig;
    }

    private void createLandConfig() {
        landConfigFile = new File(plugin.getDataFolder(), "landConfig.yml");
        if (!landConfigFile.exists()) {
            landConfigFile.getParentFile().mkdirs();
            plugin.saveResource("landConfig.yml", false);
        }

        landConfig= new YamlConfiguration();
        try {
            landConfig.load(landConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    // Reload command
    public void reloadVariables(CommandSender sender) {
        plugin.reloadConfig();
        this.loadVariables();

        // Land template
        if(this.landConfigFile == null)
            this.landConfigFile = new File(this.plugin.getDataFolder(), "clansTemplate.yml");

        this.landConfig = YamlConfiguration.loadConfiguration(this.landConfigFile);
        InputStream defaultStream = this.plugin.getResource("clansTemplate.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.landConfig.setDefaults(defaultConfig);
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + plugin.Config.getGeneralCReload()));
    }

    // No permissions
    public void noPermission(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + plugin.Config.getGeneralNoPerms()));
    }
}
