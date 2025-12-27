package ru.venik.optimize.optimization;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Оптимизатор чанков:
 *  - ограничение общего количества загруженных чанков
 *  - выгрузка пустых чанков после задержки
 *  - безопасная работа без утечек памяти
 */
public class ChunkOptimizer {

    private final JavaPlugin plugin;

    private final int maxLoadedChunks;
    private final long unloadDelayMs;
    private final boolean forceUnloadEmpty;

    private boolean running = false;

    // Храним координаты, а не Chunk → нет утечек
    private final Map<String, Long> emptyChunks = new HashMap<>();

    public ChunkOptimizer(JavaPlugin plugin,
                          int maxLoadedChunks,
                          long unloadDelaySeconds,
                          boolean forceUnloadEmpty) {

        this.plugin = plugin;
        this.maxLoadedChunks = maxLoadedChunks;
        this.unloadDelayMs = unloadDelaySeconds * 1000L;
        this.forceUnloadEmpty = forceUnloadEmpty;
    }

    // ------------------------------------------------------------
    // START / STOP
    // ------------------------------------------------------------

    public void start() {
        if (running) return;
        running = true;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            optimizeLoadedCount();
            unloadEmptyChunks();
        }, 0L, 200L); // каждые 10 секунд
    }

    public void stop() {
        running = false;
        emptyChunks.clear();
    }

    public boolean isRunning() {
        return running;
    }

    // ------------------------------------------------------------
    // ОГРАНИЧЕНИЕ КОЛИЧЕСТВА ЗАГРУЖЕННЫХ ЧАНКОВ
    // ------------------------------------------------------------

    private void optimizeLoadedCount() {

        int total = Bukkit.getWorlds().stream()
                .mapToInt(w -> w.getLoadedChunks().length)
                .sum();

        if (total <= maxLoadedChunks) return;

        plugin.getLogger().warning("Loaded chunks: " + total + " (max " + maxLoadedChunks + ")");

        int worlds = Bukkit.getWorlds().size();
        int perWorldLimit = maxLoadedChunks / worlds;

        for (World world : Bukkit.getWorlds()) {

            Chunk[] chunks = world.getLoadedChunks();

            if (chunks.length <= perWorldLimit) continue;

            int toUnload = chunks.length - perWorldLimit;

            for (int i = chunks.length - 1; i >= 0 && toUnload > 0; i--) {

                Chunk chunk = chunks[i];

                if (isChunkInUse(world, chunk)) continue;

                unloadChunk(world, chunk);
                toUnload--;
            }
        }
    }

    // ------------------------------------------------------------
    // ВЫГРУЗКА ПУСТЫХ ЧАНКОВ
    // ------------------------------------------------------------

    private void unloadEmptyChunks() {

        if (!forceUnloadEmpty) return;

        long now = System.currentTimeMillis();
        int unloaded = 0;

        for (World world : Bukkit.getWorlds()) {

            for (Chunk chunk : world.getLoadedChunks()) {

                if (isChunkInUse(world, chunk)) {
                    emptyChunks.remove(key(world, chunk));
                    continue;
                }

                String key = key(world, chunk);

                long firstSeen = emptyChunks.getOrDefault(key, now);
                emptyChunks.putIfAbsent(key, now);

                if (now - firstSeen >= unloadDelayMs) {
                    unloadChunk(world, chunk);
                    emptyChunks.remove(key);
                    unloaded++;
                }
            }
        }

        if (unloaded > 0) {
            plugin.getLogger().info("Unloaded " + unloaded + " empty chunks");
        }
    }

    // ------------------------------------------------------------
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ------------------------------------------------------------

    private boolean isChunkInUse(World world, Chunk chunk) {

        // Игроки в чанке
        for (Player p : world.getPlayers()) {
            if (p.getLocation().getChunk().equals(chunk)) {
                return true;
            }
        }

        // Сущности
        if (chunk.getEntities().length > 0) return true;

        // TileEntities (блоки с данными)
        if (chunk.getTileEntities().length > 0) return true;

        return false;
    }

    private void unloadChunk(World world, Chunk chunk) {
        world.unloadChunkRequest(chunk.getX(), chunk.getZ(), true);
    }

    private String key(World world, Chunk chunk) {
        return world.getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }
}
