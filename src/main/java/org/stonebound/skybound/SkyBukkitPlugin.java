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

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyBukkitPlugin extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(Island.class);
        ConfigurationSerialization.registerClass(Party.class);
    }
    private static final Logger log = Logger.getLogger("Minecraft");

    public static String toChat(String msg) {
        return ChatColor.GOLD + "[SkyBukkit] " + ChatColor.RESET + msg;
    }
    private File dataFile;
    private Island spawnIsland;
    private int partyMaxSize, islandHeight, islandSpacing, islandSize, rootsBlockID;
    private World world;
    private Map<String, Party> invites;
    private Stack<Island> orphaned;
    private Island last;
    private ArrayList<Island> islands;
    private ArrayList<Party> parties;

    @Override
    public void onEnable() {
        dataFile = new File(getDataFolder(), "data.yml");
        invites = new HashMap();

        parseConfig();
        backupConfig();

        parseData();
        backupData();

        getCommand("island").setExecutor(new PluginCommandExecutor(this));
        getServer().getPluginManager().registerEvents(new PluginEventListener(this), this);

        log.log(Level.INFO, getDescription().getFullName().concat(" is enabled! By Keeley Hoek (escortkeel)"));
    }

    @Override
    public void onDisable() {
        log.log(Level.INFO, getDescription().getFullName().concat(" is disabled!"));
    }

    private void parseConfig() {
        if (getConfig().getConfigurationSection("center") != null
                && ((ConfigurationSection) getConfig().getConfigurationSection("center")).get("x") instanceof Integer
                && ((ConfigurationSection) getConfig().getConfigurationSection("center")).get("z") instanceof Integer) {
            spawnIsland = new Island(null,
                    (Integer) ((ConfigurationSection) getConfig().getConfigurationSection("center")).get("x"),
                    (Integer) ((ConfigurationSection) getConfig().getConfigurationSection("center")).get("z"));
        } else {
            spawnIsland = new Island(null, 0, 0);

            log.log(Level.WARNING, toChat("Invalid or no center island coords specified. Defaulting to (0,0)."));
        }

        if (getConfig().get("world") instanceof String) {
            world = Bukkit.getWorld((String) getConfig().get("world"));
        }

        if (world == null) {
            world = Bukkit.getWorld("world");

            if (world == null) {
                world = Bukkit.getWorlds().get(0);

                log.log(Level.WARNING, toChat("Invalid or no world specified in config and the default (called \"world\") does not exist either. Defaulting to the server spawn world."));
            } else {
                log.log(Level.WARNING, toChat("Invalid or no world name specified. Defaulting to \"world\"."));
            }
        }

        if (getConfig().get("partyMaxSize") instanceof Integer) {
            partyMaxSize = (Integer) getConfig().get("partyMaxSize");

            if (partyMaxSize < 2 && partyMaxSize != 0) {
                partyMaxSize = 0;

                log.log(Level.WARNING, toChat("Max party size must be greater than 1 or equal to 0. Defaulting to INF (0)."));
            }
        } else {
            partyMaxSize = 0;

            log.log(Level.WARNING, toChat("Invalid or no max party size specified. Defaulting to INF (0)."));
        }

        if (getConfig().get("islandHeight") instanceof Integer) {
            islandHeight = (Integer) getConfig().get("islandHeight");
        } else {
            islandHeight = 160;

            log.log(Level.WARNING, toChat("Invalid or no island height specified. Defaulting to 128."));
        }

        if (getConfig().get("islandSpacing") instanceof Integer) {
            islandSpacing = (Integer) getConfig().get("islandSpacing");
        } else {
            islandSpacing = 120;

            log.log(Level.WARNING, toChat("Invalid or no island spacing specified. Defaulting to 120."));
        }

        if (getConfig().get("islandSize") instanceof Integer) {
            islandSize = (Integer) getConfig().get("islandSize");
        } else {
            islandSize = 100;

            log.log(Level.WARNING, toChat("Invalid or no island protection zone size specified. Defaulting to 100."));
        }

        if (getConfig().get("rootsBlockID") instanceof Integer) {
            rootsBlockID = (Integer) getConfig().get("rootsBlockID");
        } else {
            rootsBlockID = 17;

            log.log(Level.WARNING, toChat("Invalid or no Botania roots block id specified. Defaulting to Vanilla logs."));
        }

    }

    private void backupConfig() {
        try {
            getConfig().loadFromString("");
        } catch (InvalidConfigurationException ex) {
        }

        ConfigurationSection centerSection = getConfig().createSection("center");
        centerSection.set("x", spawnIsland.getX());
        centerSection.set("z", spawnIsland.getZ());

        if (world == null) {
            getConfig().set("world", "world");
        } else {
            getConfig().set("world", world.getName());
        }

        getConfig().set("partyMaxSize", partyMaxSize);

        getConfig().set("islandHeight", islandHeight);
        getConfig().set("islandSpacing", islandSpacing);
        getConfig().set("islandSize", islandSize);

        getConfig().set("rootsBlockID", islandSize);

        ArrayList itemList = new ArrayList();

        saveConfig();
    }

    private void parseData() {
        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

        last = spawnIsland;
        if (data.getConfigurationSection("lastIsland") != null) {
            last = (Island) data.get("lastIsland");
        }

        islands = new ArrayList<Island>();
        if (data.getList("islands") != null) {
            islands = new ArrayList(data.getList("islands"));
        }

        orphaned = new Stack<Island>();
        if (data.getList("orphaned") != null) {
            for (Object entry : data.getList("orphaned")) {
                orphaned.add((Island) entry);
            }
        }

        parties = new ArrayList<Party>();
        if (data.getList("parties") != null) {
            parties = new ArrayList(data.getList("parties"));
        }

    }

    private void backupData() {
        dataFile.delete();

        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

        data.options().header("This is the SkyBukkit data file. Please do not modify it unless you know what you are doing.");

        data.set("lastIsland", last);
        data.set("islands", islands);
        data.set("orphaned", orphaned);
        data.set("parties", parties);

        try {
            data.save(dataFile);
        } catch (IOException ex) {
            log.log(Level.SEVERE, toChat("Failed to save data file."), ex);
        }
    }

    public void createIsland(Player player) {
        Island island = getOrphanedIsland();

        if (island == null) {
            do {
                island = pickNewLocation(player.getName());
                last = island;
            } while (islands.contains(island));
        }

        island.setOwner(player.getName());

        // This is similar to how Botania itself generates an island in GoG. This is being done to avoid a soft dependency.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 3; k++) {
                    world.getBlockAt(island.getX() - 1 + i, islandHeight - j, island.getZ() - 1 + k).setType(Material.GRASS);
                }
            }
        }
        for (int c = -3; c < 2; c++) {
            for (int d = -3; d < 2; d++) {
                for (int e = 3; e < 5; e++) {
                    world.getBlockAt(island.getX() + (c) + 1, islandHeight + e, d + (island.getZ()) + 1).setType(Material.LEAVES);
                }
            }
        }
        for (int c = -2; c < 1; c++) {
            for (int d = -2; d < 1; d++) {
                world.getBlockAt(island.getX() + (c) + 1, islandHeight + 5, d + (island.getZ()) + 1).setType(Material.LEAVES);
            }
        }

        world.getBlockAt(island.getX(), islandHeight + 6, island.getZ()).setType(Material.LEAVES);
        world.getBlockAt(island.getX() + 1, islandHeight + 6, island.getZ()).setType(Material.LEAVES);
        world.getBlockAt(island.getX(), islandHeight + 6, island.getZ() + 1).setType(Material.LEAVES);
        world.getBlockAt(island.getX() - 1, islandHeight + 6, island.getZ()).setType(Material.LEAVES);
        world.getBlockAt(island.getX(), islandHeight + 6, island.getZ() - 1).setType(Material.LEAVES);
        world.getBlockAt(island.getX() + 2, islandHeight + 4, island.getZ() + 2).setType(Material.AIR);

        for (int c = 0; c < 5; c++) {
            world.getBlockAt(island.getX(), islandHeight + c + 1, island.getZ()).setType(Material.LOG);
        }
        world.getBlockAt(island.getX() - 1, islandHeight - 1, island.getZ()).setType(Material.WATER);
        int[][] roots = new int[][]{
                {-1, -2, -1},
                {-1, -4, -2},
                {-2, -3, -1},
                {-2, -3, -2},
                {1, -3, -1},
                {1, -4, -1},
                {2, -4, -1},
                {2, -4, 0},
                {3, -5, 0},
                {0, -2, 1},
                {0, -3, 2},
                {0, -4, 3},
                {1, -4, 3},
                {1, -5, 2},
                {1, -2, 0},
        };
        world.getBlockAt(island.getX() + 1, islandHeight + 3, island.getZ() + 1).setTypeId(rootsBlockID);
        world.getBlockAt(island.getX(), islandHeight - 3, island.getZ()).setType(Material.BEDROCK);
        for (int[] pos : roots) {
            world.getBlockAt(island.getX() + pos[0], islandHeight + pos[1], island.getZ() + pos[2]).setTypeId(rootsBlockID);
        }


        islands.add(island);

        final double x = islandSize / 2;
        final double z = islandSize / 2;

        for (Entity entity : world.getEntities()) {
            if (((x + islandSize / 2) > entity.getLocation().getX()
                    || entity.getLocation().getX() > (x - islandSize / 2))
                    || ((z + islandSize / 2) > entity.getLocation().getZ()
                    || entity.getLocation().getZ() > (z - islandSize / 2))) {
                entity.remove();
            }
        }

        backupData();
    }

    public void destroyIsland(Island island) {
        if (islands.contains(island)) {
            for (int x = island.getX() - islandSize / 2; x < island.getX() + islandSize / 2; x++) {
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    for (int z = island.getZ() - islandSize / 2; z < island.getZ() + islandSize / 2; z++) {
                        Block block = world.getBlockAt(x, y, z);

                        if (block.getTypeId() != 0) {
                            block.setTypeId(0);
                        }
                    }
                }
            }

            orphaned.push(island);
            islands.remove(island);
            backupData();
        }
    }

    public Island pickNewLocation(String owner) {
        int x = last.getX();
        int z = last.getZ();

        if (last.getX() < last.getZ()) {
            if (((-1) * last.getX()) < last.getZ()) {
                x += islandSpacing;
                return new Island(owner, x, z);
            }

            z += islandSpacing;

            return new Island(owner, x, z);
        }

        if (last.getX() > last.getZ()) {
            if (((-1) * last.getX()) >= last.getZ()) {
                x -= islandSpacing;
                return new Island(owner, x, z);
            }

            z -= islandSpacing;

            return new Island(owner, x, z);
        }

        if (last.getX() <= 0) {
            z += islandSpacing;

            return new Island(owner, x, z);
        }

        z -= islandSpacing;

        return new Island(owner, x, z);
    }

    public Island getIslandByName(String player) {
        for (Island island : islands) {
            if (island.getOwner().equals(player)) {
                return island;
            }
        }

        return null;
    }

    public Island getOrphanedIsland() {
        if (!orphaned.empty()) {
            Island orphan = orphaned.pop();

            backupData();

            return orphan;
        }

        return null;
    }

    public Party addParty(Island island, String player) {
        Party party = new Party(island);
        party.addMember(player);

        parties.add(party);

        backupData();

        return party;
    }

    public void removeParty(Party party) {
        parties.remove(party);

        backupData();
    }

    public Party getPartyByPlayer(String player) {
        for (Party party : parties) {
            for (String name : party.getMembers()) {
                if (name.equalsIgnoreCase(player)) {
                    return party;
                }
            }
        }

        return null;
    }

    public Party getPartyByIsland(Island island) {
        for (Party party : parties) {
            if (party.getIsland().equals(island)) {
                return party;
            }
        }

        return null;
    }

    public void sendInvite(Party party, String player) {
        invites.put(player, party);
    }

    public Party getInvite(String player) {
        return invites.get(player);
    }

    public void clearInvite(String player) {
        invites.remove(player);
    }

    public void goHome(Player player) {
        Island island = getIslandByName(player.getName());

        if (island == null) {
            Party party = getPartyByPlayer(player.getName());

            if (party == null) {
                throw new RuntimeException("Attempted to teleport a player to his island when he has no island and is not in a party.");
            } else {
                island = party.getIsland();
            }
        }

        teleportToIsland(player, island);
        backupData();
    }

    private boolean canPlace(World world, int x, int y, int z) {
        if (world.getBlockTypeIdAt(x, y, z) == 0 && world.getBlockTypeIdAt(x, y + 1, z) == 0) {
            return true;
        }

        return false;
    }

    public Location placePlayer(World world, int x, int y, int z) {
        while (!canPlace(world, x, y, z)) {
            y++;
        }

        return new Location(world, x, y, z);
    }

    private void teleportTo(Player player, Location location) {
        world.loadChunk(location.getBlockX(), location.getBlockZ());
        player.teleport(location);
    }

    public void teleportToIsland(Player player, Island island) {
        teleportTo(player, placePlayer(world, island.getX(), islandHeight, island.getZ()));
    }

    public int getMaxPartySize() {
        return partyMaxSize;
    }

    public int getIslandHeight() {
        return islandHeight;
    }

    public World getSkyWorld() {
        return world;
    }
}
