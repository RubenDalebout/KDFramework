package io.github.deechtezeeuw.kdframework;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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

        if (params.equalsIgnoreCase("tab_land")) {
            String tab = "";
            Speler speler = plugin.SQLSelect.player_get_by_uuid(p.getUniqueId());
            if (speler!=null) {
                if (speler.getLand()!=null) {
                    Land land = plugin.SQLSelect.land_get_by_player(speler);
                    if (land.getTab() != null)
                        tab = land.getTab();
                }
            }
            return tab;
        }

        if (params.equalsIgnoreCase("tab_rank")) {
            String tab = "";
            Speler speler = plugin.SQLSelect.player_get_by_uuid(p.getUniqueId());
            if (speler!=null) {
                if (speler.getRank()!=null) {
                    Rank rank = plugin.SQLSelect.get_rank(speler.getRank());
                    if (rank.getTab() != null)
                        tab = rank.getTab();
                }
            }
            return tab;
        }

        // rank prefix command
        if (params.equalsIgnoreCase("rank_prefix")) {
            String prefix = "";
            Speler speler = plugin.SQLSelect.player_get_by_uuid(p.getUniqueId());
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

        if (params.equalsIgnoreCase("region")) {
            LocalPlayer localPlayer = plugin.worldGuardPlugin.wrapPlayer(p);
            Vector playerVector = localPlayer.getPosition();
            RegionManager regionManager = plugin.worldGuardPlugin.getRegionManager(p.getWorld());
            ApplicableRegionSet applicableRegionSet = regionManager.getApplicableRegions(playerVector);

            for (ProtectedRegion regions : applicableRegionSet) {
                if (regions.contains(playerVector)) {
                    String name = regions.getId();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    return name;
                }
            }

            if (applicableRegionSet.size() == 0) {
                return "Wildernis";
            }
        }

        return null;
    }
}
