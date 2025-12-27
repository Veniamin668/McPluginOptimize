package ru.venik.optimize.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.venik.optimize.cases.CaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда /case:
 * - /case → GUI
 * - /case open <id>
 * - /case give <player> <id> [amount]
 * - /case list
 */
public class CaseCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final CaseManager caseManager;

    public CaseCommand(JavaPlugin plugin, CaseManager caseManager) {
        this.plugin = plugin;
        this.caseManager = caseManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /case → GUI
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cЭта команда доступна только игрокам!");
                return true;
            }

            Player player = (Player) sender;
            caseManager.openGui(player); // GUI-открытие кейсов
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "open": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cЭта команда доступна только игрокам!");
                    return true;
                }

                Player player = (Player) sender;

                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /case open <id>");
                    return true;
                }

                caseManager.openCase(player, args[1]);
                return true;
            }

            case "give": {
                if (!sender.hasPermission("mcoptimize.admin")) {
                    sender.sendMessage("§cУ вас нет прав!");
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage("§cИспользование: /case give <игрок> <id> [кол-во]");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null || !target.isOnline()) {
                    sender.sendMessage("§cИгрок не найден или оффлайн!");
                    return true;
                }

                int amount = args.length > 3 ? parseInt(args[3], 1) : 1;

                caseManager.giveCase(target, args[2], amount);
                sender.sendMessage("§aВы выдали " + amount + "x кейс §e" + args[2] + " §aигроку §e" + target.getName());
                return true;
            }

            case "list": {
                sender.sendMessage("§6§l=== Доступные кейсы ===");
                caseManager.getAllCases().forEach(c ->
                        sender.sendMessage("§e" + c.getId() + " §7- " + c.getDisplayName() +
                                " §7(редкость: §f" + c.getRarity() + "§7)")
                );
                return true;
            }

            default: {
                sender.sendMessage("§cНеизвестная подкоманда. Используйте: /case, /case open, /case give, /case list");
                return true;
            }
        }
    }

    private int parseInt(String input, int fallback) {
        try {
            return Math.max(1, Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("open");
            completions.add("give");
            completions.add("list");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("give"))) {
            caseManager.getAllCases().forEach(c -> completions.add(c.getId()));
        }

        return completions;
    }
}
