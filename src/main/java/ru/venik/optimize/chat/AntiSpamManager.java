package ru.venik.optimize.chat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AntiSpamManager {
    private final JavaPlugin plugin;
    private final Map<String, Queue<Long>> messages = new ConcurrentHashMap<>();
    private final Map<String, String> lastMessage = new ConcurrentHashMap<>();
    private final Map<String, Long> mutedUntil = new ConcurrentHashMap<>();

    public AntiSpamManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean allowMessage(Player player) {
        String key = player.getUniqueId().toString();
        long now = System.currentTimeMillis();
        Long muted = mutedUntil.get(key);
        if (muted != null && muted > now) return false;

        int limit = plugin.getConfig().getInt("chat.message-rate-limit", 5);
        int window = plugin.getConfig().getInt("chat.rate-window-seconds", 10);
        Queue<Long> q = messages.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>());
        q.add(now);
        while (!q.isEmpty() && q.peek() < now - window * 1000L) q.poll();
        if (q.size() <= limit) return true;
        // exceed limit -> mute briefly
        int muteSeconds = plugin.getConfig().getInt("chat.mute-seconds", 10);
        mutedUntil.put(key, now + muteSeconds * 1000L);
        return false;
    }

    public boolean isDuplicate(Player player, String message) {
        if (!plugin.getConfig().getBoolean("chat.duplicate-check", true)) return false;
        String key = player.getUniqueId().toString();
        String last = lastMessage.get(key);
        lastMessage.put(key, message);
        return last != null && last.equals(message);
    }

    public boolean isMuted(Player player) {
        Long t = mutedUntil.get(player.getUniqueId().toString());
        return t != null && t > System.currentTimeMillis();
    }
}
