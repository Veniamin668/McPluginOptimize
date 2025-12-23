package ru.venik.optimize.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.UUID;import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NPCManager {
    private final JavaPlugin plugin;
    private final Map<String, UUID> spawned = new HashMap<>();
    private BukkitTask spawnTask;
    private final Random random = new Random();

    public NPCManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        this.loadFromConfig();
    }

    public void stop() {
        // remove spawned entities
        for (UUID id : spawned.values()) {
            Entity e = Bukkit.getEntity(id);
            if (e != null && !e.isDead()) e.remove();
        }
        spawned.clear();
        if (spawnTask != null) spawnTask.cancel();
    }

    public void loadFromConfig() {
        if (!plugin.getConfig().getBoolean("npc.enabled", true)) return;
        if (!plugin.getConfig().isConfigurationSection("npc.list")) return;
        for (String key : plugin.getConfig().getConfigurationSection("npc.list").getKeys(false)) {
            String path = "npc.list." + key;
            String world = plugin.getConfig().getString(path + ".world", "world");
            double x = plugin.getConfig().getDouble(path + ".x", 0.0);
            double y = plugin.getConfig().getDouble(path + ".y", 64.0);
            double z = plugin.getConfig().getDouble(path + ".z", 0.0);
            boolean randomize = plugin.getConfig().getBoolean(path + ".randomize", true);
            Location loc = new Location(Bukkit.getWorld(world), x, y, z);
            spawnNPC(key, loc, randomize);
        }
    }

    public void spawnNPC(String id, Location loc, boolean randomize) {
        if (Bukkit.getWorld(loc.getWorld().getName()) == null) return;
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setCustomNameVisible(true);
        String name = "NPC-" + id;
        if (randomize) {
            String[] variants = plugin.getConfig().getString("npc.variants", "ModelA,ModelB,ModelC").split(",");
            name = variants[random.nextInt(variants.length)].trim();
        }
        villager.setCustomName(name);
        spawned.put(id, villager.getUniqueId());
    }

    public void createAndSave(String id, Location loc, boolean randomize) {
        String path = "npc.list." + id;
        plugin.getConfig().set(path + ".world", loc.getWorld().getName());
        plugin.getConfig().set(path + ".x", loc.getX());
        plugin.getConfig().set(path + ".y", loc.getY());
        plugin.getConfig().set(path + ".z", loc.getZ());
        plugin.getConfig().set(path + ".randomize", randomize);
        plugin.saveConfig();
        spawnNPC(id, loc, randomize);
    }

    public void remove(String id) {
        UUID entId = spawned.remove(id);
        if (entId != null) {
            Entity e = Bukkit.getEntity(entId);
            if (e != null && !e.isDead()) e.remove();
        }
        plugin.getConfig().set("npc.list." + id, null);
        plugin.saveConfig();
    }

    public Map<String, UUID> getSpawned() {
        return spawned;
    }
}
