package ru.venik.optimize.rtp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RandomTP:
 *  - безопасная телепортация
 *  - кулдаун
 *  - поиск безопасного места
 */
public class RandomTPManager {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    private final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    private final int minDistance;
    private final int maxDistance;
    private final long cooldownMillis;

    public RandomTPManager(JavaPlugin plugin,
                           int minDistance,
                           int maxDistance,
                           long cooldownSeconds) {

        this.plugin = plugin;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.cooldownMillis = cooldownSeconds * 1000L;
    }

    // ------------------------------------------------------------
    // PUBLIC API
    // ------------------------------------------------------------

    public void randomTeleport(Player player) {

        if (hasCooldown(player)) {
            long remaining = getCooldownRemaining(player);
            player.sendMessage("§cПодождите ещё §e" + remaining + "§c секунд!");
            return;
        }

        player.sendMessage("§eТелепортация... Пожалуйста, подождите...");

        World world = player.getWorld();
        Location safe = findSafeLocation(world);

        if (safe == null) {
            player.sendMessage("§cНе удалось найти безопасное место. Попробуйте ещё раз.");
            return;
        }

        player.teleport(safe);
        player.sendMessage("§aВы телепортированы на координаты §e" +
                safe.getBlockX() + ", " + safe.getBlockY() + ", " + safe.getBlockZ());

        setCooldown(player);
    }

    // ------------------------------------------------------------
    // SAFE LOCATION SEARCH
    // ------------------------------------------------------------

    private Location findSafeLocation(World world) {

        for (int attempts = 0; attempts < 50; attempts++) {

            int x = randomCoord(minDistance, maxDistance);
            int z = randomCoord(minDistance, maxDistance);

            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (isSafe(loc)) return loc;
        }

        return null;
    }

    private int randomCoord(int min, int max) {
        int value = random.nextInt(max - min) + min;
        return random.nextBoolean() ? value : -value;
    }

    private boolean isSafe(Location loc) {

        Material ground = loc.clone().add(0, -1, 0).getBlock().getType();
        Material feet = loc.getBlock().getType();
        Material head = loc.clone().add(0, 1, 0).getBlock().getType();

        if (ground.isAir()) return false;
        if (!feet.isAir()) return false;
        if (!head.isAir()) return false;

        return ground != Material.LAVA && ground != Material.WATER;
    }

    // ------------------------------------------------------------
    // COOLDOWN
    // ------------------------------------------------------------

    private boolean hasCooldown(Player player) {
        UUID id = player.getUniqueId();
        Long end = cooldowns.get(id);
        return end != null && System.currentTimeMillis() < end;
    }

    private long getCooldownRemaining(Player player) {
        long end = cooldowns.get(player.getUniqueId());
        return Math.max(0, (end - System.currentTimeMillis()) / 1000L);
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldownMillis);
    }
}
