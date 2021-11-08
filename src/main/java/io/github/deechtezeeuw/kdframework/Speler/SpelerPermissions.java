package io.github.deechtezeeuw.kdframework.Speler;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpelerPermissions {
    private KDFramework plugin;
    private List<String> permissionUser = new ArrayList<>();

    public SpelerPermissions(KDFramework plugin) {
        this.plugin = plugin;
    }

    public HashMap<UUID, PermissionAttachment> playerPermissions = new HashMap<>();

    public void SetupPermissions(Player player) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        playerPermissions.put(player.getUniqueId(), attachment);
        permissionSetter(player);
    }

    private void permissionSetter(Player player) {
        PermissionAttachment attachment = this.playerPermissions.get(player.getUniqueId());

        permissionUser = new ArrayList<>();
        Speler speler = plugin.SQLSelect.player_get_by_name(player.getName());

        // Get default permissions
        for (String defaultPerms : plugin.Config.getPermissionConfig().getStringList("default")) {
            permissionUser.add(defaultPerms);
        }

        // Check if user has a rank
        if (speler.getRank() != null) {
            Rank rank = plugin.SQLSelect.get_rank(speler.getRank());

            // loop through sections
            for (String section : plugin.Config.getPermissionConfig().getConfigurationSection("permissions").getKeys(false)) {
                // Loop through ranks of section
                for (String ranks : plugin.Config.getPermissionConfig().getStringList("permissions."+section+".ranks")) {
                    if (ranks.equalsIgnoreCase(rank.getName())) {
                        // Add permisions from that rank section
                        for (String sectionPerms : plugin.Config.getPermissionConfig().getStringList("permissions." + section + ".permissions")) {
                            if (!permissionUser.contains(sectionPerms))
                                permissionUser.add(sectionPerms);
                        }
                        // Check if section has inheritance if true add those permissions also
                        for (String sectionInher : plugin.Config.getPermissionConfig().getStringList("permissions." + section + ".inheritance")) {
                            permissions_add_inheritance(player, sectionInher);
                        }
                    }
                }
            }
        }

        for (String a : permissionUser) {
            attachment.setPermission(a, true);
        }
    }

    void permissions_add_inheritance(Player player, String Inheritance) {
        // Get permissions of inheritance
        for (String inheritancePerm : plugin.Config.getPermissionConfig().getStringList("permissions."+ Inheritance + ".permissions")) {
            permissionUser.add(inheritancePerm);
        }
        // Get inheritance of inheritance
        for (String inheritanceInher : plugin.Config.getPermissionConfig().getStringList("permissions."+Inheritance+".inheritance")) {
            permissions_add_inheritance(player, inheritanceInher);
        }
    }

    public void reload_permissions(Player player) {
        String pluginperms = "k.";
        PermissionAttachment attachment = this.playerPermissions.get(player.getUniqueId());
        if (attachment!= null) {
            for (PermissionAttachmentInfo s : player.getEffectivePermissions()) {
                if (s.getPermission().toLowerCase().startsWith(pluginperms.toLowerCase())) {
                    attachment.unsetPermission(s.getPermission());
                }
            }
        }
        plugin.SpelerPerms.playerPermissions.remove(player.getUniqueId());
        plugin.SpelerPerms.SetupPermissions(player);
    }
}
