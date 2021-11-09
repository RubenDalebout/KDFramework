package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Rank.Rank;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteDelete {
    private KDFramework plugin;

    public SQLiteDelete(KDFramework plugin) {
        this.plugin = plugin;
    }

    // Land
    public void land_delete(String KDName) {
        String sql = "DELETE FROM lands WHERE Name = ?";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, KDName);
            // execute the delete statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Player

    // Rank
    public void rank_delete(Rank rank) {
        String sql = "DELETE FROM ranks WHERE UUID = ?";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, rank.getUuid().toString());
            // execute the delete statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
