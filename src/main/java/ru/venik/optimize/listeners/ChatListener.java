package ru.venik.optimize.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.venik.optimize.chat.ChatManager;
import ru.venik.optimize.chat.AntiSpamManager;
import ru.venik.optimize.social.SocialManager;

/**
 * Обработчик чата:
 *  - муты
 *  - антиспам
 *  - игнор
 *  - форматирование
 */
public class ChatListener implements Listener {

    private final ChatManager chatManager;
    private final AntiSpamManager antiSpamManager;
    private final SocialManager socialManager;

    public ChatListener(ChatManager chatManager,
                        AntiSpamManager antiSpamManager,
                        SocialManager socialManager) {

        this.chatManager = chatManager;
        this.antiSpamManager = antiSpamManager;
        this.socialManager = socialManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();

        // ------------------------------------------------------------
        // Мут чата
        // ------------------------------------------------------------
        if (chatManager.isChatMuted(player)) {
            event.setCancelled(true);
            player.sendMessage("§cВы не можете писать в чат!");
            return;
        }

        // ------------------------------------------------------------
        // Антиспам: мут
        // ------------------------------------------------------------
        if (antiSpamManager.isMuted(player)) {
            event.setCancelled(true);
            player.sendMessage("§cВы замьючены за спам. Подождите немного.");
            return;
        }

        // ------------------------------------------------------------
        // Антиспам: частота сообщений
        // ------------------------------------------------------------
        if (!antiSpamManager.allowMessage(player)) {
            event.setCancelled(true);
            player.sendMessage("§cВы пишете слишком часто, вы временно замьючены.");
            return;
        }

        // ------------------------------------------------------------
        // Антиспам: дублирование
        // ------------------------------------------------------------
        if (antiSpamManager.isDuplicate(player, message)) {
            event.setCancelled(true);
            player.sendMessage("§cПожалуйста, не спамьте одинаковыми сообщениями.");
            return;
        }

        // ------------------------------------------------------------
        // Игнор
        // ------------------------------------------------------------
        event.getRecipients().removeIf(recipient ->
                socialManager.isIgnored(recipient, player)
        );

        // ------------------------------------------------------------
        // Форматирование
        // ------------------------------------------------------------
        String formatted = chatManager.formatChatMessage(player, message);
        event.setFormat(formatted);
    }
}
