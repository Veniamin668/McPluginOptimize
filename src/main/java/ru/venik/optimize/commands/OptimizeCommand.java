package ru.venik.optimize.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.venik.optimize.cleanup.CleanupManager;
import ru.venik.optimize.performance.BlockProfiler;
import ru.venik.optimize.performance.PerformanceMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Команда /vo (VenikOptimize):
 *  - /vo tps
 *  - /vo performance
 *  - /vo profile [top]
 *  - /vo cleanup
 *  - /vo status
 *  - /vo reload
 */
public class OptimizeCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final PerformanceMonitor performanceMonitor;
    private final BlockProfiler blockProfiler;
    private final CleanupManager cleanupManager;

    public OptimizeCommand(
            Plugin plugin,
            PerformanceMonitor performanceMonitor,
            BlockProfiler blockProfiler,
            CleanupManager cleanupManager
    ) {
        this.plugin = plugin;
        this.performanceMonitor = performanceMonitor;
        this.blockProfiler = blockProfiler;
        this.cleanupManager = cleanupManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("venikoptimize.use")) {
            sender.sendMessage("§cУ вас нет прав для использования этой команды!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "tps":
            case "performance":
                handlePerformance(sender);
                return true;

            case "profile":
            case "blocks":
                handleProfile(sender, args);
                return true;

            case "cleanup":
            case "clean":
                handleCleanup(sender);
                return true;

            case "status":
            case "info":
                handleStatus(sender);
                return true;

            case "reload":
                if (!sender.hasPermission("venikoptimize.admin")) {
                    sender.sendMessage("§cУ вас нет прав для этой команды!");
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage("§aКонфигурация перезагружена!");
                return true;

            default:
                sendHelp(sender);
                return true;
        }
    }

    // ------------------------------------------------------------
    // HELP
    // ------------------------------------------------------------

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== VenikOptimize ===");
        sender.sendMessage("§e/vo tps §7- Показать TPS/MSPT");
        sender.sendMessage("§e/vo profile [top] §7- Профилирование блоков");
        sender.sendMessage("§e/vo cleanup §7- Очистка мусора");
        sender.sendMessage("§e/vo status §7- Статус систем");
        if (sender.hasPermission("venikoptimize.admin")) {
            sender.sendMessage("§e/vo reload §7- Перезагрузить конфигурацию");
        }
    }

    // ------------------------------------------------------------
    // PERFORMANCE
    // ------------------------------------------------------------

    private void handlePerformance(CommandSender sender) {
        double tps = performanceMonitor.getCurrentTps();
        double mspt = performanceMonitor.getCurrentMspt();
        double avgTps = performanceMonitor.getAverageTps();
        double avgMspt = performanceMonitor.getAverageMspt();

        sender.sendMessage("§6=== Производительность сервера ===");
        sender.sendMessage(String.format("§eТекущий TPS: §f%.2f", tps));
        sender.sendMessage(String.format("§eСредний TPS: §f%.2f", avgTps));
        sender.sendMessage(String.format("§eТекущий MSPT: §f%.2f ms", mspt));
        sender.sendMessage(String.format("§eСредний MSPT: §f%.2f ms", avgMspt));

        if (tps < 18) {
            sender.sendMessage("§c⚠ Низкий TPS!");
        } else if (tps >= 19.5) {
            sender.sendMessage("§a✓ Производительность отличная");
        } else {
            sender.sendMessage("§e⚠ Производительность нормальная");
        }
    }

    // ------------------------------------------------------------
    // PROFILE
    // ------------------------------------------------------------

    private void handleProfile(CommandSender sender, String[] args) {

        if (!blockProfiler.isProfiling()) {
            sender.sendMessage("§cПрофилирование блоков не запущено!");
            return;
        }

        int top = 10;

        if (args.length > 1) {
            try {
                top = Math.max(1, Math.min(50, Integer.parseInt(args[1])));
            } catch (Exception e) {
                sender.sendMessage("§cНеверное число: " + args[1]);
                return;
            }
        }

        List<BlockProfiler.BlockTimingData> list = blockProfiler.getTopBlocks(top);

        sender.sendMessage("§6=== Top " + top + " блоков по нагрузке ===");

        if (list.isEmpty()) {
            sender.sendMessage("§7Нет данных для отображения");
            return;
        }

        int index = 1;
        for (BlockProfiler.BlockTimingData data : list) {
            sender.sendMessage(String.format(
                    "§e%d. §f%s §7- §e%.3f ms §7(ticks: %d)",
                    index++,
                    data.getMaterial().name(),
                    data.getAverageMs(),
                    data.getTickCount()
            ));
        }
    }

    // ------------------------------------------------------------
    // CLEANUP
    // ------------------------------------------------------------

    private void handleCleanup(CommandSender sender) {

        if (!sender.hasPermission("venikoptimize.admin")) {
            sender.sendMessage("§cУ вас нет прав для выполнения очистки!");
            return;
        }

        sender.sendMessage("§eВыполняется очистка...");

        int removed = cleanupManager.performCleanup();

        sender.sendMessage("§aОчистка завершена! Удалено объектов: " + removed);
    }

    // ------------------------------------------------------------
    // STATUS
    // ------------------------------------------------------------

    private void handleStatus(CommandSender sender) {

        sender.sendMessage("§6=== Статус VenikOptimize ===");

        sender.sendMessage("§eПроизводительность: §f" +
                (performanceMonitor.isRunning() ? "§aВключена" : "§cВыключена"));

        sender.sendMessage("§eПрофилирование блоков: §f" +
                (blockProfiler.isProfiling() ? "§aВключено" : "§cВыключено"));

        sender.sendMessage("§eОчистка: §f" +
                (cleanupManager.isRunning() ? "§aВключена" : "§cВыключена"));

        if (performanceMonitor.isRunning()) {
            sender.sendMessage(String.format(
                    "§eTPS: §f%.2f §7| MSPT: §f%.2f ms",
                    performanceMonitor.getCurrentTps(),
                    performanceMonitor.getCurrentMspt()
            ));
        }
    }

    // ------------------------------------------------------------
    // TAB COMPLETER
    // ------------------------------------------------------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            List<String> options = Arrays.asList(
                    "tps", "performance",
                    "profile", "blocks",
                    "cleanup", "clean",
                    "status", "info",
                    "reload"
            );

            for (String s : options) {
                if (s.startsWith(args[0].toLowerCase())) {
                    list.add(s);
                }
            }
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("profile") || args[0].equalsIgnoreCase("blocks"))) {
            list.add("10");
            list.add("20");
            list.add("50");
        }

        return list;
    }
}
