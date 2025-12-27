package ru.venik.optimize.auth;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {

    private final JavaPlugin plugin;

    // Потокобезопасные коллекции
    private final Set<UUID> authenticated = ConcurrentHashMap.newKeySet();
    private final Set<UUID> pending = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Integer> timeoutTaskId = new ConcurrentHashMap<>();

    private final String salt;

    public AuthManager(JavaPlugin plugin) {
        this.plugin = plugin;

        FileConfiguration cfg = plugin.getConfig();

        // Гарантируем наличие дефолтов
        cfg.addDefault("auth.enabled", true);
        cfg.addDefault("auth.timeout-seconds", 60);
        cfg.addDefault("auth.passwords", new HashMap<>());
        cfg.addDefault("auth.salt", "change-this-salt");

        cfg.options().copyDefaults(true);
        plugin.saveConfig();

        this.salt = cfg.getString("auth.salt", "change-this-salt");
    }

    public boolean hasPassword(UUID uuid) {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("auth.passwords");
        return sec != null && sec.contains(uuid.toString());
    }

    public void scheduleLoginTimeout(Player player) {
        UUID id = player.getUniqueId();
        int timeout = plugin.getConfig().getInt("auth.timeout-seconds", 60);

        pending.add(id);

        // Отменяем старую задачу, если есть
        Integer oldTask = timeoutTaskId.remove(id);
        if (oldTask != null) {
            Bukkit.getScheduler().cancelTask(oldTask);
        }

        int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!authenticated.contains(id)) {
                Player p = Bukkit.getPlayer(id);
                if (p != null && p.isOnline()) {
                    String msg = plugin.getConfig().getString("auth.messages.kick_timeout",
                            "Вы не авторизовались вовремя.");
                    p.kickPlayer(msg);
                }
            }

            pending.remove(id);
            timeoutTaskId.remove(id);

        }, timeout * 20L).getTaskId();

        timeoutTaskId.put(id, taskId);
    }

    public boolean register(Player player, String password) {
        UUID id = player.getUniqueId();
        String hash = hash(password);

        plugin.getConfig().set("auth.passwords." + id.toString(), hash);
        plugin.saveConfig();

        authenticate(id);
        return true;
    }

    public boolean authenticate(UUID id, String password) {
        String stored = plugin.getConfig().getString("auth.passwords." + id.toString(), null);
        if (stored == null) return false;

        if (stored.equals(hash(password))) {
            authenticate(id);
            return true;
        }
        return false;
    }

    public void authenticate(UUID id) {
        pending.remove(id);
        authenticated.add(id);

        Integer task = timeoutTaskId.remove(id);
        if (task != null) {
            Bukkit.getScheduler().cancelTask(task);
        }
    }

    public boolean isAuthenticated(UUID id) {
        return authenticated.contains(id);
    }

    public boolean isPending(UUID id) {
        return pending.contains(id);
    }

    public void logout(UUID id) {
        pending.remove(id);
        authenticated.remove(id);

        Integer task = timeoutTaskId.remove(id);
        if (task != null) {
            Bukkit.getScheduler().cancelTask(task);
        }
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));

            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            plugin.getLogger().severe("SHA-256 недоступен: " + e.getMessage());
            return Integer.toHexString(input.hashCode()); // fallback, чтобы не крашить сервер
        }
    }
}
