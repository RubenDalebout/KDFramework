package io.github.deechtezeeuw.kdframework.Commands;

import com.sk89q.worldguard.protection.flags.Flag;
import io.github.deechtezeeuw.kdframework.GUI.*;
import io.github.deechtezeeuw.kdframework.Invite.CreateInvite;
import io.github.deechtezeeuw.kdframework.Invite.DeleteInvite;
import io.github.deechtezeeuw.kdframework.KDFramework;
import io.github.deechtezeeuw.kdframework.Land.CreateLand;
import io.github.deechtezeeuw.kdframework.Land.DeleteLand;
import io.github.deechtezeeuw.kdframework.Land.EditLand;
import io.github.deechtezeeuw.kdframework.Land.Land;
import io.github.deechtezeeuw.kdframework.Rank.CreateRank;
import io.github.deechtezeeuw.kdframework.Rank.DeleteRank;
import io.github.deechtezeeuw.kdframework.Rank.EditRank;
import io.github.deechtezeeuw.kdframework.Rank.Rank;
import io.github.deechtezeeuw.kdframework.Region.createRegion;
import io.github.deechtezeeuw.kdframework.Region.deleteRegion;
import io.github.deechtezeeuw.kdframework.Relaties.RelateAlly;
import io.github.deechtezeeuw.kdframework.Relaties.RelateEnemy;
import io.github.deechtezeeuw.kdframework.Relaties.RelateNeutral;
import io.github.deechtezeeuw.kdframework.Set.SetLand;
import io.github.deechtezeeuw.kdframework.Set.SetRank;
import io.github.deechtezeeuw.kdframework.Set.SetSpawn;
import io.github.deechtezeeuw.kdframework.Set.SetWorldSpawn;
import io.github.deechtezeeuw.kdframework.Speler.Speler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KingdomCommand implements CommandExecutor {

    // Global plugin variable
    private KDFramework plugin;
    private Integer Timer = 0;

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

            // Ally
            if (args[0].equalsIgnoreCase("ally") || args[0].equalsIgnoreCase("alliantie")) {
                if (sender.hasPermission("k.ally")) {
                    new RelateAlly(sender, label, args);
                    return true;
                } else {
                    // No permission to do k.ally
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }

            // Invite
            if (args[0].equalsIgnoreCase("invite")) {
                // Check if there ar at least 2 arguments
                if (args.length >= 2) {
                    // Check if arg[1] is add
                    if (args[1].equalsIgnoreCase("add")) {
                        // Check if user has permission to do k.invite.add or k.invite.add.other
                        if (sender.hasPermission("k.invite.add") || sender.hasPermission("k.invite.add.other")) {
                            new CreateInvite(plugin, sender, label, args);
                            return true;
                        } else {
                            plugin.Config.noPermission(sender);
                            return true;
                        }
                    }
                    // check if args[1] is remove
                    if (args[1].equalsIgnoreCase("remove")) {
                        // Check if user has permission to do k.invite.remove or k.invite.remove.other
                        if (sender.hasPermission("k.invite.remove") || sender.hasPermission("k.invite.remove.other")) {
                            new DeleteInvite(plugin, sender, label, args);
                            return true;
                        } else {
                            plugin.Config.noPermission(sender);
                            return true;
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" invite [add/remove]"));
                }
            }

            // Invites
            if (args[0].equalsIgnoreCase("invites")) {
                if (sender.hasPermission("k.invites") || sender.hasPermission("k.invites.other")) {
                    new GUIInvites(plugin, sender, label, args);
                    return true;
                } else {
                    // No permission to do k.rank
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }


            // Join
            if (args[0].equalsIgnoreCase("join")) {
                if (sender.hasPermission("k.join")) {
                    if (args.length == 2) {
                        new KingdomJoin(plugin, sender, label, args);
                        return true;
                    } else {
                        plugin.guiJoin.GUIInstall(sender, label, args);
                        return true;
                    }
                } else {
                    // No permission to do k.join
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }

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

            // Neutraal
            if (args[0].equalsIgnoreCase("neutraal") || args[0].equalsIgnoreCase("neutral")) {
                if (sender.hasPermission("k.neutraal")) {
                    new RelateNeutral(sender, label, args);
                    return true;
                } else {
                    // No permission to do k.ally
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
                        if (sender.hasPermission("k.rank.create") || sender.hasPermission("k.rank.create.other")) {
                            new CreateRank(plugin, sender, label, args);
                            return true;
                        } else {
                            // No permission to do k.rank.create
                            plugin.Config.noPermission(sender);
                            return true;
                        }
                    }

                    if (args[1].equalsIgnoreCase("edit")) {
                        if (sender.hasPermission("k.rank.edit") || sender.hasPermission("k.rank.edit.other")) {
                            new EditRank(plugin, sender, label, args);
                            return true;
                        } else {
                            // No permission to do k.rank.edit
                            plugin.Config.noPermission(sender);
                            return true;
                        }
                    }

                    if (args[1].equalsIgnoreCase("delete")) {
                        if (sender.hasPermission("k.rank.delete") || sender.hasPermission("k.rank.delete.other")) {
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
                if (sender.hasPermission("k.ranks") || sender.hasPermission("k.ranks.other")) {
                    new GUIRanks(plugin, sender, label, args);
                    return true;
                } else {
                    // No permission to do k.rank
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }

            // Relations
            if (args[0].equalsIgnoreCase("relations")) {
                if (sender.hasPermission("k.relations") || sender.hasPermission("k.relations.other")) {
                    new GUIRelations(plugin, sender, label, args);
                    return true;
                } else {
                    // No permission to do k.relations
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }

            // Region
            if (args[0].equalsIgnoreCase("region")) {
                if (sender.hasPermission("k.region")) {
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("create")) {
                            if (sender.hasPermission("k.region.create")) {
                                new createRegion(plugin, sender, label, args);
                                return true;
                            } else {
                                // No permission to do k.region.create
                                plugin.Config.noPermission(sender);
                                return true;
                            }
                        }

                        if (args[1].equalsIgnoreCase("delete")) {
                            if (sender.hasPermission("k.region.delete")) {
                                new deleteRegion(plugin, sender, label, args);
                                return true;
                            } else {
                                // No permission to do k.region.delete
                                plugin.Config.noPermission(sender);
                                return true;
                            }
                        }
                    }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cFoutief: &4&l/"+label+" "+args[0]+" [create/delete]"));
                    return true;
                } else {
                    // No permission to do k.region
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

            // SB (toggle scoreboard)
            if (args[0].equalsIgnoreCase("sb")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cJe kan alleen in-game je scoreboard aan-/uitzetten!"));
                    return true;
                }
                Player player = (Player) sender;
                if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) {
                    KDFramework.getInstance().sidebar.setSidebar(player);
                    KDFramework.getInstance().sidebar.runnable(player);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&aJe &2&lscoreboard &ais aangezet!"));
                    return true;
                } else {
                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.Config.getGeneralPrefix() + "&cJe &4&lscoreboard &cis uitgezet!"));
                    return true;
                }
            }

            // Set
            if (args[0].equalsIgnoreCase("set")) {
                // Check if it has more then 2 argument
                if (args.length >= 2) {
                    // Check if its set land
                    if (args[1].equalsIgnoreCase("land")) {
                       new SetLand(plugin, sender, label, args);
                       return true;
                    }

                    // Check if its set rank
                    if (args[1].equalsIgnoreCase("rank")) {
                        if (sender.hasPermission("k.set.rank") || sender.hasPermission("k.set.rank.other")) {
                            new SetRank(plugin, sender, label, args);
                            return true;
                        } else {
                            plugin.Config.noPermission(sender);
                            return true;
                        }
                    }

                    // Check if its set spawn
                    if (args[1].equalsIgnoreCase("spawn")) {
                        if (sender.hasPermission("k.set.spawn") || sender.hasPermission("k.set.spawn.other")) {
                            new SetSpawn(plugin, sender, label, args);
                            return true;
                        } else {
                            plugin.Config.noPermission(sender);
                            return true;
                        }
                    }

                    // Check if its set worldspawn
                    if (args[1].equalsIgnoreCase("worldspawn")) {
                        if (sender.hasPermission("k.set.worldspawn")) {
                            new SetWorldSpawn(plugin, sender, label, args);
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
            }

            // Spawn
            if (args[0].equalsIgnoreCase("spawn")) {
                if (sender.hasPermission("k.spawn")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        // Check if user is in database
                        if (!plugin.SQLSelect.player_exists(player)) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.Config.getGeneralPrefix() + "&cIn verband met veiligheids redenen kunnen wij niet toestaan dat u niet bestaat in onze database!"));
                            return true;
                        }
                        Speler speler = plugin.SQLSelect.player_get_by_name(player.getName());
                        Land land = null;
                        if (sender.hasPermission("k.spawn.other") && args.length > 1) {
                            String OtherKD = args[1];
                            if (!plugin.SQLSelect.land_exists(OtherKD)) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.Config.getGeneralPrefix() + "&cHet land &4&l"+args[2]+" &cbestaat niet!"));
                                return true;
                            }
                            land = plugin.SQLSelect.land_get_by_name(OtherKD);
                        } else {
                            // Check if user is in a land
                            if (speler == null || speler.getLand() == null) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.Config.getGeneralPrefix() + "&cJe moet in een land zitten op &4&l/k spawn &cte kunnen!"));
                                return true;
                            }
                            land = plugin.SQLSelect.land_get_by_player(speler);
                        }

                        // check if land has an spawn
                        if (land.getSpawn() == null) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.Config.getGeneralPrefix() + "&cHet kingdom &4&l"+land.getName()+" &cheeft nog geen spawn!"));
                            return true;
                        }
                        List<String> Locatie = new ArrayList<>();
                        for (String a : land.getSpawn().split("/")) {
                            Locatie.add(a);
                        }
                        Location LocationOld = player.getLocation();
                        Location tpLocation = player.getLocation();
                        tpLocation.setX(Integer.parseInt(Locatie.get(0)));tpLocation.setY(Integer.parseInt(Locatie.get(1)));tpLocation.setZ(Integer.parseInt(Locatie.get(2)));

                        // Cooldown
                        Timer = 0;
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.Config.getGeneralPrefix() + "&aJe word over &2&l"+plugin.Config.getGeneralSpawnCooldown()+" &aseconden geteleporteerd! Niet bewegen!"));
                        Integer cooldown = plugin.Config.getGeneralSpawnCooldown();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                //methods
                                if (LocationOld.getBlockZ() != player.getLocation().getBlockZ() || LocationOld.getBlockX() != player.getLocation().getBlockX()) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.Config.getGeneralPrefix() + "&cTeleportatie afgebroken! Je bewoog."));
                                    cancel();
                                }
                                if (Timer != cooldown) {
                                    Timer++;
                                }

                                if (Timer == cooldown) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.Config.getGeneralPrefix() + "&aJe word geteleporteerd!"));
                                    player.teleport(tpLocation);
                                    cancel();
                                }
                            }
                        }.runTaskTimer(plugin, 0, 20);
                    }
                } else {
                    plugin.Config.noPermission(sender);
                }
            }

            // Vijandig
            if (args[0].equalsIgnoreCase("vijand") || args[0].equalsIgnoreCase("enemy")) {
                if (sender.hasPermission("k.enemy")) {
                    new RelateEnemy(sender, label, args);
                    return true;
                } else {
                    // No permission to do k.ally
                    plugin.Config.noPermission(sender);
                    return true;
                }
            }
        }

        return true;
    }
}