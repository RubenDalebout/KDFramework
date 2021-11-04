package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteSelect {
    private KDFramework plugin;

    public SQLiteSelect(KDFramework plugin) {
        this.plugin = plugin;
    }

    // Land selects
    public boolean land_exists(String land) {
        String sql = "SELECT * "
                + "FROM lands WHERE Name == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){
            // set the value
            pstmt.setString(1, land);
            ResultSet results  = pstmt.executeQuery();

            if (results.next()) {
                // land is found
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public List<Land> land_list() {
        List<Land> lands = new ArrayList<>();

        String sql = "SELECT * "
                + "FROM lands";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){
            // set the value
            ResultSet results  = pstmt.executeQuery();

            while(results.next()) {
                Land land = new Land(results.getString("Name"),
                        results.getString("Prefix"),
                        results.getBoolean("Invite"),
                        results.getInt("Maximum"));
                lands.add(land);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lands;
    }

    public Land land_get_by_name(String KDName) {
        Land land = null;
        String sql = "SELECT * "
                + "FROM lands WHERE Name == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){
            // set the value
            pstmt.setString(1, KDName);
            ResultSet results  = pstmt.executeQuery();

            while(results.next()) {
                land = new Land(results.getString("Name"),
                        results.getString("Prefix"),
                        results.getBoolean("Invite"),
                        results.getInt("Maximum"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return land;
    }

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

    // Rank selects
}
