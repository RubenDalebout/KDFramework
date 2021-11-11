package io.github.deechtezeeuw.kdframework.Scoreboard;

import io.github.deechtezeeuw.kdframework.KDFramework;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.List;

public class Sidebar {
    private KDFramework plugin;

    public Sidebar (KDFramework plugin) {
        this.plugin = plugin;
    }

    public void ReceiveBoard(Player p) {
        // scoreboard
        String title = "scoreboard title";
        Scoreboard board =  Bukkit.getScoreboardManager().getNewScoreboard();

        Objective obj = board.registerNewObjective("dummy", "title");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(title);

        List<String> lines = plugin.Config.getSidebarList();
        int size = lines.size()+1;

        for (String linestring : lines) {
            size--;
            Team line = board.registerNewTeam("line"+size);
            line.setPrefix(PlaceholderAPI.setPlaceholders(p,ChatColor.translateAlternateColorCodes('&', linestring)));
            obj.getScore(PlaceholderAPI.setPlaceholders(p,ChatColor.translateAlternateColorCodes('&', linestring))).setScore(size);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                //methods
                for (Team team : board.getTeams()) {

                }
            }
        }.runTaskTimer(plugin, 1, 20);

        p.setScoreboard(board);
    }
}
