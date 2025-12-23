/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.optimization;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class ChunkOptimizer {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private BukkitTask optimizationTask;
    private final Map<Chunk, Long> chunkUnloadTimes = new HashMap<Chunk, Long>();
    private boolean running = false;

    public ChunkOptimizer(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        if (this.running || !this.configManager.isChunkOptimizationEnabled()) {
            return;
        }
        this.running = true;
        this.optimizationTask = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            this.optimizeChunks();
            this.unloadEmptyChunks();
        }, 0L, 200L);
    }

    private void optimizeChunks() {
        int maxChunks = this.configManager.getConfig().getInt("chunk.max-loaded-chunks", 5000);
        int totalChunks = 0;
        for (World world : Bukkit.getWorlds()) {
            totalChunks += world.getLoadedChunks().length;
        }
        if (totalChunks > maxChunks) {
            this.plugin.getLogger().warning("Too many loaded chunks: " + totalChunks + " (max: " + maxChunks + ")");
            for (World world : Bukkit.getWorlds()) {
                Chunk[] chunks = world.getLoadedChunks();
                if (chunks.length <= maxChunks / Bukkit.getWorlds().size()) continue;
                for (int i = chunks.length - 1; i >= maxChunks / Bukkit.getWorlds().size(); --i) {
                    if (chunks[i].getEntities().length != 0) continue;
                    world.unloadChunk(chunks[i]);
                }
            }
        }
    }

    private void unloadEmptyChunks() {
        if (!this.configManager.getConfig().getBoolean("chunk.force-unload-empty-chunks", true)) {
            return;
        }
        long unloadDelay = this.configManager.getConfig().getLong("chunk.unload-delay", 6000L) * 50L;
        long currentTime = System.currentTimeMillis();
        int unloaded = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                if (chunk.getEntities().length == 0 && chunk.getTileEntities().length == 0) {
                    Long unloadTime = this.chunkUnloadTimes.get(chunk);
                    if (unloadTime == null) {
                        this.chunkUnloadTimes.put(chunk, currentTime);
                        continue;
                    }
                    if (currentTime - unloadTime <= unloadDelay) continue;
                    world.unloadChunk(chunk);
                    this.chunkUnloadTimes.remove(chunk);
                    ++unloaded;
                    continue;
                }
                this.chunkUnloadTimes.remove(chunk);
            }
        }
        if (unloaded > 0 && this.configManager.getConfig().getBoolean("logging.log-optimizations", true)) {
            this.plugin.getLogger().info("Unloaded " + unloaded + " empty chunks");
        }
    }

    public void stop() {
        this.running = false;
        if (this.optimizationTask != null) {
            this.optimizationTask.cancel();
        }
        this.chunkUnloadTimes.clear();
    }

    public boolean isRunning() {
        return this.running;
    }
}

