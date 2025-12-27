package ru.venik.optimize.trade;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TradeSession {

    private final Player p1;
    private final Player p2;

    private final Inventory inv1;
    private final Inventory inv2;

    private boolean p1Confirmed = false;
    private boolean p2Confirmed = false;

    private static final int CONFIRM_SLOT = 49; // центр нижней линии
    private static final int BORDER_START = 36; // нижние 18 слотов — зона интерфейса

    public TradeSession(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;

        this.inv1 = Bukkit.createInventory(null, 54, "§6Торговля: " + p2.getName());
        this.inv2 = Bukkit.createInventory(null, 54, "§6Торговля: " + p1.getName());

        setupInterface(inv1, true);
        setupInterface(inv2, false);
    }

    // ------------------------------------------------------------
    // GUI SETUP
    // ------------------------------------------------------------

    private void setupInterface(Inventory inv, boolean isP1) {

        // Панель разделения
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.setDisplayName(" ");
        glass.setItemMeta(gm);

        for (int i = 36; i < 45; i++) {
            inv.setItem(i, glass);
        }

        // Кнопка подтверждения
        inv.setItem(CONFIRM_SLOT, createConfirmButton(false));
    }

    private ItemStack createConfirmButton(boolean confirmed) {
        ItemStack item = new ItemStack(confirmed ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta meta = item.getItemMeta();

        if (confirmed) {
            meta.setDisplayName("§a§lПОДТВЕРЖДЕНО");
            meta.setLore(List.of("§7Ожидание второго игрока..."));
        } else {
            meta.setDisplayName("§c§lПОДТВЕРДИТЬ");
            meta.setLore(List.of("§7Нажмите для подтверждения обмена"));
        }

        item.setItemMeta(meta);
        return item;
    }

    // ------------------------------------------------------------
    // OPEN
    // ------------------------------------------------------------

    public void openInventories() {
        p1.openInventory(inv1);
        p2.openInventory(inv2);
    }

    // ------------------------------------------------------------
    // CONFIRMATION
    // ------------------------------------------------------------

    public void confirm(Player player) {
        if (player.equals(p1)) {
            p1Confirmed = true;
            p2.sendMessage("§e" + p1.getName() + " §7подтвердил обмен.");
            inv1.setItem(CONFIRM_SLOT, createConfirmButton(true));
        } else {
            p2Confirmed = true;
            p1.sendMessage("§e" + p2.getName() + " §7подтвердил обмен.");
            inv2.setItem(CONFIRM_SLOT, createConfirmButton(true));
        }
    }

    public boolean isBothConfirmed() {
        return p1Confirmed && p2Confirmed;
    }

    // ------------------------------------------------------------
    // CANCEL / CLOSE
    // ------------------------------------------------------------

    public void cancel() {
        p1.closeInventory();
        p2.closeInventory();
    }

    public void close() {
        p1.closeInventory();
        p2.closeInventory();
    }

    // ------------------------------------------------------------
    // ITEM EXTRACTION
    // ------------------------------------------------------------

    public List<ItemStack> getPlayer1Items() {
        return extractItems(inv1);
    }

    public List<ItemStack> getPlayer2Items() {
        return extractItems(inv2);
    }

    private List<ItemStack> extractItems(Inventory inv) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < 36; i++) { // верхние 36 слотов — зона обмена
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                items.add(item.clone());
            }
        }

        return items;
    }

    // ------------------------------------------------------------
    // RESET CONFIRMATION ON CHANGE
    // ------------------------------------------------------------

    public void resetConfirmation() {
        if (p1Confirmed) {
            p1Confirmed = false;
            inv1.setItem(CONFIRM_SLOT, createConfirmButton(false));
            p1.sendMessage("§cВаше подтверждение сброшено из-за изменения предметов.");
        }
        if (p2Confirmed) {
            p2Confirmed = false;
            inv2.setItem(CONFIRM_SLOT, createConfirmButton(false));
            p2.sendMessage("§cВаше подтверждение сброшено из-за изменения предметов.");
        }
    }

    // ------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------

    public Player getPlayer1() {
        return p1;
    }

    public Player getPlayer2() {
        return p2;
    }

    public Player getOtherPlayer(Player p) {
        return p.equals(p1) ? p2 : p1;
    }

    public Inventory getInventory1() {
        return inv1;
    }

    public Inventory getInventory2() {
        return inv2;
    }
}
