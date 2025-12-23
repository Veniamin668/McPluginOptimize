/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.rtp;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class RandomTPManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Random random = new Random();
    private final ConcurrentHashMap<Player, Long> cooldowns = new ConcurrentHashMap();
    private static final int MIN_DISTANCE = 1000;
    private static final int MAX_DISTANCE = 10000;
    private static final long COOLDOWN_TIME = 300000L;

    public RandomTPManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void randomTeleport(Player player) {
        if (this.hasCooldown(player)) {
            long remaining = this.getCooldownRemaining(player);
            player.sendMessage("\u00a7c\u041f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435 \u0435\u0449\u0435 \u00a7e" + remaining + "\u00a7c \u0441\u0435\u043a\u0443\u043d\u0434!");
            return;
        }
        player.sendMessage("\u00a7e\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f... \u041f\u043e\u0436\u0430\u043b\u0443\u0439\u0441\u0442\u0430, \u043f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435...");
        World world = player.getWorld();
        Location location = this.findSafeLocation(world);
        if (location != null) {
            player.teleport(location);
            player.sendMessage("\u00a7a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043d\u0430 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u00a7e" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            this.setCooldown(player);
        } else {
            player.sendMessage("\u00a7c\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043d\u0430\u0439\u0442\u0438 \u0431\u0435\u0437\u043e\u043f\u0430\u0441\u043d\u043e\u0435 \u043c\u0435\u0441\u0442\u043e \u0434\u043b\u044f \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u0438! \u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u0435\u0449\u0435 \u0440\u0430\u0437.");
        }
    }

    private Location findSafeLocation(World world) {
        for (int attempts = 0; attempts < 50; ++attempts) {
            int y;
            Location location;
            int x = this.random.nextInt(9000) + 1000;
            if (this.random.nextBoolean()) {
                x = -x;
            }
            int z = this.random.nextInt(9000) + 1000;
            if (this.random.nextBoolean()) {
                z = -z;
            }
            if (!this.isSafeLocation(location = new Location(world, (double)x, (double)((y = world.getHighestBlockYAt(x, z)) + 1), (double)z))) continue;
            return location;
        }
        return null;
    }

    private boolean isSafeLocation(Location location) {
        Material ground = location.getBlock().getRelative(0, -1, 0).getType();
        Material feet = location.getBlock().getType();
        Material head = location.getBlock().getRelative(0, 1, 0).getType();
        return !ground.isAir() && feet.isAir() && head.isAir() && !ground.equals((Object)Material.LAVA) && !ground.equals((Object)Material.WATER);
    }

    private boolean hasCooldown(Player player) {
        return this.cooldowns.containsKey(player) && System.currentTimeMillis() < this.cooldowns.get(player);
    }

    private long getCooldownRemaining(Player player) {
        long endTime = this.cooldowns.get(player);
        return (endTime - System.currentTimeMillis()) / 1000L;
    }

    private void setCooldown(Player player) {
        this.cooldowns.put(player, System.currentTimeMillis() + 300000L);
    }
}

