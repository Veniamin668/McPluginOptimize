/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class PerformanceMonitor {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final List<Double> tpsHistory = new ArrayList<Double>();
    private final List<Double> msptHistory = new ArrayList<Double>();
    private final AtomicInteger tickCount = new AtomicInteger(0);
    private final AtomicLong lastTickTime = new AtomicLong(System.currentTimeMillis());
    private double currentTps = 20.0;
    private double currentMspt = 50.0;
    private boolean running = false;

    public PerformanceMonitor(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - this.lastTickTime.get();
            this.currentMspt = (double)elapsed / (double)this.tickCount.get();
            if (elapsed > 0L) {
                this.currentTps = Math.min(20.0, 1000.0 / ((double)elapsed / (double)this.tickCount.get()));
            }
            this.tpsHistory.add(this.currentTps);
            this.msptHistory.add(this.currentMspt);
            if (this.tpsHistory.size() > 100) {
                this.tpsHistory.remove(0);
                this.msptHistory.remove(0);
            }
            if (this.currentTps < this.configManager.getAlertLowTps()) {
                this.plugin.getLogger().warning(String.format("Low TPS detected! Current TPS: %.2f (Alert threshold: %.2f)", this.currentTps, this.configManager.getAlertLowTps()));
            }
            if (this.currentMspt > this.configManager.getAlertHighMspt()) {
                this.plugin.getLogger().warning(String.format("High MSPT detected! Current MSPT: %.2f ms (Alert threshold: %.2f ms)", this.currentMspt, this.configManager.getAlertHighMspt()));
            }
            this.tickCount.set(0);
            this.lastTickTime.set(currentTime);
        }, 0L, (long)this.configManager.getTpsCheckInterval());
        Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> this.tickCount.incrementAndGet(), 0L, 1L);
    }

    public void stop() {
        this.running = false;
    }

    public double getCurrentTps() {
        return this.currentTps;
    }

    public double getCurrentMspt() {
        return this.currentMspt;
    }

    public double getAverageTps() {
        if (this.tpsHistory.isEmpty()) {
            return 20.0;
        }
        return this.tpsHistory.stream().mapToDouble(Double::doubleValue).average().orElse(20.0);
    }

    public double getAverageMspt() {
        if (this.msptHistory.isEmpty()) {
            return 50.0;
        }
        return this.msptHistory.stream().mapToDouble(Double::doubleValue).average().orElse(50.0);
    }

    public List<Double> getTpsHistory() {
        return new ArrayList<Double>(this.tpsHistory);
    }

    public List<Double> getMsptHistory() {
        return new ArrayList<Double>(this.msptHistory);
    }

    public boolean isRunning() {
        return this.running;
    }
}

