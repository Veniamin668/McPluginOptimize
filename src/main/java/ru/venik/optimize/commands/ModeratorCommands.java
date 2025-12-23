/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.venik.optimize.VenikOptimize;

public class ModeratorCommands
implements CommandExecutor,
TabCompleter {
    private final VenikOptimize plugin;
    private final Set<UUID> flyingPlayers = new HashSet<UUID>();
    private final Set<UUID> godModePlayers = new HashSet<UUID>();
    private final Set<UUID> vanishedPlayers = new HashSet<UUID>();

    public ModeratorCommands(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        Player target = null;
        if (args.length > 0 && sender.hasPermission("venikoptimize.admin")) {
            target = this.plugin.getServer().getPlayer(args[0]);
        }
        if (target == null && sender instanceof Player) {
            target = (Player)sender;
        }
        if (target == null || !target.isOnline()) {
            sender.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return true;
        }
        switch (cmd) {
            case "fly": {
                this.toggleFly(target);
                sender.sendMessage((String)(target == sender ? (target.getAllowFlight() ? "\u00a7a\u041f\u043e\u043b\u0435\u0442 \u0432\u043a\u043b\u044e\u0447\u0435\u043d!" : "\u00a7c\u041f\u043e\u043b\u0435\u0442 \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d!") : "\u00a7a\u041f\u043e\u043b\u0435\u0442 " + (target.getAllowFlight() ? "\u0432\u043a\u043b\u044e\u0447\u0435\u043d" : "\u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d") + " \u0434\u043b\u044f " + target.getName()));
                break;
            }
            case "god": {
                this.toggleGod(target);
                sender.sendMessage((String)(target == sender ? (this.godModePlayers.contains(target.getUniqueId()) ? "\u00a7a\u0420\u0435\u0436\u0438\u043c \u0431\u043e\u0433\u0430 \u0432\u043a\u043b\u044e\u0447\u0435\u043d!" : "\u00a7c\u0420\u0435\u0436\u0438\u043c \u0431\u043e\u0433\u0430 \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d!") : "\u00a7a\u0420\u0435\u0436\u0438\u043c \u0431\u043e\u0433\u0430 " + (this.godModePlayers.contains(target.getUniqueId()) ? "\u0432\u043a\u043b\u044e\u0447\u0435\u043d" : "\u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d") + " \u0434\u043b\u044f " + target.getName()));
                break;
            }
            case "vanish": 
            case "v": {
                this.toggleVanish(target);
                sender.sendMessage((String)(target == sender ? (this.vanishedPlayers.contains(target.getUniqueId()) ? "\u00a7a\u0412\u044b \u0441\u0442\u0430\u043b\u0438 \u043d\u0435\u0432\u0438\u0434\u0438\u043c\u044b!" : "\u00a7c\u0412\u044b \u0441\u0442\u0430\u043b\u0438 \u0432\u0438\u0434\u0438\u043c\u044b!") : "\u00a7a\u041d\u0435\u0432\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u044c " + (this.vanishedPlayers.contains(target.getUniqueId()) ? "\u0432\u043a\u043b\u044e\u0447\u0435\u043d\u0430" : "\u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0430") + " \u0434\u043b\u044f " + target.getName()));
                break;
            }
            case "speed": {
                if (args.length < 2) {
                    sender.sendMessage("\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /speed <1-10> [\u0438\u0433\u0440\u043e\u043a]");
                    return true;
                }
                int speed = this.parseInteger(args[0], 5);
                speed = Math.max(1, Math.min(10, speed));
                this.setSpeed(target, speed);
                sender.sendMessage("\u00a7a\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430 \u043d\u0430 \u00a7e" + speed + "\u00a7a \u0434\u043b\u044f " + target.getName());
                break;
            }
            case "heal": {
                target.setHealth(target.getMaxHealth());
                target.setFoodLevel(20);
                target.setSaturation(20.0f);
                sender.sendMessage("\u00a7a" + (target == sender ? "\u0412\u044b" : target.getName()) + " \u0438\u0441\u0446\u0435\u043b\u0435\u043d\u044b!");
                break;
            }
            case "feed": {
                target.setFoodLevel(20);
                target.setSaturation(20.0f);
                sender.sendMessage("\u00a7a" + (target == sender ? "\u0412\u044b" : target.getName()) + " \u043d\u0430\u043a\u043e\u0440\u043c\u043b\u0435\u043d\u044b!");
            }
        }
        return true;
    }

    private void toggleFly(Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            this.flyingPlayers.remove(player.getUniqueId());
        } else {
            player.setAllowFlight(true);
            this.flyingPlayers.add(player.getUniqueId());
        }
    }

    private void toggleGod(Player player) {
        if (this.godModePlayers.contains(player.getUniqueId())) {
            this.godModePlayers.remove(player.getUniqueId());
        } else {
            this.godModePlayers.add(player.getUniqueId());
        }
    }

    private void toggleVanish(Player player) {
        if (this.vanishedPlayers.contains(player.getUniqueId())) {
            this.vanishedPlayers.remove(player.getUniqueId());
            for (Player p : this.plugin.getServer().getOnlinePlayers()) {
                p.showPlayer((Plugin)this.plugin, player);
            }
        } else {
            this.vanishedPlayers.add(player.getUniqueId());
            for (Player p : this.plugin.getServer().getOnlinePlayers()) {
                if (p.hasPermission("venikoptimize.vanish.see")) continue;
                p.hidePlayer((Plugin)this.plugin, player);
            }
        }
    }

    private void setSpeed(Player player, int speed) {
        float speedValue = (float)speed / 10.0f;
        player.setWalkSpeed(Math.min(1.0f, speedValue * 0.2f));
        player.setFlySpeed(Math.min(1.0f, speedValue * 0.1f));
    }

    public boolean isFlying(Player player) {
        return this.flyingPlayers.contains(player.getUniqueId());
    }

    public boolean isGodMode(Player player) {
        return this.godModePlayers.contains(player.getUniqueId());
    }

    public boolean isVanished(Player player) {
        return this.vanishedPlayers.contains(player.getUniqueId());
    }

    private int parseInteger(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1 && sender.hasPermission("venikoptimize.admin")) {
            this.plugin.getServer().getOnlinePlayers().forEach(p -> completions.add(p.getName()));
        }
        return completions;
    }
}

