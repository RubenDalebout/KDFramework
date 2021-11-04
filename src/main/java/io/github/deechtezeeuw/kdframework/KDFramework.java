package io.github.deechtezeeuw.kdframework;

import io.github.deechtezeeuw.kdframework.Commands.KingdomCommand;
import io.github.deechtezeeuw.kdframework.Configuraties.Configuratie;
import io.github.deechtezeeuw.kdframework.Events.PlayerEvents;
import io.github.deechtezeeuw.kdframework.Events.PlayerTabComplete;
import io.github.deechtezeeuw.kdframework.SQL.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
