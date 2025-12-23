/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.venik.optimize.VenikOptimize;

public class PvPListener
implements Listener {
    private final VenikOptimize plugin;

    public PvPListener(VenikOptimize plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player victim = (Player)entity;
            entity = event.getDamager();
            if (entity instanceof Player) {
                Player attacker = (Player)entity;
                this.plugin.getPvPManager().tagPlayers(attacker, victim);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer != null) {
            this.plugin.getPvPManager().addKill(killer, victim);
            this.plugin.getBountyManager().claimBounty(killer, victim);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getPvPManager().isInCombat(player)) {
            this.plugin.getServer().broadcastMessage("\u00a7c\u00a7l" + player.getName() + " \u0432\u044b\u0448\u0435\u043b \u0432\u043e \u0432\u0440\u0435\u043c\u044f \u0431\u043e\u044f!");
        }
    }
}

