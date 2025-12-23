/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.cases;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.cases.Case;
import ru.venik.optimize.cases.CaseReward;
import ru.venik.optimize.config.ConfigManager;

public class CaseManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<String, Case> cases = new HashMap<String, Case>();
    private final Map<UUID, Integer> playerCases = new HashMap<UUID, Integer>();

    public CaseManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.loadCases();
    }

    private void loadCases() {
        if (!this.configManager.getConfig().getBoolean("cases.enabled", true)) {
            return;
        }
        Case commonCase = new Case("common", "\u00a77\u041e\u0431\u044b\u0447\u043d\u044b\u0439 \u043a\u0435\u0439\u0441", Material.CHEST, 0.5);
        commonCase.addReward(new CaseReward(Material.DIAMOND, 1, 5, 30.0));
        commonCase.addReward(new CaseReward(Material.EMERALD, 5, 10, 25.0));
        commonCase.addReward(new CaseReward(Material.GOLD_INGOT, 10, 20, 20.0));
        commonCase.addReward(new CaseReward(Material.IRON_INGOT, 20, 30, 15.0));
        commonCase.addReward(new CaseReward(Material.COAL, 32, 64, 10.0));
        this.cases.put("common", commonCase);
        Case rareCase = new Case("rare", "\u00a7b\u0420\u0435\u0434\u043a\u0438\u0439 \u043a\u0435\u0439\u0441", Material.ENDER_CHEST, 0.3);
        rareCase.addReward(new CaseReward(Material.DIAMOND_BLOCK, 1, 3, 25.0));
        rareCase.addReward(new CaseReward(Material.EMERALD_BLOCK, 3, 5, 20.0));
        rareCase.addReward(new CaseReward(Material.NETHERITE_INGOT, 1, 2, 15.0));
        rareCase.addReward(new CaseReward(Material.DIAMOND, 10, 20, 20.0));
        rareCase.addReward(new CaseReward(Material.EMERALD, 20, 30, 20.0));
        this.cases.put("rare", rareCase);
        Case epicCase = new Case("epic", "\u00a75\u042d\u043f\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u043a\u0435\u0439\u0441", Material.SHULKER_BOX, 0.15);
        epicCase.addReward(new CaseReward(Material.NETHERITE_BLOCK, 1, 2, 30.0));
        epicCase.addReward(new CaseReward(Material.BEACON, 1, 1, 20.0));
        epicCase.addReward(new CaseReward(Material.DIAMOND_BLOCK, 5, 10, 25.0));
        epicCase.addReward(new CaseReward(Material.EMERALD_BLOCK, 10, 20, 25.0));
        this.cases.put("epic", epicCase);
        Case legendaryCase = new Case("legendary", "\u00a76\u00a7l\u041b\u0435\u0433\u0435\u043d\u0434\u0430\u0440\u043d\u044b\u0439 \u043a\u0435\u0439\u0441", Material.ENDER_CHEST, 0.05);
        legendaryCase.addReward(new CaseReward(Material.NETHERITE_BLOCK, 5, 10, 40.0));
        legendaryCase.addReward(new CaseReward(Material.BEACON, 3, 5, 30.0));
        legendaryCase.addReward(new CaseReward(Material.ENCHANTED_GOLDEN_APPLE, 5, 10, 30.0));
        this.cases.put("legendary", legendaryCase);
    }

    public void giveCase(Player player, String caseId, int amount) {
        Case caseItem = this.cases.get(caseId);
        if (caseItem == null) {
            player.sendMessage("\u00a7c\u041a\u0435\u0439\u0441 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        ItemStack caseStack = this.createCaseItem(caseItem);
        caseStack.setAmount(amount);
        Map<Integer, ItemStack> excess = player.getInventory().addItem(caseStack);
        if (!excess.isEmpty()) {
            player.sendMessage("\u00a7c\u0412\u0430\u0448 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c \u043f\u0435\u0440\u0435\u043f\u043e\u043b\u043d\u0435\u043d!");
            for (ItemStack item : excess.values()) {
                player.getWorld().dropItem(player.getLocation(), item);
            }
        } else {
            player.sendMessage("\u00a7a\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 " + amount + "x " + caseItem.getDisplayName());
        }
    }

    public void openCase(Player player, String caseId) {
        Case caseItem = this.cases.get(caseId);
        if (caseItem == null) {
            player.sendMessage("\u00a7c\u041a\u0435\u0439\u0441 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        ItemStack caseStack = this.createCaseItem(caseItem);
        if (!player.getInventory().containsAtLeast(caseStack, 1)) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u044d\u0442\u043e\u0433\u043e \u043a\u0435\u0439\u0441\u0430!");
            return;
        }
        player.getInventory().removeItem(new ItemStack[]{caseStack});
        player.sendMessage("\u00a7e\u041e\u0442\u043a\u0440\u044b\u0432\u0430\u044e \u043a\u0435\u0439\u0441 " + caseItem.getDisplayName() + "\u00a7e...");
        Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            CaseReward reward = this.getRandomReward(caseItem);
            if (reward != null) {
                ItemStack rewardItem = new ItemStack(reward.getMaterial(), reward.getRandomAmount());
                player.getInventory().addItem(new ItemStack[]{rewardItem});
                player.sendMessage("\u00a7a\u00a7l\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438: \u00a7f" + rewardItem.getAmount() + "x " + reward.getMaterial().name());
                player.sendMessage("\u00a77\u0412\u0435\u0440\u043e\u044f\u0442\u043d\u043e\u0441\u0442\u044c: \u00a7e" + String.format("%.1f", reward.getChance()) + "%");
            }
        }, 40L);
    }

    private CaseReward getRandomReward(Case caseItem) {
        List<CaseReward> rewards = caseItem.getRewards();
        if (rewards.isEmpty()) {
            return null;
        }
        double totalWeight = rewards.stream().mapToDouble(CaseReward::getChance).sum();
        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;
        double currentWeight = 0.0;
        for (CaseReward reward : rewards) {
            if (!(random <= (currentWeight += reward.getChance()))) continue;
            return reward;
        }
        return rewards.get(0);
    }

    private ItemStack createCaseItem(Case caseItem) {
        ItemStack item = new ItemStack(caseItem.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(caseItem.getDisplayName());
            meta.setLore(Arrays.asList("\u00a77\u041a\u043b\u0438\u043a\u043d\u0438\u0442\u0435 \u041f\u041a\u041c, \u0447\u0442\u043e\u0431\u044b \u043e\u0442\u043a\u0440\u044b\u0442\u044c", "\u00a77\u0420\u0435\u0434\u043a\u043e\u0441\u0442\u044c: \u00a7f" + caseItem.getRarity()));
            item.setItemMeta(meta);
        }
        return item;
    }

    public void addCase(Player player, String caseId, int amount) {
        this.playerCases.put(player.getUniqueId(), this.playerCases.getOrDefault(player.getUniqueId(), 0) + amount);
    }

    public int getPlayerCases(Player player, String caseId) {
        return this.playerCases.getOrDefault(player.getUniqueId(), 0);
    }

    public Collection<Case> getAllCases() {
        return this.cases.values();
    }

    public Case getCase(String caseId) {
        return this.cases.get(caseId);
    }

    public boolean isRunning() {
        return this.configManager.getConfig().getBoolean("cases.enabled", true);
    }
}

