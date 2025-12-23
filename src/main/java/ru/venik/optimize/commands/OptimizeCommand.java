/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.cleanup.CleanupManager;
import ru.venik.optimize.performance.BlockProfiler;
import ru.venik.optimize.performance.PerformanceMonitor;

public class OptimizeCommand
implements CommandExecutor,
TabCompleter {
    private final VenikOptimize plugin;
    private final PerformanceMonitor performanceMonitor;
    private final BlockProfiler blockProfiler;
    private final CleanupManager cleanupManager;

    public OptimizeCommand(VenikOptimize plugin, PerformanceMonitor performanceMonitor, BlockProfiler blockProfiler, CleanupManager cleanupManager) {
        this.plugin = plugin;
        this.performanceMonitor = performanceMonitor;
        this.blockProfiler = blockProfiler;
        this.cleanupManager = cleanupManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("venikoptimize.use")) {
            sender.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432 \u0434\u043b\u044f \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u044f \u044d\u0442\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
            return true;
        }
        if (args.length == 0) {
            this.sendHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "tps": 
            case "performance": {
                this.handlePerformance(sender);
                break;
            }
            case "profile": 
            case "blocks": {
                this.handleProfile(sender, args);
                break;
            }
            case "cleanup": 
            case "clean": {
                this.handleCleanup(sender);
                break;
            }
            case "reload": {
                if (!sender.hasPermission("venikoptimize.admin")) {
                    sender.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432 \u0434\u043b\u044f \u044d\u0442\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
                    return true;
                }
                this.plugin.reloadConfig();
                sender.sendMessage("\u00a7a\u041a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u044f \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u0430!");
                break;
            }
            case "info": 
            case "status": {
                this.handleStatus(sender);
                break;
            }
            default: {
                this.sendHelp(sender);
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("\u00a76=== VenikOptimize v" + this.plugin.getDescription().getVersion() + " ===");
        sender.sendMessage("\u00a7e/vo tps \u00a77- \u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u043f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u0441\u0435\u0440\u0432\u0435\u0440\u0430 (TPS/MSPT)");
        sender.sendMessage("\u00a7e/vo profile [top] \u00a77- \u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u043f\u0440\u043e\u0444\u0438\u043b\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435 \u0431\u043b\u043e\u043a\u043e\u0432");
        sender.sendMessage("\u00a7e/vo cleanup \u00a77- \u0412\u044b\u043f\u043e\u043b\u043d\u0438\u0442\u044c \u043e\u0447\u0438\u0441\u0442\u043a\u0443");
        sender.sendMessage("\u00a7e/vo status \u00a77- \u0421\u0442\u0430\u0442\u0443\u0441 \u0432\u0441\u0435\u0445 \u0441\u0438\u0441\u0442\u0435\u043c");
        if (sender.hasPermission("venikoptimize.admin")) {
            sender.sendMessage("\u00a7e/vo reload \u00a77- \u041f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u044e");
        }
    }

    private void handlePerformance(CommandSender sender) {
        double tps = this.performanceMonitor.getCurrentTps();
        double mspt = this.performanceMonitor.getCurrentMspt();
        double avgTps = this.performanceMonitor.getAverageTps();
        double avgMspt = this.performanceMonitor.getAverageMspt();
        sender.sendMessage("\u00a76=== \u041f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u0441\u0435\u0440\u0432\u0435\u0440\u0430 ===");
        sender.sendMessage(String.format("\u00a7e\u0422\u0435\u043a\u0443\u0449\u0438\u0439 TPS: \u00a7f%.2f", tps));
        sender.sendMessage(String.format("\u00a7e\u0421\u0440\u0435\u0434\u043d\u0438\u0439 TPS: \u00a7f%.2f", avgTps));
        sender.sendMessage(String.format("\u00a7e\u0422\u0435\u043a\u0443\u0449\u0438\u0439 MSPT: \u00a7f%.2f ms", mspt));
        sender.sendMessage(String.format("\u00a7e\u0421\u0440\u0435\u0434\u043d\u0438\u0439 MSPT: \u00a7f%.2f ms", avgMspt));
        if (tps < 18.0) {
            sender.sendMessage("\u00a7c\u26a0 \u0412\u043d\u0438\u043c\u0430\u043d\u0438\u0435: \u041d\u0438\u0437\u043a\u0438\u0439 TPS!");
        } else if (tps >= 19.5) {
            sender.sendMessage("\u00a7a\u2713 \u041f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u043e\u0442\u043b\u0438\u0447\u043d\u0430\u044f");
        } else {
            sender.sendMessage("\u00a7e\u26a0 \u041f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u043d\u043e\u0440\u043c\u0430\u043b\u044c\u043d\u0430\u044f");
        }
    }

    private void handleProfile(CommandSender sender, String[] args) {
        if (!this.blockProfiler.isProfiling()) {
            sender.sendMessage("\u00a7c\u041f\u0440\u043e\u0444\u0438\u043b\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435 \u0431\u043b\u043e\u043a\u043e\u0432 \u043d\u0435 \u0437\u0430\u043f\u0443\u0449\u0435\u043d\u043e!");
            return;
        }
        int topCount = 10;
        if (args.length > 1) {
            try {
                topCount = Integer.parseInt(args[1]);
                topCount = Math.max(1, Math.min(50, topCount));
            }
            catch (NumberFormatException e) {
                sender.sendMessage("\u00a7c\u041d\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u0447\u0438\u0441\u043b\u043e: " + args[1]);
                return;
            }
        }
        List<BlockProfiler.BlockTimingData> topBlocks = this.blockProfiler.getTopBlocks(topCount);
        sender.sendMessage("\u00a76=== Top " + topCount + " \u0431\u043b\u043e\u043a\u043e\u0432 \u043f\u043e \u0432\u0440\u0435\u043c\u0435\u043d\u0438 \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u043a\u0438 ===");
        if (topBlocks.isEmpty()) {
            sender.sendMessage("\u00a77\u041d\u0435\u0442 \u0434\u0430\u043d\u043d\u044b\u0445 \u0434\u043b\u044f \u043e\u0442\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u044f");
            return;
        }
        for (int i = 0; i < topBlocks.size(); ++i) {
            BlockProfiler.BlockTimingData data = topBlocks.get(i);
            sender.sendMessage(String.format("\u00a7e%d. \u00a7f%s \u00a77- \u00a7e%.3f ms \u00a77(ticks: %d)", i + 1, data.getMaterial().name(), data.getAverageMs(), data.getTickCount()));
        }
    }

    private void handleCleanup(CommandSender sender) {
        if (!sender.hasPermission("venikoptimize.admin")) {
            sender.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432 \u0434\u043b\u044f \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043e\u0447\u0438\u0441\u0442\u043a\u0438!");
            return;
        }
        sender.sendMessage("\u00a7e\u0412\u044b\u043f\u043e\u043b\u043d\u044f\u0435\u0442\u0441\u044f \u043e\u0447\u0438\u0441\u0442\u043a\u0430...");
        int cleaned = this.cleanupManager.performCleanup();
        sender.sendMessage("\u00a7a\u041e\u0447\u0438\u0441\u0442\u043a\u0430 \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d\u0430! \u0423\u0434\u0430\u043b\u0435\u043d\u043e \u043e\u0431\u044a\u0435\u043a\u0442\u043e\u0432: " + cleaned);
    }

    private void handleStatus(CommandSender sender) {
        sender.sendMessage("\u00a76=== \u0421\u0442\u0430\u0442\u0443\u0441 VenikOptimize ===");
        sender.sendMessage("\u00a7e\u041f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c: \u00a7f" + (this.performanceMonitor.isRunning() ? "\u00a7a\u0412\u043a\u043b\u044e\u0447\u0435\u043d" : "\u00a7c\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d"));
        sender.sendMessage("\u00a7e\u041f\u0440\u043e\u0444\u0438\u043b\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435 \u0431\u043b\u043e\u043a\u043e\u0432: \u00a7f" + (this.blockProfiler.isProfiling() ? "\u00a7a\u0412\u043a\u043b\u044e\u0447\u0435\u043d" : "\u00a7c\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d"));
        sender.sendMessage("\u00a7e\u041e\u0447\u0438\u0441\u0442\u043a\u0430: \u00a7f" + (this.cleanupManager.isRunning() ? "\u00a7a\u0412\u043a\u043b\u044e\u0447\u0435\u043d" : "\u00a7c\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d"));
        if (this.performanceMonitor.isRunning()) {
            double tps = this.performanceMonitor.getCurrentTps();
            double mspt = this.performanceMonitor.getCurrentMspt();
            sender.sendMessage(String.format("\u00a7e\u0422\u0435\u043a\u0443\u0449\u0438\u0439 TPS: \u00a7f%.2f \u00a77| MSPT: \u00a7f%.2f ms", tps, mspt));
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            List<String> commands = Arrays.asList("tps", "performance", "profile", "blocks", "cleanup", "clean", "reload", "info", "status");
            for (String cmd : commands) {
                if (!cmd.toLowerCase().startsWith(args[0].toLowerCase())) continue;
                completions.add(cmd);
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("profile") || args[0].equalsIgnoreCase("blocks"))) {
            completions.add("10");
            completions.add("20");
            completions.add("50");
        }
        return completions;
    }
}

