package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SQLiteUpdate {
    private KDFramework plugin;

    public SQLiteUpdate(KDFramework plugin) {
        this.plugin = plugin;
    }

    // Land update
    public void update_land(String Kingdom, String Column, String Value) {
        String sql = null;
        if (Column.equalsIgnoreCase("prefix")) {
            sql = "UPDATE lands SET Prefix = ? WHERE Name = ?";
        }
        if (Column.equalsIgnoreCase("invite")) {
            sql = "UPDATE lands SET Invite = ? WHERE Name = ?";
        }
        if (Column.equalsIgnoreCase("maximum")) {
            sql = "UPDATE lands SET Maximum = ? WHERE Name = ?";
        }

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {

            // set the corresponding param
            if (Column.equalsIgnoreCase("prefix")) {
                pstmt.setString(1, Value);
            }
            if (Column.equalsIgnoreCase("invite")) {
                pstmt.setBoolean(1, Value.equalsIgnoreCase("true") ? true : false);
            }
            if (Column.equalsIgnoreCase("maximum")) {
                pstmt.setInt(1, Integer.parseInt(Value));
            }
            pstmt.setString(2, Kingdom);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Player update
    public void update_player(Player player) {
        String sql = "UPDATE players SET Name = ? WHERE UUID = ?";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, player.getName());
            pstmt.setString(2, player.getUniqueId().toString());
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Rank update
}
