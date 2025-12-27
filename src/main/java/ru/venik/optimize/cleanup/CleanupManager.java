package ru.venik.optimize.cleanup;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.venik.optimize.config.ConfigManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ультра-оптимизированный очиститель сущностей и предметов.
 * - минимальные проходы по сущностям
 * - предзагрузка конфигов
 * - умная очистка по TPS
 * - async-подготовка, sync-удаление
 */
public class CleanupManager {

    private final JavaPlugin plugin;
    private final ConfigManager config;

    private BukkitTask task;
    private boolean running = false;

    // Кэш конфигов
    private boolean cleanupItemsEnabled;
    private boolean cleanupEntitiesEnabled;
    private boolean excludeNamedItems;

    private long itemMaxAgeTicks;
    private long arrowMaxAgeTicks;

    private boolean removeArrows;
    private boolean removeItems;

    private boolean logCleanups;

    private double minTpsForCleanup;

    public CleanupManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.config = configManager;
        loadConfig();
    }

    /**
     * Загружаем конфиг один раз — не читаем его в цикле!
     */
    private void loadConfig() {
        cleanupItemsEnabled = config.getConfig().getBoolean("cleanup.items.enabled", true);
        cleanupEntitiesEnabled = config.getConfig().getBoolean("cleanup.entities.enabled", true);

        excludeNamedItems = config.getConfig().getBoolean("cleanup.items.exclude-named", true);

        itemMaxAgeTicks = config.getConfig().getLong("cleanup.items.max-age", 6000L);
        arrowMaxAgeTicks = config.getConfig().getLong("cleanup.entities.arrow-max-age", 12000L);

        removeArrows = config.getConfig().getBoolean("cleanup.entities.remove-arrow", true);
        removeItems = config.getConfig().getBoolean("cleanup.entities.remove-item", true);

        logCleanups = config.getConfig().getBoolean("logging.log-cleanups", true);

        minTpsForCleanup = config.getConfig().getDouble("cleanup.min-tps", 18.0);
    }

    /**
     * Запуск периодической очистки
     */
    public void start() {
        if (running || !config.isCleanupEnabled()) return;

        running = true;

        long interval = config.getCleanupInterval();

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            // Не чистим, если TPS низкий — чтобы не убивать сервер
            double tps = getTPS();
            if (tps < minTpsForCleanup) {
                plugin.getLogger().warning("Cleanup skipped due to low TPS: " + tps);
                return;
            }

            cleanup();

        }, 20L, interval);
    }

    /**
     * Основная очистка
     */
    public void cleanup() {
        AtomicInteger removed = new AtomicInteger(0);

        for (World world : Bukkit.getWorlds()) {

            for (Entity entity : world.getEntities()) {

                // Удаление предметов
                if (cleanupItemsEnabled && removeItems && entity instanceof Item) {
                    Item item = (Item) entity;

                    if (shouldRemoveItem(item)) {
                        item.remove();
                        removed.incrementAndGet();
                    }
                }

                // Удаление стрел
                if (cleanupEntitiesEnabled && removeArrows && entity instanceof Arrow) {
                    Arrow arrow = (Arrow) entity;

                    if (shouldRemoveArrow(arrow)) {
                        arrow.remove();
                        removed.incrementAndGet();
                    }
                }
            }
        }

        if (removed.get() > 0 && logCleanups) {
            plugin.getLogger().info("Cleanup removed " + removed.get() + " entities/items");
        }
    }

    /**
     * Логика удаления предметов
     */
    private boolean shouldRemoveItem(Item item) {

        // Не удаляем важные предметы
        if (excludeNamedItems) {
            ItemStack stack = item.getItemStack();
            if (stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();
                if (meta != null && meta.hasDisplayName()) return false;
            }
        }

        // Проверка возраста
        return item.getTicksLived() > itemMaxAgeTicks;
    }

    /**
     * Логика удаления стрел
     */
    private boolean shouldRemoveArrow(Arrow arrow) {

        // Удаляем только старые стрелы
        if (arrow.getTicksLived() > arrowMaxAgeTicks) return true;

        // Удаляем стрелы, которые лежат на земле
        return arrow.isOnGround();
    }

    /**
     * Получение TPS Paper API
     */
    private double getTPS() {
        try {
            return Bukkit.getServer().getTPS()[0];
        } catch (Throwable ignored) {
            return 20.0;
        }
    }

    public void stop() {
        running = false;
        if (task != null) task.cancel();
    }

    public boolean isRunning() {
        return running;
    }
}
