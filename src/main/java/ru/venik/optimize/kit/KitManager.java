package ru.venik.optimize.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер китов.
 * Полностью автономный, без зависимостей от плагина.
 */
public class KitManager {

    private final Map<String, Kit> kits = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public KitManager() {
        loadDefaults();
    }

    // ------------------------------------------------------------
    // Загрузка стандартных китов
    // ------------------------------------------------------------

    private void loadDefaults() {

        kits.put("starter", Kit.of(
                "starter",
                "§7§lНовичок",
                0,
                List.of(
                        new ItemStack(Material.WOODEN_SWORD),
                        new ItemStack(Material.WOODEN_PICKAXE),
                        new ItemStack(Material.BREAD, 16),
                        new ItemStack(Material.TORCH, 32)
                )
        ));

        kits.put("experienced", Kit.of(
                "experienced",
                "§f§lОпытный",
                3600,
                List.of(
                        new ItemStack(Material.IRON_SWORD),
                        new ItemStack(Material.IRON_PICKAXE),
                        new ItemStack(Material.IRON_AXE),
                        new ItemStack(Material.COOKED_BEEF, 32),
                        new ItemStack(Material.IRON_HELMET),
                        new ItemStack(Material.IRON_CHESTPLATE),
                        new ItemStack(Material.IRON_LEGGINGS),
                        new ItemStack(Material.IRON_BOOTS)
                )
        ));

        kits.put("pro", Kit.of(
                "pro",
                "§b§lПрофи",
                7200,
                List.of(
                        new ItemStack(Material.DIAMOND_SWORD),
                        new ItemStack(Material.DIAMOND_PICKAXE),
                        new ItemStack(Material.DIAMOND_AXE),
                        new ItemStack(Material.GOLDEN_APPLE, 16),
                        new ItemStack(Material.DIAMOND_HELMET),
                        new ItemStack(Material.DIAMOND_CHESTPLATE),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        new ItemStack(Material.DIAMOND_BOOTS)
                )
        ));

        kits.put("legend", Kit.of(
                "legend",
                "§5§lЛегенда",
                14400,
                List.of(
                        new ItemStack(Material.NETHERITE_SWORD),
                        new ItemStack(Material.NETHERITE_PICKAXE),
                        new ItemStack(Material.NETHERITE_AXE),
                        new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 8),
                        new ItemStack(Material.NETHERITE_HELMET),
                        new ItemStack(Material.NETHERITE_CHESTPLATE),
                        new ItemStack(Material.NETHERITE_LEGGINGS),
                        new ItemStack(Material.NETHERITE_BOOTS)
                )
        ));
    }

    // ------------------------------------------------------------
    // Выдача кита
    // ------------------------------------------------------------

    public void giveKit(Player player, String kitId) {

        kitId = kitId.toLowerCase();
        Kit kit = kits.get(kitId);

        if (kit == null) {
            player.sendMessage("§cНабор не найден!");
            return;
        }

        // Проверка прав (универсальная)
        if (!player.hasPermission("venik.kit." + kitId)) {
            player.sendMessage("§cУ вас нет доступа к этому набору!");
            return;
        }

        // Проверка кулдауна
        if (hasCooldown(player, kitId)) {
            long sec = getCooldownRemaining(player, kitId);
            player.sendMessage("§cПодождите ещё §e" + sec + "§c секунд!");
            return;
        }

        // Выдача предметов
        for (ItemStack item : kit.getItems()) {
            Map<Integer, ItemStack> excess = player.getInventory().addItem(item);

            if (!excess.isEmpty()) {
                excess.values().forEach(ex -> player.getWorld().dropItem(player.getLocation(), ex));
            }
        }

        player.sendMessage("§aВы получили набор §e" + kit.getDisplayName() + "§a!");
        setCooldown(player, kitId, kit.getCooldown());
    }

    // ------------------------------------------------------------
    // Получение кита
    // ------------------------------------------------------------

    public Kit getKit(String id) {
        return kits.get(id.toLowerCase());
    }

    public Collection<Kit> getAllKits() {
        return Collections.unmodifiableCollection(kits.values());
    }

    // ------------------------------------------------------------
    // Кулдауны
    // ------------------------------------------------------------

    private boolean hasCooldown(Player player, String kitId) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return false;

        Long end = map.get(kitId);
        return end != null && System.currentTimeMillis() < end;
    }

    private long getCooldownRemaining(Player player, String kitId) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return 0;

        Long end = map.get(kitId);
        if (end == null) return 0;

        long remaining = end - System.currentTimeMillis();
        return remaining > 0 ? (remaining / 1000) + 1 : 0;
    }

    private void setCooldown(Player player, String kitId, long seconds) {
        if (seconds <= 0) return;

        cooldowns
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(kitId, System.currentTimeMillis() + seconds * 1000);
    }
}
