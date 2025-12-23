/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.chat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class ChatManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<UUID, UUID> lastMessenger = new ConcurrentHashMap<UUID, UUID>();
    private final Map<UUID, Boolean> chatMuted = new ConcurrentHashMap<UUID, Boolean>();

    public ChatManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void sendPrivateMessage(Player sender, Player receiver, String message) {
        if (receiver == null || !receiver.isOnline()) {
            sender.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        String formattedMessage = "\u00a77[\u00a7e" + sender.getName() + " \u00a77-> \u00a7e" + receiver.getName() + "\u00a77] \u00a7f" + message;
        sender.sendMessage(formattedMessage);
        receiver.sendMessage(formattedMessage);
        this.lastMessenger.put(receiver.getUniqueId(), sender.getUniqueId());
        this.lastMessenger.put(sender.getUniqueId(), receiver.getUniqueId());
    }

    public void reply(Player player, String message) {
        UUID lastUuid = this.lastMessenger.get(player.getUniqueId());
        if (lastUuid == null) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0439 \u0434\u043b\u044f \u043e\u0442\u0432\u0435\u0442\u0430!");
            return;
        }
        Player target = this.plugin.getServer().getPlayer(lastUuid);
        if (target == null || !target.isOnline()) {
            player.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043e\u0444\u0444\u043b\u0430\u0439\u043d!");
            return;
        }
        this.sendPrivateMessage(player, target, message);
    }

    public String formatChatMessage(Player player, String message) {
        String prefix = "\u00a77";
        if (player.hasPermission("venikoptimize.vip")) {
            prefix = "\u00a76[VIP] \u00a77";
        }
        String formatted = prefix + player.getName() + "\u00a7f: " + message;
        return formatted;
    }

    public void toggleChatMute(Player player) {
        boolean muted = this.chatMuted.getOrDefault(player.getUniqueId(), false);
        this.chatMuted.put(player.getUniqueId(), !muted);
        player.sendMessage(!muted ? "\u00a7c\u0427\u0430\u0442 \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d!" : "\u00a7a\u0427\u0430\u0442 \u0432\u043a\u043b\u044e\u0447\u0435\u043d!");
    }

    public boolean isChatMuted(Player player) {
        return this.chatMuted.getOrDefault(player.getUniqueId(), false);
    }

    public UUID getLastMessenger(Player player) {
        return this.lastMessenger.get(player.getUniqueId());
    }
}

