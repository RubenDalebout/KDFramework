package io.github.deechtezeeuw.kdframework.Events;

import io.github.deechtezeeuw.kdframework.KDFramework;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    private KDFramework plugin;

    public PlayerEvents(KDFramework plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPlayedBefore()) {
            // Join message
            event.setJoinMessage(PlaceholderAPI.setPlaceholders(player,
                    ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralOnJoin())));
        } else {
            // First join message
            event.setJoinMessage(PlaceholderAPI.setPlaceholders(player,
                    ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralOnFirst())));
        }

        // Check user in database
        if (plugin.SQLSelect.player_exists(player)) {
            // Update username if needed
            plugin.SQLUpdate.update_player(player);
        } else {
            // Create user in database
            plugin.SQLInsert.player_create(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Quit message
        event.setQuitMessage(PlaceholderAPI.setPlaceholders(player,
                ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralOnQuit())));
    }
}
