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

public class TeleportCommand
implements CommandExecutor,
TabCompleter {
    private final VenikOptimize plugin;

    public TeleportCommand(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
            return true;
        }
        Player player = (Player)sender;
        switch (cmd) {
            case "tpa": {
                if (args.length < 1) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /tpa <\u0438\u0433\u0440\u043e\u043a>");
                    return true;
                }
                Player target = this.plugin.getServer().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
                    return true;
                }
                this.plugin.getTeleportManager().requestTeleport(player, target);
                break;
            }
            case "tpahere": {
                if (args.length < 1) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /tpahere <\u0438\u0433\u0440\u043e\u043a>");
                    return true;
                }
                Player target2 = this.plugin.getServer().getPlayer(args[0]);
                if (target2 == null) {
                    player.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
                    return true;
                }
                this.plugin.getTeleportManager().requestTeleportHere(player, target2);
                break;
            }
            case "tpaccept": 
            case "tpyes": {
                this.plugin.getTeleportManager().acceptTeleport(player);
                break;
            }
            case "tpdeny": 
            case "tpno": {
                this.plugin.getTeleportManager().denyTeleport(player);
                break;
            }
            case "back": {
                this.plugin.getTeleportManager().teleportBack(player);
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            this.plugin.getServer().getOnlinePlayers().forEach(p -> completions.add(p.getName()));
        }
        return completions;
    }
}

