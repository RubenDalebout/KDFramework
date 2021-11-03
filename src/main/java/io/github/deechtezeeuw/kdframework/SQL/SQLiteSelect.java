package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteSelect {
    private KDFramework plugin;

    public SQLiteSelect(KDFramework plugin) {
        this.plugin = plugin;
    }

    // Land selects

    // Player selects
    public boolean player_exists(Player player) {
        String sql = "SELECT * "
                + "FROM players WHERE UUID == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){

            // set the value
            pstmt.setString(1, player.getUniqueId().toString());
            ResultSet results  = pstmt.executeQuery();

            if (results.next()) {
                // player is found
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void player_update() {

    }

    // Rank selects
}
