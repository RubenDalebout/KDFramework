package io.github.deechtezeeuw.kdframework.Land;

import org.bukkit.command.CommandSender;

public class CreateLand {
    private String name;
    private String prefix;
    private Boolean invite;
    private Integer maximum;

    public CreateLand(CommandSender sender, String KDName) {
        this.name = KDName;
        this.prefix = "&7[&f"+KDName+"&7]";
        this.invite = true;
        this.maximum = 25;

        create_land(sender);
    }

    // Global action
    private void create_land(CommandSender sender) {
        // Check if clan exists

    }
}
