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

public class WarpCommand
implements CommandExecutor,
TabCompleter {
    private final VenikOptimize plugin;

    public WarpCommand(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            this.plugin.getMenuManager().openMenu(player, "warps");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "list": {
                player.sendMessage("\u00a76\u00a7l=== \u0412\u0430\u0440\u043f\u044b ===");
                this.plugin.getWarpManager().getAllWarps().forEach((name, warp) -> player.sendMessage("\u00a7e" + name));
                break;
            }
            case "create": 
            case "set": {
                if (!player.hasPermission("venikoptimize.warp.create")) {
                    player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /warp create <\u0438\u043c\u044f>");
                    return true;
                }
                this.plugin.getWarpManager().createWarp(args[1], player.getLocation());
                player.sendMessage("\u00a7a\u0412\u0430\u0440\u043f \u00a7e" + args[1] + "\u00a7a \u0441\u043e\u0437\u0434\u0430\u043d!");
                break;
            }
            case "delete": 
            case "remove": {
                if (!player.hasPermission("venikoptimize.warp.delete")) {
                    player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /warp delete <\u0438\u043c\u044f>");
                    return true;
                }
                this.plugin.getWarpManager().deleteWarp(args[1]);
                player.sendMessage("\u00a7a\u0412\u0430\u0440\u043f \u00a7e" + args[1] + "\u00a7a \u0443\u0434\u0430\u043b\u0435\u043d!");
                break;
            }
            default: {
                this.plugin.getWarpManager().warp(player, args[0]);
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            completions.add("list");
            if (sender.hasPermission("venikoptimize.warp.create")) {
                completions.add("create");
                completions.add("set");
            }
            if (sender.hasPermission("venikoptimize.warp.delete")) {
                completions.add("delete");
                completions.add("remove");
            }
            this.plugin.getWarpManager().getAllWarps().keySet().forEach(completions::add);
        }
        return completions;
    }
}

