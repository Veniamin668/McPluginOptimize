package ru.venik.optimize.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.venik.optimize.cases.CaseManager;

/**
 * Обработчик открытия кейсов.
 * Определяет тип кейса по названию предмета.
 */
public class CaseListener implements Listener {

    private final CaseManager caseManager;

    public CaseListener(CaseManager caseManager) {
        this.caseManager = caseManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        // Проверяем, что это предмет-кейс
        if (!isCaseItem(item)) return;

        event.setCancelled(true);

        String caseId = detectCaseId(item);

        caseManager.openCase(event.getPlayer(), caseId);
    }

    // ------------------------------------------------------------
    // Проверка, что предмет — кейс
    // ------------------------------------------------------------

    private boolean isCaseItem(ItemStack item) {

        Material type = item.getType();

        if (type != Material.CHEST &&
            type != Material.ENDER_CHEST &&
            type != Material.SHULKER_BOX) {
            return false;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return false;
        }

        String name = item.getItemMeta().getDisplayName();
        if (name == null) return false;

        return name.toLowerCase().contains("кейс");
    }

    // ------------------------------------------------------------
    // Определение типа кейса
    // ------------------------------------------------------------

    private String detectCaseId(ItemStack item) {

        String name = item.getItemMeta().getDisplayName().toLowerCase();

        if (name.contains("редкий")) return "rare";
        if (name.contains("эпический")) return "epic";
        if (name.contains("легендарный")) return "legendary";

        return "common";
    }
}
