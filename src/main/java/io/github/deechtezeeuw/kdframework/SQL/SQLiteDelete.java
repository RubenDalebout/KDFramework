package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Speler.Speler;

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


    // Invite
    public void invite_delete(Invite invite) {
        String sql = "DELETE FROM invites WHERE UUID = ?";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, invite.getUuid().toString());
            // execute the delete statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void invite_delete_player(Speler speler) {
        String sql = "DELETE FROM invites WHERE Player = ?";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, speler.getUuid().toString());
            // execute the delete statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
