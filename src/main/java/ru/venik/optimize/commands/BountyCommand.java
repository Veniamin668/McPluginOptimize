package ru.venik.optimize.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.venik.optimize.bounty.BountyManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Команда /bounty:
 * - /bounty → список наград
 * - /bounty set <игрок> <сумма> → установить награду
 */
public class BountyCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final BountyManager bountyManager;

    public BountyCommand(JavaPlugin plugin, BountyManager bountyManager) {
        this.plugin = plugin;
        this.bountyManager = bountyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Только игроки
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        // /bounty → список
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            bountyManager.listBounties(player);
            return true;
        }

        // /bounty set <игрок> <сумма>
        if (args[0].equalsIgnoreCase("set")) {

            if (args.length < 3) {
                player.sendMessage("§cИспользование: /bounty set <игрок> <сумма>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null || !target.isOnline()) {
                player.sendMessage("§cИгрок не найден или оффлайн!");
                return true;
            }

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage("§cВы не можете поставить награду на себя.");
                return true;
            }

            double amount = parseDouble(args[2], -1);

            if (amount <= 0) {
                player.sendMessage("§cСумма должна быть больше 0!");
                return true;
            }

            bountyManager.setBounty(player, target, amount);
            return true;
        }

        // Неизвестная подкоманда
        player.sendMessage("§cНеизвестная подкоманда. Используйте: /bounty, /bounty set <игрок> <сумма>");
        return true;
    }

    private double parseDouble(String input, double fallback) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
