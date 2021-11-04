package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.CreateLand;
import io.github.deechtezeeuw.kdframework.Land.Land;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SQLiteInsert {
    private KDFramework plugin;

    public SQLiteInsert(KDFramework plugin) {
        this.plugin = plugin;
    }

    // Land insert
    public void land_create(Land land) {
        String sql = "INSERT INTO "
                + "lands(UUID, Name, Prefix, Invite, Maximum) "
                + "VALUES(?,?,?,?,?)";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, land.getName());
            pstmt.setString(3, land.getPrefix());
            pstmt.setBoolean(4, land.getInvite());
            pstmt.setInt(5, land.getMaximum());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Player insert
    public void player_create(Player player) {
        String sql = "INSERT INTO "
                + "players(UUID, Name) "
                + "VALUES(?,?)";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, player.getUniqueId().toString());
            pstmt.setString(2, player.getName());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Rank insert
}