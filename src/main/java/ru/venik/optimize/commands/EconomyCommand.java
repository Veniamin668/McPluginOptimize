/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;

public class EconomyCommand
implements CommandExecutor,
TabCompleter {
    private final VenikOptimize plugin;

    public EconomyCommand(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd;
        switch (cmd = command.getName().toLowerCase()) {
            case "pay": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
                    return true;
                }
                Player player = (Player)sender;
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /pay <\u0438\u0433\u0440\u043e\u043a> <\u0441\u0443\u043c\u043c\u0430>");
                    return true;
                }
                Player target = Bukkit.getPlayer((String)args[0]);
                if (target == null) {
                    player.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
                    return true;
                }
                double amount = this.parseDouble(args[1], 0.0);
                if (amount <= 0.0) {
                    player.sendMessage("\u00a7c\u0421\u0443\u043c\u043c\u0430 \u0434\u043e\u043b\u0436\u043d\u0430 \u0431\u044b\u0442\u044c \u0431\u043e\u043b\u044c\u0448\u0435 0!");
                    return true;
                }
                if (this.plugin.getDonationManager().getBalance(player) < amount) {
                    player.sendMessage("\u00a7c\u041d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432!");
                    return true;
                }
                this.plugin.getDonationManager().removeBalance(player, amount);
                this.plugin.getDonationManager().addBalance(target, amount);
                player.sendMessage("\u00a7a\u0412\u044b \u043f\u0435\u0440\u0435\u0432\u0435\u043b\u0438 \u00a7e" + amount + "\u00a7a \u043c\u043e\u043d\u0435\u0442 \u0438\u0433\u0440\u043e\u043a\u0443 \u00a7e" + target.getName());
                target.sendMessage("\u00a7a\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 \u00a7e" + amount + "\u00a7a \u043c\u043e\u043d\u0435\u0442 \u043e\u0442 \u0438\u0433\u0440\u043e\u043a\u0430 \u00a7e" + player.getName());
                break;
            }
            case "baltop": 
            case "top": {
                this.showTop(sender);
                break;
            }
            case "balance": 
            case "bal": 
            case "money": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
                    return true;
                }
                Player player = (Player)sender;
                double balance = this.plugin.getDonationManager().getBalance(player);
                player.sendMessage("\u00a76\u00a7l=== \u0412\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441 ===");
                player.sendMessage("\u00a7e\u041c\u043e\u043d\u0435\u0442: \u00a7f" + balance);
            }
        }
        return true;
    }

    private void showTop(CommandSender sender) {
        sender.sendMessage("\u00a76\u00a7l=== \u0422\u043e\u043f \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u043f\u043e \u0431\u0430\u043b\u0430\u043d\u0441\u0443 ===");
        sender.sendMessage("\u00a77(\u0424\u0443\u043d\u043a\u0446\u0438\u044f \u0432 \u0440\u0430\u0437\u0440\u0430\u0431\u043e\u0442\u043a\u0435)");
        sender.sendMessage("\u00a77\u0411\u0430\u0437\u0430 \u0434\u0430\u043d\u043d\u044b\u0445 \u0431\u0443\u0434\u0435\u0442 \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0430 \u043f\u043e\u0441\u043b\u0435 \u043f\u0435\u0440\u0435\u0437\u0430\u043f\u0443\u0441\u043a\u0430");
    }

    private double parseDouble(String str, double defaultValue) {
        try {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (command.getName().equalsIgnoreCase("pay") && args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(p -> completions.add(p.getName()));
        }
        return completions;
    }
}

