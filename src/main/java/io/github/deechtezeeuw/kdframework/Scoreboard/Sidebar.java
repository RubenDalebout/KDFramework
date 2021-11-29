package io.github.deechtezeeuw.kdframework.Scoreboard;

import io.github.deechtezeeuw.kdframework.KDFramework;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Sidebar {

    private String MainColor;
    private String SecondColor;


    public void setSidebar(Player p) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("sidebar", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', KDFramework.getInstance().Config.getSidebarTitle()));

        MainColor = KDFramework.getInstance().Config.getSidebarColor();
        SecondColor = this.getSecondColor(KDFramework.getInstance().Config.getSidebarColor());

        Team coins = board.registerNewTeam("coins");
        Team friends = board.registerNewTeam("friends");
        Team region = board.registerNewTeam("region");

        obj.getScore("  ").setScore(9);
        obj.getScore(ChatColor.translateAlternateColorCodes('&', MainColor+"&lKingdom:")).setScore(8);
        obj.getScore("§c").setScore(7);
        obj.getScore(ChatColor.translateAlternateColorCodes('&', MainColor+"&lRank:")).setScore(6);
        obj.getScore("§b").setScore(5);
        obj.getScore(" ").setScore(4);
        obj.getScore(ChatColor.translateAlternateColorCodes('&', MainColor+"&lRegio:")).setScore(3);
        obj.getScore("§a").setScore(2);
        obj.getScore("").setScore(1);
        obj.getScore(ChatColor.translateAlternateColorCodes('&', MainColor+KDFramework.getInstance().Config.getSidebarIP())).setScore(0);

        coins.addEntry("§c");
        String line = PlaceholderAPI.setPlaceholders(p, "%kdf_land%");
        if (line.length() > 14) {
            coins.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line.substring(0, 13)));
            coins.setSuffix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line.substring(14)));
        } else {
            coins.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line));
            coins.setSuffix("");
        }

        friends.addEntry("§b");
        line = PlaceholderAPI.setPlaceholders(p, "%kdf_rank%");
        if (line.length() > 14) {
            friends.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line.substring(0, 13)));
            friends.setSuffix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line.substring(14)));
        } else {
            friends.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line));
            friends.setSuffix("");
        }

        region.addEntry("§a");
        line = PlaceholderAPI.setPlaceholders(p, "%kdf_region%");
        if (line.length() > 14) {
            region.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line.substring(0, 13)));
            region.setSuffix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line.substring(14)));
        } else {
            region.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line));
            region.setSuffix("");
        }
        p.setScoreboard(board);
    }

    private String getSecondColor(String sidebarColor) {
        String color = "";

        switch (sidebarColor) {
            case "&0":
                color = "&f";
                break;
            case "&1":
                color = "&9";
                break;
            case "&2":
                color = "&a";
                break;
            case "&3":
                color = "&b";
                break;
            case "&4":
                color = "&c";
                break;
            case "&5":
                color = "&d";
                break;
            case "&6":
                color = "&e";
                break;
            case "&7":
                color = "&8";
                break;
            case "&8":
                color = "&7";
                break;
            case "&9":
                color = "&1";
                break;
            case "&a":
                color = "&2";
                break;
            case "&b":
                color = "&3";
                break;
            case "&c":
                color = "&4";
                break;
            case "&d":
                color = "&5";
                break;
            case "&e":
                color = "&6";
                break;
            case "&f":
                color = "&0";
                break;
            default:
                color = "&0";
                break;
        }

        return color;
    }

    public void runnable(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) {
                    cancel();
                }
                KDFramework.getInstance().sidebar.updateSidebar(p);
                p.setPlayerListName(PlaceholderAPI.setPlaceholders(p,
                        ChatColor.translateAlternateColorCodes('&',
                                "%kdf_land_tab% &f%player_displayname% %kdf_tab%")));
            }
        }.runTaskTimer(KDFramework.getInstance(), 0, 20);
    }

    public void updateSidebar(Player p) {
        Scoreboard board = p.getScoreboard();

        if (board.getObjective(DisplaySlot.SIDEBAR) == null) return;

        // Update static items
        // Colors
        if (!MainColor.equalsIgnoreCase(KDFramework.getInstance().Config.getSidebarColor()))
            MainColor = KDFramework.getInstance().Config.getSidebarColor();
        if (!SecondColor.equalsIgnoreCase(this.getSecondColor(KDFramework.getInstance().Config.getSidebarColor())))
            SecondColor = this.getSecondColor(KDFramework.getInstance().Config.getSidebarColor());
        // Displayname
        if (board.getObjective(DisplaySlot.SIDEBAR).getDisplayName().equalsIgnoreCase(KDFramework.getInstance().Config.getSidebarTitle()))
            board.getObjective(DisplaySlot.SIDEBAR).setDisplayName(ChatColor.translateAlternateColorCodes('&', KDFramework.getInstance().Config.getSidebarTitle()));

        Team coins = board.getTeam("coins");
        Team friends = board.getTeam("friends");
        Team region = board.getTeam("region");

        if (coins == null || friends == null || region == null) return;

        String line = PlaceholderAPI.setPlaceholders(p, "%kdf_land%");
        if (line.length() > 14) {
            coins.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor + line.substring(0, 13)));
            coins.setSuffix(ChatColor.translateAlternateColorCodes('&', SecondColor + line.substring(14)));
        } else {
            coins.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor + line));
            coins.setSuffix("");
        }

        line = PlaceholderAPI.setPlaceholders(p, "%kdf_rank%");
        if (line.length() > 14) {
            friends.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor + line.substring(0, 13)));
            friends.setSuffix(ChatColor.translateAlternateColorCodes('&', SecondColor + line.substring(14)));
        } else {
            friends.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor + line));
            friends.setSuffix("");
        }

        line = PlaceholderAPI.setPlaceholders(p, "%kdf_region%");
        if (line.length() > 14) {
            region.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line.substring(0, 13)));
            region.setSuffix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line.substring(14)));
        } else {
            region.setPrefix(ChatColor.translateAlternateColorCodes('&', SecondColor+ line));
            region.setSuffix("");
        }
    }
}