package io.github.deechtezeeuw.kdframework.Region;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.command.CommandSender;

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

        sender.sendMessage("Create region");
    }
}
