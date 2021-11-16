package io.github.deechtezeeuw.kdframework;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.github.deechtezeeuw.kdframework.Commands.KingdomCommand;
import io.github.deechtezeeuw.kdframework.Configuraties.Configuratie;
import io.github.deechtezeeuw.kdframework.Events.PlayerEvents;
import io.github.deechtezeeuw.kdframework.Events.PlayerTabComplete;
import io.github.deechtezeeuw.kdframework.GUI.GUIJoin;
import io.github.deechtezeeuw.kdframework.SQL.*;
import io.github.deechtezeeuw.kdframework.Scoreboard.Sidebar;
import io.github.deechtezeeuw.kdframework.Speler.SpelerPermissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public final class KDFramework extends JavaPlugin {

    // Global calls
    public Logger log = Logger.getLogger("Minecraft");
    public Configuratie Config;

    // SQLite
    public SQLite SQL;
    public SQLiteInstall SQLInstall;
    public SQLiteInsert SQLInsert;
    public SQLiteSelect SQLSelect;
    public SQLiteUpdate SQLUpdate;
    public SQLiteDelete SQLDelete;

    // Placeholders
    public PlaceHolders KDPlaceholders;
    // Worldguard
    public WorldGuardPlugin worldGuardPlugin;
    public WorldGuarding worldGuarding;
    // Worldedit
    public WorldEditPlugin worldEditPlugin;
    // Permissions
    public SpelerPermissions SpelerPerms;
    // Sidebar
    private static KDFramework instance;
    public Sidebar sidebar = new Sidebar();
    // GUIs
    public GUIJoin guiJoin;
    // Cooldown
    public HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile + " Version: " + pdfFile.getVersion() + " is now enabled");

        // Create plugin folder
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        // Create data folder
        File dataFolder = new File(this.getDataFolder()+"/data");
        if (!dataFolder.exists()) dataFolder.mkdir();

        // Create configuration
        this.saveDefaultConfig();

        // Create Sqlite connection
        this.SQL = new SQLite();
        this.SQLInstall = new SQLiteInstall(this);
        this.SQLInsert = new SQLiteInsert(this);
        this.SQLSelect = new SQLiteSelect(this);
        this.SQLUpdate = new SQLiteUpdate(this);
        this.SQLDelete = new SQLiteDelete(this);

        // Try connection
        try {
            SQL.connect();
        } catch (Exception e) {
            Bukkit.getLogger().info("Database is not connected");
        }

        // Check if connected
        if (SQL.isConnected()) {
            Bukkit.getLogger().info("Database is connected!");
            SQLInstall.createTables();
        }

        // Save config in variable
        this.Config = new Configuratie(this);

         // Register commands
        this.getCommand("kingdom").setExecutor(new KingdomCommand(this));
        this.getCommand("kingdom").setTabCompleter(new PlayerTabComplete(this));

        // Register events
        this.getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.KDPlaceholders = new PlaceHolders(this);
            KDPlaceholders.register();
        }

        worldGuardPlugin = getWorldGuard();
        worldEditPlugin = getWorldEdit();
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            this.worldGuarding = new WorldGuarding(this);
        }

        this.SpelerPerms = new SpelerPermissions(this);
        this.guiJoin = new GUIJoin(this);

        instance = this;

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                KDFramework.getInstance().sidebar.setSidebar(player);
                KDFramework.getInstance().sidebar.runnable(player);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static KDFramework getInstance() {
        return instance;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) plugin;
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldEdit");

        if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
            return null;
        }

        return (WorldEditPlugin) plugin;
    }
}
