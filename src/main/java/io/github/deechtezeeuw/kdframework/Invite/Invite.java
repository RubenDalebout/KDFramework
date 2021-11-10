package io.github.deechtezeeuw.kdframework.Invite;

import java.util.UUID;

public class Invite {
    private UUID uuid;
    private UUID land;
    private UUID player;

    public Invite (UUID Invite_UUID, UUID Invite_Land, UUID Invite_Player) {
        this.uuid = Invite_UUID;
        this.land = Invite_Land;
        this.player = Invite_Player;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public UUID getLand() {
        return this.land;
    }

    public UUID getPlayer() {
        return this.player;
    }
}
