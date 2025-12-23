/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.config;

import org.bukkit.configuration.file.FileConfiguration;
import ru.venik.optimize.VenikOptimize;

public class ConfigManager {
    private final VenikOptimize plugin;
    private FileConfiguration config;

    public ConfigManager(VenikOptimize plugin) {
        this.plugin = plugin;
        this.loadConfig();
    }

    public void loadConfig() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
    }

    public boolean isPerformanceEnabled() {
        return this.config.getBoolean("performance.enabled", true);
    }

    public int getTpsCheckInterval() {
        return this.config.getInt("performance.tps-check-interval", 20);
    }

    public double getAlertLowTps() {
        return this.config.getDouble("performance.alert-low-tps", 18.0);
    }

    public double getAlertHighMspt() {
        return this.config.getDouble("performance.alert-high-mspt", 50.0);
    }

    public boolean isBlockProfilerEnabled() {
        return this.config.getBoolean("block-profiler.enabled", true);
    }

    public int getSamplingInterval() {
        return this.config.getInt("block-profiler.sampling-interval", 100);
    }

    public int getMaxSamples() {
        return this.config.getInt("block-profiler.max-samples", 120);
    }

    public int getTrackTopBlocks() {
        return this.config.getInt("block-profiler.track-top-blocks", 20);
    }

    public boolean isEntityOptimizationEnabled() {
        return this.config.getBoolean("entity.enabled", true);
    }

    public int getMaxEntitiesPerChunk() {
        return this.config.getInt("entity.max-entities-per-chunk", 50);
    }

    public boolean isChunkOptimizationEnabled() {
        return this.config.getBoolean("chunk.enabled", true);
    }

    public boolean isRedstoneOptimizationEnabled() {
        return this.config.getBoolean("redstone.enabled", true);
    }

    public boolean isCleanupEnabled() {
        return this.config.getBoolean("cleanup.enabled", true);
    }

    public int getCleanupInterval() {
        return this.config.getInt("cleanup.cleanup-interval", 36000);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }
}

