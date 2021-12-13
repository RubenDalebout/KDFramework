package io.github.deechtezeeuw.kdframework.Events;

import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryView;

import java.util.List;

public class PlayerEvents implements Listener {

    private KDFramework plugin;

    public PlayerEvents(KDFramework plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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

        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Quit message
        event.setQuitMessage(PlaceholderAPI.setPlaceholders(player,
                ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralOnQuit())));

        plugin.SpelerPerms.playerPermissions.remove(player.getUniqueId());

        if (plugin.worldGuarding.entered.contains(player) || plugin.worldGuarding.left.contains(player)) {
            plugin.worldGuarding.left.remove(player);
            plugin.worldGuarding.entered.remove(player);
        }
    }

    @EventHandler
    public void moveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        plugin.worldGuarding.enterRegion(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player eventPlayer = event.getPlayer();
        Speler speler = plugin.SQLSelect.player_get_by_name(eventPlayer.getName());
        Boolean chatFormatSet = false;

        for (String section : plugin.Config.getGeneralChats().getKeys(false)) {
            if (speler.getLand() != null) {
                Land land = plugin.SQLSelect.land_get_by_player(speler);
                if (event.getMessage().substring(0, 1).equalsIgnoreCase(plugin.Config.getGeneralChats().getString(section+".prefix"))) {
                    event.setFormat(PlaceholderAPI.setPlaceholders(eventPlayer,
                            ChatColor.translateAlternateColorCodes('&',
                                    plugin.Config.getGeneralChats().getString(section+".format") + event.getMessage().substring(1))));
                    chatFormatSet = true;

                    if (section.equalsIgnoreCase("kingdom")) {
                        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                            Speler otherSpeler = plugin.SQLSelect.player_get_by_uuid(player.getUniqueId());
                            if (otherSpeler.getLand() == null) {
                                event.getRecipients().remove(player);
                            }
                            if (!speler.getLand().equals(otherSpeler.getLand())) {
                                event.getRecipients().remove(player);
                            }
                        }
                    }
                    break;
                }
            }
        }

        if (!chatFormatSet && speler.getLand() != null) {
            for (String section : plugin.Config.getGeneralChats().getKeys(false)) {
                if (plugin.Config.getGeneralChats().getBoolean(section+".default")) {
                    if (section.equalsIgnoreCase("kingdom")) {
                        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                            Speler otherSpeler = plugin.SQLSelect.player_get_by_uuid(player.getUniqueId());
                            if (otherSpeler.getLand() == null) {
                                event.getRecipients().remove(player);
                            }
                            if (!speler.getLand().equals(otherSpeler.getLand())) {
                                event.getRecipients().remove(player);
                            }
                        }
                    }
                    event.setFormat(PlaceholderAPI.setPlaceholders(eventPlayer,
                            ChatColor.translateAlternateColorCodes('&',
                                    plugin.Config.getGeneralChats().getString(section+".format") + event.getMessage())));
                    break;
                }
            }
        }

        if (speler.getLand() == null) {
            event.setFormat(PlaceholderAPI.setPlaceholders(eventPlayer,
                    ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralChats().getString("public.format") + event.getMessage())));
        }
    }

    // PVP
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Speler whoWasHit = plugin.SQLSelect.player_get_by_uuid(((Player) e.getEntity()).getPlayer().getUniqueId());
            Speler whoHit = plugin.SQLSelect.player_get_by_uuid(((Player) e.getDamager()).getPlayer().getUniqueId());

            if (whoHit.getLand() == null || whoWasHit.getLand() == null) return;

            // Check on land mates
            if (whoHit.getLand().equals(whoWasHit.getLand())) {
                Player sendto = ((Player) e.getDamager()).getPlayer();
                sendto.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe geen landgenoten slaan!"));
                e.setCancelled(true);
                return;
            }

            // Check on ally
            Land land = plugin.SQLSelect.land_get_by_player(whoHit);
            Land other = plugin.SQLSelect.land_get_by_player(whoWasHit);

            Player sendto = ((Player) e.getDamager()).getPlayer();

            if (land.get_relation_number(other) == 1) {
                sendto.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe mag geen alliantie slaan!"));
                e.setCancelled(true);
                return;
            }
        }
    }

    // GUI clicks
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

        if(view.getTitle().contains("invites") && e.getClickedInventory().getType() != InventoryType.PLAYER || view.getTitle().contains("Relaties") && e.getClickedInventory().getType() != InventoryType.PLAYER || view.getTitle().contains("Tiers") && e.getClickedInventory().getType() != InventoryType.PLAYER) {
            e.setCancelled(true);
            Integer GUISize = e.getClickedInventory().getSize();
            if (e.getSlot() == GUISize-5 && e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&4&lSluiten"))) {
                p.closeInventory();
            }
        }

        if(view.getTitle().contains("Landen") && e.getClickedInventory().getType() != InventoryType.PLAYER || view.getTitle().contains("Staff informatie") && e.getClickedInventory().getType() != InventoryType.PLAYER) {
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
                                        plugin.SQLDelete.invite_delete_player(speler);
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
                                    plugin.SQLDelete.invite_delete_player(speler);
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
