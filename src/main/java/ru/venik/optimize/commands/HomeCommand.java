package ru.venik.optimize.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.venik.optimize.home.HomeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда /home:
 *  - /home → список домов
 *  - /home set <имя> → установить дом
 *  - /home delete <имя> → удалить дом
 *  - /home list → список домов
 *  - /home <имя> → телепорт
 */
public class HomeCommand implements CommandExecutor, TabCompleter {

    private final HomeManager homeManager;

    public HomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        // /home → список домов
        if (args.length == 0) {
            homeManager.listHomes(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            // ------------------------------------------------------------
            // /home set <имя>
            // ------------------------------------------------------------
            case "set": {

                String name = (args.length >= 2) ? args[1] : "home";

                homeManager.setHome(player, name);
                return true;
            }

            // ------------------------------------------------------------
            // /home delete <имя>
            // ------------------------------------------------------------
            case "delete":
            case "remove": {

                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /home delete <имя>");
                    return true;
                }

                homeManager.deleteHome(player, args[1]);
                return true;
            }

            // ------------------------------------------------------------
            // /home list
            // ------------------------------------------------------------
            case "list": {
                homeManager.listHomes(player);
                return true;
            }

            // ------------------------------------------------------------
            // /home <имя> → телепорт
            // ------------------------------------------------------------
            default: {
                homeManager.teleportHome(player, args[0]);
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

        if (!(sender instanceof Player)) return list;

        Player player = (Player) sender;

        if (args.length == 1) {
            list.add("set");
            list.add("delete");
            list.add("list");

            // Добавляем имена домов игрока
            homeManager.getPlayerHomes(player).keySet().forEach(list::add);
        }

        return list;
    }
}
