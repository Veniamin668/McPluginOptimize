package ru.venik.optimize.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.venik.optimize.commands.ModeratorCommands;

import java.util.Collection;

/**
 * Listener для функций модератора:
 *  - GodMode
 *  - Vanish
 */
public class ModeratorListener implements Listener {

    private final ModeratorCommands moderator;

    public ModeratorListener(ModeratorCommands moderator) {
        this.moderator = moderator;
    }

    // ------------------------------------------------------------
    // GODMODE
    // ------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {

        Entity entity = event.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        if (moderator.isGodMode(player)) {
            event.setCancelled(true);
        }
    }

    // ------------------------------------------------------------
    // VANISH: скрываем модераторов от нового игрока
    // ------------------------------------------------------------

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player joining = event.getPlayer();
        Collection<? extends Player> online = joining.getServer().getOnlinePlayers();

        boolean canSeeVanished = joining.hasPermission("venik.vanish.see");

        for (Player other : online) {

            if (!moderator.isVanished(other)) {
                continue;
            }

            if (!canSeeVanished) {
                joining.hidePlayer(other);
            }
        }
    }
}
