package ru.venik.optimize.chat;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.venik.optimize.config.ConfigManager;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Универсальный менеджер чата с поддержкой PlaceholderAPI,
 * приватных сообщений, reply, глобального и персонального мута.
 */
public class ChatManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    // Последний собеседник
    private final Map<UUID, UUID> lastMessenger = new ConcurrentHashMap<>();

    // Персональный мут
    private final Map<UUID, Boolean> personalMute = new ConcurrentHashMap<>();

    // Глобальный мут
    private volatile boolean globalMute = false;

    public ChatManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    // ------------------------------------------------------------
    // ЛИЧНЫЕ СООБЩЕНИЯ
    // ------------------------------------------------------------

    public void sendPrivateMessage(Player sender, Player receiver, String message) {

        if (receiver == null || !receiver.isOnline()) {
            sender.sendMessage("§cИгрок не найден!");
            return;
        }

        if (sender.getUniqueId().equals(receiver.getUniqueId())) {
            sender.sendMessage("§cВы не можете отправить сообщение самому себе.");
            return;
        }

        String formatted = "§7[§e" + sender.getName() + " §7→ §e" + receiver.getName() + "§7] §f" + message;

        sender.sendMessage(formatted);
        receiver.sendMessage(formatted);

        lastMessenger.put(sender.getUniqueId(), receiver.getUniqueId());
        lastMessenger.put(receiver.getUniqueId(), sender.getUniqueId());
    }

    public void reply(Player player, String message) {
        UUID last = lastMessenger.get(player.getUniqueId());

        if (last == null) {
            player.sendMessage("§cНет сообщений для ответа!");
            return;
        }

        Player target = Bukkit.getPlayer(last);

        if (target == null || !target.isOnline()) {
            player.sendMessage("§cИгрок оффлайн!");
            return;
        }

        sendPrivateMessage(player, target, message);
    }

    // ------------------------------------------------------------
    // ФОРМАТИРОВАНИЕ ЧАТА (с PlaceholderAPI)
    // ------------------------------------------------------------

    public String formatChatMessage(Player player, String message) {

        // Формат из конфига
        String format = configManager.getConfig().getString(
                "chat.format",
                "%prefix% %player_name%: %message%"
        );

        // Встроенные плейсхолдеры
        format = format
                .replace("%player_name%", player.getName())
                .replace("%player_displayname%", player.getDisplayName())
                .replace("%player_world%", player.getWorld().getName())
                .replace("%player_ping%", String.valueOf(getPing(player)))
                .replace("%server_online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%server_time%", getServerTime())
                .replace("%message%", message);

        // PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }

        return format;
    }

    private int getPing(Player player) {
        try {
            return player.spigot().getPing();
        } catch (Throwable ignored) {
            return 0;
        }
    }

    private String getServerTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    // ------------------------------------------------------------
    // ПЕРСОНАЛЬНЫЙ МУТ
    // ------------------------------------------------------------

    public void togglePersonalMute(Player player) {
        boolean muted = personalMute.getOrDefault(player.getUniqueId(), false);
        personalMute.put(player.getUniqueId(), !muted);

        player.sendMessage(!muted
                ? "§cЧат отключён!"
                : "§aЧат включён!");
    }

    public boolean isPersonalMuted(Player player) {
        return personalMute.getOrDefault(player.getUniqueId(), false);
    }

    // ------------------------------------------------------------
    // ГЛОБАЛЬНЫЙ МУТ
    // ------------------------------------------------------------

    public void toggleGlobalMute(Player admin) {
        globalMute = !globalMute;

        Bukkit.broadcastMessage(globalMute
                ? "§c§lГлобальный чат отключён!"
                : "§a§lГлобальный чат включён!");

        if (admin != null) {
            admin.sendMessage(globalMute
                    ? "§cВы включили глобальный мут."
                    : "§aВы отключили глобальный мут.");
        }
    }

    public boolean isGlobalMuted() {
        return globalMute;
    }

    // ------------------------------------------------------------
    // LAST MESSENGER
    // ------------------------------------------------------------

    public UUID getLastMessenger(Player player) {
        return lastMessenger.get(player.getUniqueId());
    }
}
