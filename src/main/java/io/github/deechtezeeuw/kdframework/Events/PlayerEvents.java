package io.github.deechtezeeuw.kdframework.Events;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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

        if (event.getPlayer().hasPlayedBefore()) {
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

        plugin.SpelerPerms.SetupPermissions(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Quit message
        event.setQuitMessage(PlaceholderAPI.setPlaceholders(player,
                ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralOnQuit())));

        plugin.SpelerPerms.playerPermissions.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player eventPlayer = event.getPlayer();
        Speler speler = plugin.SQLSelect.player_get_by_name(eventPlayer.getName());

        event.setFormat(PlaceholderAPI.setPlaceholders(eventPlayer,
                ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralChat())));
    }
}
