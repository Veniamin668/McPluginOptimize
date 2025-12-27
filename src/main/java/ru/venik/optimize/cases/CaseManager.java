package ru.venik.optimize.cases;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ru.venik.optimize.config.ConfigManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CaseManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    // Кейсы по ID
    private final Map<String, Case> cases = new ConcurrentHashMap<>();

    // Количество кейсов у игроков
    private final Map<UUID, Map<String, Integer>> playerCases = new ConcurrentHashMap<>();

    public CaseManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        loadCases();
    }

    /**
     * Загрузка кейсов из конфига
     */
    private void loadCases() {
        if (!configManager.getConfig().getBoolean("cases.enabled", true)) {
            plugin.getLogger().info("Cases disabled in config.");
            return;
        }

        // Пример: cases.common.material: CHEST
        // Можно расширить под кастомные кейсы
        addDefaultCases();
    }

    /**
     * Дефолтные кейсы (можно вынести в config.yml)
     */
    private void addDefaultCases() {

        Case common = new Case("common", "§7Обычный кейс", Material.CHEST, 0.5);
        common.addReward(new CaseReward(Material.DIAMOND, 1, 5, 30));
        common.addReward(new CaseReward(Material.EMERALD, 5, 10, 25));
        common.addReward(new CaseReward(Material.GOLD_INGOT, 10, 20, 20));
        common.addReward(new CaseReward(Material.IRON_INGOT, 20, 30, 15));
        common.addReward(new CaseReward(Material.COAL, 32, 64, 10));
        cases.put("common", common);

        Case rare = new Case("rare", "§bРедкий кейс", Material.ENDER_CHEST, 0.3);
        rare.addReward(new CaseReward(Material.DIAMOND_BLOCK, 1, 3, 25));
        rare.addReward(new CaseReward(Material.EMERALD_BLOCK, 3, 5, 20));
        rare.addReward(new CaseReward(Material.NETHERITE_INGOT, 1, 2, 15));
        rare.addReward(new CaseReward(Material.DIAMOND, 10, 20, 20));
        rare.addReward(new CaseReward(Material.EMERALD, 20, 30, 20));
        cases.put("rare", rare);

        Case epic = new Case("epic", "§5Эпический кейс", Material.SHULKER_BOX, 0.15);
        epic.addReward(new CaseReward(Material.NETHERITE_BLOCK, 1, 2, 30));
        epic.addReward(new CaseReward(Material.BEACON, 1, 1, 20));
        epic.addReward(new CaseReward(Material.DIAMOND_BLOCK, 5, 10, 25));
        epic.addReward(new CaseReward(Material.EMERALD_BLOCK, 10, 20, 25));
        cases.put("epic", epic);

        Case legendary = new Case("legendary", "§6§lЛегендарный кейс", Material.ENDER_CHEST, 0.05);
        legendary.addReward(new CaseReward(Material.NETHERITE_BLOCK, 5, 10, 40));
        legendary.addReward(new CaseReward(Material.BEACON, 3, 5, 30));
        legendary.addReward(new CaseReward(Material.ENCHANTED_GOLDEN_APPLE, 5, 10, 30));
        cases.put("legendary", legendary);
    }

    /**
     * Выдать кейс игроку
     */
    public void giveCase(Player player, String caseId, int amount) {
        Case caseItem = cases.get(caseId);

        if (caseItem == null) {
            player.sendMessage("§cКейс не найден!");
            return;
        }

        ItemStack item = createCaseItem(caseItem);
        item.setAmount(amount);

        Map<Integer, ItemStack> excess = player.getInventory().addItem(item);

        if (!excess.isEmpty()) {
            player.sendMessage("§cИнвентарь переполнен!");
            excess.values().forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
        } else {
            player.sendMessage("§aВы получили " + amount + "x " + caseItem.getDisplayName());
        }
    }

    /**
     * Открыть кейс
     */
    public void openCase(Player player, String caseId) {
        Case caseItem = cases.get(caseId);

        if (caseItem == null) {
            player.sendMessage("§cКейс не найден!");
            return;
        }

        // Проверка наличия кейса
        if (!hasCaseItem(player, caseItem)) {
            player.sendMessage("§cУ вас нет этого кейса!");
            return;
        }

        removeCaseItem(player, caseItem);

        player.sendMessage("§eОткрываю кейс " + caseItem.getDisplayName() + "§e...");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            CaseReward reward = getRandomReward(caseItem);

            if (reward != null) {
                ItemStack rewardItem = new ItemStack(reward.getMaterial(), reward.getRandomAmount());
                player.getInventory().addItem(rewardItem);

                player.sendMessage("§a§lВы получили: §f" + rewardItem.getAmount() + "x " + reward.getMaterial().name());
                player.sendMessage("§7Вероятность: §e" + String.format("%.1f", reward.getChance()) + "%");
            }
        }, 40L);
    }

    /**
     * Проверка наличия кейса (по имени предмета)
     */
    private boolean hasCaseItem(Player player, Case caseItem) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isCaseItem(item, caseItem)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Удаление кейса из инвентаря
     */
    private void removeCaseItem(Player player, Case caseItem) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isCaseItem(item, caseItem)) {
                item.setAmount(item.getAmount() - 1);
                return;
            }
        }
    }

    /**
     * Проверка, что предмет — это кейс
     */
    private boolean isCaseItem(ItemStack item, Case caseItem) {
        if (item.getType() != caseItem.getMaterial()) return false;

        ItemMeta meta = item.getItemMeta();
        return meta != null && caseItem.getDisplayName().equals(meta.getDisplayName());
    }

    /**
     * Выбор случайной награды
     */
    private CaseReward getRandomReward(Case caseItem) {
        List<CaseReward> rewards = caseItem.getRewards();
        if (rewards.isEmpty()) return null;

        double total = rewards.stream().mapToDouble(CaseReward::getChance).sum();
        double rnd = ThreadLocalRandom.current().nextDouble() * total;

        double current = 0;
        for (CaseReward reward : rewards) {
            current += reward.getChance();
            if (rnd <= current) return reward;
        }

        return rewards.get(0);
    }

    /**
     * Создание предмета кейса
     */
    private ItemStack createCaseItem(Case caseItem) {
        ItemStack item = new ItemStack(caseItem.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(caseItem.getDisplayName());
            meta.setLore(Arrays.asList(
                    "§7Кликните ПКМ, чтобы открыть",
                    "§7Редкость: §f" + caseItem.getRarity()
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    public Collection<Case> getAllCases() {
        return cases.values();
    }

    public Case getCase(String caseId) {
        return cases.get(caseId);
    }

    public boolean isRunning() {
        return configManager.getConfig().getBoolean("cases.enabled", true);
    }
}
