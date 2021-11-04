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
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public UUID getLand() {
        return this.land;
    }

    public UUID getRank() {
        return this.rank;
    }
}
