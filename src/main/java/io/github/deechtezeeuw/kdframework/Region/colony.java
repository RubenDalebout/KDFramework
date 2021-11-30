package io.github.deechtezeeuw.kdframework.Region;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class colony {
    private Land land;

    public colony(Land land) {
        this.land = land;
    }

    // Check if land has colonies
    public List<ProtectedRegion> list() {
        List<ProtectedRegion> regions = new ArrayList<>();
        for(ProtectedRegion region : KDFramework.getInstance().worldGuardPlugin.getRegionManager(Bukkit.getServer().getWorld("VintageKingdom")).getRegions().values()) {
            if (StringUtils.containsIgnoreCase(region.getId(), "kolonie-"+land.getName()))
                regions.add(region);
        }
        return regions;
    }

    public void addColony(Player player) {
        // Check the tier of the land
        Integer tier = land.getTier();
        if (tier != 5) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    KDFramework.getInstance().Config.getGeneralPrefix() + "&cHet kingdom moet &4&ltier 5 &czijn voor kolonies!"));
            return;
        }

        // Check if they have the maximum of their tier
        if (this.list().size() >= 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    KDFramework.getInstance().Config.getGeneralPrefix() + "&cHet kingdom heeft al een kolonie!"));
            return;
        }

        // Get worldedit selection
        Selection sel = KDFramework.getInstance().worldEditPlugin.getSelection(player);
        if (sel == null || !(sel instanceof Polygonal2DSelection)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    KDFramework.getInstance().Config.getGeneralPrefix() + "&cMaak eerst een &4&l//sel poly&c!"));
            return;
        }

        // Create polygonal region
        ProtectedPolygonalRegion colonyRegion = this.createPolygonalRegion((Polygonal2DSelection) sel, player);
        if (colonyRegion == null || !(colonyRegion instanceof ProtectedPolygonalRegion)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    KDFramework.getInstance().Config.getGeneralPrefix() + "&cEr is iets fout gegaan in het maken van de polygonal region!"));
            return;
        }
        // Permissions
        String command = "rg addmember "+colonyRegion.getId()+" g:"+land.getName().toLowerCase() + " -w "+player.getWorld().getName();
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, command);
        for (String cmd : KDFramework.getInstance().Config.getRegionConfig().getStringList("kingdom-region.rules")) {
            cmd = cmd.replaceAll("\\{region\\}",colonyRegion.getId());
            cmd = cmd.replaceAll("\\{world\\}", player.getWorld().getName());

            Bukkit.dispatchCommand(console, cmd);
        }
        // Create region
        try {
            KDFramework.getInstance().worldGuardPlugin.getRegionManager(player.getWorld()).addRegion(colonyRegion);
        } catch (Exception e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    KDFramework.getInstance().Config.getGeneralPrefix() + "&cEr is iets fout gegaan tijdens het aanmaken van de region!"));
            return;
        }
        // Create marker
        try {
            this.markerCreate(colonyRegion, player);
        } catch (Exception e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    KDFramework.getInstance().Config.getGeneralPrefix() + "&cEr is iets fout gegaan tijdens het aanmaken van de marker!"));
            return;
        }
        // Send message
        if (player != null && player instanceof Player)
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    KDFramework.getInstance().Config.getGeneralPrefix() + "&aJe kolonie is aangemaakt!"));

        KDFramework.getInstance().log.info(ChatColor.translateAlternateColorCodes('&',
                "&aDe kolonie voor &2&l"+land.getName()+" &ais aangemaakt!"));

        return;
    }

    private ProtectedPolygonalRegion createPolygonalRegion(Polygonal2DSelection sel, Player player) {
        ProtectedPolygonalRegion region = null;
        List<BlockVector2D> points = sel.getNativePoints();
        int minY = 0;
        int maxY = player.getWorld().getMaxHeight();
        region = new ProtectedPolygonalRegion("Kolonie-"+land.getName(), points, minY, maxY);
        return region;
    }

    private void markerCreate(ProtectedPolygonalRegion region, Player player) {
        // Commands need to be done by console
        List<String> cmds = new ArrayList<>();
        cmds.add("dmarker add id:\"" + region.getId().toLowerCase() + "\" \"" + land.getName() + "\" icon:colony world:" + player.getWorld().getName() + " x:" + player.getLocation().getBlockX() + " y:" + player.getLocation().getBlockY() + " z:" + player.getLocation().getBlockZ());
        cmds.add("plugman reload dynmap-worldguard");

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        for (String cmd : cmds) {
            Bukkit.dispatchCommand(console, cmd);
        }
    }

    // Deletion
    public void removeColony(CommandSender sender) {
        boolean regionExists = false;
        ProtectedRegion region = null;
        // Check if region exists
        for (ProtectedRegion r : this.list()) {
            region = r;
            regionExists = true;
        }

        // Send message if not found
        if (!regionExists || region == null) {
            if (sender!=null)
                this.regionNotFound(sender);
            return;
        }

        // Remove marker
        this.markerDelete(region.getId().toLowerCase());
        // Remove region
        this.regionDelete(region);

        KDFramework.getInstance().log.info(ChatColor.translateAlternateColorCodes('&',
                "&aDe kolonie voor &2&l"+land.getName()+" &ais aangemaakt!"));
    }

    private void regionDelete(ProtectedRegion region) {
        try {
            RegionManager mngr = KDFramework.getInstance().worldGuardPlugin.getRegionManager(Bukkit.getServer().getWorld("VintageKingdom"));
            mngr.removeRegion(region.getId());
        } catch (Exception e) {
            KDFramework.getInstance().log.warning("Kolonie region verwijdering mislukt!");
        }
        return;
    }

    private void markerDelete(String markerID) {
        try {
            // Commands need to be done by console
            List<String> cmds = new ArrayList<>();
            cmds.add("dmarker delete id:"+markerID.toLowerCase());
            cmds.add("plugman reload dynmap-worldguard");

            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            for (String cmd : cmds) {
                Bukkit.dispatchCommand(console, cmd);
            }
        } catch (Exception e) {
            KDFramework.getInstance().log.warning("Marker van kolonie verwijderen is mislukt!");
        }
    }

    // Messages
    private void regionNotFound(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                KDFramework.getInstance().Config.getGeneralPrefix() + "&cDe region die je wilt verwijderen bestaat niet!"));
        return;
    }
}
