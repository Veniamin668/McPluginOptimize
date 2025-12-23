/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.menu;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;
import ru.venik.optimize.menu.Menu;
import ru.venik.optimize.menu.MenuListener;

public class MenuManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<String, Menu> menus = new HashMap<String, Menu>();

    public MenuManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.loadDefaultMenus();
    }

    private void loadDefaultMenus() {
        Menu mainMenu = new Menu("main", "\u00a76\u00a7l\u0413\u043b\u0430\u0432\u043d\u043e\u0435 \u043c\u0435\u043d\u044e", 54);
        mainMenu.addItem(11, this.createMenuItem(Material.COMPASS, "\u00a7e\u00a7l\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f", Arrays.asList("\u00a77\u0412\u0430\u0440\u043f\u044b \u0438 \u0434\u043e\u043c\u0430", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f")), "warp");
        mainMenu.addItem(13, this.createMenuItem(Material.CHEST, "\u00a7b\u00a7l\u041a\u0435\u0439\u0441\u044b", Arrays.asList("\u00a77\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043a\u0435\u0439\u0441\u044b", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f")), "case");
        mainMenu.addItem(15, this.createMenuItem(Material.EMERALD, "\u00a7a\u00a7l\u041c\u0430\u0433\u0430\u0437\u0438\u043d", Arrays.asList("\u00a77\u041c\u0430\u0433\u0430\u0437\u0438\u043d \u0434\u043e\u043d\u0430\u0442\u043e\u0432", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f")), "shop");
        mainMenu.addItem(29, this.createMenuItem(Material.DIAMOND, "\u00a7b\u00a7l\u041d\u0430\u0431\u043e\u0440\u044b", Arrays.asList("\u00a77\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043d\u0430\u0431\u043e\u0440\u044b", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f")), "kit");
        mainMenu.addItem(31, this.createMenuItem(Material.PLAYER_HEAD, "\u00a76\u00a7l\u041f\u0440\u043e\u0444\u0438\u043b\u044c", Arrays.asList("\u00a77\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0441\u0435\u0431\u0435", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f")), "profile");
        mainMenu.addItem(33, this.createMenuItem(Material.NETHER_STAR, "\u00a7d\u00a7l\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", Arrays.asList("\u00a77\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f")), "settings");
        this.menus.put("main", mainMenu);
        Menu warpMenu = new Menu("warps", "\u00a76\u00a7l\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f", 54);
        warpMenu.addItem(10, this.createMenuItem(Material.GRASS_BLOCK, "\u00a7a\u00a7l\u0421\u043f\u0430\u0432\u043d", Arrays.asList("\u00a77\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u043d\u0430 \u0441\u043f\u0430\u0432\u043d", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u0438")), "warp:spawn");
        warpMenu.addItem(12, this.createMenuItem(Material.DIAMOND_ORE, "\u00a7b\u00a7l\u0428\u0430\u0445\u0442\u0430", Arrays.asList("\u00a77\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u0432 \u0448\u0430\u0445\u0442\u0443", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u0438")), "warp:mine");
        warpMenu.addItem(14, this.createMenuItem(Material.NETHER_STAR, "\u00a7c\u00a7l\u041f\u0432\u041f \u0410\u0440\u0435\u043d\u0430", Arrays.asList("\u00a77\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u043d\u0430 \u0430\u0440\u0435\u043d\u0443", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u0438")), "warp:pvp");
        warpMenu.addItem(16, this.createMenuItem(Material.ENCHANTING_TABLE, "\u00a7d\u00a7l\u041c\u0430\u0433\u0430\u0437\u0438\u043d", Arrays.asList("\u00a77\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u0432 \u043c\u0430\u0433\u0430\u0437\u0438\u043d", "\u00a77\u041a\u043b\u0438\u043a \u0434\u043b\u044f \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u0438")), "warp:shop");
        warpMenu.addItem(40, this.createMenuItem(Material.BARRIER, "\u00a7c\u00a7l\u0417\u0430\u043a\u0440\u044b\u0442\u044c", Arrays.asList("\u00a77\u0412\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f \u043d\u0430\u0437\u0430\u0434")), "close");
        this.menus.put("warps", warpMenu);
        Menu kitMenu = new Menu("kits", "\u00a76\u00a7l\u041d\u0430\u0431\u043e\u0440\u044b", 54);
        kitMenu.addItem(10, this.createMenuItem(Material.WOODEN_SWORD, "\u00a77\u00a7l\u041d\u043e\u0432\u0438\u0447\u043e\u043a", Arrays.asList("\u00a77\u0411\u0430\u0437\u043e\u0432\u044b\u0439 \u043d\u0430\u0431\u043e\u0440", "\u00a77\u0414\u043e\u0441\u0442\u0443\u043f\u0435\u043d: \u00a7a\u0412\u0441\u0435\u0433\u0434\u0430")), "kit:starter");
        kitMenu.addItem(12, this.createMenuItem(Material.IRON_SWORD, "\u00a7f\u00a7l\u041e\u043f\u044b\u0442\u043d\u044b\u0439", Arrays.asList("\u00a77\u041d\u0430\u0431\u043e\u0440 \u0434\u043b\u044f \u043e\u043f\u044b\u0442\u0430", "\u00a77\u0414\u043e\u0441\u0442\u0443\u043f\u0435\u043d: \u00a7a\u0412\u0441\u0435\u0433\u0434\u0430")), "kit:experienced");
        kitMenu.addItem(14, this.createMenuItem(Material.DIAMOND_SWORD, "\u00a7b\u00a7l\u041f\u0440\u043e\u0444\u0438", Arrays.asList("\u00a77\u041f\u0440\u043e\u0444\u0435\u0441\u0441\u0438\u043e\u043d\u0430\u043b\u044c\u043d\u044b\u0439 \u043d\u0430\u0431\u043e\u0440", "\u00a77\u0414\u043e\u0441\u0442\u0443\u043f\u0435\u043d: \u00a7eVIP")), "kit:pro");
        kitMenu.addItem(16, this.createMenuItem(Material.NETHERITE_SWORD, "\u00a75\u00a7l\u041b\u0435\u0433\u0435\u043d\u0434\u0430", Arrays.asList("\u00a77\u041b\u0435\u0433\u0435\u043d\u0434\u0430\u0440\u043d\u044b\u0439 \u043d\u0430\u0431\u043e\u0440", "\u00a77\u0414\u043e\u0441\u0442\u0443\u043f\u0435\u043d: \u00a76Premium")), "kit:legend");
        kitMenu.addItem(40, this.createMenuItem(Material.BARRIER, "\u00a7c\u00a7l\u0417\u0430\u043a\u0440\u044b\u0442\u044c", Arrays.asList("\u00a77\u0412\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f \u043d\u0430\u0437\u0430\u0434")), "close");
        this.menus.put("kits", kitMenu);
    }

    private ItemStack createMenuItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openMenu(Player player, String menuId) {
        Menu menu = this.menus.get(menuId);
        if (menu == null) {
            player.sendMessage("\u00a7c\u041c\u0435\u043d\u044e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e!");
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, (int)menu.getSize(), (String)menu.getTitle());
        menu.getItems().forEach((slot, item) -> inventory.setItem(slot.intValue(), item));
        player.openInventory(inventory);
    }

    public MenuListener getMenuListener() {
        return this.plugin.getMenuListener();
    }

    public Menu getMenu(String menuId) {
        return this.menus.get(menuId);
    }

    public Collection<Menu> getAllMenus() {
        return this.menus.values();
    }
}

