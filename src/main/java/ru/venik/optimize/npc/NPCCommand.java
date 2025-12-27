package ru.venik.optimize.npc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Команда /npc
 *  - /npc create <id> [randomize]
 *  - /npc remove <id>
 */
public class NPCCommand implements CommandExecutor {

    private final NPCManager manager;

    public NPCCommand(NPCManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Только игроки
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players may use this command.");
            return true;
        }

        // Проверка прав
        if (!player.hasPermission("venik.npc")) {
            player.sendMessage("§cУ вас нет прав.");
            return true;
        }

        // Подсказка
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            // ------------------------------------------------------------
            // CREATE
            // ------------------------------------------------------------
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /npc create <id> [randomize]");
                    return true;
                }

                String id = args[1].toLowerCase();
                boolean randomize = args.length >= 3 && Boolean.parseBoolean(args[2]);

                Location loc = player.getLocation();
                manager.createAndSave(id, loc, randomize);

                player.sendMessage("§aNPC §e" + id + " §aсоздан.");
            }

            // ------------------------------------------------------------
            // REMOVE
            // ------------------------------------------------------------
            case "remove" -> {
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /npc remove <id>");
                    return true;
                }

                String id = args[1].toLowerCase();

                if (!manager.exists(id)) {
                    player.sendMessage("§cNPC §e" + id + " §cне найден.");
                    return true;
                }

                manager.remove(id);
                player.sendMessage("§aNPC §e" + id + " §aудалён.");
            }

            // ------------------------------------------------------------
            // UNKNOWN
            // ------------------------------------------------------------
            default -> sendHelp(player);
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6§lNPC Команды:");
        player.sendMessage("§e/npc create <id> [randomize] §7- создать NPC");
        player.sendMessage("§e/npc remove <id> §7- удалить NPC");
    }
}
