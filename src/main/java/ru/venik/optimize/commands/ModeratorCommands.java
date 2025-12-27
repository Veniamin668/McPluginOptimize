package ru.venik.optimize.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Команды модератора:
 *  - /fly [игрок]
 *  - /god [игрок]
 *  - /vanish [игрок]
 *  - /speed <1-10> [игрок]
 *  - /heal [игрок]
 *  - /feed [игрок]
 */
public class ModeratorCommands implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    private final Set<UUID> flyingPlayers = new HashSet<>();
    private final Set<UUID> godModePlayers = new HashSet<>();
    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public ModeratorCommands(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String name = cmd.getName().toLowerCase();

        // ------------------------------------------------------------
        // Определяем target
        // ------------------------------------------------------------
        Player target = null;

        // Если указан игрок и есть права — берём его
        if (args.length > 0 && sender.hasPermission("venikoptimize.admin")) {
            target = Bukkit.getPlayer(args[0]);
        }

        // Если target не найден — берём отправителя
        if (target == null && sender instanceof Player) {
            target = (Player) sender;
        }

        // Если всё ещё null
        if (target == null || !target.isOnline()) {
            sender.sendMessage("§cИгрок не найден!");
            return true;
        }

        switch (name) {

            // ------------------------------------------------------------
            // /fly
            // ------------------------------------------------------------
            case "fly": {
                toggleFly(target);
                boolean enabled = target.getAllowFlight();

                sender.sendMessage(target == sender
                        ? (enabled ? "§aПолет включён!" : "§cПолет выключен!")
                        : "§aПолет " + (enabled ? "включён" : "выключен") + " для " + target.getName());

                return true;
            }

            // ------------------------------------------------------------
            // /god
            // ------------------------------------------------------------
            case "god": {
                toggleGod(target);
                boolean enabled = godModePlayers.contains(target.getUniqueId());

                sender.sendMessage(target == sender
                        ? (enabled ? "§aРежим бога включён!" : "§cРежим бога выключен!")
                        : "§aРежим бога " + (enabled ? "включён" : "выключен") + " для " + target.getName());

                return true;
            }

            // ------------------------------------------------------------
            // /vanish
            // ------------------------------------------------------------
            case "vanish":
            case "v": {
                toggleVanish(target);
                boolean enabled = vanishedPlayers.contains(target.getUniqueId());

                sender.sendMessage(target == sender
                        ? (enabled ? "§aВы стали невидимы!" : "§cВы стали видимы!")
                        : "§aНевидимость " + (enabled ? "включена" : "выключена") + " для " + target.getName());

                return true;
            }

            // ------------------------------------------------------------
            // /speed <1-10>
            // ------------------------------------------------------------
            case "speed": {

                if (args.length < 1) {
                    sender.sendMessage("§cИспользование: /speed <1-10> [игрок]");
                    return true;
                }

                int speed = parseInt(args[0], 5);
                speed = Math.max(1, Math.min(10, speed));

                setSpeed(target, speed);

                sender.sendMessage("§aСкорость установлена на §e" + speed + "§a для " + target.getName());
                return true;
            }

            // ------------------------------------------------------------
            // /heal
            // ------------------------------------------------------------
            case "heal": {
                target.setHealth(target.getMaxHealth());
                target.setFoodLevel(20);
                target.setSaturation(20);

                sender.sendMessage("§a" + (target == sender ? "Вы" : target.getName()) + " исцелены!");
                return true;
            }

            // ------------------------------------------------------------
            // /feed
            // ------------------------------------------------------------
            case "feed": {
                target.setFoodLevel(20);
                target.setSaturation(20);

                sender.sendMessage("§a" + (target == sender ? "Вы" : target.getName()) + " накормлены!");
                return true;
            }
        }

        return true;
    }

    // ------------------------------------------------------------
    // ЛОГИКА
    // ------------------------------------------------------------

    private void toggleFly(Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            flyingPlayers.remove(player.getUniqueId());
        } else {
            player.setAllowFlight(true);
            flyingPlayers.add(player.getUniqueId());
        }
    }

    private void toggleGod(Player player) {
        UUID id = player.getUniqueId();
        if (godModePlayers.contains(id)) {
            godModePlayers.remove(id);
        } else {
            godModePlayers.add(id);
        }
    }

    private void toggleVanish(Player player) {
        UUID id = player.getUniqueId();

        if (vanishedPlayers.contains(id)) {
            vanishedPlayers.remove(id);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(plugin, player);
            }
        } else {
            vanishedPlayers.add(id);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("venikoptimize.vanish.see")) {
                    p.hidePlayer(plugin, player);
                }
            }
        }
    }

    private void setSpeed(Player player, int speed) {
        float value = speed / 10f;

        player.setWalkSpeed(Math.min(1f, value * 0.2f));
        player.setFlySpeed(Math.min(1f, value * 0.1f));
    }

    private int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return fallback;
        }
    }

    // ------------------------------------------------------------
    // TAB COMPLETER
    // ------------------------------------------------------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("venikoptimize.admin")) {
            Bukkit.getOnlinePlayers().forEach(p -> list.add(p.getName()));
        }

        return list;
    }
}
