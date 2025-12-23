/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class BlockProfiler {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<Material, BlockTiming> blockTimings = new ConcurrentHashMap<Material, BlockTiming>();
    private final Map<Material, List<Long>> blockTimingHistory = new ConcurrentHashMap<Material, List<Long>>();
    private BukkitTask profilingTask;
    private boolean profiling = false;

    public BlockProfiler(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        if (this.profiling || !this.configManager.isBlockProfilerEnabled()) {
            return;
        }
        this.profiling = true;
        this.profilingTask = Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this.plugin, () -> {
            this.sampleBlockTimings();
            if (this.configManager.getConfig().getBoolean("block-profiler.auto-report", true)) {
                this.reportTopBlocks();
            }
        }, 0L, (long)this.configManager.getSamplingInterval());
        this.monitorBlockTicks();
    }

    private void monitorBlockTicks() {
        Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            if (!this.profiling) {
                return;
            }
            long startTime = System.nanoTime();
            int totalBlocks = 0;
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    BlockState[] tileEntities = chunk.getTileEntities();
                    totalBlocks += tileEntities.length;
                    long chunkStart = System.nanoTime();
                    for (BlockState tileEntity : tileEntities) {
                        Block block = tileEntity.getBlock();
                        Material material = block.getType();
                        this.blockTimings.computeIfAbsent(material, m -> new BlockTiming((Material)m)).incrementCount();
                    }
                    long chunkEnd = System.nanoTime();
                    if (tileEntities.length <= 0) continue;
                    long timePerBlock = (chunkEnd - chunkStart) / (long)tileEntities.length;
                    for (BlockState tileEntity : tileEntities) {
                        Material material = tileEntity.getBlock().getType();
                        this.blockTimings.get(material).addTiming(timePerBlock);
                    }
                }
            }
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;
        }, 0L, 1L);
    }

    private void sampleBlockTimings() {
        this.blockTimings.forEach((material, timing) -> {
            this.blockTimingHistory.computeIfAbsent((Material)material, m -> new ArrayList()).add(timing.getAverageTime());
            List<Long> history = this.blockTimingHistory.get(material);
            int maxSamples = this.configManager.getMaxSamples();
            if (history.size() > maxSamples) {
                history.remove(0);
            }
            timing.reset();
        });
    }

    private void reportTopBlocks() {
        ArrayList<Map.Entry<Material, BlockTiming>> sorted = new ArrayList<Map.Entry<Material, BlockTiming>>(this.blockTimings.entrySet());
        sorted.sort((a, b) -> Long.compare(((BlockTiming)b.getValue()).getAverageTime(), ((BlockTiming)a.getValue()).getAverageTime()));
        int topCount = Math.min(this.configManager.getTrackTopBlocks(), sorted.size());
        if (topCount == 0) {
            return;
        }
        this.plugin.getLogger().info("=== Top " + topCount + " Block Types by Tick Time ===");
        for (int i = 0; i < topCount; ++i) {
            Map.Entry entry = (Map.Entry)sorted.get(i);
            BlockTiming timing = (BlockTiming)entry.getValue();
            this.plugin.getLogger().info(String.format("%d. %s: %.3f ms avg (%d ticks)", i + 1, ((Material)entry.getKey()).name(), (double)timing.getAverageTime() / 1000000.0, timing.getTickCount()));
        }
    }

    public void stop() {
        this.profiling = false;
        if (this.profilingTask != null) {
            this.profilingTask.cancel();
        }
    }

    public Map<Material, BlockTiming> getBlockTimings() {
        return new HashMap<Material, BlockTiming>(this.blockTimings);
    }

    public List<BlockTimingData> getTopBlocks(int count) {
        ArrayList<Map.Entry<Material, BlockTiming>> sorted = new ArrayList<Map.Entry<Material, BlockTiming>>(this.blockTimings.entrySet());
        sorted.sort((a, b) -> Long.compare(((BlockTiming)b.getValue()).getAverageTime(), ((BlockTiming)a.getValue()).getAverageTime()));
        ArrayList<BlockTimingData> result = new ArrayList<BlockTimingData>();
        for (int i = 0; i < Math.min(count, sorted.size()); ++i) {
            Map.Entry entry = (Map.Entry)sorted.get(i);
            BlockTiming timing = (BlockTiming)entry.getValue();
            result.add(new BlockTimingData((Material)entry.getKey(), (double)timing.getAverageTime() / 1000000.0, timing.getTickCount()));
        }
        return result;
    }

    public boolean isProfiling() {
        return this.profiling;
    }

    private static class BlockTiming {
        private final Material material;
        private long totalTime = 0L;
        private int tickCount = 0;

        public BlockTiming(Material material) {
            this.material = material;
        }

        public void addTiming(long nanoseconds) {
            this.totalTime += nanoseconds;
            ++this.tickCount;
        }

        public void incrementCount() {
            ++this.tickCount;
        }

        public long getAverageTime() {
            return this.tickCount > 0 ? this.totalTime / (long)this.tickCount : 0L;
        }

        public int getTickCount() {
            return this.tickCount;
        }

        public void reset() {
            this.totalTime = 0L;
            this.tickCount = 0;
        }
    }

    public static class BlockTimingData {
        private final Material material;
        private final double averageMs;
        private final int tickCount;

        public BlockTimingData(Material material, double averageMs, int tickCount) {
            this.material = material;
            this.averageMs = averageMs;
            this.tickCount = tickCount;
        }

        public Material getMaterial() {
            return this.material;
        }

        public double getAverageMs() {
            return this.averageMs;
        }

        public int getTickCount() {
            return this.tickCount;
        }
    }
}

