package ru.venik.optimize.protection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IPManager:
 *  - отслеживает IP игроков
 *  - хранит временные IP-сессии (1 час)
 *  - если IP совпадает и сессия активна — пароль не нужен
 *  - если IP другой или сессия истекла — требуется пароль
 */
public class IPManager {

    private final JavaPlugin plugin;

    // UUID -> Session
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    // 1 час в миллисекундах
    private static final long SESSION_DURATION = 60 * 60 * 1000L;

    public IPManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    // Проверка при входе
    // ------------------------------------------------------------

    /**
     * Проверяет, можно ли пропустить ввод пароля.
     * @return true — IP доверенный, пароль не нужен
     */
    public boolean isIpTrusted(Player player) {

        UUID uuid = player.getUniqueId();
        String currentIp = extractIp(player);

        if (currentIp == null) return false;

        Session session = sessions.get(uuid);

        if (session == null) return false;

        // IP совпадает?
        if (!session.ip.equals(currentIp)) return false;

        // Сессия ещё жива?
        return System.currentTimeMillis() < session.expiresAt;
    }

    // ------------------------------------------------------------
    // Создание новой сессии после успешного ввода пароля
    // ------------------------------------------------------------

    public void createSession(Player player) {

        String ip = extractIp(player);
        if (ip == null) return;

        sessions.put(player.getUniqueId(),
                new Session(ip, System.currentTimeMillis() + SESSION_DURATION));

        plugin.getLogger().info("IP session created for " + player.getName() + " (" + ip + ")");
    }

    // ------------------------------------------------------------
    // Очистка сессии (опционально)
    // ------------------------------------------------------------

    public void clearSession(Player player) {
        sessions.remove(player.getUniqueId());
    }

    // ------------------------------------------------------------
    // Вспомогательные методы
    // ------------------------------------------------------------

    private String extractIp(Player player) {
        try {
            return player.getAddress().getAddress().getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    // ------------------------------------------------------------
    // Внутренний класс сессии
    // ------------------------------------------------------------

    private static class Session {
        final String ip;
        final long expiresAt;

        Session(String ip, long expiresAt) {
            this.ip = ip;
            this.expiresAt = expiresAt;
        }
    }
}
