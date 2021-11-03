package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteInsert {
    private KDFramework plugin;

    public SQLiteInsert(KDFramework plugin) {
        this.plugin = plugin;
    }

    // Land insert

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
