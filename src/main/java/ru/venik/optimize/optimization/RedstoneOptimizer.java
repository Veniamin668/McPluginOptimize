/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.optimization;

import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class RedstoneOptimizer
implements Listener {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final AtomicInteger redstoneUpdatesThisTick = new AtomicInteger(0);
    private BukkitTask resetTask;
    private boolean running = false;

    public RedstoneOptimizer(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        if (this.running || !this.configManager.isRedstoneOptimizationEnabled()) {
            return;
        }
        this.running = true;
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
        this.resetTask = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> this.redstoneUpdatesThisTick.set(0), 0L, 1L);
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onRedstoneUpdate(BlockRedstoneEvent event) {
        if (!this.running) {
            return;
        }
        int maxUpdates = this.configManager.getConfig().getInt("redstone.max-redstone-updates-per-tick", 10000);
        if (this.redstoneUpdatesThisTick.incrementAndGet() > maxUpdates) {
            event.setNewCurrent(0);
            return;
        }
        if (this.configManager.getConfig().getBoolean("redstone.disable-far-redstone", true)) {
            int distance = this.configManager.getConfig().getInt("redstone.far-redstone-distance", 64);
            Block block = event.getBlock();
            boolean hasNearbyPlayer = Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getWorld().equals((Object)block.getWorld()) && player.getLocation().distance(block.getLocation()) <= (double)distance);
            if (!hasNearbyPlayer) {
                event.setNewCurrent(0);
            }
        }
        if (this.configManager.getConfig().getBoolean("redstone.optimize-redstone-clocks", true)) {
            this.optimizeRedstoneClock(event.getBlock());
        }
    }

    private void optimizeRedstoneClock(Block block) {
        Material type = block.getType();
        if (type == Material.REDSTONE_WIRE || type == Material.REDSTONE_LAMP) {
            // empty if block
        }
    }

    public void stop() {
        this.running = false;
        if (this.resetTask != null) {
            this.resetTask.cancel();
        }
    }

    public boolean isRunning() {
        return this.running;
    }
}

