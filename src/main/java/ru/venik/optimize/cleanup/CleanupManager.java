/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.cleanup;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class CleanupManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private BukkitTask cleanupTask;
    private boolean running = false;

    public CleanupManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        if (this.running || !this.configManager.isCleanupEnabled()) {
            return;
        }
        this.running = true;
        if (this.configManager.getConfig().getBoolean("cleanup.auto-cleanup", true)) {
            this.cleanupTask = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
                this.cleanupItems();
                this.cleanupEntities();
            }, 0L, (long)this.configManager.getCleanupInterval());
        }
    }

    public void cleanupItems() {
        if (!this.configManager.getConfig().getBoolean("cleanup.items.enabled", true)) {
            return;
        }
        long maxAge = this.configManager.getConfig().getLong("cleanup.items.max-age", 6000L) * 50L;
        boolean excludeNamed = this.configManager.getConfig().getBoolean("cleanup.items.exclude-named", true);
        long currentTime = System.currentTimeMillis();
        int removed = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Item item : world.getEntitiesByClass(Item.class)) {
                long spawnTime;
                ItemMeta meta;
                ItemStack stack;
                if (item.isDead() || excludeNamed && (stack = item.getItemStack()).hasItemMeta() && (meta = stack.getItemMeta()) != null && meta.hasDisplayName() || (spawnTime = (long)item.getTicksLived()) <= maxAge / 50L) continue;
                item.remove();
                ++removed;
            }
        }
        if (removed > 0 && this.configManager.getConfig().getBoolean("logging.log-cleanups", true)) {
            this.plugin.getLogger().info("Cleaned up " + removed + " items");
        }
    }

    public void cleanupEntities() {
        int removed = 0;
        long currentTime = System.currentTimeMillis();
        if (this.configManager.getConfig().getBoolean("cleanup.entities.remove-arrow", true)) {
            long arrowMaxAge = this.configManager.getConfig().getLong("cleanup.entities.arrow-max-age", 12000L) * 50L;
            for (World world : Bukkit.getWorlds()) {
                for (Arrow arrow : world.getEntitiesByClass(Arrow.class)) {
                    if (!arrow.isOnGround() && (long)arrow.getTicksLived() <= arrowMaxAge / 50L) continue;
                    arrow.remove();
                    ++removed;
                }
            }
        }
        if (this.configManager.getConfig().getBoolean("cleanup.entities.remove-item", true)) {
            long itemMaxAge = this.configManager.getConfig().getLong("cleanup.entities.item-max-age", 6000L) * 50L;
            for (World world : Bukkit.getWorlds()) {
                for (Item item : world.getEntitiesByClass(Item.class)) {
                    if ((long)item.getTicksLived() <= itemMaxAge / 50L) continue;
                    item.remove();
                    ++removed;
                }
            }
        }
        if (removed > 0 && this.configManager.getConfig().getBoolean("logging.log-cleanups", true)) {
            this.plugin.getLogger().info("Cleaned up " + removed + " entities");
        }
    }

    public int performCleanup() {
        int total = 0;
        this.cleanupItems();
        this.cleanupEntities();
        return total;
    }

    public void stop() {
        this.running = false;
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel();
        }
    }

    public boolean isRunning() {
        return this.running;
    }
}

