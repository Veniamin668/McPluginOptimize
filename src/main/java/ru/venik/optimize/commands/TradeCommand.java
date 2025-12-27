package ru.venik.optimize.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.venik.optimize.trade.TradeManager;

/**
 * Команда /trade:
 *  - /trade request <игрок>
 *  - /trade accept
 *  - /trade deny
 *  - /trade confirm
 *  - /trade cancel
 */
public class TradeCommand implements CommandExecutor {

    private final TradeManager tradeManager;

    public TradeCommand(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§cИспользование: /trade <request|accept|deny|confirm|cancel> [игрок]");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            // ------------------------------------------------------------
            // /trade request <игрок>
            // ------------------------------------------------------------
            case "request":
            case "req": {

                if (args.length < 2) {
                    player.sendMessage("§cИспользование: /trade request <игрок>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null || !target.isOnline()) {
                    player.sendMessage("§cИгрок не найден или оффлайн!");
                    return true;
                }

                if (target.equals(player)) {
                    player.sendMessage("§cВы не можете отправить запрос самому себе!");
                    return true;
                }

                tradeManager.requestTrade(player, target);
                return true;
            }

            // ------------------------------------------------------------
            // /trade accept
            // ------------------------------------------------------------
            case "accept": {
                tradeManager.acceptTrade(player);
                return true;
            }

            // ------------------------------------------------------------
            // /trade deny
            // ------------------------------------------------------------
            case "deny": {
                tradeManager.denyTrade(player);
                return true;
            }

            // ------------------------------------------------------------
            // /trade confirm
            // ------------------------------------------------------------
            case "confirm": {
                tradeManager.confirmTrade(player);
                return true;
            }

            // ------------------------------------------------------------
            // /trade cancel
            // ------------------------------------------------------------
            case "cancel": {
                tradeManager.cancelTrade(player);
                return true;
            }

            // ------------------------------------------------------------
            // Unknown subcommand
            // ------------------------------------------------------------
            default:
                player.sendMessage("§cНеизвестная подкоманда!");
                return true;
        }
    }
}
