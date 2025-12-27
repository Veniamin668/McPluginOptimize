package ru.venik.optimize.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.venik.optimize.world.WorldManager;

/**
 * Команда /lobby:
 *  - Телепортирует игрока в лобби
 */
public class LobbyCommand implements CommandExecutor {

    private final WorldManager worldManager;

    public LobbyCommand(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        worldManager.teleportToLobby(player);
        return true;
    }
}
