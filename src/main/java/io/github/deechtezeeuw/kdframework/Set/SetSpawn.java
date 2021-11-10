package io.github.deechtezeeuw.kdframework.Set;

import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public SetSpawn (KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        this.checkArguments();
    }

    private void checkArguments() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cJe kan alleen in-game de spawn bepalen!"));
            return;
        }

        Player player = (Player) sender;
        String Location = player.getLocation().getBlockX()+"/"+player.getLocation().getBlockY()+"/"+player.getLocation().getBlockZ();

        // Check if user is in database
        if (!plugin.SQLSelect.player_exists(player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cIn verband met veiligheids redenen kunnen wij niet toestaan dat u niet bestaat in onze database!"));
            return;
        }

        Speler speler = plugin.SQLSelect.player_get_by_name(player.getName());
        Land land = null;

        if (sender.hasPermission("k.set.spawn.other") && args.length > 2) {
            // Check if argument 2 is an kingdom
            if (!plugin.SQLSelect.land_exists(args[2])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cbestaat niet!"));
                return;
            }
            land = plugin.SQLSelect.land_get_by_name(args[2]);
        } else {
            // Check if user is in a land
            if (speler == null || speler.getLand() == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cU kunt alleen spawn aanmaken als u lid bent van een land!"));
                return;
            }
            land = plugin.SQLSelect.land_get_by_player(speler);
        }

        // Update spawn from land
        plugin.SQLUpdate.update_spawn_land(land, Location);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + "&aSuccesvol de spawn gewijzigd naar &2&l"+Location.replace("/", " ")+" &avan het land &2&l"+land.getName()+"&a!"));
        return;
    }
}
