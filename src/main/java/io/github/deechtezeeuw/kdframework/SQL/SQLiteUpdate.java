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
