/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.teleport;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class TeleportManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<UUID, UUID> tpaRequests = new ConcurrentHashMap<UUID, UUID>();
    private final Map<UUID, Long> tpaRequestTimes = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Location> lastLocations = new ConcurrentHashMap<UUID, Location>();
    private final Map<UUID, Boolean> teleportHereRequests = new ConcurrentHashMap<UUID, Boolean>();

    public TeleportManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void requestTeleport(Player sender, Player target) {
        if (target == null || !target.isOnline()) {
            sender.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u0438\u043b\u0438 \u043e\u0444\u0444\u043b\u0430\u0439\u043d!");
            return;
        }
        UUID targetUuid = target.getUniqueId();
        this.tpaRequests.put(targetUuid, sender.getUniqueId());
        this.tpaRequestTimes.put(targetUuid, System.currentTimeMillis());
        sender.sendMessage("\u00a7a\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d \u0438\u0433\u0440\u043e\u043a\u0443 \u00a7e" + target.getName() + "\u00a7a!");
        target.sendMessage("\u00a7e" + sender.getName() + "\u00a77 \u0437\u0430\u043f\u0440\u043e\u0441\u0438\u043b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e \u043a \u0432\u0430\u043c!");
        target.sendMessage("\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435: \u00a7e/tpaccept \u00a77\u0438\u043b\u0438 \u00a7c/tpdeny");
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            if (this.tpaRequests.containsKey(targetUuid) && this.tpaRequests.get(targetUuid).equals(sender.getUniqueId())) {
                this.tpaRequests.remove(targetUuid);
                this.tpaRequestTimes.remove(targetUuid);
                sender.sendMessage("\u00a7c\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e \u0438\u0441\u0442\u0435\u043a!");
            }
        }, 1200L);
    }

    public void requestTeleportHere(Player sender, Player target) {
        if (target == null || !target.isOnline()) {
            sender.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u0438\u043b\u0438 \u043e\u0444\u0444\u043b\u0430\u0439\u043d!");
            return;
        }
        UUID targetUuid = target.getUniqueId();
        this.tpaRequests.put(targetUuid, sender.getUniqueId());
        this.tpaRequestTimes.put(targetUuid, System.currentTimeMillis());
        sender.sendMessage("\u00a7a\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e \u0438\u0433\u0440\u043e\u043a\u0430 \u00a7e" + target.getName() + "\u00a7a \u043a \u0432\u0430\u043c \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d!");
        target.sendMessage("\u00a7e" + sender.getName() + "\u00a77 \u0437\u0430\u043f\u0440\u043e\u0441\u0438\u043b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e \u0432\u0430\u0441 \u043a \u0441\u0435\u0431\u0435!");
        target.sendMessage("\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435: \u00a7e/tpaccept \u00a77\u0438\u043b\u0438 \u00a7c/tpdeny");
        this.teleportHereRequests.put(targetUuid, true);
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            if (this.tpaRequests.containsKey(targetUuid) && this.tpaRequests.get(targetUuid).equals(sender.getUniqueId())) {
                this.tpaRequests.remove(targetUuid);
                this.tpaRequestTimes.remove(targetUuid);
                this.teleportHereRequests.remove(targetUuid);
                sender.sendMessage("\u00a7c\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e \u0438\u0441\u0442\u0435\u043a!");
            }
        }, 1200L);
    }

    public void acceptTeleport(Player player) {
        UUID playerUuid = player.getUniqueId();
        UUID senderUuid = this.tpaRequests.get(playerUuid);
        if (senderUuid == null) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0445 \u0437\u0430\u043f\u0440\u043e\u0441\u043e\u0432 \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e!");
            return;
        }
        Player sender = this.plugin.getServer().getPlayer(senderUuid);
        if (sender == null || !sender.isOnline()) {
            player.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a, \u043e\u0442\u043f\u0440\u0430\u0432\u0438\u0432\u0448\u0438\u0439 \u0437\u0430\u043f\u0440\u043e\u0441, \u043e\u0444\u0444\u043b\u0430\u0439\u043d!");
            this.tpaRequests.remove(playerUuid);
            this.tpaRequestTimes.remove(playerUuid);
            return;
        }
        Boolean isTeleportHere = this.teleportHereRequests.get(playerUuid);
        if (isTeleportHere != null && isTeleportHere.booleanValue()) {
            this.saveLastLocation(player);
            player.teleport(sender.getLocation());
            player.sendMessage("\u00a7a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043a \u0438\u0433\u0440\u043e\u043a\u0443 \u00a7e" + sender.getName() + "\u00a7a!");
            sender.sendMessage("\u00a7e" + player.getName() + "\u00a7a \u043f\u0440\u0438\u043d\u044f\u043b \u0432\u0430\u0448 \u0437\u0430\u043f\u0440\u043e\u0441!");
        } else {
            this.saveLastLocation(sender);
            sender.teleport(player.getLocation());
            sender.sendMessage("\u00a7a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043a \u0438\u0433\u0440\u043e\u043a\u0443 \u00a7e" + player.getName() + "\u00a7a!");
            player.sendMessage("\u00a7e" + sender.getName() + "\u00a7a \u043f\u0440\u0438\u043d\u044f\u043b \u0432\u0430\u0448 \u0437\u0430\u043f\u0440\u043e\u0441!");
        }
        this.tpaRequests.remove(playerUuid);
        this.tpaRequestTimes.remove(playerUuid);
        this.getTeleportHereRequests().remove(playerUuid);
    }

    public void denyTeleport(Player player) {
        UUID playerUuid = player.getUniqueId();
        UUID senderUuid = this.tpaRequests.get(playerUuid);
        if (senderUuid == null) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0445 \u0437\u0430\u043f\u0440\u043e\u0441\u043e\u0432 \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e!");
            return;
        }
        Player sender = this.plugin.getServer().getPlayer(senderUuid);
        if (sender != null && sender.isOnline()) {
            sender.sendMessage("\u00a7c" + player.getName() + " \u043e\u0442\u043a\u043b\u043e\u043d\u0438\u043b \u0432\u0430\u0448 \u0437\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e!");
        }
        player.sendMessage("\u00a7a\u0412\u044b \u043e\u0442\u043a\u043b\u043e\u043d\u0438\u043b\u0438 \u0437\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e!");
        this.tpaRequests.remove(playerUuid);
        this.tpaRequestTimes.remove(playerUuid);
        this.teleportHereRequests.remove(playerUuid);
    }

    public Map<UUID, Boolean> getTeleportHereRequests() {
        return this.teleportHereRequests;
    }

    public void saveLastLocation(Player player) {
        this.lastLocations.put(player.getUniqueId(), player.getLocation().clone());
    }

    public void teleportBack(Player player) {
        Location lastLoc = this.lastLocations.get(player.getUniqueId());
        if (lastLoc == null) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u043d\u043e\u0439 \u0442\u043e\u0447\u043a\u0438 \u0434\u043b\u044f \u0432\u043e\u0437\u0432\u0440\u0430\u0442\u0430!");
            return;
        }
        player.teleport(lastLoc);
        player.sendMessage("\u00a7a\u0412\u044b \u0432\u0435\u0440\u043d\u0443\u043b\u0438\u0441\u044c \u043d\u0430 \u043f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0443\u044e \u043f\u043e\u0437\u0438\u0446\u0438\u044e!");
        this.lastLocations.remove(player.getUniqueId());
    }
}

