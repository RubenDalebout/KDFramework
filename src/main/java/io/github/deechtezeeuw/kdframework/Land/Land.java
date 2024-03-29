package io.github.deechtezeeuw.kdframework.Land;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Region.colony;
import io.github.deechtezeeuw.kdframework.SQL.SQLiteSelect;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Land {
    private UUID uuid;
    private String name;
    private String prefix;
    private Boolean invite;
    private Integer maximum;
    private String spawn;
    private Integer tier;
    private String tab;
    private List<Speler> leden;
    private List<Rank> ranks;
    private List<Invite> invites;

    public Land(UUID Land_ID, String Land_Name, String Land_Prefix, Boolean Land_Invite, Integer Land_Maximum, String Land_Spawn, Integer Land_Tier, List<Speler> Land_Leden, List<Rank> Land_Ranks, List<Invite> Land_Invites, String Land_Tab) {
        this.uuid = Land_ID;
        this.name = Land_Name;
        this.prefix = Land_Prefix;
        this.invite = Land_Invite;
        this.maximum = Land_Maximum;
        this.leden = Land_Leden;
        this.ranks = Land_Ranks;
        this.invites = Land_Invites;
        this.spawn = Land_Spawn;
        this.tier = Land_Tier;
        this.tab = Land_Tab;
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

    public String getSpawn() {
        return this.spawn;
    }

    public Integer getTier() {
        return this.tier;
    }

    public List<Speler> getLeden() {
        return this.leden;
    }

    public List<Rank> getRanks() {
        return this.ranks;
    }

    public List<Invite> getInvites() {
        return this.invites;
    }

    public String getTab() { return this.tab; }

    public List<ProtectedRegion> getColony() {
        return new colony(this).list();
    }

    public Integer getColonyMax() {
        return 1;
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

    public void goto_spawn(Player player) {
        List<String> Locatie = new ArrayList<>();
        for (String a : this.getSpawn().split("/")) {
            Locatie.add(a);
        }
        Location tpLocation = player.getLocation();
        tpLocation.setWorld(Bukkit.getServer().getWorld("VintageKingdom"));
        tpLocation.setX(Integer.parseInt(Locatie.get(0)));tpLocation.setY(Integer.parseInt(Locatie.get(1)));tpLocation.setZ(Integer.parseInt(Locatie.get(2)));
        player.teleport(tpLocation);
    }

    public String get_relation_string(Land land) {
        String relation = "";

        Integer relationNumber = KDFramework.getInstance().SQLSelect.relationship_land(this, land);
        if (relationNumber != 1 && relationNumber != 2) relation = "&fNeutraal";
        if (relationNumber == 1) relation = "&9Alliantie";
        if (relationNumber == 2) relation = "&4Vijand";

        return relation;
    }

    public Integer get_relation_number(Land land) {
        return KDFramework.getInstance().SQLSelect.relationship_land(this, land);
    }
}
