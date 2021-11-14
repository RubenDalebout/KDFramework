package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.CreateLand;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
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
    public void rank_create(UUID Land, Rank rank) {
        String sql = "INSERT INTO "
                + "ranks(UUID, Land, Name, Prefix, Level, Maximum, KDDefault) "
                + "VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, Land.toString());
            pstmt.setString(3, rank.getName());
            pstmt.setString(4, rank.getPrefix());
            pstmt.setInt(5, rank.getLevel());
            if (rank.getMaximum() != null) {
                pstmt.setInt(6, rank.getMaximum());
            } else {
                pstmt.setString(6, null);
            }
            pstmt.setBoolean(7, rank.getKdDefault());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // Invite insert
    public void invite_create(Invite invite) {
        String sql = "INSERT INTO "
                + "invites(UUID, Land, Player) "
                + "VALUES(?,?,?)";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, invite.getUuid().toString());
            pstmt.setString(2, invite.getLand().toString());
            pstmt.setString(3, invite.getPlayer().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // Relationship
    public void relationship_create(Land land) {
        String sql = "INSERT INTO "
                + "relationships(UUID, Land) "
                + "VALUES(?,?)";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, land.getUuid().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // Relationship request
    public void relationship_request_create(Land land, Land other, String What) {
        String sql = "INSERT INTO "
                + "relationships_request(UUID, Land, Other, What) "
                + "VALUES(?,?,?,?)";

        try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, land.getUuid().toString());
            pstmt.setString(3, other.getUuid().toString());
            pstmt.setString(4, What);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
