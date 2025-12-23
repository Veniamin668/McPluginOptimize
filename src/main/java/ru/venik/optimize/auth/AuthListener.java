package ru.venik.optimize.auth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        if (!plugin.getConfig().getBoolean("auth.enabled", true)) return;
        // IP check
        try {
            ru.venik.optimize.VenikOptimize.getInstance().getIpManager().onLogin(p);
        } catch (Exception ignored) {}
        if (auth.hasPassword(id)) {
            p.sendMessage(plugin.getConfig().getString("auth.messages.enter_password", "Введите пароль командой /login <пароль>"));
            auth.scheduleLoginTimeout(p);
        } else {
            p.sendMessage("У вас нет пароля. Зарегистрируйтесь: /register <пароль>");
            auth.scheduleLoginTimeout(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        auth.logout(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        if (!auth.isAuthenticated(id) && !auth.isPending(id)) {
            // we still treat them as requiring login
            if (changedBlock(e)) {
                e.setCancelled(true);
            }
        } else if (!auth.isAuthenticated(id)) {
            // pending but not authenticated
            if (changedBlock(e)) {
                e.setCancelled(true);
            }
        }
    }

    private boolean changedBlock(PlayerMoveEvent e) {
        return e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockY() != e.getTo().getBlockY();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        if (!auth.isAuthenticated(id)) {
            e.getPlayer().sendMessage("Вы должны авторизоваться прежде чем писать в чат.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        String msg = e.getMessage().toLowerCase();
        if (msg.startsWith("/login") || msg.startsWith("/l") || msg.startsWith("/register")) return;
        if (!auth.isAuthenticated(id)) {
            e.getPlayer().sendMessage("Вы должны авторизоваться прежде чем использовать команды.");
            e.setCancelled(true);
        }
    }
}
