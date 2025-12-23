/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.menu.Menu;

public class MenuListener
implements Listener {
    private final VenikOptimize plugin;
    private final Map<UUID, Menu> playerMenus = new HashMap<UUID, Menu>();

    public MenuListener(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public static MenuListener create(VenikOptimize plugin) {
        return new MenuListener(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        Menu menu = this.playerMenus.get(player.getUniqueId());
        if (menu == null) {
            return;
        }
        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }
        int slot = event.getSlot();
        String action = menu.getAction(slot);
        if (action == null) {
            return;
        }
        this.handleAction(player, action);
    }

    private void handleAction(Player player, String action) {
        if (action.equals("close")) {
            player.closeInventory();
            return;
        }
        String[] parts = action.split(":");
        if (parts.length < 1) {
            return;
        }
        switch (parts[0]) {
            case "warp": {
                if (parts.length > 1) {
                    this.plugin.getWarpManager().warp(player, parts[1]);
                    break;
                }
                this.plugin.getMenuManager().openMenu(player, "warps");
                break;
            }
            case "case": {
                this.plugin.getMenuManager().openMenu(player, "cases");
                break;
            }
            case "shop": {
                this.plugin.getDonationManager().openShop(player);
                player.closeInventory();
                break;
            }
            case "kit": {
                if (parts.length > 1) {
                    this.plugin.getKitManager().giveKit(player, parts[1]);
                    break;
                }
                this.plugin.getMenuManager().openMenu(player, "kits");
                break;
            }
            case "profile": {
                this.showProfile(player);
                break;
            }
            case "settings": {
                this.showSettings(player);
            }
        }
    }

    private void showProfile(Player player) {
        player.closeInventory();
        player.sendMessage("\u00a76\u00a7l=== \u0412\u0430\u0448 \u043f\u0440\u043e\u0444\u0438\u043b\u044c ===");
        player.sendMessage("\u00a7e\u0418\u043c\u044f: \u00a7f" + player.getName());
        player.sendMessage("\u00a7e\u0411\u0430\u043b\u0430\u043d\u0441: \u00a7f" + this.plugin.getDonationManager().getBalance(player) + " \u043c\u043e\u043d\u0435\u0442");
        player.sendMessage("\u00a7e\u041e\u043d\u043b\u0430\u0439\u043d: \u00a7f" + this.plugin.getServer().getOnlinePlayers().size());
        player.sendMessage("\u00a7eTPS: \u00a7f" + String.format("%.2f", this.plugin.getPerformanceMonitor().getCurrentTps()));
    }

    private void showSettings(Player player) {
        player.closeInventory();
        player.sendMessage("\u00a76\u00a7l=== \u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 ===");
        player.sendMessage("\u00a7e/menu \u00a77- \u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043c\u0435\u043d\u044e");
        player.sendMessage("\u00a7e/warp \u00a77- \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f");
        player.sendMessage("\u00a7e/home \u00a77- \u0414\u043e\u043c\u0430");
        player.sendMessage("\u00a7e/kit \u00a77- \u041d\u0430\u0431\u043e\u0440\u044b");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        if (humanEntity instanceof Player) {
            Player player = (Player)humanEntity;
            this.playerMenus.remove(player.getUniqueId());
        }
    }

    public void setPlayerMenu(Player player, Menu menu) {
        this.playerMenus.put(player.getUniqueId(), menu);
    }

    public Menu getPlayerMenu(Player player) {
        return this.playerMenus.get(player.getUniqueId());
    }
}

