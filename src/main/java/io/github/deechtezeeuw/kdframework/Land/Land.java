package io.github.deechtezeeuw.kdframework.Land;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.SQL.SQLiteSelect;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class Land {
    private UUID uuid;
    private String name;
    private String prefix;
    private Boolean invite;
    private Integer maximum;
    private List<Speler> leden;
    private List<Rank> ranks;

    public Land(UUID Land_ID, String Land_Name, String Land_Prefix, Boolean Land_Invite, Integer Land_Maximum, List<Speler> Land_Leden, List<Rank> Land_Ranks) {
        this.uuid = Land_ID;
        this.name = Land_Name;
        this.prefix = Land_Prefix;
        this.invite = Land_Invite;
        this.maximum = Land_Maximum;
        this.leden = Land_Leden;
        this.ranks = Land_Ranks;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public Boolean getInvite() {
        return this.invite;
    }

    public Integer getMaximum() {
        return this.maximum;
    }

    public List<Speler> getLeden() {
        return this.leden;
    }

    public List<Rank> getRanks() {
        return this.ranks;
    }

    public String getLeiding() {
        String leiding = "&c&lGeen leiding";

        Rank highestRank = null;

        for (Rank ranks : getRanks()) {
            if (highestRank == null)
                highestRank = ranks;
            if (highestRank.getLevel() < ranks.getLevel())
                highestRank = ranks;
        }

        if (highestRank != null) {
            boolean first = true;
            for (Speler speler : getLeden()) {
                if (speler.getRank().equals(highestRank.getUuid())) {
                    if (first) {
                        leiding = "&2&l"+speler.getName();
                        first = false;
                    } else {
                        leiding += "&a, &2&l"+speler.getLand();
                    }
                }
            }
        }

        return leiding;
    }

    public Rank get_defaultRank() {
        Rank rank = null;

        for (Rank ranks : getRanks()) {
            if (ranks.getKdDefault() == true) {
                rank = ranks;
                break;
            }
        }

        return rank;
    }
}
