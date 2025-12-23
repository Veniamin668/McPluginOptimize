/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.optimization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class EntityOptimizer {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private BukkitTask optimizationTask;
    private boolean running = false;

    public EntityOptimizer(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        if (this.running || !this.configManager.isEntityOptimizationEnabled()) {
            return;
        }
        this.running = true;
        this.optimizationTask = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            this.optimizeEntities();
            this.optimizeItemStacks();
            this.checkStuckEntities();
        }, 0L, 100L);
    }

    private void optimizeEntities() {
        int maxPerChunk = this.configManager.getMaxEntitiesPerChunk();
        int removed = 0;
        for (World world : Bukkit.getWorlds()) {
            block1: for (Chunk chunk : world.getLoadedChunks()) {
                Entity[] entities = chunk.getEntities();
                if (entities.length <= maxPerChunk) continue;
                ArrayList<Entity> sortedEntities = new ArrayList<Entity>(Arrays.asList(entities));
                sortedEntities.sort(Comparator.comparing(e -> e.getType().name()));
                for (Entity entity : sortedEntities) {
                    if (entities.length <= maxPerChunk) continue block1;
                    if (!(entity instanceof Item) || entity.isDead()) continue;
                    entity.remove();
                    ++removed;
                    entities = chunk.getEntities();
                }
            }
        }
        if (removed > 0 && this.configManager.getConfig().getBoolean("logging.log-optimizations", true)) {
            this.plugin.getLogger().info("Removed " + removed + " excess entities");
        }
    }

    private void optimizeItemStacks() {
        if (!this.configManager.getConfig().getBoolean("entity.optimize-item-stacks", true)) {
            return;
        }
        double mergeRadius = this.configManager.getConfig().getDouble("entity.item-merge-radius", 2.5);
        int merged = 0;
        for (World world : Bukkit.getWorlds()) {
            Collection<Item> itemsCollection = world.getEntitiesByClass(Item.class);
            ArrayList<Item> items = new ArrayList<>(itemsCollection);
            HashMap<String, List<Item>> itemGroups = new HashMap<>();
            for (Item item : items) {
                if (item.isDead() || !item.getItemStack().getType().isItem()) continue;
                String key = item.getItemStack().getType().name() + "_" + (int)(item.getLocation().getX() / mergeRadius) + "_" + (int)(item.getLocation().getY() / mergeRadius) + "_" + (int)(item.getLocation().getZ() / mergeRadius);
                itemGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
            }
            for (List<Item> group : itemGroups.values()) {
                if (group.size() < 2) continue;
                Item base = group.get(0);
                for (int i = 1; i < group.size(); ++i) {
                    Item item = group.get(i);
                    if (item.isDead() || !(base.getLocation().distance(item.getLocation()) <= mergeRadius) || !base.getItemStack().isSimilar(item.getItemStack())) continue;
                    int newAmount = Math.min(64, base.getItemStack().getAmount() + item.getItemStack().getAmount());
                    base.getItemStack().setAmount(newAmount);
                    item.remove();
                    ++merged;
                }
            }
        }
        if (merged > 0 && this.configManager.getConfig().getBoolean("logging.log-optimizations", true)) {
            this.plugin.getLogger().info("Merged " + merged + " item stacks");
        }
    }

    private void checkStuckEntities() {
        if (!this.configManager.getConfig().getBoolean("entity.remove-stuck-entities", true)) {
            return;
        }
        int removed = 0;
        long stuckThreshold = 300000L;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                boolean hasNearbyPlayer;
                Item item;
                if (!(entity instanceof Item) || !(item = (Item)entity).isOnGround() || (long)item.getTicksLived() <= stuckThreshold / 50L || (hasNearbyPlayer = Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getWorld().equals((Object)world) && player.getLocation().distance(item.getLocation()) <= 32.0))) continue;
                item.remove();
                ++removed;
            }
        }
        if (removed > 0 && this.configManager.getConfig().getBoolean("logging.log-cleanups", true)) {
            this.plugin.getLogger().info("Removed " + removed + " stuck entities");
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

