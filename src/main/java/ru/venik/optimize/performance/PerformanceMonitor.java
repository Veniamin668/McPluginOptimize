package ru.venik.optimize.performance;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Монитор производительности:
 *  - точное измерение TPS
 *  - точное измерение MSPT
 *  - история значений
 */
public class PerformanceMonitor {

    private final JavaPlugin plugin;

    private final List<Double> tpsHistory = new ArrayList<>();
    private final List<Double> msptHistory = new ArrayList<>();

    private boolean running = false;

    private double currentTps = 20.0;
    private double currentMspt = 50.0;

    // Для измерения MSPT
    private long lastTickNano = System.nanoTime();

    public PerformanceMonitor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    // START / STOP
    // ------------------------------------------------------------

    public void start() {
        if (running) return;
        running = true;

        // Измеряем MSPT каждый тик
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.nanoTime();
            long diff = now - lastTickNano;
            lastTickNano = now;

            // MSPT = наносекунды → миллисекунды
            currentMspt = diff / 1_000_000.0;

            // TPS = 1000 / MSPT
            currentTps = Math.min(20.0, 1000.0 / currentMspt);

            // История
            tpsHistory.add(currentTps);
            msptHistory.add(currentMspt);

            if (tpsHistory.size() > 200) {
                tpsHistory.remove(0);
                msptHistory.remove(0);
            }

        }, 0L, 1L);
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    // ------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------

    public double getCurrentTps() {
        return currentTps;
    }

    public double getCurrentMspt() {
        return currentMspt;
    }

    public double getAverageTps() {
        if (tpsHistory.isEmpty()) return 20.0;
        return tpsHistory.stream().mapToDouble(Double::doubleValue).average().orElse(20.0);
    }

    public double getAverageMspt() {
        if (msptHistory.isEmpty()) return 50.0;
        return msptHistory.stream().mapToDouble(Double::doubleValue).average().orElse(50.0);
    }

    public List<Double> getTpsHistory() {
        return new ArrayList<>(tpsHistory);
    }

    public List<Double> getMsptHistory() {
        return new ArrayList<>(msptHistory);
    }
}
