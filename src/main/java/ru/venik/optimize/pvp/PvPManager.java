/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.pvp;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class PvPManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Long> combatTagged = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Integer> killStreaks = new ConcurrentHashMap<UUID, Integer>();
    private final Map<UUID, Integer> deaths = new ConcurrentHashMap<UUID, Integer>();
    private final Map<UUID, Integer> kills = new ConcurrentHashMap<UUID, Integer>();
    private BukkitTask combatCheckTask;
    private static final long COMBAT_TIME = 30000L;

    public PvPManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        this.combatCheckTask = this.plugin.getServer().getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            long currentTime = System.currentTimeMillis();
            Iterator<Map.Entry<UUID, Long>> iterator = this.combatTagged.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Long> entry = iterator.next();
                if (currentTime - entry.getValue() <= 30000L) continue;
                iterator.remove();
                Player player = Bukkit.getPlayer((UUID)entry.getKey());
                if (player == null || !player.isOnline()) continue;
                player.sendMessage("\u00a7a\u0412\u044b \u0431\u043e\u043b\u044c\u0448\u0435 \u043d\u0435 \u0432 \u0431\u043e\u044e!");
            }
        }, 0L, 20L);
    }

    public void tagPlayer(Player player) {
        this.combatTagged.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage("\u00a7c\u00a7l\u0412\u042b \u0412 \u0411\u041e\u042e! \u041d\u0435 \u0432\u044b\u0445\u043e\u0434\u0438\u0442\u0435 30 \u0441\u0435\u043a\u0443\u043d\u0434!");
    }

    public void tagPlayers(Player attacker, Player victim) {
        this.tagPlayer(attacker);
        this.tagPlayer(victim);
    }

    public boolean isInCombat(Player player) {
        return this.combatTagged.containsKey(player.getUniqueId());
    }

    public long getCombatTimeRemaining(Player player) {
        Long tagTime = this.combatTagged.get(player.getUniqueId());
        if (tagTime == null) {
            return 0L;
        }
        long remaining = 30000L - (System.currentTimeMillis() - tagTime);
        return Math.max(0L, remaining / 1000L);
    }

    public void addKill(Player killer, Player victim) {
        int streak = this.killStreaks.getOrDefault(killer.getUniqueId(), 0) + 1;
        this.killStreaks.put(killer.getUniqueId(), streak);
        this.kills.put(killer.getUniqueId(), this.kills.getOrDefault(killer.getUniqueId(), 0) + 1);
        this.deaths.put(victim.getUniqueId(), this.deaths.getOrDefault(victim.getUniqueId(), 0) + 1);
        this.killStreaks.put(victim.getUniqueId(), 0);
        String killMessage = "\u00a7c" + victim.getName() + " \u00a77\u0431\u044b\u043b \u0443\u0431\u0438\u0442 \u0438\u0433\u0440\u043e\u043a\u043e\u043c \u00a7c" + killer.getName();
        if (streak > 1) {
            killMessage = killMessage + " \u00a77(\u00a7e\u0421\u0435\u0440\u0438\u0435\u0439: " + streak + "\u00a77)";
            this.broadcastKillStreak(killer, streak);
        }
        Bukkit.broadcastMessage((String)killMessage);
    }

    private void broadcastKillStreak(Player player, int streak) {
        if (streak == 5) {
            Bukkit.broadcastMessage((String)("\u00a76\u00a7l>>> \u00a7e" + player.getName() + " \u00a76\u043f\u043e\u043b\u0443\u0447\u0438\u043b \u0441\u0435\u0440\u0438\u044e \u0438\u0437 5 \u0443\u0431\u0438\u0439\u0441\u0442\u0432! \u00a76\u00a7l<<<"));
        } else if (streak == 10) {
            Bukkit.broadcastMessage((String)("\u00a7c\u00a7l>>> \u00a74" + player.getName() + " \u00a7c\u043f\u043e\u043b\u0443\u0447\u0438\u043b \u0441\u0435\u0440\u0438\u044e \u0438\u0437 10 \u0443\u0431\u0438\u0439\u0441\u0442\u0432! \u00a7c\u00a7l<<<"));
        } else if (streak == 20) {
            Bukkit.broadcastMessage((String)("\u00a75\u00a7l>>> \u00a7d" + player.getName() + " \u00a75\u043f\u043e\u043b\u0443\u0447\u0438\u043b \u0441\u0435\u0440\u0438\u044e \u0438\u0437 20 \u0443\u0431\u0438\u0439\u0441\u0442\u0432! \u00a75\u00a7l<<<"));
        }
    }

    public int getKillStreak(Player player) {
        return this.killStreaks.getOrDefault(player.getUniqueId(), 0);
    }

    public int getKills(Player player) {
        return this.kills.getOrDefault(player.getUniqueId(), 0);
    }

    public int getDeaths(Player player) {
        return this.deaths.getOrDefault(player.getUniqueId(), 0);
    }

    public double getKD(Player player) {
        int deaths = this.getDeaths(player);
        if (deaths == 0) {
            return this.getKills(player);
        }
        return (double)this.getKills(player) / (double)deaths;
    }

    public void stop() {
        if (this.combatCheckTask != null) {
            this.combatCheckTask.cancel();
        }
    }
}

