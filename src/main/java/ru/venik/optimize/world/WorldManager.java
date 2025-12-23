/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class WorldManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<String, World> worlds = new ConcurrentHashMap<String, World>();
    private World lobbyWorld;
    private World spawnWorld;
    private World anarchyWorld;

    public WorldManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void initializeWorlds() {
        this.createLobbyWorld();
        this.createSpawnWorld();
        this.createAnarchyWorld();
    }

    private void createLobbyWorld() {
        WorldCreator creator = new WorldCreator("lobby");
        creator.type(WorldType.FLAT);
        creator.generatorSettings("3;minecraft:air;127;decoration");
        try {
            this.lobbyWorld = creator.createWorld();
            if (this.lobbyWorld != null) {
                this.worlds.put("lobby", this.lobbyWorld);
                this.lobbyWorld.setSpawnLocation(0, 64, 0);
                this.plugin.getLogger().info("Lobby world created!");
                this.lobbyWorld.setPVP(false);
                this.lobbyWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                this.lobbyWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                this.lobbyWorld.setTime(6000L);
            }
        }
        catch (Exception e) {
            this.plugin.getLogger().warning("Failed to create lobby world: " + e.getMessage());
        }
    }

    private void createSpawnWorld() {
        WorldCreator creator = new WorldCreator("spawn");
        creator.type(WorldType.NORMAL);
        creator.generateStructures(true);
        try {
            this.spawnWorld = creator.createWorld();
            if (this.spawnWorld != null) {
                this.worlds.put("spawn", this.spawnWorld);
                this.plugin.getLogger().info("Spawn world created!");
                this.generateSpawnPlatform(this.spawnWorld);
            }
        }
        catch (Exception e) {
            this.plugin.getLogger().warning("Failed to create spawn world: " + e.getMessage());
        }
    }

    private void createAnarchyWorld() {
        WorldCreator creator = new WorldCreator("world");
        creator.type(WorldType.NORMAL);
        creator.generateStructures(true);
        try {
            this.anarchyWorld = Bukkit.getWorld((String)"world");
            if (this.anarchyWorld == null) {
                this.anarchyWorld = creator.createWorld();
            }
            if (this.anarchyWorld != null) {
                this.worlds.put("world", this.anarchyWorld);
                this.plugin.getLogger().info("Anarchy world loaded!");
            }
        }
        catch (Exception e) {
            this.plugin.getLogger().warning("Failed to create anarchy world: " + e.getMessage());
        }
    }

    private void generateSpawnPlatform(World world) {
        int centerX = 0;
        int centerZ = 0;
        int y = 100;
        int radius = 15;
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                if (x * x + z * z > radius * radius) continue;
                world.getBlockAt(centerX + x, y, centerZ + z).setType(Material.STONE);
            }
        }
        world.setSpawnLocation(centerX, y + 1, centerZ);
        this.plugin.getLogger().info("Spawn platform generated at " + centerX + ", " + (y + 1) + ", " + centerZ);
    }

    public World getLobbyWorld() {
        return this.lobbyWorld;
    }

    public World getSpawnWorld() {
        return this.spawnWorld;
    }

    public World getAnarchyWorld() {
        return this.anarchyWorld;
    }

    public void teleportToLobby(Player player) {
        if (this.lobbyWorld != null) {
            player.teleport(this.lobbyWorld.getSpawnLocation());
            player.sendMessage("\u00a7a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u0432 \u043b\u043e\u0431\u0431\u0438!");
        } else {
            player.sendMessage("\u00a7c\u041b\u043e\u0431\u0431\u0438 \u043d\u0435 \u0441\u043e\u0437\u0434\u0430\u043d\u043e!");
        }
    }

    public void teleportToSpawn(Player player) {
        if (this.spawnWorld != null) {
            player.teleport(this.spawnWorld.getSpawnLocation());
            player.sendMessage("\u00a7a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043d\u0430 \u0441\u043f\u0430\u0432\u043d!");
        } else {
            player.sendMessage("\u00a7c\u0421\u043f\u0430\u0432\u043d \u043d\u0435 \u0441\u043e\u0437\u0434\u0430\u043d!");
        }
    }
}

