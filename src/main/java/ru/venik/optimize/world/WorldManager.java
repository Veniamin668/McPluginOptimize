package ru.venik.optimize.world;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldManager {

    private final JavaPlugin plugin;

    private final Map<String, World> worlds = new ConcurrentHashMap<>();

    private World lobbyWorld;
    private World spawnWorld;
    private World anarchyWorld;

    public WorldManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    // INITIALIZATION
    // ------------------------------------------------------------

    public void initializeWorlds() {
        lobbyWorld = loadOrCreateVoidWorld("lobby");
        spawnWorld = loadOrCreateNormalWorld("spawn");
        anarchyWorld = loadOrCreateNormalWorld("world");

        if (spawnWorld != null) generateSpawnPlatform(spawnWorld);

        plugin.getLogger().info("WorldManager initialized.");
    }

    // ------------------------------------------------------------
    // WORLD CREATION
    // ------------------------------------------------------------

    private World loadOrCreateNormalWorld(String name) {

        World world = Bukkit.getWorld(name);
        if (world != null) {
            worlds.put(name, world);
            return world;
        }

        WorldCreator creator = new WorldCreator(name);
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.NORMAL);
        creator.generateStructures(true);

        world = creator.createWorld();
        if (world != null) {
            worlds.put(name, world);
            plugin.getLogger().info("World loaded: " + name);
        }

        return world;
    }

    private World loadOrCreateVoidWorld(String name) {

        World world = Bukkit.getWorld(name);
        if (world != null) {
            worlds.put(name, world);
            return world;
        }

        WorldCreator creator = new WorldCreator(name);
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generator(new VoidGenerator());

        world = creator.createWorld();

        if (world != null) {
            worlds.put(name, world);
            world.setSpawnLocation(0, 64, 0);
            world.setPVP(false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setTime(6000);

            plugin.getLogger().info("Void world created: " + name);
        }

        return world;
    }

    // ------------------------------------------------------------
    // SPAWN PLATFORM
    // ------------------------------------------------------------

    private void generateSpawnPlatform(World world) {

        int y = 100;
        int radius = 15;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                if (x * x + z * z > radius * radius) continue;

                world.getBlockAt(x, y, z).setType(Material.STONE);
            }
        }

        world.setSpawnLocation(0, y + 1, 0);

        plugin.getLogger().info("Spawn platform generated.");
    }

    // ------------------------------------------------------------
    // TELEPORTS
    // ------------------------------------------------------------

    public void teleportToLobby(Player p) {
        if (lobbyWorld == null) {
            p.sendMessage("§cЛобби не создано.");
            return;
        }
        p.teleport(lobbyWorld.getSpawnLocation());
        p.sendMessage("§aТелепортация в лобби.");
    }

    public void teleportToSpawn(Player p) {
        if (spawnWorld == null) {
            p.sendMessage("§cСпавн не создан.");
            return;
        }
        p.teleport(spawnWorld.getSpawnLocation());
        p.sendMessage("§aТелепортация на спавн.");
    }

    // ------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------

    public World getLobbyWorld() {
        return lobbyWorld;
    }

    public World getSpawnWorld() {
        return spawnWorld;
    }

    public World getAnarchyWorld() {
        return anarchyWorld;
    }

    public World getWorld(String name) {
        return worlds.get(name.toLowerCase());
    }
}
