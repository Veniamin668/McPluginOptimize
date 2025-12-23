/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.warp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;
import ru.venik.optimize.warp.Warp;

public class WarpManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<String, Warp> warps = new ConcurrentHashMap<String, Warp>();
    private final Map<Player, Long> cooldowns = new HashMap<Player, Long>();

    public WarpManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.loadDefaultWarps();
    }

    private void loadDefaultWarps() {
    }

    public void createWarp(String name, Location location) {
        this.warps.put(name.toLowerCase(), new Warp(name, location));
        this.plugin.getLogger().info("Warp created: " + name + " at " + String.valueOf(location));
    }

    public void deleteWarp(String name) {
        this.warps.remove(name.toLowerCase());
    }

    public boolean warp(Player player, String warpName) {
        Warp warp = this.warps.get(warpName.toLowerCase());
        if (warp == null) {
            if (warpName.equalsIgnoreCase("spawn")) {
                player.teleport(player.getWorld().getSpawnLocation());
                player.sendMessage("\u00a7a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043d\u0430 \u0441\u043f\u0430\u0432\u043d!");
                return true;
            }
            player.sendMessage("\u00a7c\u0412\u0430\u0440\u043f \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return false;
        }
        if (this.hasCooldown(player)) {
            long remaining = this.getCooldownRemaining(player);
            player.sendMessage("\u00a7c\u041f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435 \u0435\u0449\u0435 \u00a7e" + remaining + "\u00a7c \u0441\u0435\u043a\u0443\u043d\u0434!");
            return false;
        }
        player.teleport(warp.getLocation());
        player.sendMessage("\u00a7a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u0432 \u0432\u0430\u0440\u043f \u00a7e" + warp.getName() + "\u00a7a!");
        this.setCooldown(player);
        return true;
    }

    public Warp getWarp(String name) {
        return this.warps.get(name.toLowerCase());
    }

    public Map<String, Warp> getAllWarps() {
        return new HashMap<String, Warp>(this.warps);
    }

    private boolean hasCooldown(Player player) {
        if (!this.configManager.getConfig().getBoolean("warp.cooldown-enabled", true)) {
            return false;
        }
        return this.cooldowns.containsKey(player) && System.currentTimeMillis() < this.cooldowns.get(player);
    }

    private long getCooldownRemaining(Player player) {
        if (!this.hasCooldown(player)) {
            return 0L;
        }
        return (this.cooldowns.get(player) - System.currentTimeMillis()) / 1000L;
    }

    private void setCooldown(Player player) {
        long cooldownTime = this.configManager.getConfig().getLong("warp.cooldown-seconds", 5L) * 1000L;
        this.cooldowns.put(player, System.currentTimeMillis() + cooldownTime);
    }
}

