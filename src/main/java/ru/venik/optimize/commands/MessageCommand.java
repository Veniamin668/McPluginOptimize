/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.commands;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;

public class MessageCommand
implements CommandExecutor {
    private final VenikOptimize plugin;

    public MessageCommand(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
            return true;
        }
        Player player = (Player)sender;
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("msg") || cmd.equals("tell") || cmd.equals("w")) {
            if (args.length < 2) {
                player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /msg <\u0438\u0433\u0440\u043e\u043a> <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
                return true;
            }
            Player target = this.plugin.getServer().getPlayer(args[0]);
            String message = String.join((CharSequence)" ", Arrays.copyOfRange(args, 1, args.length));
            this.plugin.getChatManager().sendPrivateMessage(player, target, message);
        } else if (cmd.equals("r") || cmd.equals("reply")) {
            if (args.length < 1) {
                player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /r <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
                return true;
            }
            String message = String.join((CharSequence)" ", args);
            this.plugin.getChatManager().reply(player, message);
        }
        return true;
    }
}

