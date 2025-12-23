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

public class HomeCommand
implements CommandExecutor,
TabCompleter {
    private final VenikOptimize plugin;

    public HomeCommand(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            this.plugin.getHomeManager().listHomes(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "set": {
                if (args.length < 2) {
                    String homeName = args.length > 1 ? args[1] : "home";
                    this.plugin.getHomeManager().setHome(player, homeName);
                    break;
                }
                this.plugin.getHomeManager().setHome(player, args[1]);
                break;
            }
            case "delete": 
            case "remove": {
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /home delete <\u0438\u043c\u044f>");
                    return true;
                }
                this.plugin.getHomeManager().deleteHome(player, args[1]);
                break;
            }
            case "list": {
                this.plugin.getHomeManager().listHomes(player);
                break;
            }
            default: {
                this.plugin.getHomeManager().teleportHome(player, args[0]);
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (args.length == 1) {
                completions.add("set");
                completions.add("delete");
                completions.add("list");
                this.plugin.getHomeManager().getPlayerHomes(player).keySet().forEach(completions::add);
            }
        }
        return completions;
    }
}

