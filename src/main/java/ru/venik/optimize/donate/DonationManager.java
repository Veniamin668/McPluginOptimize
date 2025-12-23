/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.donate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;
import ru.venik.optimize.donate.DonateItem;

public class DonationManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Double> playerBalance = new HashMap<UUID, Double>();
    private final Map<String, DonateItem> shopItems = new HashMap<String, DonateItem>();

    public DonationManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.loadShopItems();
    }

    private void loadShopItems() {
        if (!this.configManager.getConfig().getBoolean("donate.enabled", true)) {
            return;
        }
        this.shopItems.put("diamond", new DonateItem("diamond", "\u00a7b\u0410\u043b\u043c\u0430\u0437", Material.DIAMOND, 1, 100.0));
        this.shopItems.put("diamond_block", new DonateItem("diamond_block", "\u00a7b\u0411\u043b\u043e\u043a \u0430\u043b\u043c\u0430\u0437\u043e\u0432", Material.DIAMOND_BLOCK, 1, 900.0));
        this.shopItems.put("emerald", new DonateItem("emerald", "\u00a7a\u0418\u0437\u0443\u043c\u0440\u0443\u0434", Material.EMERALD, 1, 50.0));
        this.shopItems.put("emerald_block", new DonateItem("emerald_block", "\u00a7a\u0411\u043b\u043e\u043a \u0438\u0437\u0443\u043c\u0440\u0443\u0434\u043e\u0432", Material.EMERALD_BLOCK, 1, 450.0));
        this.shopItems.put("netherite_ingot", new DonateItem("netherite_ingot", "\u00a78\u041d\u0435\u0437\u0435\u0440\u0438\u0442\u043e\u0432\u044b\u0439 \u0441\u043b\u0438\u0442\u043e\u043a", Material.NETHERITE_INGOT, 1, 500.0));
        this.shopItems.put("beacon", new DonateItem("beacon", "\u00a7b\u041c\u0430\u044f\u043a", Material.BEACON, 1, 2000.0));
        this.shopItems.put("case_common", new DonateItem("case_common", "\u00a77\u041e\u0431\u044b\u0447\u043d\u044b\u0439 \u043a\u0435\u0439\u0441", Material.CHEST, 1, 200.0));
        this.shopItems.put("case_rare", new DonateItem("case_rare", "\u00a7b\u0420\u0435\u0434\u043a\u0438\u0439 \u043a\u0435\u0439\u0441", Material.ENDER_CHEST, 1, 500.0));
        this.shopItems.put("case_epic", new DonateItem("case_epic", "\u00a75\u042d\u043f\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u043a\u0435\u0439\u0441", Material.SHULKER_BOX, 1, 1000.0));
        this.shopItems.put("case_legendary", new DonateItem("case_legendary", "\u00a76\u041b\u0435\u0433\u0435\u043d\u0434\u0430\u0440\u043d\u044b\u0439 \u043a\u0435\u0439\u0441", Material.ENDER_CHEST, 1, 2000.0));
    }

    public void addBalance(Player player, double amount) {
        double current = this.playerBalance.getOrDefault(player.getUniqueId(), 0.0);
        this.playerBalance.put(player.getUniqueId(), current + amount);
        player.sendMessage("\u00a7a\u0412\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441 \u043f\u043e\u043f\u043e\u043b\u043d\u0435\u043d \u043d\u0430 \u00a7e" + amount + "\u00a7a \u043c\u043e\u043d\u0435\u0442!");
        player.sendMessage("\u00a77\u0422\u0435\u043a\u0443\u0449\u0438\u0439 \u0431\u0430\u043b\u0430\u043d\u0441: \u00a7e" + this.getBalance(player) + " \u043c\u043e\u043d\u0435\u0442");
    }

    public void removeBalance(Player player, double amount) {
        double current = this.getBalance(player);
        if (current < amount) {
            player.sendMessage("\u00a7c\u041d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432! \u041d\u0443\u0436\u043d\u043e: \u00a7e" + amount + "\u00a7c, \u0443 \u0432\u0430\u0441: \u00a7e" + current);
            return;
        }
        this.playerBalance.put(player.getUniqueId(), current - amount);
    }

    public double getBalance(Player player) {
        return this.playerBalance.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void setBalance(Player player, double amount) {
        this.playerBalance.put(player.getUniqueId(), amount);
    }

    public void buyItem(Player player, String itemId, int amount) {
        DonateItem item = this.shopItems.get(itemId);
        if (item == null) {
            player.sendMessage("\u00a7c\u0422\u043e\u0432\u0430\u0440 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        double totalPrice = item.getPrice() * (double)amount;
        if (this.getBalance(player) < totalPrice) {
            player.sendMessage("\u00a7c\u041d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432! \u041d\u0443\u0436\u043d\u043e: \u00a7e" + totalPrice + "\u00a7c \u043c\u043e\u043d\u0435\u0442");
            return;
        }
        this.removeBalance(player, totalPrice);
        ItemStack itemStack = new ItemStack(item.getMaterial(), amount);
        Map<Integer, ItemStack> excess = player.getInventory().addItem(itemStack);
        if (!excess.isEmpty()) {
            for (ItemStack excessItem : excess.values()) {
                player.getWorld().dropItem(player.getLocation(), excessItem);
            }
            player.sendMessage("\u00a7e\u0427\u0430\u0441\u0442\u044c \u0442\u043e\u0432\u0430\u0440\u0430 \u0443\u043f\u0430\u043b\u0430 \u043d\u0430 \u0437\u0435\u043c\u043b\u044e \u0438\u0437-\u0437\u0430 \u043f\u0435\u0440\u0435\u043f\u043e\u043b\u043d\u0435\u043d\u043d\u043e\u0433\u043e \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f!");
        }
        player.sendMessage("\u00a7a\u0412\u044b \u043a\u0443\u043f\u0438\u043b\u0438 \u00a7e" + amount + "x " + item.getDisplayName() + "\u00a7a \u0437\u0430 \u00a7e" + totalPrice + "\u00a7a \u043c\u043e\u043d\u0435\u0442!");
    }

    public void openShop(Player player) {
        player.sendMessage("\u00a76\u00a7l=== \u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043e\u043d\u0430\u0442\u043e\u0432 ===");
        player.sendMessage("\u00a77\u0412\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441: \u00a7e" + this.getBalance(player) + " \u043c\u043e\u043d\u0435\u0442");
        player.sendMessage("");
        for (DonateItem item : this.shopItems.values()) {
            player.sendMessage("\u00a7e" + item.getId() + " \u00a77- \u00a7f" + item.getDisplayName() + " \u00a77- \u00a7e" + item.getPrice() + " \u043c\u043e\u043d\u0435\u0442");
        }
        player.sendMessage("");
        player.sendMessage("\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435: \u00a7e/donate buy <id> [\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e]");
    }

    public Collection<DonateItem> getShopItems() {
        return this.shopItems.values();
    }

    public DonateItem getShopItem(String itemId) {
        return this.shopItems.get(itemId);
    }

    public boolean isRunning() {
        return this.configManager.getConfig().getBoolean("donate.enabled", true);
    }
}

