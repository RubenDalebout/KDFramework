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
            String prefix = "&7[&cLand-Loos&7]";
            Speler speler = plugin.SQLSelect.player_get_by_name(p.getName());
            if (speler.getLand() != null) {
                Land land = plugin.SQLSelect.land_get_by_player(speler);
                prefix = land.getPrefix();
            }
            return prefix;
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

        return null;
    }
}
