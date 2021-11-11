package io.github.deechtezeeuw.kdframework.GUI;

import io.github.deechtezeeuw.kdframework.Invite.Invite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIJoin {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    // GUI
    private Inventory gui;
    public Integer guiSize;

    // Gegevens
    private Speler speler;
    private List<Land> lands;
    public Integer pagination;

    public GUIJoin (KDFramework plugin) {
        this.plugin = plugin;
        this.pagination = 0;
    }

    public void GUIInstall(CommandSender sender, String label, String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = args;

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe kan alleen in-game een land joinen!"));
            return;
        }
        Player player = (Player) sender;
        speler = plugin.SQLSelect.player_get_by_name(player.getName());
        lands = plugin.SQLSelect.land_list();
        this.createGUI(player, lands.get(pagination));
    }

    public void openGUI(Player player) {
        lands = plugin.SQLSelect.land_list();
        this.createGUI(player, lands.get(pagination));
    }

    private void createGUI(Player player, Land land) {
        if (player instanceof Player) {
            Integer rankCount = land.getInvites().size();
            double Chestvalue = 54;
            this.guiSize = (int) Chestvalue;

            gui = Bukkit.getServer().createInventory(player, guiSize, ChatColor.translateAlternateColorCodes('&',"Kingdom "+land.getName()));

            // Create glass panes
            this.createGlasspanes();

            // Create Kingdom Flag
            this.createLand(land);

            // Create join button
            this.createJoin(player);

            // Create close button
            this.createCloseButton();

            // Create pagination
            this.createPagination();

            player.openInventory(this.gui);
        }
    }

    private void createGlasspanes() {
        // Create glass panes
        ItemStack GlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);

        ItemMeta MetaGlassPane = GlassPane.getItemMeta();
        ArrayList<String> GlassLore = new ArrayList<String>();
        MetaGlassPane.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        GlassPane.setItemMeta(MetaGlassPane);
        MetaGlassPane.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));

        GlassPane.setItemMeta(MetaGlassPane);

        for (int i = 0; i < this.guiSize; i++) {
            gui.setItem(i, GlassPane);
        }
    }

    private void createLand(Land land) {
        // Create Flag
        ItemStack LandFlag = new ItemStack(Material.BANNER);

        ItemMeta MetaLandFlag = LandFlag.getItemMeta();
        ArrayList<String> LandFlagLore = new ArrayList<String>();
        MetaLandFlag.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        LandFlag.setItemMeta(MetaLandFlag);
        MetaLandFlag.setDisplayName(ChatColor.translateAlternateColorCodes('&', land.getPrefix()));

        LandFlag.setItemMeta(MetaLandFlag);

        gui.setItem(13, LandFlag);
    }

    private void createJoin(Player player) {
        if (plugin.SQLSelect.player_exists_name(player.getName())) {
            Speler speler = plugin.SQLSelect.player_get_by_uuid(player.getUniqueId());
            if (speler.getLand() != null) {
                // Create Green Glass Pane
                ItemStack LandJoinKnop = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

                ItemMeta MetaLandJoinKnop = LandJoinKnop.getItemMeta();
                ArrayList<String> LandJoinKnopLore = new ArrayList<>();
                LandJoinKnopLore.add(ChatColor.translateAlternateColorCodes('&', "&cJe zit al in een land!"));
                MetaLandJoinKnop.setLore(LandJoinKnopLore);
                MetaLandJoinKnop.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

                LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                MetaLandJoinKnop.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lNiet join baar"));

                LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                gui.setItem(31, LandJoinKnop);
            } else {
                // Check if land is full
                if (lands.get(pagination).getLeden().size() < lands.get(pagination).getMaximum()) {
                    if (lands.get(pagination).getInvite()) {
                        // Get invites from user
                        List<Invite> invites = plugin.SQLSelect.invite_get_from_user(speler);
                        boolean found = false;
                        for (Invite invite : invites) {
                            if (invite.getLand().equals(lands.get(pagination).getUuid()))
                                found = true;
                        }

                        if (found) {
                            // Create Green Glass Pane
                            ItemStack LandJoinKnop = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);

                            ItemMeta MetaLandJoinKnop = LandJoinKnop.getItemMeta();
                            ArrayList<String> LandJoinKnopLore = new ArrayList<>();
                            MetaLandJoinKnop.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

                            LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                            MetaLandJoinKnop.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2&lJoinen"));

                            LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                            gui.setItem(31, LandJoinKnop);
                        } else {
                            // Create Green Glass Pane
                            ItemStack LandJoinKnop = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);

                            ItemMeta MetaLandJoinKnop = LandJoinKnop.getItemMeta();
                            ArrayList<String> LandJoinKnopLore = new ArrayList<>();
                            LandJoinKnopLore.add(ChatColor.translateAlternateColorCodes('&', "&6Het land is op invite"));
                            MetaLandJoinKnop.setLore(LandJoinKnopLore);
                            MetaLandJoinKnop.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

                            LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                            MetaLandJoinKnop.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lGeen invite"));

                            LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                            gui.setItem(31, LandJoinKnop);
                        }
                    } else {
                        // Create Green Glass Pane
                        ItemStack LandJoinKnop = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);

                        ItemMeta MetaLandJoinKnop = LandJoinKnop.getItemMeta();
                        ArrayList<String> LandJoinKnopLore = new ArrayList<>();
                        MetaLandJoinKnop.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

                        LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                        MetaLandJoinKnop.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2&lJoinen"));

                        LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                        gui.setItem(31, LandJoinKnop);
                    }
                } else {
                    // Create Green Glass Pane
                    ItemStack LandJoinKnop = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

                    ItemMeta MetaLandJoinKnop = LandJoinKnop.getItemMeta();
                    ArrayList<String> LandJoinKnopLore = new ArrayList<>();
                    LandJoinKnopLore.add(ChatColor.translateAlternateColorCodes('&', "&cHet land zit vol!"));
                    MetaLandJoinKnop.setLore(LandJoinKnopLore);
                    MetaLandJoinKnop.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

                    LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                    MetaLandJoinKnop.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lNiet join baar"));

                    LandJoinKnop.setItemMeta(MetaLandJoinKnop);
                    gui.setItem(31, LandJoinKnop);
                }
            }
        } else {
            // Create Green Glass Pane
            ItemStack LandJoinKnop = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

            ItemMeta MetaLandJoinKnop = LandJoinKnop.getItemMeta();
            ArrayList<String> LandJoinKnopLore = new ArrayList<>();
            LandJoinKnopLore.add(ChatColor.translateAlternateColorCodes('&', "&cJe bestaat niet in onze database!"));
            MetaLandJoinKnop.setLore(LandJoinKnopLore);
            MetaLandJoinKnop.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

            LandJoinKnop.setItemMeta(MetaLandJoinKnop);
            MetaLandJoinKnop.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lNiet join baar"));

            LandJoinKnop.setItemMeta(MetaLandJoinKnop);
            gui.setItem(31, LandJoinKnop);
        }
    }

    private void createCloseButton() {
        // Create glass pane
        ItemStack GlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

        ItemMeta MetaGlassPane = GlassPane.getItemMeta();
        ArrayList<String> GlassLore = new ArrayList<String>();
        MetaGlassPane.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        GlassPane.setItemMeta(MetaGlassPane);
        MetaGlassPane.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lSluiten"));

        GlassPane.setItemMeta(MetaGlassPane);

        gui.setItem(this.guiSize-5, GlassPane);
    }

    private void createPagination() {
        if (pagination != 0) {
            // Previous
            ItemStack skullLeft = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
            meta.setOwner("MHF_ArrowLeft");
            skullLeft.setItemMeta(meta);

            ItemMeta MetaskullLeft = skullLeft.getItemMeta();
            ArrayList<String> skullLeftLore = new ArrayList<String>();
            MetaskullLeft.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

            skullLeft.setItemMeta(MetaskullLeft);
            MetaskullLeft.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2&lVorige"));

            skullLeft.setItemMeta(MetaskullLeft);

            gui.setItem(this.guiSize - 16, skullLeft);
        }

        if (pagination != lands.size()-1) {
            // Next
            ItemStack skullRight = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta metaRight = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
            metaRight.setOwner("MHF_ArrowRight");
            skullRight.setItemMeta(metaRight);

            ItemMeta MetaskullRight = skullRight.getItemMeta();
            ArrayList<String> skullRightLore = new ArrayList<String>();
            MetaskullRight.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

            skullRight.setItemMeta(MetaskullRight);
            MetaskullRight.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2&lVolgende"));

            skullRight.setItemMeta(MetaskullRight);

            gui.setItem(this.guiSize - 12, skullRight);
        }
    }
}
