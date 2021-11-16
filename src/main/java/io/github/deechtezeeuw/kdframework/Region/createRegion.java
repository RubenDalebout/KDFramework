package io.github.deechtezeeuw.kdframework.Region;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.swing.plaf.synth.Region;
import java.util.List;

public class createRegion {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public createRegion(KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        // Check if there are 3 arguments
        if (args.length != 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" region create <kingdom>"));
            return;
        }

        // Check if its done by player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe kan alleen in-game een regio aanmaken!"));
            return;
        }
        Player player = (Player) sender;

        // Check if player exists in db
        if (!plugin.SQLSelect.player_exists(player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cIn verband met de veiligheid moet u in de database bestaan!"));
            return;
        }
        Speler speler = plugin.SQLSelect.player_get_by_uuid(player.getUniqueId());

        // Check if args[2] is an kingdom
        if (!plugin.SQLSelect.land_exists(args[2])) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cbestaat niet!"));
            return;
        }
        Land land = plugin.SQLSelect.land_get_by_name(args[2]);
        Selection sel = plugin.worldEditPlugin.getSelection(player);

        // Check if region already exists
        if (plugin.worldGuardPlugin.getRegionManager(player.getWorld()).hasRegion(land.getName())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cheeft al een region!"));
            return;
        }

        // Get polygon selection
        if (sel == null || !(sel instanceof Polygonal2DSelection)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cMaak eerst een &4&l//sel poly&c!"));
            return;
        }
        List<BlockVector2D> points = ((Polygonal2DSelection) sel).getNativePoints();
        int minY = 0;
        int maxY = player.getWorld().getMaxHeight();

        ProtectedPolygonalRegion landRegion = new ProtectedPolygonalRegion(land.getName(), points, minY, maxY);

        plugin.worldGuardPlugin.getRegionManager(player.getWorld()).addRegion(landRegion);

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        // Permissions
        String command = "rg addmember "+landRegion.getId()+" g:"+land.getName().toLowerCase() + " -w "+player.getWorld().getName();
        Bukkit.dispatchCommand(console, command);
        for (String cmd : plugin.Config.getRegionConfig().getStringList("kingdom-region.rules")) {
            cmd = cmd.replaceAll("\\{region\\}",landRegion.getId());
            cmd = cmd.replaceAll("\\{world\\}", player.getWorld().getName());

            Bukkit.dispatchCommand(console, cmd);
        }
        // Dmarker
        command = "dmarker add id:\"kd-"+land.getName().toLowerCase()+"\" \""+land.getName()+"\" icon:"+plugin.Config.getRegionConfig().getString("kingdom-region.dynmap.icon")+" world:"+player.getWorld().getName()+" x:"+player.getLocation().getBlockX()+" y:"+player.getLocation().getBlockY()+" z:"+player.getLocation().getBlockZ();
        Bukkit.dispatchCommand(console, command);
        command = "plugman reload dynmap-worldguard";
        Bukkit.dispatchCommand(console, command);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + "&aLand regio succesvol aangemaakt!"));
        return;
    }
}
