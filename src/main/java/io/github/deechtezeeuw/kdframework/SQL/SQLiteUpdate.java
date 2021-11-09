package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
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
        return;
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
        return;
    }

    public void update_player_land(UUID uuid, UUID Land) {
        String sql = "UPDATE players SET Land = ? WHERE UUID = ?";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {

            // set the corresponding param
            if (Land != null) {
                pstmt.setString(1, Land.toString());
            } else {
                pstmt.setString(1, null);
            }
            pstmt.setString(2, uuid.toString());
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return;
    }

    // Rank update
    public void update_player_rank(UUID uuid, UUID Rank) {
        String sql = "UPDATE players SET Rank = ? WHERE UUID = ?";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {

            // set the corresponding param
            if (Rank != null) {
                pstmt.setString(1, Rank.toString());
            } else {
                pstmt.setString(1, null);
            }
            pstmt.setString(2, uuid.toString());
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return;
    }

    public void update_rank(Rank rank, String Column, String newValue) {
        if (Column.equalsIgnoreCase("default")) {
            String sql = "UPDATE ranks SET KDDefault = ? WHERE UUID = ?";
            try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
                pstmt.setBoolean(1, newValue.equalsIgnoreCase("true") ? true : false);
                pstmt.setString(2, rank.getUuid().toString());

                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return;
    }
}
