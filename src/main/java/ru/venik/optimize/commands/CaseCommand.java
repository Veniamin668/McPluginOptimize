/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.cases.CaseManager;

public class CaseCommand
implements CommandExecutor,
TabCompleter {
    private final VenikOptimize plugin;
    private final CaseManager caseManager;

    public CaseCommand(VenikOptimize plugin, CaseManager caseManager) {
        this.plugin = plugin;
        this.caseManager = caseManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("\u00a76=== \u041a\u0435\u0439\u0441\u044b ===");
            sender.sendMessage("\u00a7e/case open <id> \u00a77- \u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043a\u0435\u0439\u0441");
            sender.sendMessage("\u00a7e/case give <player> <id> [\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e] \u00a77- \u0412\u044b\u0434\u0430\u0442\u044c \u043a\u0435\u0439\u0441");
            sender.sendMessage("\u00a7e/case list \u00a77- \u0421\u043f\u0438\u0441\u043e\u043a \u043a\u0435\u0439\u0441\u043e\u0432");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "open": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
                    return true;
                }
                Player player = (Player)sender;
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /case open <id>");
                    return true;
                }
                this.caseManager.openCase(player, args[1]);
                break;
            }
            case "give": {
                if (!sender.hasPermission("venikoptimize.admin")) {
                    sender.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432!");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /case give <player> <id> [\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e]");
                    return true;
                }
                Player target = this.plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
                    return true;
                }
                int amount = args.length > 3 ? this.parseInteger(args[3], 1) : 1;
                this.caseManager.giveCase(target, args[2], amount);
                sender.sendMessage("\u00a7a\u0412\u044b\u0434\u0430\u043b\u0438 " + amount + "x \u043a\u0435\u0439\u0441 " + args[2] + " \u0438\u0433\u0440\u043e\u043a\u0443 " + target.getName());
                break;
            }
            case "list": {
                sender.sendMessage("\u00a76=== \u0414\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0435 \u043a\u0435\u0439\u0441\u044b ===");
                this.caseManager.getAllCases().forEach(caseItem -> sender.sendMessage("\u00a7e" + caseItem.getId() + " \u00a77- " + caseItem.getDisplayName() + " \u00a77(\u0440\u0435\u0434\u043a\u043e\u0441\u0442\u044c: \u00a7f" + caseItem.getRarity() + "\u00a77)"));
                break;
            }
            default: {
                sender.sendMessage("\u00a7c\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043f\u043e\u0434\u043a\u043e\u043c\u0430\u043d\u0434\u0430! \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435: /case open|give|list");
            }
        }
        return true;
    }

    private int parseInteger(String str, int defaultValue) {
        try {
            return Math.max(1, Integer.parseInt(str));
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            completions.add("open");
            completions.add("give");
            completions.add("list");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("give"))) {
            this.caseManager.getAllCases().forEach(caseItem -> completions.add(caseItem.getId()));
        }
        return completions;
    }
}

