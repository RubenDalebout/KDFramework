package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {
    private File dataFolder = ((KDFramework) Bukkit.getPluginManager().getPlugin("KDFramework")).getDataFolder();
    private String url = "jdbc:sqlite:"+dataFolder.getAbsolutePath()+"/data/database.db";
    private Connection connection;

    // Check if there is an connection
    public boolean isConnected() {
        return (connection == null ? false : true);
    }

    // Connection function
    public void connect() {
        if (!isConnected()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Failed to load SQLite JDBC class", e);
            }

            try {
                // create a connection to the database
                connection = DriverManager.getConnection(url);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return;
    }

    // Disconnect function
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return;
    }

    public Connection getConnection() {
        return connection;
    }
}
