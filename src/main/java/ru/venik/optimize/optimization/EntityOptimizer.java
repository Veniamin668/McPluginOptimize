package ru.venik.optimize.optimization;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Оптимизация сущностей:
 *  - ограничение количества сущностей в чанке
 *  - объединение item-стаков
 *  - удаление "застрявших" предметов
 */
public class EntityOptimizer {

    private final JavaPlugin plugin;

    private final int maxEntitiesPerChunk;
    private final boolean mergeItems;
    private final double mergeRadius;
    private final boolean removeStuck;
    private final long stuckThresholdTicks;

    private boolean running = false;

    public EntityOptimizer(JavaPlugin plugin,
                           int maxEntitiesPerChunk,
                           boolean mergeItems,
                           double mergeRadius,
                           boolean removeStuck,
                           long stuckThresholdSeconds) {

        this.plugin = plugin;
        this.maxEntitiesPerChunk = maxEntitiesPerChunk;
        this.mergeItems = mergeItems;
        this.mergeRadius = mergeRadius;
        this.removeStuck = removeStuck;
        this.stuckThresholdTicks = stuckThresholdSeconds * 20L;
    }

    // ------------------------------------------------------------
    // START / STOP
    // ------------------------------------------------------------

    public void start() {
        if (running) return;
        running = true;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            optimizeEntities();
            optimizeItemStacks();
            removeStuckItems();
        }, 0L, 100L); // каждые 5 секунд
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    // ------------------------------------------------------------
    // ОГРАНИЧЕНИЕ КОЛИЧЕСТВА СУЩНОСТЕЙ В ЧАНКЕ
    // ------------------------------------------------------------

    private void optimizeEntities() {

        int removed = 0;

        for (World world : Bukkit.getWorlds()) {

            for (Chunk chunk : world.getLoadedChunks()) {

                Entity[] entities = chunk.getEntities();

                if (entities.length <= maxEntitiesPerChunk) continue;

                // Удаляем только Item, чтобы не ломать геймплей
                for (Entity e : entities) {

                    if (entities.length <= maxEntitiesPerChunk) break;

                    if (e instanceof Item item && !item.isDead()) {
                        item.remove();
                        removed++;
                    }
                }
            }
        }

        if (removed > 0) {
            plugin.getLogger().info("Removed " + removed + " excess entities");
        }
    }

    // ------------------------------------------------------------
    // ОБЪЕДИНЕНИЕ ITEM-СТАКОВ
    // ------------------------------------------------------------

    private void optimizeItemStacks() {

        if (!mergeItems) return;

        int merged = 0;

        for (World world : Bukkit.getWorlds()) {

            List<Item> items = new ArrayList<>(world.getEntitiesByClass(Item.class));

            // Группируем предметы по "ячейкам"
            Map<String, List<Item>> groups = new HashMap<>();

            for (Item item : items) {

                if (item.isDead()) continue;

                var stack = item.getItemStack();
                if (!stack.getType().isItem()) continue;

                String key = stack.getType().name() + "_"
                        + (int) (item.getLocation().getX() / mergeRadius) + "_"
                        + (int) (item.getLocation().getY() / mergeRadius) + "_"
                        + (int) (item.getLocation().getZ() / mergeRadius);

                groups.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
            }

            // Объединяем группы
            for (List<Item> group : groups.values()) {

                if (group.size() < 2) continue;

                Item base = group.get(0);

                for (int i = 1; i < group.size(); i++) {

                    Item other = group.get(i);

                    if (other.isDead()) continue;

                    if (!base.getItemStack().isSimilar(other.getItemStack())) continue;

                    if (base.getLocation().distance(other.getLocation()) > mergeRadius) continue;

                    int newAmount = Math.min(64,
                            base.getItemStack().getAmount() + other.getItemStack().getAmount());

                    base.getItemStack().setAmount(newAmount);
                    other.remove();
                    merged++;
                }
            }
        }

        if (merged > 0) {
            plugin.getLogger().info("Merged " + merged + " item stacks");
        }
    }

    // ------------------------------------------------------------
    // УДАЛЕНИЕ "ЗАСТРЯВШИХ" ПРЕДМЕТОВ
    // ------------------------------------------------------------

    private void removeStuckItems() {

        if (!removeStuck) return;

        int removed = 0;

        for (World world : Bukkit.getWorlds()) {

            for (Item item : world.getEntitiesByClass(Item.class)) {

                if (!item.isOnGround()) continue;

                if (item.getTicksLived() < stuckThresholdTicks) continue;

                boolean nearPlayer = world.getPlayers().stream()
                        .anyMatch(p -> p.getLocation().distance(item.getLocation()) <= 32);

                if (nearPlayer) continue;

                item.remove();
                removed++;
            }
        }

        if (removed > 0) {
            plugin.getLogger().info("Removed " + removed + " stuck items");
        }
    }
}
