package ru.venik.optimize.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LoginCommand implements CommandExecutor {
    private final AuthManager auth;

    public LoginCommand(AuthManager auth) {
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
            player.sendMessage("Usage: /login <password>");
            return true;
        }
        String password = args[0];
        boolean ok = auth.authenticate(player.getUniqueId(), password);
        if (ok) {
            player.sendMessage(player.getServer().getPluginManager().getPlugin("VenikOptimize").getConfig().getString("auth.messages.login_success", "Вы успешно авторизованы."));
        } else {
            player.sendMessage(player.getServer().getPluginManager().getPlugin("VenikOptimize").getConfig().getString("auth.messages.login_fail", "Неверный пароль."));
        }
        return true;
    }
}
