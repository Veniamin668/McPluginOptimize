package ru.venik.optimize.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.venik.optimize.donate.DonationManager;
import ru.venik.optimize.kit.KitManager;
import ru.venik.optimize.performance.PerformanceMonitor;
import ru.venik.optimize.warp.WarpManager;

import java.util.*;

/**
 * Менеджер GUI-меню.
 * Полностью автономный, без зависимостей от плагина.
 */
public class MenuManager {

    private final Map<String, Menu> menus = new HashMap<>();

    private final WarpManager warpManager;
    private final KitManager kitManager;
    private final DonationManager donationManager;
    private final PerformanceMonitor performanceMonitor;

    public MenuManager(WarpManager warpManager,
                       KitManager kitManager,
                       DonationManager donationManager,
                       PerformanceMonitor performanceMonitor) {

        this.warpManager = warpManager;
        this.kitManager = kitManager;
        this.donationManager = donationManager;
        this.performanceMonitor = performanceMonitor;

        loadDefaultMenus();
    }

    // ------------------------------------------------------------
    // Загрузка стандартных меню
    // ------------------------------------------------------------

    private void loadDefaultMenus() {

        // ---------------- MAIN MENU ----------------
        Menu main = Menu.builder("main", "§6§lГлавное меню", 54)
                .item(11, icon(Material.COMPASS, "§e§lТелепортация",
                        "§7Варпы и дома", "§7Клик для открытия"), "warp")
                .item(13, icon(Material.CHEST, "§b§lКейсы",
                        "§7Открыть кейсы", "§7Клик для открытия"), "case")
                .item(15, icon(Material.EMERALD, "§a§lМагазин",
                        "§7Магазин донатов", "§7Клик для открытия"), "shop")
                .item(29, icon(Material.DIAMOND, "§b§lНаборы",
                        "§7Получить наборы", "§7Клик для открытия"), "kit")
                .item(31, icon(Material.PLAYER_HEAD, "§6§lПрофиль",
                        "§7Информация о себе", "§7Клик для открытия"), "profile")
                .item(33, icon(Material.NETHER_STAR, "§d§lНастройки",
                        "§7Настройки сервера", "§7Клик для открытия"), "settings")
                .build();

        menus.put("main", main);

        // ---------------- WARP MENU ----------------
        Menu warps = Menu.builder("warps", "§6§lТелепортация", 54)
                .item(10, icon(Material.GRASS_BLOCK, "§a§lСпавн",
                        "§7Телепорт на спавн", "§7Клик для телепортации"), "warp:spawn")
                .item(12, icon(Material.DIAMOND_ORE, "§b§lШахта",
                        "§7Телепорт в шахту", "§7Клик для телепортации"), "warp:mine")
                .item(14, icon(Material.NETHER_STAR, "§c§lПвП Арена",
                        "§7Телепорт на арену", "§7Клик для телепортации"), "warp:pvp")
                .item(16, icon(Material.ENCHANTING_TABLE, "§d§lМагазин",
                        "§7Телепорт в магазин", "§7Клик для телепортации"), "warp:shop")
                .item(40, icon(Material.BARRIER, "§c§lЗакрыть",
                        "§7Вернуться назад"), "close")
                .build();

        menus.put("warps", warps);

        // ---------------- KIT MENU ----------------
        Menu kits = Menu.builder("kits", "§6§lНаборы", 54)
                .item(10, icon(Material.WOODEN_SWORD, "§7§lНовичок",
                        "§7Базовый набор", "§7Доступен: §aВсегда"), "kit:starter")
                .item(12, icon(Material.IRON_SWORD, "§f§lОпытный",
                        "§7Набор для опыта", "§7Доступен: §aВсегда"), "kit:experienced")
                .item(14, icon(Material.DIAMOND_SWORD, "§b§lПрофи",
                        "§7Профессиональный набор", "§7Доступен: §eVIP"), "kit:pro")
                .item(16, icon(Material.NETHERITE_SWORD, "§5§lЛегенда",
                        "§7Легендарный набор", "§7Доступен: §6Premium"), "kit:legend")
                .item(40, icon(Material.BARRIER, "§c§lЗакрыть",
                        "§7Вернуться назад"), "close")
                .build();

        menus.put("kits", kits);
    }

    // ------------------------------------------------------------
    // Создание предмета
    // ------------------------------------------------------------

    private ItemStack icon(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }

        return item;
    }

    // ------------------------------------------------------------
    // Открытие меню
    // ------------------------------------------------------------

    public void open(Player player, String id) {

        Menu menu = menus.get(id.toLowerCase());

        if (menu == null) {
            player.sendMessage("§cМеню не найдено!");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, menu.getSize(), menu.getTitle());
        menu.getItems().forEach(inv::setItem);

        player.openInventory(inv);
    }

    // ------------------------------------------------------------
    // API
    // ------------------------------------------------------------

    public Menu getMenu(String id) {
        return menus.get(id.toLowerCase());
    }

    public Collection<Menu> getAllMenus() {
        return menus.values();
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public DonationManager getDonationManager() {
        return donationManager;
    }

    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }
}
