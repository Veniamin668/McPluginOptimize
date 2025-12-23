/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;

public class BountyCommand
implements CommandExecutor {
    private final VenikOptimize plugin;

    public BountyCommand(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            this.plugin.getBountyManager().listBounties(player);
            return true;
        }
        if (args.length < 3) {
            player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /bounty <set> <\u0438\u0433\u0440\u043e\u043a> <\u0441\u0443\u043c\u043c\u0430>");
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            Player target = this.plugin.getServer().getPlayer(args[1]);
            double amount = this.parseDouble(args[2], 0.0);
            if (amount <= 0.0) {
                player.sendMessage("\u00a7c\u0421\u0443\u043c\u043c\u0430 \u0434\u043e\u043b\u0436\u043d\u0430 \u0431\u044b\u0442\u044c \u0431\u043e\u043b\u044c\u0448\u0435 0!");
                return true;
            }
            this.plugin.getBountyManager().setBounty(player, target, amount);
        }
        return true;
    }

    private double parseDouble(String str, double defaultValue) {
        try {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

