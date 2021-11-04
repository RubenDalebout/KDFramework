package io.github.deechtezeeuw.kdframework.Land;

import io.github.deechtezeeuw.kdframework.KDFramework;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EditLand {
    private KDFramework plugin;
    private CommandSender sender;
    private String label;
    private String[] args;

    public EditLand(KDFramework plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;

        this.edit_check_args();
    }

    public void edit_check_args() {
        // Check kingdom name
        if (args.length >= 3) {
            // Check if kingdom exists
            if(!plugin.SQLSelect.land_exists(args[2])) {
                // Kingdom does not exists
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cbestaat niet!"));
                return;
            }
        } else {
            // Wrong argumentation
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutfief: &4&l/"+label+" "+args[0]+" "+args[1]+" <kingdom> [column] <value>"));
            return;
        }

        // current land values
        Land land = plugin.SQLSelect.land_get_by_name(args[2]);

        // Check what he wants to edit
        if (args.length >= 4) {
            // Check if its not one of those
            if (!args[3].equalsIgnoreCase("prefix") && !args[3].equalsIgnoreCase("invite") && !args[3].equalsIgnoreCase("maximum")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&cJe kan alleen &4&lPrefix&c, &4&lInvite &cof &4&lMaximum &ceditten!"));
                return;
            }
        } else {
            // Wrong argumentation
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutfief: &4&l/"+label+" "+args[0]+" "+args[1]+" "+args[2]+" [column] <value>"));
            return;
        }

        // Check the new value
        if (args.length == 5) {
            // Prefix check
            if (args[3].equalsIgnoreCase("prefix")) {
                // Check if it has more then 36 characters
                if (args[4].length() > 36) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cJe prefix bevat meer dan het maximum van &4&l36 &ckarakters!"));
                    return;
                }

                // Check if its not the same as the current value
                if (land.getPrefix().equalsIgnoreCase(args[4])) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cJe prefix is al &4&l"+args[4]+"&c!"));
                    return;
                }
            }

            // Invite check
            if (args[3].equalsIgnoreCase("invite")) {
                // Check if new value is not true or false
                if (!args[4].equalsIgnoreCase("true") && !args[4].equalsIgnoreCase("false")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cJe invite waarde kan alleen &4&ltrue&c/&4&lfalse &czijn!"));
                    return;
                }

                // Check if its the same as the current value
                Boolean newValue = args[4].equalsIgnoreCase("true") ? true : false;
                if (land.getInvite() == newValue) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cJe invite staat al op &4&l"+land.getInvite()+"&c!"));
                    return;
                }
            }

            // Maximum check
            if (args[3].equalsIgnoreCase("maximum")) {
                // Check if its number is higher then 0
                if (!(Integer.parseInt(args[4]) > 0)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cJe maximum waarde kan alleen &4&l1 of hoger &czijn!"));
                    return;
                }

                // Check if its new number is the same as the current
                if (land.getMaximum() == Integer.parseInt(args[4])) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cJe maximum is al &4&l"+args[4]+"&c!"));
                    return;
                }

                // Check if its new number is not lower then the current members
                if (Integer.parseInt(args[4]) < land.getLeden().size()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cJe nieuwe maximum moet gelijk of hoger zijn dan je leden aantal, je hebt nu &4&l"+land.getLeden().size()+" &cleden!"));
                    return;
                }
            }
        } else {
            // Wrong argumentation
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.Config.getGeneralPrefix() + "&cFoutfief: &4&l/"+label+" "+args[0]+" "+args[1]+" "+args[2]+" "+args[3]+" <value>"));
            return;
        }

        plugin.SQLUpdate.update_land(args[2], args[3], args[4]);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.Config.getGeneralPrefix() + "&aSuccesvol de &2&l"+args[3]+" &aaangepast naar &2&l"+args[4]+" &avan het land &2&l"+args[2]+"&a!"));
        return;
    }
}
