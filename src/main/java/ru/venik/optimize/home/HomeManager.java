package ru.venik.optimize.home;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер домов игрока.
 * Полностью автономный, без зависимостей от плагина.
 */
public class HomeManager {

    private final Map<UUID, Map<String, Home>> homes = new ConcurrentHashMap<>();

    // Лимиты домов (можно вынести в конфиг или донат-систему)
    private final int defaultMaxHomes;
    private final int vipHomes;
    private final int premiumHomes;

    public HomeManager(int defaultMaxHomes, int vipHomes, int premiumHomes) {
        this.defaultMaxHomes = defaultMaxHomes;
        this.vipHomes = vipHomes;
        this.premiumHomes = premiumHomes;
    }

    // ------------------------------------------------------------
    // Установка дома
    // ------------------------------------------------------------

    public void setHome(Player player, String name) {

        String key = name.toLowerCase();
        UUID uuid = player.getUniqueId();

        Map<String, Home> map = homes.computeIfAbsent(uuid, k -> new HashMap<>());

        int max = getMaxHomes(player);

        if (map.size() >= max && !map.containsKey(key)) {
            player.sendMessage("§cУ вас максимум домов (§e" + max + "§c)");
            return;
        }

        map.put(key, new Home(name, player.getLocation()));
        player.sendMessage("§aДом §e" + name + " §aустановлен!");
    }

    // ------------------------------------------------------------
    // Удаление дома
    // ------------------------------------------------------------

    public void deleteHome(Player player, String name) {

        String key = name.toLowerCase();
        UUID uuid = player.getUniqueId();

        Map<String, Home> map = homes.get(uuid);

        if (map == null || !map.containsKey(key)) {
            player.sendMessage("§cДом не найден!");
            return;
        }

        map.remove(key);
        player.sendMessage("§aДом §e" + name + " §aудалён!");
    }

    // ------------------------------------------------------------
    // Телепорт домой
    // ------------------------------------------------------------

    public boolean teleportHome(Player player, String name) {

        String key = name.toLowerCase();
        UUID uuid = player.getUniqueId();

        Map<String, Home> map = homes.get(uuid);

        if (map == null || map.isEmpty()) {
            player.sendMessage("§cУ вас нет домов! Используйте /home set <имя>");
            return false;
        }

        Home home = map.get(key);

        if (home == null) {
            player.sendMessage("§cДом не найден! Ваши дома: §e" + String.join(", ", map.keySet()));
            return false;
        }

        player.teleport(home.getLocation());
        player.sendMessage("§aТелепортация домой §e" + home.getName());
        return true;
    }

    // ------------------------------------------------------------
    // Список домов
    // ------------------------------------------------------------

    public void listHomes(Player player) {

        UUID uuid = player.getUniqueId();
        Map<String, Home> map = homes.get(uuid);

        if (map == null || map.isEmpty()) {
            player.sendMessage("§cУ вас нет домов! Используйте /home set <имя>");
            return;
        }

        player.sendMessage("§6§l=== Ваши дома ===");

        for (Home home : map.values()) {
            Location loc = home.getLocation();
            player.sendMessage("§e" + home.getName() + " §7- §f" +
                    loc.getWorld().getName() + " §7(" +
                    loc.getBlockX() + ", " +
                    loc.getBlockY() + ", " +
                    loc.getBlockZ() + ")");
        }
    }

    // ------------------------------------------------------------
    // Получение домов игрока
    // ------------------------------------------------------------

    public Map<String, Home> getPlayerHomes(Player player) {
        return new HashMap<>(homes.getOrDefault(player.getUniqueId(), Collections.emptyMap()));
    }

    // ------------------------------------------------------------
    // Лимиты домов
    // ------------------------------------------------------------

    private int getMaxHomes(Player player) {

        if (player.hasPermission("venik.home.unlimited"))
            return Integer.MAX_VALUE;

        if (player.hasPermission("venik.home.premium"))
            return premiumHomes;

        if (player.hasPermission("venik.home.vip"))
            return vipHomes;

        return defaultMaxHomes;
    }
}
