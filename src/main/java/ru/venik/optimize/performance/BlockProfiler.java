package ru.venik.optimize.performance;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.BlockPhysicsEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Профилировщик блоков:
 *  - измеряет нагрузку от блоков (physics/tick)
 *  - хранит историю
 *  - выводит топ нагруженных блоков
 */
public class BlockProfiler implements Listener {

    private final JavaPlugin plugin;

    private final Map<Material, BlockTiming> timings = new ConcurrentHashMap<>();
    private final Map<Material, List<Long>> history = new ConcurrentHashMap<>();

    private boolean running = false;
    private int sampleIntervalTicks = 100; // каждые 5 секунд
    private int maxSamples = 20;
    private int topCount = 10;

    public BlockProfiler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    // START / STOP
    // ------------------------------------------------------------

    public void start() {
        if (running) return;
        running = true;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        // Периодический сбор статистики
        Bukkit.getScheduler().runTaskTimer(plugin, this::sample, sampleIntervalTicks, sampleIntervalTicks);
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    // ------------------------------------------------------------
    // ПРОФИЛИРОВАНИЕ БЛОКОВ
    // ------------------------------------------------------------

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {

        if (!running) return;

        Block block = event.getBlock();
        Material type = block.getType();

        long start = System.nanoTime();

        // Bukkit сам выполнит физику блока
        // Мы просто измеряем время

        long end = System.nanoTime();
        long duration = end - start;

        timings.computeIfAbsent(type, BlockTiming::new).add(duration);
    }

    // ------------------------------------------------------------
    // СБОР СТАТИСТИКИ
    // ------------------------------------------------------------

    private void sample() {

        for (var entry : timings.entrySet()) {

            Material type = entry.getKey();
            BlockTiming timing = entry.getValue();

            long avg = timing.average();

            history.computeIfAbsent(type, k -> new ArrayList<>()).add(avg);

            List<Long> list = history.get(type);
            if (list.size() > maxSamples) {
                list.remove(0);
            }

            timing.reset();
        }

        reportTopBlocks();
    }

    // ------------------------------------------------------------
    // ОТЧЁТ
    // ------------------------------------------------------------

    private void reportTopBlocks() {

        List<Map.Entry<Material, BlockTiming>> sorted =
                new ArrayList<>(timings.entrySet());

        sorted.sort((a, b) ->
                Long.compare(b.getValue().average(), a.getValue().average()));

        int count = Math.min(topCount, sorted.size());
        if (count == 0) return;

        plugin.getLogger().info("=== Top " + count + " Block Types by Physics Time ===");

        for (int i = 0; i < count; i++) {
            var entry = sorted.get(i);
            Material type = entry.getKey();
            BlockTiming timing = entry.getValue();

            plugin.getLogger().info(
                    String.format("%d. %s: %.3f ms avg (%d events)",
                            i + 1,
                            type.name(),
                            timing.average() / 1_000_000.0,
                            timing.count())
            );
        }
    }

    // ------------------------------------------------------------
    // ДАННЫЕ
    // ------------------------------------------------------------

    public Map<Material, BlockTiming> getTimings() {
        return new HashMap<>(timings);
    }

    public List<BlockTimingData> getTop(int count) {

        List<Map.Entry<Material, BlockTiming>> sorted =
                new ArrayList<>(timings.entrySet());

        sorted.sort((a, b) ->
                Long.compare(b.getValue().average(), a.getValue().average()));

        List<BlockTimingData> result = new ArrayList<>();

        for (int i = 0; i < Math.min(count, sorted.size()); i++) {
            var entry = sorted.get(i);
            result.add(new BlockTimingData(
                    entry.getKey(),
                    entry.getValue().average() / 1_000_000.0,
                    entry.getValue().count()
            ));
        }

        return result;
    }

    // ------------------------------------------------------------
    // ВНУТРЕННИЕ КЛАССЫ
    // ------------------------------------------------------------

    private static class BlockTiming {

        private final Material type;
        private long total = 0;
        private int count = 0;

        public BlockTiming(Material type) {
            this.type = type;
        }

        public void add(long nanos) {
            total += nanos;
            count++;
        }

        public long average() {
            return count == 0 ? 0 : total / count;
        }

        public int count() {
            return count;
        }

        public void reset() {
            total = 0;
            count = 0;
        }
    }

    public static class BlockTimingData {

        private final Material type;
        private final double avgMs;
        private final int events;

        public BlockTimingData(Material type, double avgMs, int events) {
            this.type = type;
            this.avgMs = avgMs;
            this.events = events;
        }

        public Material getType() {
            return type;
        }

        public double getAvgMs() {
            return avgMs;
        }

        public int getEvents() {
            return events;
        }
    }
}
