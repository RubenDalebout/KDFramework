package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;

import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteInstall {
    private KDFramework plugin;

    public SQLiteInstall(KDFramework plugin) {
        this.plugin = plugin;
    }

    public void createTables() {
        this.table_player();
        this.table_landen();
        this.table_ranks();
    }

    private void table_player() {
        String sql = "CREATE TABLE IF NOT EXISTS players (\n"
                + "	UUID char(36) PRIMARY KEY,\n"
                + " Name char(36) NOT NULL,\n"
                + " Land char(36),\n"
                + " Rank char(36)\n"
                + ");";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return;
    }

    public void table_landen() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS lands (\n"
                + "	UUID char(36) PRIMARY KEY,\n"
                + "	Name varchar(16) NOT NULL,\n"
                + "	Prefix varchar(36) NOT NULL,\n"
                + "	Invite tinyint(1) NOT NULL,\n"
                + "	Maximum integer(100) NOT NULL,\n"
                + " Spawn varchar(25)\n"
                + ");";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return;
    }

    public void table_ranks() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS ranks (\n"
                + "	UUID char(36) PRIMARY KEY,\n"
                + " Land char(36) NOT NULL,\n"
                + "	Name varchar(16) NOT NULL,\n"
                + "	Prefix varchar(36) NOT NULL,\n"
                + "	Level integer(100) NOT NULL,\n"
                + "	Maximum integer(100),\n"
                + " KDDefault tinyint(1) NOT NULL\n"
                + ");";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return;
    }

}
