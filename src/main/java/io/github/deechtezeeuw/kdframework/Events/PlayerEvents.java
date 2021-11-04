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

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player eventPlayer = event.getPlayer();
        Speler speler = plugin.SQLSelect.player_get_by_name(eventPlayer.getName());

        // If not kingdom member
        if (speler.getLand()!= null) {
            Land land = plugin.SQLSelect.land_get_by_player(speler);
            event.setFormat(ChatColor.translateAlternateColorCodes('&',land.getPrefix()+"[rank] &a%s &a&l> &f%s"));
        } else {
            event.setFormat(ChatColor.translateAlternateColorCodes('&',"&7[&cLand-loos&7] &a%s &a&l> &f%s"));
        }
    }
}
