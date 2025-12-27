package ru.venik.optimize.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class RegisterCommand implements CommandExecutor {

    private final AuthManager auth;
    private final JavaPlugin plugin;

    public RegisterCommand(JavaPlugin plugin, AuthManager auth) {
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
        UUID uuid = player.getUniqueId();

        // Уже зарегистрирован
        if (auth.hasPassword(uuid)) {
            player.sendMessage(plugin.getConfig().getString(
                    "auth.messages.already_registered",
                    "Вы уже зарегистрированы. Используйте /login."
            ));
            return true;
        }

        // Нет аргументов
        if (args.length < 1) {
            player.sendMessage("Usage: /register <password>");
            return true;
        }

        String password = args[0];

        // Минимальная длина
        if (password.length() < 3) {
            player.sendMessage(plugin.getConfig().getString(
                    "auth.messages.password_too_short",
                    "Пароль слишком короткий."
            ));
            return true;
        }

        // Максимальная длина
        if (password.length() > 64) {
            player.sendMessage(plugin.getConfig().getString(
                    "auth.messages.password_too_long",
                    "Пароль слишком длинный."
            ));
            return true;
        }

        // Регистрация
        auth.register(player, password);

        player.sendMessage(plugin.getConfig().getString(
                "auth.messages.registered",
                "Пароль установлен, вы авторизованы."
        ));

        return true;
    }
}
