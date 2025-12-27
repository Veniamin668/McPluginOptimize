package ru.venik.optimize.chat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Антиспам-система:
 * - ограничение сообщений в секунду
 * - проверка дубликатов
 * - временный мут
 */
public class AntiSpamManager {

    private final JavaPlugin plugin;

    // Очередь временных меток сообщений
    private final Map<String, Queue<Long>> messageHistory = new ConcurrentHashMap<>();

    // Последнее сообщение игрока
    private final Map<String, String> lastMessage = new ConcurrentHashMap<>();

    // Мут до времени (timestamp)
    private final Map<String, Long> mutedUntil = new ConcurrentHashMap<>();

    public AntiSpamManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Проверка: можно ли отправить сообщение
     */
    public boolean allowMessage(Player player) {
        String key = player.getUniqueId().toString();
        long now = System.currentTimeMillis();

        // Проверка мута
        Long muteEnd = mutedUntil.get(key);
        if (muteEnd != null && muteEnd > now) {
            return false;
        }

        int limit = plugin.getConfig().getInt("chat.message-rate-limit", 5);
        int windowSeconds = plugin.getConfig().getInt("chat.rate-window-seconds", 10);
        long windowMillis = windowSeconds * 1000L;

        Queue<Long> history = messageHistory.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>());
        history.add(now);

        // Удаляем старые сообщения
        while (!history.isEmpty() && history.peek() < now - windowMillis) {
            history.poll();
        }

        // Если в окне меньше лимита — ок
        if (history.size() <= limit) {
            return true;
        }

        // Превышение лимита → мут
        int muteSeconds = plugin.getConfig().getInt("chat.mute-seconds", 10);
        mutedUntil.put(key, now + muteSeconds * 1000L);

        return false;
    }

    /**
     * Проверка на дубликат сообщения
     */
    public boolean isDuplicate(Player player, String message) {
        if (!plugin.getConfig().getBoolean("chat.duplicate-check", true)) {
            return false;
        }

        String key = player.getUniqueId().toString();
        String last = lastMessage.put(key, message);

        return last != null && last.equalsIgnoreCase(message);
    }

    /**
     * Проверка: игрок в муте?
     */
    public boolean isMuted(Player player) {
        Long until = mutedUntil.get(player.getUniqueId().toString());
        return until != null && until > System.currentTimeMillis();
    }

    /**
     * Снять мут (например, при выходе)
     */
    public void clear(Player player) {
        String key = player.getUniqueId().toString();
        mutedUntil.remove(key);
        lastMessage.remove(key);
        messageHistory.remove(key);
    }
}
