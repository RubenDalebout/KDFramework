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

}
