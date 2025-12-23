/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;

public class SocialCommand
implements CommandExecutor {
    private final VenikOptimize plugin;

    public SocialCommand(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
            return true;
        }
        Player player = (Player)sender;
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("friend") || cmd.equals("f")) {
            this.handleFriendCommand(player, args);
        } else if (cmd.equals("ignore")) {
            this.handleIgnoreCommand(player, args);
        }
        return true;
    }

    private void handleFriendCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /friend <add|remove|list|accept|deny> [\u0438\u0433\u0440\u043e\u043a]");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add": {
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /friend add <\u0438\u0433\u0440\u043e\u043a>");
                    return;
                }
                Player target = this.plugin.getServer().getPlayer(args[1]);
                this.plugin.getSocialManager().sendFriendRequest(player, target);
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /friend remove <\u0438\u0433\u0440\u043e\u043a>");
                    return;
                }
                Player friend = this.plugin.getServer().getPlayer(args[1]);
                this.plugin.getSocialManager().removeFriend(player, friend);
                break;
            }
            case "list": {
                List<String> friends = this.plugin.getSocialManager().getFriendsList(player);
                if (friends.isEmpty()) {
                    player.sendMessage("\u00a77\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0434\u0440\u0443\u0437\u0435\u0439.");
                    break;
                }
                player.sendMessage("\u00a76\u00a7l=== \u0412\u0430\u0448\u0438 \u0434\u0440\u0443\u0437\u044c\u044f ===");
                friends.forEach(arg_0 -> ((Player)player).sendMessage(arg_0));
                break;
            }
            case "accept": {
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /friend accept <\u0438\u0433\u0440\u043e\u043a>");
                    return;
                }
                Player sender = this.plugin.getServer().getPlayer(args[1]);
                this.plugin.getSocialManager().acceptFriendRequest(player, sender);
                break;
            }
            case "deny": {
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /friend deny <\u0438\u0433\u0440\u043e\u043a>");
                    return;
                }
                Player sender2 = this.plugin.getServer().getPlayer(args[1]);
                this.plugin.getSocialManager().denyFriendRequest(player, sender2);
            }
        }
    }

    private void handleIgnoreCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /ignore <add|remove> <\u0438\u0433\u0440\u043e\u043a>");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /ignore <add|remove> <\u0438\u0433\u0440\u043e\u043a>");
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args[1]);
        if (args[0].equalsIgnoreCase("add")) {
            this.plugin.getSocialManager().ignorePlayer(player, target);
        } else if (args[0].equalsIgnoreCase("remove")) {
            this.plugin.getSocialManager().unignorePlayer(player, target);
        }
    }
}

