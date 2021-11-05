package io.github.deechtezeeuw.kdframework.Rank;

import java.util.UUID;

public class Rank {
    private UUID uuid;
    private String name;
    private Integer level;
    private Integer maximum;
    private String prefix;
    private Boolean kdDefault;

    public Rank (UUID uuid, String name, Integer level, Integer maximum, String prefix, Boolean kdDefault) {
        this.uuid = uuid;
        this.name = name;
        this.level = level;
        this.maximum = maximum;
        this.prefix = prefix;
        this.kdDefault = kdDefault;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public Integer getLevel() {
        return this.level;
    }

    public Integer getMaximum() {
        return this.maximum;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public Boolean getKdDefault() {
        return this.kdDefault;
    }
}
