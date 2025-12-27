package ru.venik.optimize.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.venik.optimize.menu.MenuManager;

/**
 * Команда /menu:
 *  - /menu → открыть главное меню
 *  - /menu <id> → открыть конкретное меню
 */
public class MenuCommand implements CommandExecutor {

    private final MenuManager menuManager;

    public MenuCommand(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        // /menu → открыть главное меню
        String menuId = (args.length > 0) ? args[0].toLowerCase() : "main";

        menuManager.openMenu(player, menuId);
        return true;
    }
}
