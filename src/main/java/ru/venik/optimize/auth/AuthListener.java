package ru.venik.optimize.auth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AuthListener implements Listener {
    private final JavaPlugin plugin;
    private final AuthManager auth;

    public AuthListener(JavaPlugin plugin, AuthManager auth) {
        this.plugin = plugin;
        this.auth = auth;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("auth.enabled", true)) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // IP check (без краша)
        try {
            ru.venik.optimize.VenikOptimize.getInstance().getIpManager().onLogin(player);
        } catch (Throwable t) {
            plugin.getLogger().warning("IP check failed for " + player.getName() + ": " + t.getMessage());
        }

        // Авторизация
        if (auth.hasPassword(uuid)) {
            player.sendMessage(plugin.getConfig().getString("auth.messages.enter_password", "Введите пароль командой /login <пароль>"));
        } else {
            player.sendMessage("У вас нет пароля. Зарегистрируйтесь: /register <пароль>");
        }

        auth.scheduleLoginTimeout(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        auth.logout(uuid);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!auth.isAuthenticated(uuid)) {
            if (changedBlock(event)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean changedBlock(PlayerMoveEvent event) {
        return event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!auth.isAuthenticated(uuid)) {
            player.sendMessage("Вы должны авторизоваться прежде чем писать в чат.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String msg = event.getMessage().toLowerCase();

        if (msg.startsWith("/login") || msg.startsWith("/l") || msg.startsWith("/register")) return;

        if (!auth.isAuthenticated(uuid)) {
            player.sendMessage("Вы должны авторизоваться прежде чем использовать команды.");
            event.setCancelled(true);
        }
    }
}
