package ru.venik.optimize.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.venik.optimize.chat.ChatManager;

import java.util.Arrays;

/**
 * Команды личных сообщений:
 *  - /msg <игрок> <сообщение>
 *  - /tell <игрок> <сообщение>
 *  - /w <игрок> <сообщение>
 *  - /r <сообщение>
 *  - /reply <сообщение>
 */
public class MessageCommand implements CommandExecutor {

    private final ChatManager chatManager;

    public MessageCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;
        String name = cmd.getName().toLowerCase();

        // ------------------------------------------------------------
        // /msg, /tell, /w
        // ------------------------------------------------------------
        if (name.equals("msg") || name.equals("tell") || name.equals("w")) {

            if (args.length < 2) {
                player.sendMessage("§cИспользование: /msg <игрок> <сообщение>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                player.sendMessage("§cИгрок не найден или оффлайн!");
                return true;
            }

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage("§cВы не можете отправить сообщение самому себе!");
                return true;
            }

            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            chatManager.sendPrivateMessage(player, target, message);
            return true;
        }

        // ------------------------------------------------------------
        // /r, /reply
        // ------------------------------------------------------------
        if (name.equals("r") || name.equals("reply")) {

            if (args.length < 1) {
                player.sendMessage("§cИспользование: /r <сообщение>");
                return true;
            }

            String message = String.join(" ", args);

            boolean ok = chatManager.reply(player, message);

            if (!ok) {
                player.sendMessage("§cНекому отвечать — вам никто не писал!");
            }

            return true;
        }

        return true;
    }
}
