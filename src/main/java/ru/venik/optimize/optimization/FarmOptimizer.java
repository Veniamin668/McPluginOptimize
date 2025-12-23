/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.optimization;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class FarmOptimizer {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private BukkitTask optimizationTask;
    private boolean running = false;

    public FarmOptimizer(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        if (this.running) {
            return;
        }
        boolean enabled = this.configManager.getConfig().getBoolean("farm.enabled", true);
        if (!enabled) {
            return;
        }
        this.running = true;
        this.optimizationTask = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            if (this.configManager.getConfig().getBoolean("farm.optimize-hoppers", true)) {
                this.optimizeHoppers();
            }
            if (this.configManager.getConfig().getBoolean("farm.optimize-crops", true)) {
                this.optimizeCrops();
            }
        }, 0L, (long)this.configManager.getConfig().getInt("farm.hopper-check-interval", 20));
    }

    private void optimizeHoppers() {
        int optimized = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState tileEntity : chunk.getTileEntities()) {
                    boolean hasNearbyPlayer;
                    Hopper hopper;
                    Block block;
                    if (tileEntity instanceof Hopper && (block = (hopper = (Hopper)tileEntity).getBlock()).getChunk().isLoaded() && (hasNearbyPlayer = Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getWorld().equals((Object)world) && player.getLocation().distance(block.getLocation()) <= 64.0))) continue;
                }
            }
        }
        if (optimized > 0 && this.configManager.getConfig().getBoolean("logging.log-optimizations", true)) {
            this.plugin.getLogger().fine("Optimized " + optimized + " hoppers");
        }
    }

    private void optimizeCrops() {
        if (!this.configManager.getConfig().getBoolean("farm.crop-growth-optimization", true)) {
            return;
        }
        int cropsChecked = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                boolean hasNearbyPlayer = Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getWorld().equals((Object)world) && player.getLocation().distance(chunk.getBlock(0, 64, 0).getLocation()) <= 64.0);
                if (!hasNearbyPlayer) continue;
                cropsChecked += chunk.getTileEntities().length;
            }
        }
    }

    public void stop() {
        this.running = false;
        if (this.optimizationTask != null) {
            this.optimizationTask.cancel();
        }
    }

    public boolean isRunning() {
        return this.running;
    }
}

