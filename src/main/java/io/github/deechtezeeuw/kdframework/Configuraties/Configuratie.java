package io.github.deechtezeeuw.kdframework.Configuraties;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Configuratie {

    // Global plugin variable
    private KDFramework plugin;

    // Configuration file general
    private String generalPrefix;
    private ConfigurationSection generalChats;
    private String generalChat;
    private String generalWorld;
    private Location generalWorldSpawn;
    private Integer generalSpawnCooldown;
    private String generalOnFirst;
    private String generalOnJoin;
    private String generalOnQuit;
    private String generalCReload;
    private String generalNoPerms;

    // Scoreboard
    private String sidebarTitle;
    private String sidebarIP;
    private String sidebarColor;

    private File landConfigFile;
    private FileConfiguration landConfig;

    private File regionConfigFile;
    private FileConfiguration regionConfig;

    private File permissonConfigFile;
    private FileConfiguration permissionConfig;

    private File tierConfigFile;
    private FileConfiguration tierConfig;

    public Configuratie (KDFramework plugin) {
        this.plugin = plugin;
        createLandConfig();
        createRegionConfig();
        createPermissionConfig();
        createTierConfig();
        loadVariables();
    }

    // get Configuration file General
    public String getGeneralPrefix() {
        return this.generalPrefix + " &f";
    }

    public ConfigurationSection getGeneralChats() {
        return this.generalChats;
    }

    public String getGeneralChat() {
        return this.generalChat;
    }

    public String getGeneralWorld() {
        return this.generalWorld;
    }

    public Location getGeneralWorldSpawn() {
        return this.generalWorldSpawn;
    }

    public Integer getGeneralSpawnCooldown() {
        return this.generalSpawnCooldown;
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

    public String getSidebarTitle() {
        return this.sidebarTitle;
    }

    public String getSidebarIP() {
        return this.sidebarIP;
    }

    public String getSidebarColor() {
        return this.sidebarColor;
    }

    private void loadVariables() {

        // Configuration file general
        this.generalPrefix = plugin.getConfig().getString("general.settings.prefix");
        // Chat
        this.generalChats = plugin.getConfig().getConfigurationSection("general.settings.chats");

        this.generalChat = plugin.getConfig().getString("general.settings.chat");
        this.generalWorld = plugin.getConfig().getString("general.settings.world.name");
        // Spawn
        int posX = plugin.getConfig().getInt("general.settings.world.spawn.x");
        int posY = plugin.getConfig().getInt("general.settings.world.spawn.y");
        int posZ = plugin.getConfig().getInt("general.settings.world.spawn.z");
        float yaw = plugin.getConfig().getInt("general.settings.world.spawn.yaw");
        float pitch = plugin.getConfig().getInt("general.settings.world.spawn.pitch");
        this.generalWorldSpawn = new Location(Bukkit.getWorld(plugin.getConfig().getString("general.settings.world.name")), posX, posY, posZ, yaw, pitch);
        this.generalSpawnCooldown = plugin.getConfig().getInt("general.settings.world.spawn-delay");

        this.generalOnFirst = plugin.getConfig().getString("general.messages.on-first");
        this.generalOnJoin = plugin.getConfig().getString("general.messages.on-join");
        this.generalOnQuit = plugin.getConfig().getString("general.messages.on-quit");
        this.generalCReload = plugin.getConfig().getString("general.messages.config-reload");
        this.generalNoPerms = plugin.getConfig().getString("general.messages.no-permission");

        // Scoreboard
        this.sidebarTitle = plugin.getConfig().getString("general.scoreboard.title");
        this.sidebarIP = plugin.getConfig().getString("general.scoreboard.ip");
        this.sidebarColor = plugin.getConfig().getString("general.scoreboard.color");
    }

    public FileConfiguration getRegionConfig() { return this.regionConfig; }

    private void createRegionConfig() {
        regionConfigFile = new File(plugin.getDataFolder(), "regions.yml");
        if (!regionConfigFile.exists()) {
            regionConfigFile.getParentFile().mkdirs();
            plugin.saveResource("regions.yml", false);
        }

        regionConfig = new YamlConfiguration();
        try {
            regionConfig.load(regionConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
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

    public FileConfiguration getPermissionConfig() {
        return this.permissionConfig;
    }

    private void createPermissionConfig() {
        permissonConfigFile = new File(plugin.getDataFolder(), "permissions.yml");
        if (!permissonConfigFile.exists()) {
            permissonConfigFile.getParentFile().mkdirs();
            plugin.saveResource("permissions.yml", false);
        }

        permissionConfig= new YamlConfiguration();
        try {
            permissionConfig.load(permissonConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getTierConfig() {
        return this.tierConfig;
    }

    private void createTierConfig() {
        tierConfigFile = new File(plugin.getDataFolder(), "tiers.yml");
        if (!tierConfigFile.exists()) {
            tierConfigFile.getParentFile().mkdirs();
            plugin.saveResource("tiers.yml", false);
        }

        tierConfig = new YamlConfiguration();
        try {
            tierConfig.load(tierConfigFile);
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
        // regions template
        if (this.regionConfigFile == null)
            this.regionConfigFile = new File(this.plugin.getDataFolder(), "regions.yml");

        this.regionConfig = YamlConfiguration.loadConfiguration(this.landConfigFile);
        InputStream defaultStreamReg = this.plugin.getResource("regions.yml");
        if (defaultStreamReg != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStreamReg));
            this.regionConfig.setDefaults(defaultConfig);
        }

        // permission template
        if(this.permissonConfigFile == null)
            this.permissonConfigFile = new File(this.plugin.getDataFolder(), "permissions.yml");

        this.permissionConfig = YamlConfiguration.loadConfiguration(this.permissonConfigFile);
        InputStream defaultStreamPerms = this.plugin.getResource("permissions.yml");
        if (defaultStreamPerms != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStreamPerms));
            this.permissionConfig.setDefaults(defaultConfig);
        }

        // tier template
        if(this.tierConfigFile == null)
            this.tierConfigFile = new File(this.plugin.getDataFolder(), "tiers.yml");

        this.tierConfig = YamlConfiguration.loadConfiguration(this.tierConfigFile);
        InputStream defaultStreamTiers = this.plugin.getResource("tiers.yml");
        if (defaultStreamTiers != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStreamTiers));
            this.tierConfig.setDefaults(defaultConfig);
        }

        // Reset permissions of all online players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            plugin.SpelerPerms.reload_permissions(onlinePlayer);
            onlinePlayer.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            // Sidebar
            KDFramework.getInstance().sidebar.setSidebar(onlinePlayer);
            KDFramework.getInstance().sidebar.runnable(onlinePlayer);
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
