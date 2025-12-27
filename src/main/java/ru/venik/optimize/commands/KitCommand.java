package ru.venik.optimize.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.venik.optimize.kit.KitManager;
import ru.venik.optimize.menu.MenuManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда /kit:
 *  - /kit → открыть GUI с наборами
 *  - /kit <id> → выдать набор
 */
public class KitCommand implements CommandExecutor, TabCompleter {

    private final KitManager kitManager;
    private final MenuManager menuManager;

    public KitCommand(KitManager kitManager, MenuManager menuManager) {
        this.kitManager = kitManager;
        this.menuManager = menuManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        // /kit → открыть меню
        if (args.length == 0) {
            menuManager.openMenu(player, "kits");
            return true;
        }

        // /kit <id> → выдать набор
        String kitId = args[0].toLowerCase();

        if (!kitManager.exists(kitId)) {
            player.sendMessage("§cНабор §e" + kitId + "§c не найден!");
            return true;
        }

        kitManager.giveKit(player, kitId);
        return true;
    }

    // ------------------------------------------------------------
    // TAB COMPLETER
    // ------------------------------------------------------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            kitManager.getAllKits().forEach(kit -> list.add(kit.getId()));
        }

        return list;
    }
}
