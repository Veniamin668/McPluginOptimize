package ru.venik.optimize.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.venik.optimize.teleport.RandomTPManager;

/**
 * Команда /rtp:
 *  - Телепортирует игрока в случайную точку
 */
public class RTPCommand implements CommandExecutor {

    private final RandomTPManager randomTPManager;

    public RTPCommand(RandomTPManager randomTPManager) {
        this.randomTPManager = randomTPManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        randomTPManager.randomTeleport(player);
        return true;
    }
}
