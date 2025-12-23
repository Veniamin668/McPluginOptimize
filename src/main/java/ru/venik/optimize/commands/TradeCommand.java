/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;

public class TradeCommand
implements CommandExecutor {
    private final VenikOptimize plugin;

    public TradeCommand(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /trade <request|accept|deny|confirm|cancel> [\u0438\u0433\u0440\u043e\u043a]");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "request": 
            case "req": {
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /trade request <\u0438\u0433\u0440\u043e\u043a>");
                    return true;
                }
                Player target = this.plugin.getServer().getPlayer(args[1]);
                this.plugin.getTradeManager().requestTrade(player, target);
                break;
            }
            case "accept": {
                this.plugin.getTradeManager().acceptTrade(player);
                break;
            }
            case "deny": {
                this.plugin.getTradeManager().denyTrade(player);
                break;
            }
            case "confirm": {
                this.plugin.getTradeManager().confirmTrade(player);
                break;
            }
            case "cancel": {
                this.plugin.getTradeManager().cancelTrade(player);
                break;
            }
            default: {
                player.sendMessage("\u00a7c\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043f\u043e\u0434\u043a\u043e\u043c\u0430\u043d\u0434\u0430!");
            }
        }
        return true;
    }
}

