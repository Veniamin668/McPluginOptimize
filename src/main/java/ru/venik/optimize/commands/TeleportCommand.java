package ru.venik.optimize.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.venik.optimize.teleport.TeleportManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Команды телепортации:
 *  - /tpa <игрок>
 *  - /tpahere <игрок>
 *  - /tpaccept /tpyes
 *  - /tpdeny /tpno
 *  - /back
 */
public class TeleportCommand implements CommandExecutor, TabCompleter {

    private final TeleportManager teleportManager;

    public TeleportCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String name = cmd.getName().toLowerCase();

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        switch (name) {

            // ------------------------------------------------------------
            // /tpa <игрок>
            // ------------------------------------------------------------
            case "tpa": {

                if (args.length < 1) {
                    player.sendMessage("§cИспользование: /tpa <игрок>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);

                if (target == null || !target.isOnline()) {
                    player.sendMessage("§cИгрок не найден или оффлайн!");
                    return true;
                }

                if (target.equals(player)) {
                    player.sendMessage("§cВы не можете отправить запрос самому себе!");
                    return true;
                }

                teleportManager.requestTeleport(player, target);
                return true;
            }

            // ------------------------------------------------------------
            // /tpahere <игрок>
            // ------------------------------------------------------------
            case "tpahere": {

                if (args.length < 1) {
                    player.sendMessage("§cИспользование: /tpahere <игрок>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);

                if (target == null || !target.isOnline()) {
                    player.sendMessage("§cИгрок не найден или оффлайн!");
                    return true;
                }

                if (target.equals(player)) {
                    player.sendMessage("§cВы не можете отправить запрос самому себе!");
                    return true;
                }

                teleportManager.requestTeleportHere(player, target);
                return true;
            }

            // ------------------------------------------------------------
            // /tpaccept /tpyes
            // ------------------------------------------------------------
            case "tpaccept":
            case "tpyes": {
                teleportManager.acceptTeleport(player);
                return true;
            }

            // ------------------------------------------------------------
            // /tpdeny /tpno
            // ------------------------------------------------------------
            case "tpdeny":
            case "tpno": {
                teleportManager.denyTeleport(player);
                return true;
            }

            // ------------------------------------------------------------
            // /back
            // ------------------------------------------------------------
            case "back": {
                teleportManager.teleportBack(player);
                return true;
            }
        }

        return true;
    }

    // ------------------------------------------------------------
    // TAB COMPLETER
    // ------------------------------------------------------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(p -> list.add(p.getName()));
        }

        return list;
    }
}
