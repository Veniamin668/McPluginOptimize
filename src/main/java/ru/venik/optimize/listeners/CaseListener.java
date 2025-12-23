/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.cases.CaseManager;

public class CaseListener
implements Listener {
    private final VenikOptimize plugin;
    private final CaseManager caseManager;

    public CaseListener(VenikOptimize plugin, CaseManager caseManager) {
        this.plugin = plugin;
        this.caseManager = caseManager;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        String displayName;
        if (!event.hasItem()) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        if ((item.getType() == Material.CHEST || item.getType() == Material.ENDER_CHEST || item.getType() == Material.SHULKER_BOX) && item.hasItemMeta() && item.getItemMeta().hasLore() && ((displayName = item.getItemMeta().getDisplayName()).contains("\u043a\u0435\u0439\u0441") || displayName.contains("\u041a\u0435\u0439\u0441"))) {
            event.setCancelled(true);
            String caseId = "common";
            if (displayName.contains("\u0420\u0435\u0434\u043a\u0438\u0439") || displayName.contains("\u0440\u0435\u0434\u043a\u0438\u0439")) {
                caseId = "rare";
            } else if (displayName.contains("\u042d\u043f\u0438\u0447\u0435\u0441\u043a\u0438\u0439") || displayName.contains("\u044d\u043f\u0438\u0447\u0435\u0441\u043a\u0438\u0439")) {
                caseId = "epic";
            } else if (displayName.contains("\u041b\u0435\u0433\u0435\u043d\u0434\u0430\u0440\u043d\u044b\u0439") || displayName.contains("\u043b\u0435\u0433\u0435\u043d\u0434\u0430\u0440\u043d\u044b\u0439")) {
                caseId = "legendary";
            }
            this.caseManager.openCase(event.getPlayer(), caseId);
        }
    }
}

