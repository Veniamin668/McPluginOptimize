package ru.venik.optimize.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.venik.optimize.donate.DonationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Команды экономики:
 *  - /pay <игрок> <сумма>
 *  - /balance, /bal, /money
 *  - /baltop, /top (пока заглушка)
 */
public class EconomyCommand implements CommandExecutor, TabCompleter {

    private final DonationManager donationManager;

    public EconomyCommand(JavaPlugin plugin, DonationManager donationManager) {
        this.donationManager = donationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String name = cmd.getName().toLowerCase();

        switch (name) {

            // ------------------------------------------------------------
            // /pay <player> <amount>
            // ------------------------------------------------------------
            case "pay": {

                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cЭта команда доступна только игрокам!");
                    return true;
                }

                Player player = (Player) sender;

                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /pay <игрок> <сумма>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);

                if (target == null || !target.isOnline()) {
                    player.sendMessage("§cИгрок не найден или оффлайн!");
                    return true;
                }

                if (target.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage("§cВы не можете перевести деньги самому себе!");
                    return true;
                }

                double amount = parseDouble(args[1], -1);

                if (amount <= 0) {
                    player.sendMessage("§cСумма должна быть больше 0!");
                    return true;
                }

                if (!donationManager.withdraw(player, amount)) {
                    player.sendMessage("§cНедостаточно средств!");
                    return true;
                }

                donationManager.addBalance(target, amount);

                player.sendMessage("§aВы перевели §e" + amount + "§a монет игроку §e" + target.getName());
                target.sendMessage("§aВы получили §e" + amount + "§a монет от игрока §e" + player.getName());

                return true;
            }

            // ------------------------------------------------------------
            // /baltop, /top
            // ------------------------------------------------------------
            case "baltop":
            case "top": {
                showTop(sender);
                return true;
            }

            // ------------------------------------------------------------
            // /balance, /bal, /money
            // ------------------------------------------------------------
            case "balance":
            case "bal":
            case "money": {

                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cЭта команда доступна только игрокам!");
                    return true;
                }

                Player player = (Player) sender;
                double balance = donationManager.getBalance(player);

                player.sendMessage("§6§l=== Ваш баланс ===");
                player.sendMessage("§eМонет: §f" + balance);

                return true;
            }
        }

        return true;
    }

    // ------------------------------------------------------------
    //  Вспомогательные методы
    // ------------------------------------------------------------

    private void showTop(CommandSender sender) {
        sender.sendMessage("§6§l=== Топ игроков по балансу ===");
        sender.sendMessage("§7(Функция будет работать после подключения базы данных)");
    }

    private double parseDouble(String str, double fallback) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    // ------------------------------------------------------------
    //  TAB COMPLETER
    // ------------------------------------------------------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("pay") && args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(p -> list.add(p.getName()));
        }

        return list;
    }
}
