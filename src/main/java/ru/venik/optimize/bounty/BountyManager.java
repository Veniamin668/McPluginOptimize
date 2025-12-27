package ru.venik.optimize.bounty;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.venik.optimize.config.ConfigManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BountyManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    // Потокобезопасная карта наград
    private final Map<UUID, Double> bounties = new ConcurrentHashMap<>();

    public BountyManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * Установить награду на игрока
     */
    public void setBounty(Player player, Player target, double amount) {

        // Проверка цели
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cИгрок не найден!");
            return;
        }

        // Нельзя ставить награду на себя
        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage("§cВы не можете поставить награду на себя.");
            return;
        }

        // Проверка суммы
        if (amount <= 0) {
            player.sendMessage("§cСумма должна быть больше 0!");
            return;
        }

        // Проверка баланса
        double balance = plugin.getConfig().getDouble("donation.balance." + player.getUniqueId(), 0);
        if (balance < amount) {
            player.sendMessage("§cНедостаточно средств!");
            return;
        }

        // Снятие средств
        plugin.getConfig().set("donation.balance." + player.getUniqueId(), balance - amount);
        plugin.saveConfig();

        // Обновление награды
        double newBounty = bounties.getOrDefault(target.getUniqueId(), 0.0) + amount;
        bounties.put(target.getUniqueId(), newBounty);

        // Сообщения
        player.sendMessage("§aВы установили награду §e" + amount + "§a монет на игрока §e" + target.getName());
        target.sendMessage("§c§lНа вас установлена награда! §e" + newBounty + " монет");

        Bukkit.broadcastMessage("§6§l>>> §e" + target.getName() +
                " §6теперь имеет награду §e" + newBounty + "§6 монет! §6§l<<<");
    }

    /**
     * Получение награды за убийство
     */
    public void claimBounty(Player killer, Player victim) {
        Double bounty = bounties.remove(victim.getUniqueId());

        if (bounty != null && bounty > 0) {

            double balance = plugin.getConfig().getDouble("donation.balance." + killer.getUniqueId(), 0);
            plugin.getConfig().set("donation.balance." + killer.getUniqueId(), balance + bounty);
            plugin.saveConfig();

            killer.sendMessage("§a§lВы получили награду §e" + bounty +
                    "§a монет за убийство " + victim.getName() + "!");

            Bukkit.broadcastMessage("§6§l>>> §e" + killer.getName() +
                    " §6получил награду §e" + bounty +
                    "§6 монет за убийство " + victim.getName() + "! §6§l<<<");
        }
    }

    /**
     * Получить награду на игрока
     */
    public double getBounty(Player player) {
        return bounties.getOrDefault(player.getUniqueId(), 0.0);
    }

    /**
     * Получить копию всех наград
     */
    public Map<UUID, Double> getAllBounties() {
        return new HashMap<>(bounties);
    }

    /**
     * Вывести топ наград
     */
    public void listBounties(Player player) {
        if (bounties.isEmpty()) {
            player.sendMessage("§7Нет активных наград.");
            return;
        }

        player.sendMessage("§6§l=== Активные награды ===");

        bounties.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(entry -> {
                    Player target = Bukkit.getPlayer(entry.getKey());
                    if (target != null) {
                        player.sendMessage("§e" + target.getName() + " §7- §e" + entry.getValue() + " монет");
                    }
                });
    }
}
