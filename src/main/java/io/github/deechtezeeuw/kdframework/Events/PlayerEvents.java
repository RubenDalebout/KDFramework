package io.github.deechtezeeuw.kdframework.Events;

import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.List;

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

        // Sidebar
        KDFramework.getInstance().sidebar.setSidebar(player);
        KDFramework.getInstance().sidebar.runnable(player);
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
                        plugin.Config.getGeneralChat() + event.getMessage())));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        InventoryView view = e.getView();

        if (e.getClickedInventory() == null) return;

        if(view.getTitle().contains("ranks") && e.getClickedInventory().getType() != InventoryType.PLAYER) {
            e.setCancelled(true);
            Integer GUISize = e.getClickedInventory().getSize();
            if (e.getSlot() == GUISize-5 && e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&4&lSluiten"))) {
                p.closeInventory();
            }
        }

        if(view.getTitle().contains("invites") && e.getClickedInventory().getType() != InventoryType.PLAYER) {
            e.setCancelled(true);
            Integer GUISize = e.getClickedInventory().getSize();
            if (e.getSlot() == GUISize-5 && e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&4&lSluiten"))) {
                p.closeInventory();
            }
        }

        if(view.getTitle().contains("Landen") && e.getClickedInventory().getType() != InventoryType.PLAYER) {
            e.setCancelled(true);
            Integer GUISize = e.getClickedInventory().getSize();
            if (e.getSlot() == GUISize-5 && e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&4&lSluiten"))) {
                p.closeInventory();
            }
        }

        if(view.getTitle().contains("Kingdom") && e.getClickedInventory().getType() != InventoryType.PLAYER) {
            e.setCancelled(true);
            Integer GUISize = e.getClickedInventory().getSize();
            if (e.getSlot() == GUISize-5 && e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&4&lSluiten"))) {
                p.closeInventory();
            }
            // Next
            if (e.getSlot() == GUISize-12 && e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&2&lVolgende"))) {
                plugin.guiJoin.pagination = plugin.guiJoin.pagination + 1;
                p.closeInventory();
                plugin.guiJoin.openGUI(p);
            }
            // Previous
            if (e.getSlot() == GUISize-16 && e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&2&lVorige"))) {
                plugin.guiJoin.pagination = plugin.guiJoin.pagination - 1;
                p.closeInventory();
                plugin.guiJoin.openGUI(p);
            }
            // Join
            if (e.getSlot() == GUISize-23 && e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&2&lJoinen"))) {
                // Check if player is in database
                if (plugin.SQLSelect.player_exists(p)) {
                    Speler speler = plugin.SQLSelect.player_get_by_uuid(p.getUniqueId());
                    // Check if player is in a land
                    if (speler.getLand() == null) {
                        // Check if the land exists that he or she wants to join
                        String landName = view.getTitle().replace("Kingdom ", "");
                        if (plugin.SQLSelect.land_exists(landName)) {
                            Land land = plugin.SQLSelect.land_get_by_name(landName);
                            // Check if land is at its max
                            if (land.getLeden().size() < land.getMaximum()) {
                                // Check if land is on invite or not
                                if (land.getInvite()) {
                                    // Land is on invite
                                    boolean found = false;
                                    List<Invite> invites = plugin.SQLSelect.invite_get_from_land(land.getUuid());
                                    for (Invite invite : invites) {
                                        if (invite.getPlayer().equals(p.getUniqueId()))
                                            found = true;
                                    }
                                    if (found) {
                                        p.closeInventory();
                                        plugin.guiJoin.pagination = 0;
                                        plugin.SQLUpdate.update_player_land(speler.getUuid(), land.getUuid());
                                        plugin.SQLUpdate.update_player_rank(speler.getUuid(), land.get_defaultRank().getUuid());
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                plugin.Config.getGeneralPrefix() + "&aJe bent het land &2&l"+land.getName()+ " &agejoind!"));
                                        // check if land has an spawn
                                        if (land.getSpawn() == null) {
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                    plugin.Config.getGeneralPrefix() + "&cHet kingdom &4&l"+land.getName()+" &cheeft nog geen spawn!"));
                                            return;
                                        }
                                        land.goto_spawn(p);
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                plugin.Config.getGeneralPrefix() + "&aJe word geteleporteerd naar je kingdom..."));
                                        return;
                                    }
                                } else {
                                    // Land is not on invite
                                    p.closeInventory();
                                    plugin.guiJoin.pagination = 0;
                                    plugin.SQLUpdate.update_player_land(speler.getUuid(), land.getUuid());
                                    plugin.SQLUpdate.update_player_rank(speler.getUuid(), land.get_defaultRank().getUuid());
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.Config.getGeneralPrefix() + "&aJe bent het land &2&l"+land.getName()+ " &agejoind!"));
                                    // check if land has an spawn
                                    if (land.getSpawn() == null) {
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                plugin.Config.getGeneralPrefix() + "&cHet kingdom &4&l"+land.getName()+" &cheeft nog geen spawn!"));
                                        return;
                                    }
                                    land.goto_spawn(p);
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.Config.getGeneralPrefix() + "&aJe word geteleporteerd naar je kingdom..."));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
