/*
 * Copyright (c) 2012, Keeley Hoek
 * Copyright (c) 2016, phit
 * All rights reserved.
 * 
 * Redistribution and use of this software in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 * 
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.stonebound.skybound;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginCommandExecutor implements CommandExecutor {

    private final SkyBoundPlugin plugin;

    public PluginCommandExecutor(SkyBoundPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SkyBoundPlugin.toChat("You have to be a player to excecute that command!"));

            return false;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equals("create")) {
                return create(player);
            } else if (args[0].equals("home")) {
                return home(player);
            } else if (args[0].equals("accept")) {
                return accept(player);
            } else if (args[0].equals("decline")) {
                return decline(player);
            } else if (args[0].equals("leave")) {
                return leave(player);
            } else if (args[0].equals("leader")) {
                return leader(player);
            } else if (args[0].equals("members")) {
                return members(player);
            } else if (args[0].equals("help")) {
                player.sendMessage(SkyBoundPlugin.toChat("Please specify a help page by using \"/island help <page>\". There are two pages in this help guide."));

                return true;
            } else {
                player.sendMessage(SkyBoundPlugin.toChat("Invalid command syntax. Type: /island help"));

                return false;
            }
        } else if (args.length == 2) {
            if (args[0].equals("help")) {
                return help(player, args[1]);
            } else if (args[0].equals("invite")) {
                return invite(player, args[1]);
            } else if (args[0].equals("kick")) {
                return kick(player, args[1]);
            } else if (args[0].equals("promote")) {
                return promote(player, args[1]);
            } else {
                player.sendMessage(SkyBoundPlugin.toChat("Invalid command syntax. Type: /island help"));

                return false;
            }
        } else if (args.length == 3) {
            if (args[0].equals("admin")) {
                return admin(player, args[0], args[1]);
            } else {
                player.sendMessage(SkyBoundPlugin.toChat("Invalid command syntax. Type: /island help"));

                return false;
            }
        } else {
            player.sendMessage(SkyBoundPlugin.toChat("Invalid command syntax. Type: /island help"));

            return false;
        }
    }

    private boolean create(Player player) {
        if (plugin.getPartyByPlayer(player.getName()) != null) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You cannot use this command while in a party."));
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Use \"/island leave\" to leave."));
        } else {
            Island island = plugin.getIslandByName(player.getName());

            if (island != null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You already have an island at " + ChatColor.RESET + island.getX() + ", " + island.getZ()));
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Use \"/island destroy\" to destroy it."));
            } else {
                if (player.hasPermission("skybound.island.create")) {
                    plugin.createIsland(player);

                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " Island Created Successfully!"));
                } else {
                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
                }
            }
        }

        return true;
    }

    private boolean home(Player player) {
        if (!player.hasPermission("skybound.tp.home")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            if (plugin.getIslandByName(player.getName()) == null && plugin.getPartyByPlayer(player.getName()) == null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have an island and aren't in a party."));

                return true;
            }

            plugin.goHome(player);
        }

        return true;
    }

    private boolean help(Player player, String rawPage) {
        try {
            int page = Integer.parseInt(rawPage);

            if (page == 1) {
                player.sendMessage(SkyBoundPlugin.toChat("------------ " + ChatColor.GOLD + "Help: Page 1" + ChatColor.RESET + " ------------"));
                player.sendMessage(SkyBoundPlugin.toChat("/island create - Create a new SkyBlock."));
                player.sendMessage(SkyBoundPlugin.toChat("/island home - Teleport to your SkyBlock"));
                player.sendMessage(SkyBoundPlugin.toChat("/island invite <player> - Invite <player> to your SkyBlock, creating a party."));
                player.sendMessage(SkyBoundPlugin.toChat("/island kick <player> - Kick <player> from your party."));
                player.sendMessage(SkyBoundPlugin.toChat("/island promote <player> - Make <player> the party leader."));
                player.sendMessage(SkyBoundPlugin.toChat("/island accept - Accept a pending invitation."));
                player.sendMessage(SkyBoundPlugin.toChat("/island decline - Decline a pending inviation."));
                player.sendMessage(SkyBoundPlugin.toChat("/island leave - Leave your current party."));
                player.sendMessage(SkyBoundPlugin.toChat("/island leader - Display your party leader."));
                player.sendMessage(SkyBoundPlugin.toChat("/island members - Display your party's members."));
            } else if (page == 2) {
                player.sendMessage(SkyBoundPlugin.toChat("------------ " + ChatColor.GOLD + "Help: Page 2" + ChatColor.RESET + " ------------"));
                player.sendMessage(SkyBoundPlugin.toChat("/island admin destroy <player> - Destroy <player>'s SkyBlock."));
                player.sendMessage(SkyBoundPlugin.toChat("/island admin tp <player> - Teleport to <player>'s SkyBlock."));
                player.sendMessage(SkyBoundPlugin.toChat("/island help - Display this help message."));
            } else {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Invalid page number specified. Please specify a number between 1 and 2 inclusive."));
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Invalid page number specified. Please specify a number between 1 and 2 inclusive."));
        }

        return true;
    }

    private boolean accept(Player player) {
        if (!player.hasPermission("skybound.party.accept")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Party party = plugin.getInvite(player.getName());

            if (party == null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have a pending invite!"));
            } else {
                plugin.clearInvite(player.getName());

                if (party.getMembers().size() + 1 == plugin.getMaxPartySize() && plugin.getMaxPartySize() != 0) {
                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Could not accept the invite. The party would become too large!"));

                    for (String member : party.getMembers()) {
                        if (!member.equals(player.getName())) {
                            Player memberPlayer = Bukkit.getServer().getPlayer(member);

                            if (memberPlayer != null) {
                                memberPlayer.sendMessage(SkyBoundPlugin.toChat(player.getName() + ChatColor.RED + " " + " just tried to join your party but couldn't because it would become too large!"));
                            }
                        }
                    }
                } else {
                    party.addMember(player.getName());

                    Island island = plugin.getIslandByName(player.getName());

                    if (island != null) {
                        player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + "You already have your own island!"));
                        return true;
                    }

                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " Invite Accepted!"));

                    for (String member : party.getMembers()) {
                        if (!member.equals(player.getName())) {
                            Player memberPlayer = Bukkit.getServer().getPlayer(member);

                            if (memberPlayer != null) {
                                memberPlayer.sendMessage(SkyBoundPlugin.toChat("" + player.getName() + ChatColor.GREEN + " just joined your party!"));
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean decline(Player player) {
        if (!player.hasPermission("skybound.party.decline")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Party party = plugin.getInvite(player.getName());

            if (party == null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have a pending invite!"));
            } else {
                plugin.clearInvite(player.getName());

                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " Invite Declined!"));
            }
        }

        return true;
    }

    private boolean leave(Player player) {
        if (!player.hasPermission("skybound.party.leave")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Party party = plugin.getPartyByPlayer(player.getName());

            if (party == null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You aren't in a party!"));
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Use \"/island invite <player>\" to create one."));
            } else {
                boolean wasLeader = party.getLeader().equals(player.getName());

                party.removeMember(player.getName());

                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " Successfully Left Party!"));

                if (party.getMembers().isEmpty()) {
                    plugin.removeParty(party);
                } else {
                    for (String member : party.getMembers()) {
                        if (!member.equals(player.getName())) {
                            Player memberPlayer = Bukkit.getServer().getPlayer(member);

                            if (memberPlayer != null) {
                                memberPlayer.sendMessage(SkyBoundPlugin.toChat("" + player.getName() + ChatColor.GREEN + " just left your party!"));
                            }
                        }
                    }

                    if (wasLeader) {
                        player.sendMessage(SkyBoundPlugin.toChat("" + party.getLeader() + ChatColor.GREEN + " has been made leader!"));

                        Player otherPlayer = Bukkit.getPlayer(party.getLeader());
                        if (otherPlayer != null) {
                            otherPlayer.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " You have been made the leader of your party!"));
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean leader(Player player) {
        if (!player.hasPermission("skybound.party.get.leader")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Party party = plugin.getPartyByPlayer(player.getName());

            if (party == null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You aren't in a party!"));
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Use \"/island invite <player>\" to create one."));
            } else {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " The leader of your party is: " + ChatColor.RESET + party.getLeader()));
            }
        }

        return true;
    }

    private boolean members(Player player) {
        if (!player.hasPermission("skybound.party.get.members")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Party party = plugin.getPartyByPlayer(player.getName());

            if (party == null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You aren't in a party!"));
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Use \"/island invite <player>\" to create one."));
            } else {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " The members of this party are:"));

                for (String playerName : party.getMembers()) {
                    player.sendMessage(SkyBoundPlugin.toChat(playerName));
                }
            }
        }

        return true;
    }

    private boolean admin(Player player, String type, String other) {
        if (type.equals("destroy")) {
            return adminDestroy(player, other);
        } else if (type.equals("tp")) {
            return adminTp(player, other);
        } else {
            player.sendMessage(SkyBoundPlugin.toChat("Invalid command syntax. Type: /island help"));

            return false;
        }
    }

    private boolean adminDestroy(Player player, String other) {
        if (!player.hasPermission("skybound.admin.destroy")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Island island = plugin.getIslandByName(other);

            if (island == null) {
                Party party = plugin.getPartyByPlayer(player.getName());

                if (party == null) {
                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " The specified player does not have an island and is not in a party."));
                } else {
                    island = party.getIsland();
                }
            }

            if (island != null) {
                Party victims = plugin.getPartyByIsland(island);

                if (victims == null) {
                    Player victim = Bukkit.getServer().getPlayer(other);

                    if (victim != null) {
                        victim.sendMessage(SkyBoundPlugin.toChat("" + player.getName() + ChatColor.RED + " just destroyed your SkyBlock."));
                    }

                    plugin.destroyIsland(island);

                } else {
                    for (String member : new ArrayList<String>(victims.getMembers())) {
                        victims.removeMember(member);

                        Player victim = Bukkit.getServer().getPlayer(member);

                        if (victim != null) {
                            victim.sendMessage(SkyBoundPlugin.toChat("" + player.getName() + ChatColor.RED + " just disbanded your party!"));
                        }
                    }

                    plugin.destroyIsland(island);
                    plugin.removeParty(victims);

                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " Island Destroyed Successfully!"));
                }
            }
        }

        return true;
    }

    private boolean adminTp(Player player, String other) {
        if (!player.hasPermission("skybound.admin.tp")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Island island = plugin.getIslandByName(other);

            if (island == null) {
                Party party = plugin.getPartyByPlayer(player.getName());

                if (party == null) {
                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " The specified player does not have an island and is not in a party."));
                } else {
                    island = party.getIsland();
                }
            }

            if (island != null) {
                plugin.teleportToIsland(player, island);

                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " Teleported Successfully!"));
            }
        }

        return true;
    }

    private boolean invite(Player player, String other) {
        if (!player.hasPermission("skybound.party.invite")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Player otherPlayer = Bukkit.getServer().getPlayer(other);

            if (otherPlayer == null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " The specified player is not online!"));
            } else {
                Party inviter = plugin.getInvite(player.getName());

                if (inviter != null) {
                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " The specified player already has a pending invite!"));
                } else {
                    if (plugin.getPartyByPlayer(otherPlayer.getName()) != null) {
                        player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " The specified player is already in a party!"));
                    } else {
                        Party party = plugin.getPartyByPlayer(player.getName());

                        if (party == null) {
                            if (plugin.getIslandByName(player.getName()) == null) {
                                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You have to have an island or be a party leader to do that!"));
                            } else {
                                party = plugin.addParty(plugin.getIslandByName(player.getName()), player.getName());
                            }
                        } else {
                            if (!party.getLeader().equals(player.getName())) {
                                party = null;

                                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You aren't the leader of the party!"));
                            }
                        }

                        if (party != null) {
                            plugin.sendInvite(party, otherPlayer.getName());

                            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " Invitaion Sent!"));

                            if (party.getMembers().size() == 1) {
                                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " A new party was created just for you!"));
                            }

                            otherPlayer.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " You have recieved an invite from: " + ChatColor.RESET + player.getName()));
                            otherPlayer.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " Use \"/island accept\" to accept the invite."));
                            otherPlayer.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " Use \"/island decline\" to decline the invite."));

                            if (plugin.getIslandByName(otherPlayer.getName()) != null) {
                                otherPlayer.sendMessage(SkyBoundPlugin.toChat(ChatColor.YELLOW + " WARNING: By accepting, you will destroy your current island."));
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean kick(Player player, String other) {
        if (!player.hasPermission("skybound.party.kick")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Party party = plugin.getPartyByPlayer(player.getName());

            if (party == null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You aren't in a party!"));
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Use \"/island invite <player>\" to create one."));
            } else {
                if (!party.getLeader().equals(player.getName())) {
                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You aren't the leader of the party!"));
                } else {
                    if (!party.contains(other)) {
                        player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " There is no player with that name in your party!"));
                    } else {
                        party.removeMember(other);

                        player.sendMessage(SkyBoundPlugin.toChat("" + other + ChatColor.GREEN + " has been kicked from your party!"));

                        Player victim = Bukkit.getServer().getPlayer(other);

                        if (victim != null) {
                            victim.sendMessage(SkyBoundPlugin.toChat("" + player.getName() + ChatColor.RED + " has kicked you!"));
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean promote(Player player, String other) {
        if (!player.hasPermission("skybound.party.promote")) {
            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You don't have permission to do that!"));
        } else {
            Party party = plugin.getPartyByPlayer(player.getName());

            if (party == null) {
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You aren't in a party!"));
                player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " Use \"/island invite <player>\" to create one."));
            } else {
                if (!party.getLeader().equals(player.getName())) {
                    player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You aren't the leader of the party!"));
                } else {
                    if (!party.contains(other)) {
                        player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " There is no player with that name in your party!"));
                    } else {
                        if (player.getName().equals(other)) {
                            player.sendMessage(SkyBoundPlugin.toChat(ChatColor.RED + " You already are the party leader!"));
                        } else {
                            party.changeLeader(other);

                            player.sendMessage(SkyBoundPlugin.toChat("" + other + ChatColor.GREEN + " has been made the leader of your party!"));

                            Player otherPlayer = Bukkit.getPlayer(other);
                            if (otherPlayer != null) {
                                otherPlayer.sendMessage(SkyBoundPlugin.toChat(ChatColor.GREEN + " You have been made the leader of your party!"));
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
