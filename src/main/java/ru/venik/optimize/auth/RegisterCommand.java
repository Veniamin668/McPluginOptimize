package ru.venik.optimize.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    private final AuthManager auth;

    public RegisterCommand(AuthManager auth) {
        this.auth = auth;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("Usage: /register <password>");
            return true;
        }
        String password = args[0];
        auth.register(player, password);
        player.sendMessage(player.getServer().getPluginManager().getPlugin("VenikOptimize").getConfig().getString("auth.messages.registered", "Пароль установлен, вы авторизованы."));
        return true;
    }
}
