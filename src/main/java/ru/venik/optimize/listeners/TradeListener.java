/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.trade.TradeSession;

public class TradeListener
implements Listener {
    private final VenikOptimize plugin;

    public TradeListener(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        TradeSession session = this.plugin.getTradeManager().getTradeSession(player);
        if (session == null) {
            return;
        }
        if (event.getSlot() == 45) {
            event.setCancelled(true);
            this.plugin.getTradeManager().confirmTrade(player);
            return;
        }
        if (event.getSlot() >= 36 && event.getSlot() < 45) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        if (humanEntity instanceof Player) {
            Player player = (Player)humanEntity;
            TradeSession session = this.plugin.getTradeManager().getTradeSession(player);
            if (session != null) {
                this.plugin.getTradeManager().cancelTrade(player);
            }
        }
    }
}

