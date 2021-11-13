package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                Land land = new Land(
                        UUID.fromString(results.getString("UUID")),
                        results.getString("Name"),
                        results.getString("Prefix"),
                        results.getBoolean("Invite"),
                        results.getInt("Maximum"),
                        results.getString("Spawn"),
                        plugin.SQLSelect.land_leden(UUID.fromString(results.getString("UUID"))),
                        plugin.SQLSelect.ranks_list(UUID.fromString(results.getString("UUID"))),
                        plugin.SQLSelect.invite_get_from_land(UUID.fromString(results.getString("UUID")))
                );
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
                land = new Land(
                        UUID.fromString(results.getString("UUID")),
                        results.getString("Name"),
                        results.getString("Prefix"),
                        results.getBoolean("Invite"),
                        results.getInt("Maximum"),
                        results.getString("Spawn"),
                        plugin.SQLSelect.land_leden(UUID.fromString(results.getString("UUID"))),
                        plugin.SQLSelect.ranks_list(UUID.fromString(results.getString("UUID"))),
                        plugin.SQLSelect.invite_get_from_land(UUID.fromString(results.getString("UUID")))
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return land;
    }

    public List<Speler> land_leden(UUID land) {
        List<Speler> Spelers = new ArrayList<>();

        String sql = "SELECT * "
                + "FROM players WHERE Land == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){
            // set the value
            pstmt.setString(1, land.toString());
            ResultSet results  = pstmt.executeQuery();

            while(results.next()) {
                UUID uuid = UUID.fromString(results.getString("UUID"));
                String name = results.getString("Name");
                UUID landid = null;
                if (results.getString("Land") != null) {
                    landid = UUID.fromString(results.getString("Land"));
                }
                UUID rank = null;
                if (results.getString("Rank") != null) {
                    rank = UUID.fromString(results.getString("Rank"));
                }

                Spelers.add(new Speler(uuid,name,landid,rank));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return Spelers;
    }

    public Land land_get_by_player(Speler speler) {
        Land land = null;

        String sql = "SELECT * "
                + "FROM lands WHERE UUID == ?";

        if (speler.getLand() != null) {
            try (PreparedStatement pstmt = plugin.SQL.getConnection().prepareStatement(sql)) {
                // set the value
                pstmt.setString(1, speler.getLand().toString());
                ResultSet results = pstmt.executeQuery();

                while (results.next()) {
                    land = new Land(
                            UUID.fromString(results.getString("UUID")),
                            results.getString("Name"),
                            results.getString("Prefix"),
                            results.getBoolean("Invite"),
                            results.getInt("Maximum"),
                            results.getString("Spawn"),
                            plugin.SQLSelect.land_leden(UUID.fromString(results.getString("UUID"))),
                            plugin.SQLSelect.ranks_list(UUID.fromString(results.getString("UUID"))),
                            plugin.SQLSelect.invite_get_from_land(UUID.fromString(results.getString("UUID")))
                    );
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
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

    public boolean player_exists_name(String player) {
        String sql = "SELECT * "
                + "FROM players WHERE Name == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){

            // set the value
            pstmt.setString(1, player);
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

    public Speler player_get_by_name(String Name) {
        Speler speler = null;

        String sql = "SELECT * "
                + "FROM players WHERE Name == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){

            // set the value
            pstmt.setString(1, Name);
            ResultSet results  = pstmt.executeQuery();

            while (results.next()) {
                UUID uuid = UUID.fromString(results.getString("UUID"));
                String name = results.getString("Name");
                UUID land = null;
                if (results.getString("Land") != null) {
                    land = UUID.fromString(results.getString("Land"));
                }
                UUID rank = null;
                if (results.getString("Rank") != null) {
                    rank = UUID.fromString(results.getString("Rank"));
                }

                speler = new Speler(uuid,name,land,rank);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return speler;
    }

    public Speler player_get_by_uuid(UUID id) {
        Speler speler = null;

        String sql = "SELECT * "
                + "FROM players WHERE UUID == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){

            // set the value
            pstmt.setString(1, id.toString());
            ResultSet results  = pstmt.executeQuery();

            while (results.next()) {
                UUID uuid = UUID.fromString(results.getString("UUID"));
                String name = results.getString("Name");
                UUID land = null;
                if (results.getString("Land") != null) {
                    land = UUID.fromString(results.getString("Land"));
                }
                UUID rank = null;
                if (results.getString("Rank") != null) {
                    rank = UUID.fromString(results.getString("Rank"));
                }

                speler = new Speler(uuid,name,land,rank);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return speler;
    }

    // Rank selects
    public Rank get_rank(UUID rank) {
        Rank returnRank = null;

        String sql = "SELECT * "
                + "FROM ranks WHERE UUID == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){
            // set the value
            pstmt.setString(1, rank.toString());
            ResultSet results  = pstmt.executeQuery();

            while(results.next()) {
                returnRank = new Rank(
                        UUID.fromString(results.getString("UUID")),
                        results.getString("Name"),
                        results.getInt("Level"),
                        results.getInt("Maximum"),
                        results.getString("Prefix"),
                        results.getBoolean("KDDefault")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return returnRank;
    }

    public List<Rank> ranks_list(UUID Land) {
        List<Rank> ranks = new ArrayList<>();

        String sql = "SELECT * "
                + "FROM ranks WHERE Land == ? ORDER BY Level DESC";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){
            // set the value
            pstmt.setString(1, Land.toString());
            ResultSet results  = pstmt.executeQuery();

            while(results.next()) {
                Integer Maximum = null;
                if (results.getString("Maximum") != null)
                    Maximum = results.getInt("Maximum");
                Rank rank = new Rank(
                        UUID.fromString(results.getString("UUID")),
                        results.getString("Name"),
                        results.getInt("Level"),
                        Maximum,
                        results.getString("Prefix"),
                        results.getBoolean("KDDefault")
                );
                ranks.add(rank);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return ranks;
    }


    // Invites
    public List<Invite> invite_get_from_user(Speler speler) {
        List<Invite> invites = new ArrayList<>();
        String sql = "SELECT * "
                + "FROM invites WHERE Player == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){
            // set the value
            pstmt.setString(1, speler.getUuid().toString());
            ResultSet results  = pstmt.executeQuery();

            while(results.next()) {
                Invite invite = new Invite(
                        UUID.fromString(results.getString("UUID")) ,
                        UUID.fromString(results.getString("Land")),
                        UUID.fromString(results.getString("Player"))
                );
                invites.add(invite);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return invites;
    }

    public List<Invite> invite_get_from_land(UUID land) {
        List<Invite> invites = new ArrayList<>();
        String sql = "SELECT * "
                + "FROM invites WHERE Land == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){
            // set the value
            pstmt.setString(1, land.toString());
            ResultSet results  = pstmt.executeQuery();

            while(results.next()) {
                Invite invite = new Invite(
                        UUID.fromString(results.getString("UUID")) ,
                        UUID.fromString(results.getString("Land")),
                        UUID.fromString(results.getString("Player"))
                );
                invites.add(invite);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return invites;
    }
}
