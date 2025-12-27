package ru.venik.optimize.donate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * DonationManager — полностью автономная система доната.
 * Хранит баланс игроков и список донат-предметов.
 * Не использует конфиги, всё задаётся в коде.
 */
public class DonationManager {

    private final Map<UUID, Double> balances = new HashMap<>();
    private final Map<String, DonateItem> items = new HashMap<>();

    public DonationManager() {
        loadDefaults();
    }

    // ------------------------------------------------------------
    // Загрузка предметов (вместо donate.yml)
    // ------------------------------------------------------------

    private void loadDefaults() {

        register(new DonateItem("diamond", "§bАлмаз", Material.DIAMOND, 1, 100));
        register(new DonateItem("diamond_block", "§bБлок алмазов", Material.DIAMOND_BLOCK, 1, 900));

        register(new DonateItem("emerald", "§aИзумруд", Material.EMERALD, 1, 50));
        register(new DonateItem("emerald_block", "§aБлок изумрудов", Material.EMERALD_BLOCK, 1, 450));

        register(new DonateItem("netherite_ingot", "§8Незеритовый слиток", Material.NETHERITE_INGOT, 1, 500));
        register(new DonateItem("beacon", "§bМаяк", Material.BEACON, 1, 2000));

        register(new DonateItem("case_common", "§7Обычный кейс", Material.CHEST, 1, 200));
        register(new DonateItem("case_rare", "§bРедкий кейс", Material.ENDER_CHEST, 1, 500));
        register(new DonateItem("case_epic", "§5Эпический кейс", Material.SHULKER_BOX, 1, 1000));
        register(new DonateItem("case_legendary", "§6Легендарный кейс", Material.ENDER_CHEST, 1, 2000));
    }

    // ------------------------------------------------------------
    // Регистрация предметов
    // ------------------------------------------------------------

    public void register(DonateItem item) {
        items.put(item.getId(), item);
    }

    public boolean exists(String id) {
        return items.containsKey(id.toLowerCase());
    }

    public DonateItem get(String id) {
        return items.get(id.toLowerCase());
    }

    public Collection<DonateItem> getAll() {
        return Collections.unmodifiableCollection(items.values());
    }

    // ------------------------------------------------------------
    // Баланс
    // ------------------------------------------------------------

    public double getBalance(Player player) {
        return balances.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void setBalance(Player player, double amount) {
        balances.put(player.getUniqueId(), Math.max(0, amount));
    }

    public void addBalance(Player player, double amount) {
        double newBalance = getBalance(player) + Math.max(0, amount);
        balances.put(player.getUniqueId(), newBalance);

        player.sendMessage("§aВаш баланс пополнен на §e" + amount + "§a монет.");
        player.sendMessage("§7Текущий баланс: §e" + newBalance);
    }

    public boolean withdraw(Player player, double amount) {
        double current = getBalance(player);

        if (current < amount) {
            player.sendMessage("§cНедостаточно средств! Нужно: §e" + amount + "§c, у вас: §e" + current);
            return false;
        }

        balances.put(player.getUniqueId(), current - amount);
        return true;
    }

    // ------------------------------------------------------------
    // Покупка предметов
    // ------------------------------------------------------------

    public void buy(Player player, String itemId, int amount) {

        DonateItem item = get(itemId);

        if (item == null) {
            player.sendMessage("§cТовар не найден!");
            return;
        }

        if (amount <= 0) amount = 1;

        double total = item.getPrice() * amount;

        if (!withdraw(player, total)) {
            return;
        }

        ItemStack stack = new ItemStack(item.getMaterial(), amount);

        Map<Integer, ItemStack> excess = player.getInventory().addItem(stack);

        if (!excess.isEmpty()) {
            excess.values().forEach(ex -> player.getWorld().dropItem(player.getLocation(), ex));
            player.sendMessage("§eЧасть предметов упала на землю — инвентарь переполнен.");
        }

        player.sendMessage("§aВы купили §e" + amount + "x " + item.getDisplayName() +
                " §aза §e" + total + "§a монет!");
    }

    // ------------------------------------------------------------
    // Текстовый магазин (если нет GUI)
    // ------------------------------------------------------------

    public void openShop(Player player) {
        player.sendMessage("§6§l=== Магазин донатов ===");
        player.sendMessage("§7Ваш баланс: §e" + getBalance(player));
        player.sendMessage("");

        for (DonateItem item : items.values()) {
            player.sendMessage("§e" + item.getId() + " §7- §f" + item.getDisplayName() +
                    " §7- §e" + item.getPrice() + " монет");
        }

        player.sendMessage("");
        player.sendMessage("§7Используйте: §e/donate buy <id> [количество]");
    }
}
