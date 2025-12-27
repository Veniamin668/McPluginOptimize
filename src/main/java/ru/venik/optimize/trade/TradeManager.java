package ru.venik.optimize.trade;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TradeManager:
 *  - /trade <player>
 *  - /trade accept
 *  - /trade deny
 *  - безопасная торговля
 */
public class TradeManager {

    private final JavaPlugin plugin;

    // Целевой игрок -> запрос
    private final Map<UUID, TradeRequest> requests = new ConcurrentHashMap<>();

    // Игрок -> активная сессия
    private final Map<UUID, TradeSession> activeTrades = new ConcurrentHashMap<>();

    private static final long REQUEST_LIFETIME = 30_000L; // 30 секунд

    public TradeManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    // REQUEST
    // ------------------------------------------------------------

    public void requestTrade(Player sender, Player receiver) {

        if (!validateTarget(sender, receiver)) return;

        if (activeTrades.containsKey(sender.getUniqueId()) ||
            activeTrades.containsKey(receiver.getUniqueId())) {

            sender.sendMessage("§cОдин из игроков уже в торговле.");
            return;
        }

        UUID targetId = receiver.getUniqueId();

        requests.put(targetId, new TradeRequest(
                sender.getUniqueId(),
                targetId,
                System.currentTimeMillis()
        ));

        sender.sendMessage("§aЗапрос отправлен игроку §e" + receiver.getName());
        receiver.sendMessage("§e" + sender.getName() + " §7хочет начать торговлю.");
        receiver.sendMessage("§7Используйте: §e/trade accept §7или §c/trade deny");

        scheduleExpire(targetId);
    }

    private boolean validateTarget(Player sender, Player receiver) {
        if (receiver == null || !receiver.isOnline()) {
            sender.sendMessage("§cИгрок не найден или оффлайн.");
            return false;
        }
        if (sender.equals(receiver)) {
            sender.sendMessage("§cВы не можете торговать с собой.");
            return false;
        }
        return true;
    }

    // ------------------------------------------------------------
    // ACCEPT / DENY
    // ------------------------------------------------------------

    public void acceptTrade(Player receiver) {

        UUID receiverId = receiver.getUniqueId();
        TradeRequest req = requests.get(receiverId);

        if (req == null || req.isExpired()) {
            receiver.sendMessage("§cУ вас нет активных запросов.");
            requests.remove(receiverId);
            return;
        }

        Player sender = Bukkit.getPlayer(req.sender());
        if (sender == null || !sender.isOnline()) {
            receiver.sendMessage("§cИгрок, отправивший запрос, оффлайн.");
            requests.remove(receiverId);
            return;
        }

        requests.remove(receiverId);
        startTrade(sender, receiver);
    }

    public void denyTrade(Player receiver) {

        UUID receiverId = receiver.getUniqueId();
        TradeRequest req = requests.get(receiverId);

        if (req == null) {
            receiver.sendMessage("§cУ вас нет активных запросов.");
            return;
        }

        Player sender = Bukkit.getPlayer(req.sender());
        if (sender != null && sender.isOnline()) {
            sender.sendMessage("§c" + receiver.getName() + " отклонил торговлю.");
        }

        receiver.sendMessage("§aВы отклонили запрос.");
        requests.remove(receiverId);
    }

    // ------------------------------------------------------------
    // START TRADE
    // ------------------------------------------------------------

    private void startTrade(Player p1, Player p2) {

        TradeSession session = new TradeSession(p1, p2);

        activeTrades.put(p1.getUniqueId(), session);
        activeTrades.put(p2.getUniqueId(), session);

        session.openInventories();

        p1.sendMessage("§aТорговля началась! Подтвердите обмен.");
        p2.sendMessage("§aТорговля началась! Подтвердите обмен.");
    }

    // ------------------------------------------------------------
    // CONFIRM / CANCEL
    // ------------------------------------------------------------

    public void confirmTrade(Player player) {

        TradeSession session = activeTrades.get(player.getUniqueId());

        if (session == null) {
            player.sendMessage("§cУ вас нет активной торговли.");
            return;
        }

        session.confirm(player);

        if (session.isBothConfirmed()) {
            completeTrade(session);
        }
    }

    public void cancelTrade(Player player) {

        TradeSession session = activeTrades.get(player.getUniqueId());

        if (session == null) {
            player.sendMessage("§cУ вас нет активной торговли.");
            return;
        }

        Player other = session.getOtherPlayer(player);

        session.cancel();

        activeTrades.remove(player.getUniqueId());
        activeTrades.remove(other.getUniqueId());

        player.sendMessage("§cТорговля отменена.");
        other.sendMessage("§c" + player.getName() + " отменил торговлю.");
    }

    // ------------------------------------------------------------
    // COMPLETE TRADE
    // ------------------------------------------------------------

    private void completeTrade(TradeSession session) {

        Player p1 = session.getPlayer1();
        Player p2 = session.getPlayer2();

        // Передача предметов
        for (ItemStack item : session.getPlayer1Items()) {
            if (item != null) p2.getInventory().addItem(item.clone());
        }
        for (ItemStack item : session.getPlayer2Items()) {
            if (item != null) p1.getInventory().addItem(item.clone());
        }

        p1.sendMessage("§aТорговля завершена!");
        p2.sendMessage("§aТорговля завершена!");

        session.close();

        activeTrades.remove(p1.getUniqueId());
        activeTrades.remove(p2.getUniqueId());
    }

    // ------------------------------------------------------------
    // EXPIRATION
    // ------------------------------------------------------------

    private void scheduleExpire(UUID targetId) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            TradeRequest req = requests.get(targetId);
            if (req != null && req.isExpired()) {
                Player sender = Bukkit.getPlayer(req.sender());
                if (sender != null) {
                    sender.sendMessage("§cВаш запрос на торговлю истёк.");
                }
                requests.remove(targetId);
            }
        }, 20 * 30); // 30 секунд
    }

    // ------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------

    public TradeSession getTradeSession(Player player) {
        return activeTrades.get(player.getUniqueId());
    }

    // ------------------------------------------------------------
    // DATA CLASS
    // ------------------------------------------------------------

    private record TradeRequest(UUID sender, UUID target, long time) {
        boolean isExpired() {
            return System.currentTimeMillis() - time > REQUEST_LIFETIME;
        }
    }
}
