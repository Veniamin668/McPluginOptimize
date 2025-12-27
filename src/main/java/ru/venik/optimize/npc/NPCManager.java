package ru.venik.optimize.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Менеджер NPC:
 *  - загрузка из конфига
 *  - создание
 *  - удаление
 *  - сохранение
 *  - хранение UUID
 */
public class NPCManager {

    private final JavaPlugin plugin;
    private final Map<String, UUID> spawned = new HashMap<>();
    private final Random random = new Random();

    public NPCManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    // ЖИЗНЕННЫЙ ЦИКЛ
    // ------------------------------------------------------------

    public void start() {
        loadFromConfig();
    }

    public void stop() {
        // Удаляем всех NPC
        for (UUID id : spawned.values()) {
            Entity e = Bukkit.getEntity(id);
            if (e != null && !e.isDead()) {
                e.remove();
            }
        }
        spawned.clear();
    }

    // ------------------------------------------------------------
    // ЗАГРУЗКА ИЗ КОНФИГА
    // ------------------------------------------------------------

    public void loadFromConfig() {

        if (!plugin.getConfig().getBoolean("npc.enabled", true)) {
            return;
        }

        if (!plugin.getConfig().isConfigurationSection("npc.list")) {
            return;
        }

        for (String id : plugin.getConfig().getConfigurationSection("npc.list").getKeys(false)) {

            String path = "npc.list." + id;

            String world = plugin.getConfig().getString(path + ".world");
            double x = plugin.getConfig().getDouble(path + ".x");
            double y = plugin.getConfig().getDouble(path + ".y");
            double z = plugin.getConfig().getDouble(path + ".z");
            boolean randomize = plugin.getConfig().getBoolean(path + ".randomize", true);

            if (Bukkit.getWorld(world) == null) {
                plugin.getLogger().warning("NPC '" + id + "' пропущен: мир '" + world + "' не найден.");
                continue;
            }

            Location loc = new Location(Bukkit.getWorld(world), x, y, z);
            spawnNPC(id, loc, randomize);
        }
    }

    // ------------------------------------------------------------
    // СПАВН NPC
    // ------------------------------------------------------------

    public void spawnNPC(String id, Location loc, boolean randomize) {

        if (loc.getWorld() == null) {
            plugin.getLogger().warning("Не удалось создать NPC '" + id + "': мир отсутствует.");
            return;
        }

        Villager npc = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        npc.setAI(false);
        npc.setInvulnerable(true);
        npc.setCustomNameVisible(true);

        // Имя NPC
        String name = "NPC-" + id;

        if (randomize) {
            String[] variants = plugin.getConfig()
                    .getString("npc.variants", "ModelA,ModelB,ModelC")
                    .split(",");

            name = variants[random.nextInt(variants.length)].trim();
        }

        npc.setCustomName(name);

        spawned.put(id.toLowerCase(), npc.getUniqueId());
    }

    // ------------------------------------------------------------
    // СОЗДАНИЕ И СОХРАНЕНИЕ
    // ------------------------------------------------------------

    public void createAndSave(String id, Location loc, boolean randomize) {

        id = id.toLowerCase();
        String path = "npc.list." + id;

        plugin.getConfig().set(path + ".world", loc.getWorld().getName());
        plugin.getConfig().set(path + ".x", loc.getX());
        plugin.getConfig().set(path + ".y", loc.getY());
        plugin.getConfig().set(path + ".z", loc.getZ());
        plugin.getConfig().set(path + ".randomize", randomize);

        plugin.saveConfig();

        spawnNPC(id, loc, randomize);
    }

    // ------------------------------------------------------------
    // УДАЛЕНИЕ NPC
    // ------------------------------------------------------------

    public void remove(String id) {

        id = id.toLowerCase();

        UUID uuid = spawned.remove(id);

        if (uuid != null) {
            Entity e = Bukkit.getEntity(uuid);
            if (e != null && !e.isDead()) {
                e.remove();
            }
        }

        plugin.getConfig().set("npc.list." + id, null);
        plugin.saveConfig();
    }

    // ------------------------------------------------------------
    // API
    // ------------------------------------------------------------

    public boolean exists(String id) {
        return spawned.containsKey(id.toLowerCase());
    }

    public Map<String, UUID> getSpawned() {
        return Collections.unmodifiableMap(spawned);
    }
}
