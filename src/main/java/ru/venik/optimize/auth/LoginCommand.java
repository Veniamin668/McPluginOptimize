package ru.venik.optimize.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class LoginCommand implements CommandExecutor {

    private final AuthManager auth;
    private final JavaPlugin plugin;

    public LoginCommand(JavaPlugin plugin, AuthManager auth) {
        this.plugin = plugin;
        this.auth = auth;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Только игроки
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Уже авторизован
        if (auth.isAuthenticated(player.getUniqueId())) {
            player.sendMessage(plugin.getConfig().getString(
                    "auth.messages.already_logged_in",
                    "Вы уже авторизованы."
            ));
            return true;
        }

        // Нет аргументов
        if (args.length < 1) {
            player.sendMessage("Usage: /login <password>");
            return true;
        }

        String password = args[0];

        // Ограничение длины пароля (защита от спама)
        if (password.length() > 64) {
            player.sendMessage("Пароль слишком длинный.");
            return true;
        }

        boolean ok = auth.authenticate(player.getUniqueId(), password);

        if (ok) {
            player.sendMessage(plugin.getConfig().getString(
                    "auth.messages.login_success",
                    "Вы успешно авторизованы."
            ));
        } else {
            player.sendMessage(plugin.getConfig().getString(
                    "auth.messages.login_fail",
                    "Неверный пароль."
            ));
        }

        return true;
    }
}
