/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.bounty;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class BountyManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Double> bounties = new ConcurrentHashMap<UUID, Double>();

    public BountyManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void setBounty(Player player, Player target, double amount) {
        if (target == null || !target.isOnline()) {
            player.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        if (amount <= 0.0) {
            player.sendMessage("\u00a7c\u0421\u0443\u043c\u043c\u0430 \u0434\u043e\u043b\u0436\u043d\u0430 \u0431\u044b\u0442\u044c \u0431\u043e\u043b\u044c\u0448\u0435 0!");
            return;
        }
        double currentBounty = this.bounties.getOrDefault(target.getUniqueId(), 0.0);
        double newBounty = currentBounty + amount;
        if (this.plugin.getDonationManager().getBalance(player) < amount) {
            player.sendMessage("\u00a7c\u041d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432!");
            return;
        }
        this.plugin.getDonationManager().removeBalance(player, amount);
        this.bounties.put(target.getUniqueId(), newBounty);
        player.sendMessage("\u00a7a\u0412\u044b \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u043b\u0438 \u043d\u0430\u0433\u0440\u0430\u0434\u0443 \u00a7e" + amount + "\u00a7a \u043c\u043e\u043d\u0435\u0442 \u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u0430 \u00a7e" + target.getName());
        target.sendMessage("\u00a7c\u00a7l\u041d\u0430 \u0432\u0430\u0441 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430 \u043d\u0430\u0433\u0440\u0430\u0434\u0430! \u00a7e" + newBounty + " \u043c\u043e\u043d\u0435\u0442");
        Bukkit.broadcastMessage((String)("\u00a76\u00a7l>>> \u00a7e" + target.getName() + " \u00a76\u0442\u0435\u043f\u0435\u0440\u044c \u0438\u043c\u0435\u0435\u0442 \u043d\u0430\u0433\u0440\u0430\u0434\u0443 \u00a7e" + newBounty + "\u00a76 \u043c\u043e\u043d\u0435\u0442! \u00a76\u00a7l<<<"));
    }

    public void claimBounty(Player killer, Player victim) {
        Double bounty = this.bounties.remove(victim.getUniqueId());
        if (bounty != null && bounty > 0.0) {
            this.plugin.getDonationManager().addBalance(killer, bounty);
            killer.sendMessage("\u00a7a\u00a7l\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 \u043d\u0430\u0433\u0440\u0430\u0434\u0443 \u00a7e" + bounty + "\u00a7a \u043c\u043e\u043d\u0435\u0442 \u0437\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u043e " + victim.getName() + "!");
            Bukkit.broadcastMessage((String)("\u00a76\u00a7l>>> \u00a7e" + killer.getName() + " \u00a76\u043f\u043e\u043b\u0443\u0447\u0438\u043b \u043d\u0430\u0433\u0440\u0430\u0434\u0443 \u00a7e" + bounty + "\u00a76 \u043c\u043e\u043d\u0435\u0442 \u0437\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u043e " + victim.getName() + "! \u00a76\u00a7l<<<"));
        }
    }

    public double getBounty(Player player) {
        return this.bounties.getOrDefault(player.getUniqueId(), 0.0);
    }

    public Map<UUID, Double> getAllBounties() {
        return new HashMap<UUID, Double>(this.bounties);
    }

    public void listBounties(Player player) {
        if (this.bounties.isEmpty()) {
            player.sendMessage("\u00a77\u041d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0445 \u043d\u0430\u0433\u0440\u0430\u0434.");
            return;
        }
        player.sendMessage("\u00a76\u00a7l=== \u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0435 \u043d\u0430\u0433\u0440\u0430\u0434\u044b ===");
        this.bounties.entrySet().stream().sorted((a, b) -> Double.compare((Double)b.getValue(), (Double)a.getValue())).limit(10L).forEach(entry -> {
            Player target = Bukkit.getPlayer((UUID)((UUID)entry.getKey()));
            if (target != null) {
                player.sendMessage("\u00a7e" + target.getName() + " \u00a77- \u00a7e" + String.valueOf(entry.getValue()) + " \u043c\u043e\u043d\u0435\u0442");
            }
        });
    }
}

