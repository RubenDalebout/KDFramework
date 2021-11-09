package io.github.deechtezeeuw.kdframework.Commands;

import io.github.deechtezeeuw.kdframework.GUI.GUILands;
import io.github.deechtezeeuw.kdframework.GUI.GUIRanks;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.CreateLand;
import io.github.deechtezeeuw.kdframework.Land.DeleteLand;
import io.github.deechtezeeuw.kdframework.Land.EditLand;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.CreateRank;
import io.github.deechtezeeuw.kdframework.Rank.DeleteRank;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Set.SetLand;
import io.github.deechtezeeuw.kdframework.Set.SetRank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class KingdomCommand implements CommandExecutor {

    // Global plugin variable
    private KDFramework plugin;

    public KingdomCommand (KDFramework plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("k") || label.equalsIgnoreCase("kingdom")) {
            // If arguments are null
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.Config.getGeneralPrefix() + "&aVoor hulp gebruik &2&l/"+label+" help&a!"));
                return true;
            }

            // Sub commands

            // Land
            if (args[0].equalsIgnoreCase("land")) {
                // Check if user has permission to do k.land
                if (sender.hasPermission("k.land")) {
                    // Check if has more then 1 argument
                    if (args.length > 1) {
                        // Create land
                        if (args[1].equalsIgnoreCase("create")) {
                            // Check if user has permission to do k.land.create
                            if (sender.hasPermission("k.land.create")) {
                                // Check if there is an kingdom name
                                if (args.length == 3) {
                                    new CreateLand(plugin, sender, args[2]);
                                    return true;
                                }
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " " + args[1] + " <kd-naam>"));
                                return true;
                            } else {
                                // No permission to do k.land.create
                                plugin.Config.noPermission(sender);
                                return true;
                            }
                        }

                        // Edit land
                        if (args[1].equalsIgnoreCase("edit")) {
                            // Check if user has permission to edit lands
                            if (sender.hasPermission("k.land.edit")) {
                                // Check if kingdom is argumented
                                if (args.length >= 3) {
                                    new EditLand(plugin, sender, label, args);
                                    return true;
                                } else {
                                    // Wrong argumentation
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.Config.getGeneralPrefix() + "&cFoutfief: &4&l/"+label+" "+args[0]+" "+args[1]+" <kingdom> [column] <value>"));
                                    return true;
                                }
                            }
                        }

                        // Delete land
                        if (args[1].equalsIgnoreCase("delete")) {
                            // Check if user has permission to delete lands
                            if (sender.hasPermission("k.land.delete")) {
                                // Check if name of kingdom has an argument
                                if (args.length == 3) {
                                    new DeleteLand(plugin, sender, args[2]);
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " " + args[1] + " <kd-naam>"));
                                    return true;
                                }
                            } else {
                                // No permission to do k.land.delete
                                plugin.Config.noPermission(sender);
                                return true;
                            }
                        }

                        // Info land
                        if (args[1].equalsIgnoreCase("info")) {
                            // Check if user has permission to do that
                            if (sender.hasPermission("k.land.info")) {
                                // Check if there is an kingdom given
                                if (args.length == 3) {
                                    // Check if kingdom exists
                                    if (plugin.SQLSelect.land_exists(args[2])) {
                                        // Land does exists
                                        Land land = plugin.SQLSelect.land_get_by_name(args[2]);
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&7&l(&2&l------------ "+plugin.Config.getGeneralPrefix()+"&a------------&7&l)"));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&aKingdom: &2&l"+land.getName()+"&a."));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&aPrefix: &2&l"+land.getPrefix()+"&a."));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&aInvite: &2&l"+land.getInvite()+"&a."));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&aMaximum: &2&l"+land.getMaximum()+"&a."));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&aLeiding: &2&l"+land.getLeiding()+"&a."));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&aDefault: &2&l"+land.get_defaultRank().getName()+"&a."));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&aRanks: &2&l"+land.getRanks().size()+"&a."));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&aLeden: &2&l"+land.getLeden().size()+"&a."));
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&7&l(&2&l------------ "+plugin.Config.getGeneralPrefix()+"&a------------&7&l)"));
                                        return true;
                                    } else {
                                        // Land does not exists
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cbestaat niet!"));
                                        return true;
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " " + args[1] + " <kd-naam>"));
                                    return true;
                                }
                            } else {
                                // No permission to do k.land.info
                                plugin.Config.noPermission(sender);
                                return true;
                            }
                        }

                        // Send message that arguments can have [create/edit/delete/info]
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " [create/edit/delete/info]"));
                        return true;
                    } else {
                        // Send message that arguments can have [create/edit/delete/info]
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " [create/edit/delete/info]"));
                        return true;
                    }
                } else {
                    // No permission to do k.land
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }

            // List
            if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("lijst") || args[0].equalsIgnoreCase("kingdoms") || args[0].equalsIgnoreCase("lands")) {
                if (sender.hasPermission("k.lands")) {
                    new GUILands(plugin, sender, label, args);
                    return true;
                } else {
                    // No permission to do k.rank
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }

            // Rank
            if (args[0].equalsIgnoreCase("rank")) {
                if (sender.hasPermission("k.rank")) {
                    // check if user has second argument
                    if (!(args.length >= 2)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " [create/edit/delete]"));
                        return true;
                    }

                    // Check if second argument is create/edit/delete
                    if (args[1].equalsIgnoreCase("create")) {
                        if (sender.hasPermission("k.rank.create") || sender.hasPermission("k.rank.create.others")) {
                            new CreateRank(plugin, sender, label, args);
                            return true;
                        } else {
                            // No permission to do k.rank.create
                            plugin.Config.noPermission(sender);
                            return true;
                        }
                    }

                    if (args[1].equalsIgnoreCase("edit")) {
                        sender.sendMessage("edit rank");
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("delete")) {
                        if (sender.hasPermission("k.rank.delete") || sender.hasPermission("k.rank.delete.others")) {
                            new DeleteRank(plugin, sender, label, args);
                            return true;
                        } else {
                            // No permission to do k.rank.create
                            plugin.Config.noPermission(sender);
                            return true;
                        }
                    }

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/" + label + " " + args[0] + " [create/edit/delete]"));
                    return true;
                } else {
                    // No permission to do k.rank
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }

            // Ranks
            if (args[0].equalsIgnoreCase("ranks")) {
                if (sender.hasPermission("k.ranks") || sender.hasPermission("k.ranks.others")) {
                    new GUIRanks(plugin, sender, label, args);
                    return true;
                } else {
                    // No permission to do k.rank
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }

            // Reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("k.reload")) {
                    plugin.Config.reloadVariables(sender);
                } else {
                    plugin.Config.noPermission(sender);
                }
            }

            // Set
            if (args[0].equalsIgnoreCase("set")) {
                // Check if user has permission to do set
                if (sender.hasPermission("k.set")) {
                    // Check if it has more then 2 argument
                    if (args.length >= 2) {
                        // Check if its set land
                        if (args[1].equalsIgnoreCase("land")) {
                            new SetLand(plugin, sender, label, args);
                            return true;
                        }

                        // Check if its set rank
                        if (args[1].equalsIgnoreCase("rank")) {
                            if (sender.hasPermission("k.set.rank") || sender.hasPermission("k.set.rank.all")) {
                                new SetRank(plugin, sender, label, args);
                                return true;
                            } else {
                                plugin.Config.noPermission(sender);
                                return true;
                            }
                        }
                    } else {
                        // Wrong arguments
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" "+args[0]+" [land/rank] <speler> <value>"));
                        return true;
                    }
                } else {
                    // No permissions
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }
        }

        return true;
    }
}