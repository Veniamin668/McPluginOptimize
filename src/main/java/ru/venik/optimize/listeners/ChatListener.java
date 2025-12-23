/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.chat.AntiSpamManager;

public class ChatListener
implements Listener {
    private final VenikOptimize plugin;

    public ChatListener(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (this.plugin.getChatManager().isChatMuted(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("\u00a7c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043f\u0438\u0441\u0430\u0442\u044c \u0432 \u0447\u0430\u0442!");
            return;
        }
        // anti-spam
        if (this.plugin.getAntiSpamManager().isMuted(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("\u00a7cВы замьючены за спам. Подождите немного.");
            return;
        }
        if (!this.plugin.getAntiSpamManager().allowMessage(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("\u00a7cВы пишете слишком часто, вы временно замьючены.");
            return;
        }
        if (this.plugin.getAntiSpamManager().isDuplicate(event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("\u00a7cПожалуйста, не спамьте одинаковыми сообщениями.");
            return;
        }
        event.getRecipients().removeIf(player -> this.plugin.getSocialManager().isIgnored((Player)player, event.getPlayer()));
        String formatted = this.plugin.getChatManager().formatChatMessage(event.getPlayer(), event.getMessage());
        event.setFormat(formatted);
    }
}

