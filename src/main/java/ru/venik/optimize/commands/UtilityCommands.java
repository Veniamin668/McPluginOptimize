package ru.venik.optimize.commands;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;

/**
 * Утилитарные команды:
 *  - /weather <clear|rain|storm>
 *  - /time <day|night|noon|midnight|set N>
 *  - /clear /ci
 *  - /repair
 *  - /enchant
 *  - /workbench /craft
 *  - /anvil
 *  - /enderchest /ec
 *  - /gamemode /gm <mode>
 */
public class UtilityCommands implements CommandExecutor {

    private final Plugin plugin;

    public UtilityCommands(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;
        String name = cmd.getName().toLowerCase();

        switch (name) {

            case "weather":
                handleWeather(player, args);
                return true;

            case "time":
                handleTime(player, args);
                return true;

            case "clear":
            case "ci":
                player.getInventory().clear();
                player.sendMessage("§aИнвентарь очищен!");
                return true;

            case "repair":
                handleRepair(player);
                return true;

            case "enchant":
                if (!player.hasPermission("venikoptimize.enchant")) {
                    player.sendMessage("§cУ вас нет прав!");
                    return true;
                }
                player.openEnchanting(player.getLocation(), true);
                return true;

            case "workbench":
            case "craft":
                player.openWorkbench(player.getLocation(), true);
                return true;

            case "anvil":
                player.sendMessage("§eАнвил временно недоступен (требует NMS)");
                return true;

            case "enderchest":
            case "ec":
                player.openInventory(player.getEnderChest());
                return true;

            case "gm":
            case "gamemode":
                handleGamemode(player, args);
                return true;
        }

        return true;
    }

    // ------------------------------------------------------------
    // WEATHER
    // ------------------------------------------------------------

    private void handleWeather(Player player, String[] args) {

        if (!player.hasPermission("venikoptimize.weather")) {
            player.sendMessage("§cУ вас нет прав!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cИспользование: /weather <clear|rain|storm>");
            return;
        }

        World world = player.getWorld();

        switch (args[0].toLowerCase()) {

            case "clear":
            case "sun":
                world.setStorm(false);
                world.setThundering(false);
                player.sendMessage("§aПогода установлена: Ясно");
                return;

            case "rain":
                world.setStorm(true);
                world.setThundering(false);
                player.sendMessage("§aПогода установлена: Дождь");
                return;

            case "storm":
            case "thunder":
                world.setStorm(true);
                world.setThundering(true);
                player.sendMessage("§aПогода установлена: Гроза");
                return;

            default:
                player.sendMessage("§cНеизвестный тип погоды!");
        }
    }

    // ------------------------------------------------------------
    // TIME
    // ------------------------------------------------------------

    private void handleTime(Player player, String[] args) {

        if (!player.hasPermission("venikoptimize.time")) {
            player.sendMessage("§cУ вас нет прав!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cИспользование: /time <day|night|noon|midnight|set N>");
            return;
        }

        World world = player.getWorld();

        switch (args[0].toLowerCase()) {

            case "day":
                world.setTime(1000);
                player.sendMessage("§aВремя установлено: День");
                return;

            case "night":
                world.setTime(13000);
                player.sendMessage("§aВремя установлено: Ночь");
                return;

            case "noon":
                world.setTime(6000);
                player.sendMessage("§aВремя установлено: Полдень");
                return;

            case "midnight":
                world.setTime(18000);
                player.sendMessage("§aВремя установлено: Полночь");
                return;

            case "set":
                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /time set <число>");
                    return;
                }
                try {
                    long time = Long.parseLong(args[1]);
                    world.setTime(time);
                    player.sendMessage("§aВремя установлено на §e" + time);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cНеверное число!");
                }
                return;

            default:
                player.sendMessage("§cНеизвестная подкоманда времени!");
        }
    }

    // ------------------------------------------------------------
    // REPAIR
    // ------------------------------------------------------------

    private void handleRepair(Player player) {

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("§cВозьмите предмет в руку!");
            return;
        }

        if (!(item.getItemMeta() instanceof Damageable meta)) {
            player.sendMessage("§cЭтот предмет нельзя починить!");
            return;
        }

        meta.setDamage(0);
        item.setItemMeta(meta);

        player.sendMessage("§aПредмет починен!");
    }

    // ------------------------------------------------------------
    // GAMEMODE
    // ------------------------------------------------------------

    private void handleGamemode(Player player, String[] args) {

        if (!player.hasPermission("venikoptimize.gamemode")) {
            player.sendMessage("§cУ вас нет прав!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cИспользование: /gamemode <survival|creative|adventure|spectator|0|1|2|3>");
            return;
        }

        GameMode mode = switch (args[0].toLowerCase()) {
            case "survival", "0", "s" -> GameMode.SURVIVAL;
            case "creative", "1", "c" -> GameMode.CREATIVE;
            case "adventure", "2", "a" -> GameMode.ADVENTURE;
            case "spectator", "3", "sp" -> GameMode.SPECTATOR;
            default -> null;
        };

        if (mode == null) {
            player.sendMessage("§cНеверный режим игры!");
            return;
        }

        player.setGameMode(mode);
        player.sendMessage("§aРежим игры изменён на §e" + mode.name().toLowerCase());
    }
}
