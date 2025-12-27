package ru.venik.optimize.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener для обработки кликов в меню.
 * Работает с MenuManager и Menu.
 */
public class MenuListener implements Listener {

    private final MenuManager menuManager;
    private final Map<UUID, Menu> openMenus = new HashMap<>();

    public MenuListener(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    // ------------------------------------------------------------
    // КЛИКИ В МЕНЮ
    // ------------------------------------------------------------

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Menu menu = openMenus.get(player.getUniqueId());
        if (menu == null) {
            return; // Это не меню
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

        handleAction(player, action);
    }

    // ------------------------------------------------------------
    // ОБРАБОТКА ACTION
    // ------------------------------------------------------------

    private void handleAction(Player player, String action) {

        if (action.equalsIgnoreCase("close")) {
            player.closeInventory();
            return;
        }

        String[] parts = action.split(":");
        String type = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : null;

        switch (type) {

            case "warp" -> {
                if (arg != null) {
                    menuManager.getWarpManager().warp(player, arg);
                } else {
                    menuManager.open(player, "warps");
                }
            }

            case "case" -> menuManager.open(player, "cases");

            case "shop" -> {
                menuManager.getDonationManager().openShop(player);
                player.closeInventory();
            }

            case "kit" -> {
                if (arg != null) {
                    menuManager.getKitManager().giveKit(player, arg);
                } else {
                    menuManager.open(player, "kits");
                }
            }

            case "profile" -> showProfile(player);

            case "settings" -> showSettings(player);
        }
    }

    // ------------------------------------------------------------
    // ПРОФИЛЬ
    // ------------------------------------------------------------

    private void showProfile(Player player) {
        player.closeInventory();
        player.sendMessage("§6§l=== Ваш профиль ===");
        player.sendMessage("§eИмя: §f" + player.getName());
        player.sendMessage("§eБаланс: §f" + menuManager.getDonationManager().getBalance(player) + " монет");
        player.sendMessage("§eОнлайн: §f" + player.getServer().getOnlinePlayers().size());
        player.sendMessage("§eTPS: §f" + String.format("%.2f", menuManager.getPerformanceMonitor().getCurrentTps()));
    }

    // ------------------------------------------------------------
    // НАСТРОЙКИ
    // ------------------------------------------------------------

    private void showSettings(Player player) {
        player.closeInventory();
        player.sendMessage("§6§l=== Настройки ===");
        player.sendMessage("§e/menu §7- Открыть меню");
        player.sendMessage("§e/warp §7- Телепортация");
        player.sendMessage("§e/home §7- Дома");
        player.sendMessage("§e/kit §7- Наборы");
    }

    // ------------------------------------------------------------
    // ЗАКРЫТИЕ МЕНЮ
    // ------------------------------------------------------------

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        openMenus.remove(player.getUniqueId());
    }

    // ------------------------------------------------------------
    // API
    // ------------------------------------------------------------

    public void setPlayerMenu(Player player, Menu menu) {
        openMenus.put(player.getUniqueId(), menu);
    }

    public Menu getPlayerMenu(Player player) {
        return openMenus.get(player.getUniqueId());
    }
}
