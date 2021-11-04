package io.github.deechtezeeuw.kdframework.Land;

import io.github.deechtezeeuw.kdframework.Speler.Speler;

import java.util.List;
import java.util.UUID;

public class Land {
    private UUID uuid;
    private String name;
    private String prefix;
    private Boolean invite;
    private Integer maximum;
    private List<Speler> leden;

    public Land(UUID Land_ID, String Land_Name, String Land_Prefix, Boolean Land_Invite, Integer Land_Maximum, List<Speler> Land_Leden) {
        this.uuid = Land_ID;
        this.name = Land_Name;
        this.prefix = Land_Prefix;
        this.invite = Land_Invite;
        this.maximum = Land_Maximum;
        this.leden = Land_Leden;
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
}
