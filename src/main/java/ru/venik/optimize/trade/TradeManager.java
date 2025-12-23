/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.trade;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;
import ru.venik.optimize.trade.TradeSession;

public class TradeManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<UUID, UUID> tradeRequests = new ConcurrentHashMap<UUID, UUID>();
    private final Map<UUID, TradeSession> activeTrades = new ConcurrentHashMap<UUID, TradeSession>();

    public TradeManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void requestTrade(Player sender, Player receiver) {
        if (receiver == null || !receiver.isOnline()) {
            sender.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        if (this.activeTrades.containsKey(sender.getUniqueId()) || this.activeTrades.containsKey(receiver.getUniqueId())) {
            sender.sendMessage("\u00a7c\u041e\u0434\u0438\u043d \u0438\u0437 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0443\u0436\u0435 \u0432 \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u0435!");
            return;
        }
        this.tradeRequests.put(receiver.getUniqueId(), sender.getUniqueId());
        sender.sendMessage("\u00a7a\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d \u0438\u0433\u0440\u043e\u043a\u0443 \u00a7e" + receiver.getName());
        receiver.sendMessage("\u00a7e" + sender.getName() + "\u00a77 \u0437\u0430\u043f\u0440\u043e\u0441\u0438\u043b \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e!");
        receiver.sendMessage("\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435: \u00a7e/trade accept \u00a77\u0438\u043b\u0438 \u00a7c/trade deny");
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            if (this.tradeRequests.containsKey(receiver.getUniqueId()) && this.tradeRequests.get(receiver.getUniqueId()).equals(sender.getUniqueId())) {
                this.tradeRequests.remove(receiver.getUniqueId());
                sender.sendMessage("\u00a7c\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e \u0438\u0441\u0442\u0435\u043a!");
            }
        }, 600L);
    }

    public void acceptTrade(Player player) {
        UUID playerUuid = player.getUniqueId();
        UUID senderUuid = this.tradeRequests.get(playerUuid);
        if (senderUuid == null) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0445 \u0437\u0430\u043f\u0440\u043e\u0441\u043e\u0432 \u043d\u0430 \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e!");
            return;
        }
        Player sender = Bukkit.getPlayer((UUID)senderUuid);
        if (sender == null || !sender.isOnline()) {
            player.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043e\u0444\u0444\u043b\u0430\u0439\u043d!");
            this.tradeRequests.remove(playerUuid);
            return;
        }
        this.tradeRequests.remove(playerUuid);
        this.startTrade(sender, player);
    }

    public void denyTrade(Player player) {
        UUID playerUuid = player.getUniqueId();
        UUID senderUuid = this.tradeRequests.get(playerUuid);
        if (senderUuid == null) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0445 \u0437\u0430\u043f\u0440\u043e\u0441\u043e\u0432 \u043d\u0430 \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e!");
            return;
        }
        Player sender = Bukkit.getPlayer((UUID)senderUuid);
        if (sender != null && sender.isOnline()) {
            sender.sendMessage("\u00a7c" + player.getName() + " \u043e\u0442\u043a\u043b\u043e\u043d\u0438\u043b \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e!");
        }
        player.sendMessage("\u00a7a\u0412\u044b \u043e\u0442\u043a\u043b\u043e\u043d\u0438\u043b\u0438 \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e!");
        this.tradeRequests.remove(playerUuid);
    }

    private void startTrade(Player player1, Player player2) {
        TradeSession session = new TradeSession(player1, player2);
        this.activeTrades.put(player1.getUniqueId(), session);
        this.activeTrades.put(player2.getUniqueId(), session);
        session.openInventories();
        player1.sendMessage("\u00a7a\u0422\u043e\u0440\u0433\u043e\u0432\u043b\u044f \u043d\u0430\u0447\u0430\u043b\u0430\u0441\u044c! \u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0434\u0438\u0442\u0435 \u043e\u0431\u043c\u0435\u043d \u043a\u043d\u043e\u043f\u043a\u043e\u0439.");
        player2.sendMessage("\u00a7a\u0422\u043e\u0440\u0433\u043e\u0432\u043b\u044f \u043d\u0430\u0447\u0430\u043b\u0430\u0441\u044c! \u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0434\u0438\u0442\u0435 \u043e\u0431\u043c\u0435\u043d \u043a\u043d\u043e\u043f\u043a\u043e\u0439.");
    }

    public void confirmTrade(Player player) {
        TradeSession session = this.activeTrades.get(player.getUniqueId());
        if (session == null) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u043e\u0439 \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u0438!");
            return;
        }
        session.confirm(player);
        if (session.isBothConfirmed()) {
            this.completeTrade(session);
        }
    }

    public void cancelTrade(Player player) {
        TradeSession session = this.activeTrades.get(player.getUniqueId());
        if (session == null) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u043e\u0439 \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u0438!");
            return;
        }
        Player other = session.getOtherPlayer(player);
        session.cancel();
        this.activeTrades.remove(player.getUniqueId());
        this.activeTrades.remove(other.getUniqueId());
        player.sendMessage("\u00a7c\u0422\u043e\u0440\u0433\u043e\u0432\u043b\u044f \u043e\u0442\u043c\u0435\u043d\u0435\u043d\u0430!");
        other.sendMessage("\u00a7c" + player.getName() + " \u043e\u0442\u043c\u0435\u043d\u0438\u043b \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e!");
    }

    private void completeTrade(TradeSession session) {
        Player player1 = session.getPlayer1();
        Player player2 = session.getPlayer2();
        for (ItemStack item : session.getPlayer1Items()) {
            if (item == null) continue;
            player2.getInventory().addItem(new ItemStack[]{item});
            player1.getInventory().removeItem(new ItemStack[]{item});
        }
        for (ItemStack item : session.getPlayer2Items()) {
            if (item == null) continue;
            player1.getInventory().addItem(new ItemStack[]{item});
            player2.getInventory().removeItem(new ItemStack[]{item});
        }
        player1.sendMessage("\u00a7a\u0422\u043e\u0440\u0433\u043e\u0432\u043b\u044f \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d\u0430!");
        player2.sendMessage("\u00a7a\u0422\u043e\u0440\u0433\u043e\u0432\u043b\u044f \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d\u0430!");
        session.close();
        this.activeTrades.remove(player1.getUniqueId());
        this.activeTrades.remove(player2.getUniqueId());
    }

    public TradeSession getTradeSession(Player player) {
        return this.activeTrades.get(player.getUniqueId());
    }
}

