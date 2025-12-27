package ru.venik.optimize.optimization;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Оптимизация редстоуна:
 *  - ограничение количества обновлений в тик
 *  - отключение редстоуна вдали от игроков
 *  - подавление редстоун-часов
 */
public class RedstoneOptimizer implements Listener {

    private final JavaPlugin plugin;

    private final int maxUpdatesPerTick;
    private final int playerRange;
    private final boolean disableFarRedstone;
    private final boolean optimizeClocks;

    private final AtomicInteger updates = new AtomicInteger(0);
    private boolean running = false;

    public RedstoneOptimizer(JavaPlugin plugin,
                             int maxUpdatesPerTick,
                             int playerRange,
                             boolean disableFarRedstone,
                             boolean optimizeClocks) {

        this.plugin = plugin;
        this.maxUpdatesPerTick = maxUpdatesPerTick;
        this.playerRange = playerRange;
        this.disableFarRedstone = disableFarRedstone;
        this.optimizeClocks = optimizeClocks;
    }

    // ------------------------------------------------------------
    // START / STOP
    // ------------------------------------------------------------

    public void start() {
        if (running) return;
        running = true;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        // Сбрасываем счётчик каждый тик
        Bukkit.getScheduler().runTaskTimer(plugin, () -> updates.set(0), 1L, 1L);
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    // ------------------------------------------------------------
    // ОБРАБОТКА РЕДСТОУНА
    // ------------------------------------------------------------

    @EventHandler(priority = EventPriority.LOW)
    public void onRedstone(BlockRedstoneEvent event) {

        if (!running) return;

        // 1) Ограничение количества обновлений
        if (updates.incrementAndGet() > maxUpdatesPerTick) {
            event.setNewCurrent(0);
            return;
        }

        Block block = event.getBlock();
        World world = block.getWorld();
        Chunk chunk = block.getChunk();

        // 2) Отключение редстоуна вдали от игроков
        if (disableFarRedstone && !hasNearbyPlayers(world, chunk)) {
            event.setNewCurrent(0);
            return;
        }

        // 3) Подавление редстоун-часов
        if (optimizeClocks && isClock(event)) {
            event.setNewCurrent(0);
        }
    }

    // ------------------------------------------------------------
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ------------------------------------------------------------

    private boolean hasNearbyPlayers(World world, Chunk chunk) {

        int cx = chunk.getX();
        int cz = chunk.getZ();

        return world.getPlayers().stream().anyMatch(p -> {
            Chunk pc = p.getLocation().getChunk();
            return Math.abs(pc.getX() - cx) <= playerRange &&
                   Math.abs(pc.getZ() - cz) <= playerRange;
        });
    }

    private boolean isClock(BlockRedstoneEvent event) {

        // Clock = быстрое переключение туда-сюда
        int oldPower = event.getOldCurrent();
        int newPower = event.getNewCurrent();

        // Если редстоун меняет состояние слишком часто
        return (oldPower == 0 && newPower > 0) ||
               (oldPower > 0 && newPower == 0);
    }
}
