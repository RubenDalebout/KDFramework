package io.github.deechtezeeuw.kdframework.Speler;

import java.util.UUID;

public class Speler {
    private UUID uuid;
    private String name;
    private UUID land;
    private UUID rank;

    public Speler (UUID uuid, String name, UUID land, UUID rank) {
        this.uuid = uuid;
        this.name = name;
        this.land = land;
        this.rank = rank;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public UUID getLand() {
        return land;
    }

    public UUID getRank() {
        return rank;
    }
}
