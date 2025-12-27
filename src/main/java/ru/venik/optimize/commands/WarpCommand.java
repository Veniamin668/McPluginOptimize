package ru.venik.optimize.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.venik.optimize.warp.WarpManager;
import ru.venik.optimize.menu.MenuManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда /warp:
 *  - /warp → открыть меню варпов
 *  - /warp list
 *  - /warp create <name>
 *  - /warp delete <name>
 *  - /warp <name>
 */
public class WarpCommand implements CommandExecutor, TabCompleter {

    private final WarpManager warpManager;
    private final MenuManager menuManager;

    public WarpCommand(WarpManager warpManager, MenuManager menuManager) {
        this.warpManager = warpManager;
        this.menuManager = menuManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        // /warp → открыть меню
        if (args.length == 0) {
            menuManager.openMenu(player, "warps");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            // ------------------------------------------------------------
            // /warp list
            // ------------------------------------------------------------
            case "list": {
                player.sendMessage("§6§l=== Варпы ===");

                if (warpManager.getAllWarps().isEmpty()) {
                    player.sendMessage("§7Нет доступных варпов.");
                    return true;
                }

                warpManager.getAllWarps().keySet()
                        .forEach(name -> player.sendMessage("§e• §f" + name));

                return true;
            }

            // ------------------------------------------------------------
            // /warp create <name>
            // ------------------------------------------------------------
            case "create":
            case "set": {

                if (!player.hasPermission("venikoptimize.warp.create")) {
                    player.sendMessage("§cУ вас нет прав!");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /warp create <имя>");
                    return true;
                }

                String name = args[1].toLowerCase();

                warpManager.createWarp(name, player.getLocation());
                player.sendMessage("§aВарп §e" + name + " §aсоздан!");
                return true;
            }

            // ------------------------------------------------------------
            // /warp delete <name>
            // ------------------------------------------------------------
            case "delete":
            case "remove": {

                if (!player.hasPermission("venikoptimize.warp.delete")) {
                    player.sendMessage("§cУ вас нет прав!");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /warp delete <имя>");
                    return true;
                }

                String name = args[1].toLowerCase();

                if (!warpManager.exists(name)) {
                    player.sendMessage("§cВарп §e" + name + " §cне найден!");
                    return true;
                }

                warpManager.deleteWarp(name);
                player.sendMessage("§aВарп §e" + name + " §aудалён!");
                return true;
            }

            // ------------------------------------------------------------
            // /warp <name>
            // ------------------------------------------------------------
            default: {

                String name = args[0].toLowerCase();

                if (!warpManager.exists(name)) {
                    player.sendMessage("§cВарп §e" + name + " §cне найден!");
                    return true;
                }

                warpManager.warp(player, name);
                return true;
            }
        }
    }

    // ------------------------------------------------------------
    // TAB COMPLETER
    // ------------------------------------------------------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 1) {

            list.add("list");

            if (sender.hasPermission("venikoptimize.warp.create")) {
                list.add("create");
                list.add("set");
            }

            if (sender.hasPermission("venikoptimize.warp.delete")) {
                list.add("delete");
                list.add("remove");
            }

            // Добавляем имена варпов
            list.addAll(warpManager.getAllWarps().keySet());
        }

        return list;
    }
}
