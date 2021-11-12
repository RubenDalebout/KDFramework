package io.github.deechtezeeuw.kdframework;

import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceHolders extends PlaceholderExpansion {
    private KDFramework plugin;

    public PlaceHolders (KDFramework plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "kdf";
    }

    @Override
    public String getAuthor() {
        return "DeZeeuw";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null) {
            return "";
        }

        // kingdom prefix

        if (params.equalsIgnoreCase("land_prefix")) {
            String prefix = "";
            Speler speler = plugin.SQLSelect.player_get_by_name(p.getName());
            if (speler != null) {
                if (speler.getLand() != null) {
                    Land land = plugin.SQLSelect.land_get_by_player(speler);
                    prefix = land.getPrefix();
                }
            }
            return prefix;
        }

        if (params.equalsIgnoreCase("land")) {
            String name = "";
            Speler speler = plugin.SQLSelect.player_get_by_name(p.getName());
            if (speler != null) {
                if (speler.getLand() != null) {
                    Land land = plugin.SQLSelect.land_get_by_player(speler);
                    name = land.getName();
                }
            }
            return name;
        }

        // rank prefix
        if (params.equalsIgnoreCase("rank_prefix")) {
            String prefix = "";
            Speler speler = plugin.SQLSelect.player_get_by_name(p.getName());
            if (speler.getRank() != null) {
                Rank rank = plugin.SQLSelect.get_rank(speler.getRank());
                prefix = rank.getPrefix();
            }
            return prefix;
        }

        if (params.equalsIgnoreCase("rank")) {
            String name = "";
            Speler speler = plugin.SQLSelect.player_get_by_name(p.getName());
            if (speler.getRank() != null) {
                Rank rank = plugin.SQLSelect.get_rank(speler.getRank());
                name = rank.getName();
            }
            return name;
        }

        return null;
    }
}
