package io.github.deechtezeeuw.kdframework;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class WorldGuarding {

    private KDFramework plugin;

    public WorldGuarding(KDFramework plugin) {
        this.plugin = plugin;
    }

    public ArrayList<Player> entered = new ArrayList<>();
    public ArrayList<Player> left = new ArrayList<>();
    private int test = 0;

    public void enterRegion(Player player) {
        LocalPlayer localPlayer = plugin.worldGuardPlugin.wrapPlayer(player);
        Vector playerVector = localPlayer.getPosition();
        RegionManager regionManager = plugin.worldGuardPlugin.getRegionManager(player.getWorld());
        ApplicableRegionSet applicableRegionSet = regionManager.getApplicableRegions(playerVector);

        for (ProtectedRegion regions : applicableRegionSet) {
            if (regions.contains(playerVector)) {
                if (!entered.contains(player)) {
                    try {
                        left.remove(player);
                        entered.add(player);
                        String regionName = regions.getId();

                        player.sendTitle("Kingdom", regionName, 1, 20, 1);
                    } catch (Exception e) {

                    }
                }
            }
        }

        if (applicableRegionSet.size() == 0) {
            if (!left.contains(player)) {
                try {
                    entered.remove(player);
                    left.add(player);

                    player.sendTitle("Wereld", "Wildernis", 1, 20, 1);
                } catch (Exception e) {

                }
            }
        }
    }
}
