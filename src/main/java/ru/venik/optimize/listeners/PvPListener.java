package ru.venik.optimize.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.venik.optimize.pvp.PvPManager;
import ru.venik.optimize.bounty.BountyManager;

/**
 * Listener для PvP-системы:
 *  - теги боя
 *  - убийства
 *  - combat-log
 */
public class PvPListener implements Listener {

    private final PvPManager pvp;
    private final BountyManager bounty;

    public PvPListener(PvPManager pvp, BountyManager bounty) {
        this.pvp = pvp;
        this.bounty = bounty;
    }

    // ------------------------------------------------------------
    // ТЕГ БОЯ
    // ------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        Entity victimEntity = event.getEntity();
        if (!(victimEntity instanceof Player victim)) {
            return;
        }

        Entity attackerEntity = event.getDamager();
        if (!(attackerEntity instanceof Player attacker)) {
            return;
        }

        pvp.tagPlayers(attacker, victim);
    }

    // ------------------------------------------------------------
    // УБИЙСТВО
    // ------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        pvp.addKill(killer, victim);
        bounty.claimBounty(killer, victim);
    }

    // ------------------------------------------------------------
    // COMBAT-LOG
    // ------------------------------------------------------------

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (pvp.isInCombat(player)) {
            player.getServer().broadcastMessage(
                    "§c§l" + player.getName() + " вышел во время боя!"
            );
        }
    }
}
