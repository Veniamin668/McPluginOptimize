/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.commands;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.venik.optimize.VenikOptimize;

public class UtilityCommands
implements CommandExecutor {
    private final VenikOptimize plugin;

    public UtilityCommands(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7c\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c!");
            return true;
        }
        Player player = (Player)sender;
        switch (cmd) {
            case "weather": {
                this.handleWeather(player, args);
                break;
            }
            case "time": {
                this.handleTime(player, args);
                break;
            }
            case "clear": 
            case "ci": {
                player.getInventory().clear();
                player.sendMessage("\u00a7a\u0418\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c \u043e\u0447\u0438\u0449\u0435\u043d!");
                break;
            }
            case "repair": {
                this.repairItem(player);
                break;
            }
            case "enchant": {
                if (!player.hasPermission("venikoptimize.enchant")) {
                    player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432!");
                    return true;
                }
                player.openEnchanting(player.getLocation(), true);
                break;
            }
            case "workbench": 
            case "craft": {
                player.openWorkbench(player.getLocation(), true);
                break;
            }
            case "anvil": {
                player.sendMessage("\u00a7e\u0410\u043d\u0432\u0438\u043b \u0432\u0440\u0435\u043c\u0435\u043d\u043d\u043e \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d (\u0442\u0440\u0435\u0431\u0443\u0435\u0442 NMS)");
                break;
            }
            case "enderchest": 
            case "ec": {
                player.openInventory(player.getEnderChest());
                break;
            }
            case "gm": 
            case "gamemode": {
                this.handleGamemode(player, args);
            }
        }
        return true;
    }

    private void handleWeather(Player player, String[] args) {
        if (!player.hasPermission("venikoptimize.weather")) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432!");
            return;
        }
        if (args.length == 0) {
            player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /weather <clear|rain|storm>");
            return;
        }
        World world = player.getWorld();
        switch (args[0].toLowerCase()) {
            case "clear": 
            case "sun": {
                world.setStorm(false);
                world.setThundering(false);
                player.sendMessage("\u00a7a\u041f\u043e\u0433\u043e\u0434\u0430 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430: \u042f\u0441\u043d\u043e");
                break;
            }
            case "rain": {
                world.setStorm(true);
                world.setThundering(false);
                player.sendMessage("\u00a7a\u041f\u043e\u0433\u043e\u0434\u0430 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430: \u0414\u043e\u0436\u0434\u044c");
                break;
            }
            case "storm": 
            case "thunder": {
                world.setStorm(true);
                world.setThundering(true);
                player.sendMessage("\u00a7a\u041f\u043e\u0433\u043e\u0434\u0430 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430: \u0413\u0440\u043e\u0437\u0430");
            }
        }
    }

    private void handleTime(Player player, String[] args) {
        if (!player.hasPermission("venikoptimize.time")) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432!");
            return;
        }
        if (args.length == 0) {
            player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /time <day|night|noon|midnight|set <\u0447\u0438\u0441\u043b\u043e>>");
            return;
        }
        World world = player.getWorld();
        switch (args[0].toLowerCase()) {
            case "day": {
                world.setTime(1000L);
                player.sendMessage("\u00a7a\u0412\u0440\u0435\u043c\u044f \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u043e: \u0414\u0435\u043d\u044c");
                break;
            }
            case "night": {
                world.setTime(13000L);
                player.sendMessage("\u00a7a\u0412\u0440\u0435\u043c\u044f \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u043e: \u041d\u043e\u0447\u044c");
                break;
            }
            case "noon": {
                world.setTime(6000L);
                player.sendMessage("\u00a7a\u0412\u0440\u0435\u043c\u044f \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u043e: \u041f\u043e\u043b\u0434\u0435\u043d\u044c");
                break;
            }
            case "midnight": {
                world.setTime(18000L);
                player.sendMessage("\u00a7a\u0412\u0440\u0435\u043c\u044f \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u043e: \u041f\u043e\u043b\u043d\u043e\u0447\u044c");
                break;
            }
            case "set": {
                if (args.length < 2) {
                    player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /time set <\u0447\u0438\u0441\u043b\u043e>");
                    return;
                }
                try {
                    long time = Long.parseLong(args[1]);
                    world.setTime(time);
                    player.sendMessage("\u00a7a\u0412\u0440\u0435\u043c\u044f \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u043e \u043d\u0430 \u00a7e" + time);
                    break;
                }
                catch (NumberFormatException e) {
                    player.sendMessage("\u00a7c\u041d\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u0447\u0438\u0441\u043b\u043e!");
                }
            }
        }
    }

    private void repairItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("\u00a7c\u0412\u043e\u0437\u044c\u043c\u0438\u0442\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0432 \u0440\u0443\u043a\u0443 \u0434\u043b\u044f \u043f\u043e\u0447\u0438\u043d\u043a\u0438!");
            return;
        }
        if (!item.getType().isItem() || item.getType().getMaxDurability() == 0) {
            player.sendMessage("\u00a7c\u042d\u0442\u043e\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u043d\u0435\u043b\u044c\u0437\u044f \u043f\u043e\u0447\u0438\u043d\u0438\u0442\u044c!");
            return;
        }
        item.setDurability((short)0);
        player.sendMessage("\u00a7a\u041f\u0440\u0435\u0434\u043c\u0435\u0442 \u043f\u043e\u0447\u0438\u043d\u0435\u043d!");
    }

    private void handleGamemode(Player player, String[] args) {
        if (!player.hasPermission("venikoptimize.gamemode")) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043f\u0440\u0430\u0432!");
            return;
        }
        if (args.length == 0) {
            player.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /gamemode <survival|creative|adventure|spectator|0|1|2|3>");
            return;
        }
        GameMode mode = null;
        switch (args[0].toLowerCase()) {
            case "survival": 
            case "0": 
            case "s": {
                mode = GameMode.SURVIVAL;
                break;
            }
            case "creative": 
            case "1": 
            case "c": {
                mode = GameMode.CREATIVE;
                break;
            }
            case "adventure": 
            case "2": 
            case "a": {
                mode = GameMode.ADVENTURE;
                break;
            }
            case "spectator": 
            case "3": 
            case "sp": {
                mode = GameMode.SPECTATOR;
            }
        }
        if (mode != null) {
            player.setGameMode(mode);
            player.sendMessage("\u00a7a\u0420\u0435\u0436\u0438\u043c \u0438\u0433\u0440\u044b \u0438\u0437\u043c\u0435\u043d\u0435\u043d \u043d\u0430 \u00a7e" + mode.name().toLowerCase());
        } else {
            player.sendMessage("\u00a7c\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u0440\u0435\u0436\u0438\u043c \u0438\u0433\u0440\u044b!");
        }
    }
}

