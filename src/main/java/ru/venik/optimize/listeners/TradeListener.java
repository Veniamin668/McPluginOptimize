package ru.venik.optimize.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import ru.venik.optimize.trade.TradeManager;
import ru.venik.optimize.trade.TradeSession;

/**
 * Listener для системы трейдов:
 *  - блокировка слотов
 *  - кнопка подтверждения
 *  - отмена при закрытии
 */
public class TradeListener implements Listener {

    private final TradeManager tradeManager;

    public TradeListener(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    // ------------------------------------------------------------
    // КЛИКИ В ИНВЕНТАРЕ ТРЕЙДА
    // ------------------------------------------------------------

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        TradeSession session = tradeManager.getTradeSession(player);
        if (session == null) {
            return;
        }

        int slot = event.getSlot();

        // Кнопка подтверждения (обычно слот 45)
        if (slot == session.getConfirmButtonSlot()) {
            event.setCancelled(true);
            tradeManager.confirmTrade(player);
            return;
        }

        // Блокируем "зону партнёра" (обычно 36–44)
        if (session.isBlockedSlot(slot)) {
            event.setCancelled(true);
        }
    }

    // ------------------------------------------------------------
    // ЗАКРЫТИЕ ИНВЕНТАРЯ — ОТМЕНА ТРЕЙДА
    // ------------------------------------------------------------

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        TradeSession session = tradeManager.getTradeSession(player);
        if (session != null) {
            tradeManager.cancelTrade(player);
        }
    }
}
