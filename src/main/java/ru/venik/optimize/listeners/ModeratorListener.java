/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.commands.ModeratorCommands;

public class ModeratorListener
implements Listener {
    private final VenikOptimize plugin;
    private final ModeratorCommands moderatorCommands;

    public ModeratorListener(VenikOptimize plugin, ModeratorCommands moderatorCommands) {
        this.plugin = plugin;
        this.moderatorCommands = moderatorCommands;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        Player player;
        Entity entity = event.getEntity();
        if (entity instanceof Player && this.moderatorCommands.isGodMode(player = (Player)entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Player vanished : this.plugin.getServer().getOnlinePlayers()) {
            if (this.moderatorCommands.isVanished(vanished) && player.hasPermission("venikoptimize.vanish.see") || !this.moderatorCommands.isVanished(vanished)) continue;
            player.hidePlayer((Plugin)this.plugin, vanished);
        }
        for (Player vanished : this.plugin.getServer().getOnlinePlayers()) {
            if (!this.moderatorCommands.isVanished(vanished) || player.hasPermission("venikoptimize.vanish.see")) continue;
            player.hidePlayer((Plugin)this.plugin, vanished);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
    }
}

