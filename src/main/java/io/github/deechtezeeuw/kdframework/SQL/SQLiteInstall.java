package io.github.deechtezeeuw.kdframework.SQL;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;

import java.sql.*;
import java.util.UUID;

public class SQLiteInstall {
    private KDFramework plugin;

    public SQLiteInstall(KDFramework plugin) {
        this.plugin = plugin;
    }

    public void createTables() {
        this.table_player();
        this.table_landen();
        this.table_landen_add_column("Tier", "integer(1)", "default(0)");
        this.table_landen_add_column("Tab", "varchar(16)", "");
        this.table_ranks();
        this.table_invites();
        this.table_relations();
        this.table_relations_add_columns();
        this.table_relations_request();
    }

    private void table_player() {
        String sql = "CREATE TABLE IF NOT EXISTS players (\n"
                + "	UUID char(36) PRIMARY KEY,\n"
                + " Name char(36) NOT NULL,\n"
                + " Land char(36),\n"
                + " Rank char(36)\n"
                + ");";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return;
    }

    private void table_landen() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS lands (\n"
                + "	UUID char(36) PRIMARY KEY,\n"
                + "	Name varchar(16) NOT NULL,\n"
                + "	Prefix varchar(36) NOT NULL,\n"
                + "	Invite tinyint(1) NOT NULL,\n"
                + "	Maximum integer(100) NOT NULL,\n"
                + " Spawn varchar(25)\n"
                + ");";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return;
    }

    public void table_landen_add_column(String column, String type, String SDefault) {
        try {
            DatabaseMetaData md = plugin.SQL.getConnection().getMetaData();
            ResultSet rs = md.getColumns(null, null, "lands", column);

            if (rs.next()) {
                return;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String sql = "ALTER TABLE lands ADD COLUMN "+column+" "+type+" "+SDefault;

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void table_ranks() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS ranks (\n"
                + "	UUID char(36) PRIMARY KEY,\n"
                + " Land char(36) NOT NULL,\n"
                + "	Name varchar(16) NOT NULL,\n"
                + "	Prefix varchar(36) NOT NULL,\n"
                + "	Level integer(100) NOT NULL,\n"
                + "	Maximum integer(100),\n"
                + " KDDefault tinyint(1) NOT NULL\n"
                + ");";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
            table_ranks_add_column("Tab", "varchar(10)", null);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return;
    }

    public void table_ranks_add_column(String column, String type, String DF) {
        try {
            DatabaseMetaData md = plugin.SQL.getConnection().getMetaData();
            ResultSet rs = md.getColumns(null, null, "ranks", column);

            if (rs.next()) {
                return;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String sql = "ALTER TABLE ranks ADD COLUMN "+column+" "+type+" default("+DF+")";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void table_invites() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS invites (\n"
                + " UUID char(36) PRIMARY KEY,\n"
                + " Land char(36) NOT NULL,\n"
                + " Player char(36) NOT NULL\n"
                + ");";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void table_relations() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS relationships (\n"
                + " UUID char(36) PRIMARY KEY,\n"
                + " Land char(36) NOT NULL\n"
                + ");";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void table_relations_add_columns() {
        for (Land land : plugin.SQLSelect.land_list()) {
            table_relations_add_row(land);
            try {
                DatabaseMetaData md = plugin.SQL.getConnection().getMetaData();
                ResultSet rs = md.getColumns(null, null, "relationships", land.getName());

                if (!rs.next()) {
                    this.table_relations_add_column(land);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void table_relations_add_column(Land land) {
        try {
            DatabaseMetaData md = plugin.SQL.getConnection().getMetaData();
            ResultSet rs = md.getColumns(null, null, "relationships", land.getName());

            if (rs.next()) {
                return;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String sql = "ALTER TABLE relationships ADD COLUMN "+land.getName().toLowerCase()+" integer(1) default(0)";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void table_relations_add_row(Land land) {
        String sql = "SELECT * "
                + "FROM relationships WHERE Land == ?";

        try (PreparedStatement pstmt  = plugin.SQL.getConnection().prepareStatement(sql)){
            // set the value
            pstmt.setString(1, land.getUuid().toString());
            ResultSet results  = pstmt.executeQuery();

            if (!results.next()) {
                plugin.SQLInsert.relationship_create(land);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void table_relations_request() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS relationships_request (\n"
                + " UUID char(36) PRIMARY KEY,\n"
                + " Land char(36) NOT NULL,\n"
                + " Other char(36) NOT NULL,\n"
                + " What varchar(16) NOT NULL\n"
                + ");";

        try (Statement stmt = plugin.SQL.getConnection().createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
