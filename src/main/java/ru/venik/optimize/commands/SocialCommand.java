package ru.venik.optimize.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.venik.optimize.social.SocialManager;

import java.util.List;

/**
 * Команды:
 *  - /friend <add|remove|list|accept|deny> [игрок]
 *  - /ignore <add|remove> <игрок>
 */
public class SocialCommand implements CommandExecutor {

    private final SocialManager socialManager;

    public SocialCommand(SocialManager socialManager) {
        this.socialManager = socialManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;
        String name = cmd.getName().toLowerCase();

        if (name.equals("friend") || name.equals("f")) {
            handleFriend(player, args);
            return true;
        }

        if (name.equals("ignore")) {
            handleIgnore(player, args);
            return true;
        }

        return true;
    }

    // ------------------------------------------------------------
    // FRIEND COMMAND
    // ------------------------------------------------------------

    private void handleFriend(Player player, String[] args) {

        if (args.length == 0) {
            player.sendMessage("§cИспользование: /friend <add|remove|list|accept|deny> [игрок]");
            return;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            // /friend add <игрок>
            case "add": {
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /friend add <игрок>");
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null || !target.isOnline()) {
                    player.sendMessage("§cИгрок не найден или оффлайн!");
                    return;
                }

                if (target.equals(player)) {
                    player.sendMessage("§cВы не можете добавить самого себя!");
                    return;
                }

                socialManager.sendFriendRequest(player, target);
                return;
            }

            // /friend remove <игрок>
            case "remove": {
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /friend remove <игрок>");
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage("§cИгрок не найден!");
                    return;
                }

                socialManager.removeFriend(player, target);
                return;
            }

            // /friend list
            case "list": {
                List<String> friends = socialManager.getFriendsList(player);

                if (friends.isEmpty()) {
                    player.sendMessage("§7У вас нет друзей.");
                    return;
                }

                player.sendMessage("§6§l=== Ваши друзья ===");
                friends.forEach(friend -> player.sendMessage("§e• §f" + friend));
                return;
            }

            // /friend accept <игрок>
            case "accept": {
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /friend accept <игрок>");
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage("§cИгрок не найден!");
                    return;
                }

                socialManager.acceptFriendRequest(player, target);
                return;
            }

            // /friend deny <игрок>
            case "deny": {
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /friend deny <игрок>");
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage("§cИгрок не найден!");
                    return;
                }

                socialManager.denyFriendRequest(player, target);
                return;
            }

            default:
                player.sendMessage("§cНеизвестная подкоманда. Используйте: /friend <add|remove|list|accept|deny>");
        }
    }

    // ------------------------------------------------------------
    // IGNORE COMMAND
    // ------------------------------------------------------------

    private void handleIgnore(Player player, String[] args) {

        if (args.length < 2) {
            player.sendMessage("§cИспользование: /ignore <add|remove> <игрок>");
            return;
        }

        String sub = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage("§cИгрок не найден!");
            return;
        }

        if (target.equals(player)) {
            player.sendMessage("§cВы не можете игнорировать самого себя!");
            return;
        }

        switch (sub) {

            case "add":
                socialManager.ignorePlayer(player, target);
                return;

            case "remove":
                socialManager.unignorePlayer(player, target);
                return;

            default:
                player.sendMessage("§cИспользование: /ignore <add|remove> <игрок>");
        }
    }
}
