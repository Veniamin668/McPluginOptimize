package ru.venik.optimize.protection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IPManager {
    private final JavaPlugin plugin;
    private final Map<String, String> lastIp = new ConcurrentHashMap<>();

    public IPManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void onLogin(Player player) {
        if (!plugin.getConfig().getBoolean("protection.ip-check", false)) return;
        String ip = player.getAddress().getAddress().getHostAddress();
        String key = player.getUniqueId().toString();
        String prev = lastIp.get(key);
        if (prev == null) {
            lastIp.put(key, ip);
        } else {
            if (!prev.equals(ip)) {
                // if changed, log and optionally take action
                plugin.getLogger().warning("IP changed for " + player.getName() + ": " + prev + " -> " + ip);
                // can add extra security actions here (notify, require reconfirmation, etc.)
            }
        }
    }

    public void setIp(Player player, String ip) {
        lastIp.put(player.getUniqueId().toString(), ip);
    }
}
