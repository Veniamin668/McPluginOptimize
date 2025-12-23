/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.kit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;
import ru.venik.optimize.kit.Kit;

public class KitManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<String, Kit> kits = new ConcurrentHashMap<String, Kit>();
    private final Map<UUID, Map<String, Long>> playerCooldowns = new ConcurrentHashMap<UUID, Map<String, Long>>();

    public KitManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.loadDefaultKits();
    }

    private void loadDefaultKits() {
        Kit starterKit = new Kit("starter", "\u00a77\u00a7l\u041d\u043e\u0432\u0438\u0447\u043e\u043a", 0L);
        starterKit.addItem(new ItemStack(Material.WOODEN_SWORD));
        starterKit.addItem(new ItemStack(Material.WOODEN_PICKAXE));
        starterKit.addItem(new ItemStack(Material.BREAD, 16));
        starterKit.addItem(new ItemStack(Material.TORCH, 32));
        this.kits.put("starter", starterKit);
        Kit experiencedKit = new Kit("experienced", "\u00a7f\u00a7l\u041e\u043f\u044b\u0442\u043d\u044b\u0439", 3600L);
        experiencedKit.addItem(new ItemStack(Material.IRON_SWORD));
        experiencedKit.addItem(new ItemStack(Material.IRON_PICKAXE));
        experiencedKit.addItem(new ItemStack(Material.IRON_AXE));
        experiencedKit.addItem(new ItemStack(Material.COOKED_BEEF, 32));
        experiencedKit.addItem(new ItemStack(Material.IRON_HELMET));
        experiencedKit.addItem(new ItemStack(Material.IRON_CHESTPLATE));
        experiencedKit.addItem(new ItemStack(Material.IRON_LEGGINGS));
        experiencedKit.addItem(new ItemStack(Material.IRON_BOOTS));
        this.kits.put("experienced", experiencedKit);
        Kit proKit = new Kit("pro", "\u00a7b\u00a7l\u041f\u0440\u043e\u0444\u0438", 7200L);
        proKit.addItem(new ItemStack(Material.DIAMOND_SWORD));
        proKit.addItem(new ItemStack(Material.DIAMOND_PICKAXE));
        proKit.addItem(new ItemStack(Material.DIAMOND_AXE));
        proKit.addItem(new ItemStack(Material.GOLDEN_APPLE, 16));
        proKit.addItem(new ItemStack(Material.DIAMOND_HELMET));
        proKit.addItem(new ItemStack(Material.DIAMOND_CHESTPLATE));
        proKit.addItem(new ItemStack(Material.DIAMOND_LEGGINGS));
        proKit.addItem(new ItemStack(Material.DIAMOND_BOOTS));
        this.kits.put("pro", proKit);
        Kit legendKit = new Kit("legend", "\u00a75\u00a7l\u041b\u0435\u0433\u0435\u043d\u0434\u0430", 14400L);
        legendKit.addItem(new ItemStack(Material.NETHERITE_SWORD));
        legendKit.addItem(new ItemStack(Material.NETHERITE_PICKAXE));
        legendKit.addItem(new ItemStack(Material.NETHERITE_AXE));
        legendKit.addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 8));
        legendKit.addItem(new ItemStack(Material.NETHERITE_HELMET));
        legendKit.addItem(new ItemStack(Material.NETHERITE_CHESTPLATE));
        legendKit.addItem(new ItemStack(Material.NETHERITE_LEGGINGS));
        legendKit.addItem(new ItemStack(Material.NETHERITE_BOOTS));
        this.kits.put("legend", legendKit);
    }

    public void giveKit(Player player, String kitId) {
        Kit kit = this.kits.get(kitId.toLowerCase());
        if (kit == null) {
            player.sendMessage("\u00a7c\u041d\u0430\u0431\u043e\u0440 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        if (kitId.equals("pro") && !player.hasPermission("venikoptimize.kit.pro")) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0434\u043e\u0441\u0442\u0443\u043f\u0430 \u043a \u044d\u0442\u043e\u043c\u0443 \u043d\u0430\u0431\u043e\u0440\u0443!");
            return;
        }
        if (kitId.equals("legend") && !player.hasPermission("venikoptimize.kit.legend")) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0434\u043e\u0441\u0442\u0443\u043f\u0430 \u043a \u044d\u0442\u043e\u043c\u0443 \u043d\u0430\u0431\u043e\u0440\u0443!");
            return;
        }
        if (this.hasCooldown(player, kitId)) {
            long remaining = this.getCooldownRemaining(player, kitId);
            player.sendMessage("\u00a7c\u041f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435 \u0435\u0449\u0435 \u00a7e" + remaining + "\u00a7c \u0441\u0435\u043a\u0443\u043d\u0434!");
            return;
        }
        for (ItemStack item : kit.getItems()) {
            Map<Integer, ItemStack> excess = player.getInventory().addItem(item.clone());
            if (excess.isEmpty()) continue;
            for (ItemStack excessItem : excess.values()) {
                player.getWorld().dropItem(player.getLocation(), excessItem);
            }
        }
        player.sendMessage("\u00a7a\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 \u043d\u0430\u0431\u043e\u0440 \u00a7e" + kit.getDisplayName() + "\u00a7a!");
        this.setCooldown(player, kitId, kit.getCooldown());
    }

    public Kit getKit(String kitId) {
        return this.kits.get(kitId.toLowerCase());
    }

    public Collection<Kit> getAllKits() {
        return this.kits.values();
    }

    private boolean hasCooldown(Player player, String kitId) {
        Map<String, Long> cooldowns = this.playerCooldowns.get(player.getUniqueId());
        if (cooldowns == null) {
            return false;
        }
        Long cooldownEnd = cooldowns.get(kitId.toLowerCase());
        return cooldownEnd != null && System.currentTimeMillis() < cooldownEnd;
    }

    private long getCooldownRemaining(Player player, String kitId) {
        Map<String, Long> cooldowns = this.playerCooldowns.get(player.getUniqueId());
        if (cooldowns == null) {
            return 0L;
        }
        Long cooldownEnd = cooldowns.get(kitId.toLowerCase());
        if (cooldownEnd == null) {
            return 0L;
        }
        long remaining = cooldownEnd - System.currentTimeMillis();
        return remaining > 0L ? remaining / 1000L + 1L : 0L;
    }

    private void setCooldown(Player player, String kitId, long cooldownSeconds) {
        if (cooldownSeconds <= 0L) {
            return;
        }
        Map<String, Long> cooldowns = this.playerCooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        cooldowns.put(kitId.toLowerCase(), System.currentTimeMillis() + cooldownSeconds * 1000L);
    }
}

