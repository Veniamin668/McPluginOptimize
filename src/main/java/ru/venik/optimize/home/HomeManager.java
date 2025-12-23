/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.home;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;
import ru.venik.optimize.home.Home;

public class HomeManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Map<String, Home>> playerHomes = new ConcurrentHashMap<UUID, Map<String, Home>>();

    public HomeManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void setHome(Player player, String homeName) {
        UUID uuid = player.getUniqueId();
        Map homes = this.playerHomes.computeIfAbsent(uuid, k -> new HashMap());
        int maxHomes = this.getMaxHomes(player);
        if (homes.size() >= maxHomes && !homes.containsKey(homeName.toLowerCase())) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043c\u0430\u043a\u0441\u0438\u043c\u0443\u043c \u0434\u043e\u043c\u043e\u0432! (\u00a7e" + maxHomes + "\u00a7c)");
            return;
        }
        homes.put(homeName.toLowerCase(), new Home(homeName, player.getLocation()));
        player.sendMessage("\u00a7a\u0414\u043e\u043c \u00a7e" + homeName + "\u00a7a \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d!");
    }

    public void deleteHome(Player player, String homeName) {
        UUID uuid = player.getUniqueId();
        Map<String, Home> homes = this.playerHomes.get(uuid);
        if (homes == null || !homes.containsKey(homeName.toLowerCase())) {
            player.sendMessage("\u00a7c\u0414\u043e\u043c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        homes.remove(homeName.toLowerCase());
        player.sendMessage("\u00a7a\u0414\u043e\u043c \u00a7e" + homeName + "\u00a7a \u0443\u0434\u0430\u043b\u0435\u043d!");
    }

    public boolean teleportHome(Player player, String homeName) {
        UUID uuid = player.getUniqueId();
        Map<String, Home> homes = this.playerHomes.get(uuid);
        if (homes == null) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0434\u043e\u043c\u043e\u0432! \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 /home set <\u0438\u043c\u044f>");
            return false;
        }
        Home home = homes.get(homeName.toLowerCase());
        if (home == null) {
            player.sendMessage("\u00a7c\u0414\u043e\u043c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d! \u0412\u0430\u0448\u0438 \u0434\u043e\u043c\u0430: \u00a7e" + String.join((CharSequence)", ", homes.keySet()));
            return false;
        }
        player.teleport(home.getLocation());
        player.sendMessage("\u00a7a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u0434\u043e\u043c\u043e\u0439 \u00a7e" + home.getName() + "\u00a7a!");
        return true;
    }

    public void listHomes(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, Home> homes = this.playerHomes.get(uuid);
        if (homes == null || homes.isEmpty()) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0434\u043e\u043c\u043e\u0432! \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 /home set <\u0438\u043c\u044f>");
            return;
        }
        player.sendMessage("\u00a76\u00a7l=== \u0412\u0430\u0448\u0438 \u0434\u043e\u043c\u0430 ===");
        homes.values().forEach(home -> {
            Location loc = home.getLocation();
            player.sendMessage("\u00a7e" + home.getName() + " \u00a77- \u00a7f" + loc.getWorld().getName() + " \u00a77(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
        });
    }

    public Map<String, Home> getPlayerHomes(Player player) {
        return new HashMap<String, Home>(this.playerHomes.getOrDefault(player.getUniqueId(), new HashMap()));
    }

    private int getMaxHomes(Player player) {
        int defaultHomes = this.configManager.getConfig().getInt("home.max-homes-default", 3);
        if (player.hasPermission("venikoptimize.home.unlimited")) {
            return Integer.MAX_VALUE;
        }
        if (player.hasPermission("venikoptimize.home.10")) {
            return 10;
        }
        if (player.hasPermission("venikoptimize.home.5")) {
            return 5;
        }
        return defaultHomes;
    }
}

