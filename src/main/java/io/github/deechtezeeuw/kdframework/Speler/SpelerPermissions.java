package io.github.deechtezeeuw.kdframework.Speler;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class SpelerPermissions {
    private KDFramework plugin;

    public SpelerPermissions(KDFramework plugin) {
        this.plugin = plugin;
    }

    public HashMap<UUID, PermissionAttachment> playerPermissions = new HashMap<>();

    public void SetupPermissions(Player player) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        playerPermissions.put(player.getUniqueId(), attachment);
        permissionSetter(player.getUniqueId());
    }

    private void permissionSetter(UUID uuid) {
        PermissionAttachment attachment = this.playerPermissions.get(uuid);

        attachment.setPermission("k.set", true);
    }
}
