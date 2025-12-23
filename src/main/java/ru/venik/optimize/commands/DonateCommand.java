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
import ru.venik.optimize.donate.DonationManager;

public class DonateCommand
implements CommandExecutor,
TabCompleter {
    private final VenikOptimize plugin;
    private final DonationManager donationManager;

    public DonateCommand(VenikOptimize plugin, DonationManager donationManager) {
        this.plugin = plugin;
        this.donationManager = donationManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            this.donationManager.openShop(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "balance": 
            case "bal": {
                player.sendMessage("\u00a77\u0412\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441: \u00a7e" + this.donationManager.getBalance(player) + " \u043c\u043e\u043d\u0435\u0442");
                break;
            }
            case "buy": {
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /donate buy <id> [\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e]");
                    return true;
                }
                String itemId = args[1];
                int amount = args.length > 2 ? this.parseInteger(args[2], 1) : 1;
                this.donationManager.buyItem(player, itemId, amount);
                break;
            }
            case "shop": {
                this.donationManager.openShop(player);
                break;
            }
            default: {
                this.donationManager.openShop(player);
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
            completions.add("balance");
            completions.add("buy");
            completions.add("shop");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
            this.donationManager.getShopItems().forEach(item -> completions.add(item.getId()));
        }
        return completions;
    }
}

