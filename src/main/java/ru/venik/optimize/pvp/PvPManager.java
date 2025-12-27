package ru.venik.optimize.pvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PvP Manager:
 *  - combat-tag (бой 30 секунд)
 *  - киллстрики
 *  - статистика убийств/смертей
 */
public class PvPManager {

    private final JavaPlugin plugin;

    // Combat tag: UUID -> last hit time
    private final Map<UUID, Long> combatTagged = new ConcurrentHashMap<>();

    // Stats
    private final Map<UUID, Integer> killStreaks = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> kills = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> deaths = new ConcurrentHashMap<>();

    private static final long COMBAT_DURATION = 30_000L; // 30 секунд

    public PvPManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    // START / STOP
    // ------------------------------------------------------------

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::tickCombat, 20L, 20L);
    }

    public void stop() {
        combatTagged.clear();
    }

    // ------------------------------------------------------------
    // COMBAT TAG
    // ------------------------------------------------------------

    private void tickCombat() {
        long now = System.currentTimeMillis();

        combatTagged.entrySet().removeIf(entry -> {
            long lastHit = entry.getValue();

            if (now - lastHit > COMBAT_DURATION) {
                Player p = Bukkit.getPlayer(entry.getKey());
                if (p != null && p.isOnline()) {
                    p.sendMessage("§aВы больше не в бою!");
                }
                return true;
            }
            return false;
        });
    }

    public void tagPlayer(Player player) {
        combatTagged.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage("§c§lВЫ В БОЮ! §7Не выходите 30 секунд!");
    }

    public void tagPlayers(Player attacker, Player victim) {
        tagPlayer(attacker);
        tagPlayer(victim);
    }

    public boolean isInCombat(Player player) {
        return combatTagged.containsKey(player.getUniqueId());
    }

    public long getCombatTimeRemaining(Player player) {
        Long lastHit = combatTagged.get(player.getUniqueId());
        if (lastHit == null) return 0;

        long remaining = COMBAT_DURATION - (System.currentTimeMillis() - lastHit);
        return Math.max(0, remaining / 1000);
    }

    // ------------------------------------------------------------
    // KILLS / DEATHS / STREAKS
    // ------------------------------------------------------------

    public void addKill(Player killer, Player victim) {

        // Killer stats
        int streak = killStreaks.getOrDefault(killer.getUniqueId(), 0) + 1;
        killStreaks.put(killer.getUniqueId(), streak);
        kills.put(killer.getUniqueId(), kills.getOrDefault(killer.getUniqueId(), 0) + 1);

        // Victim stats
        deaths.put(victim.getUniqueId(), deaths.getOrDefault(victim.getUniqueId(), 0) + 1);
        killStreaks.put(victim.getUniqueId(), 0);

        // Broadcast
        String msg = "§c" + victim.getName() + " §7был убит игроком §c" + killer.getName();
        if (streak > 1) {
            msg += " §7(§eСерия: " + streak + "§7)";
            broadcastStreak(killer, streak);
        }

        Bukkit.broadcastMessage(msg);
    }

    private void broadcastStreak(Player player, int streak) {
        switch (streak) {
            case 5 -> Bukkit.broadcastMessage("§6§l>>> §e" + player.getName() + " §6получил серию из 5 убийств! §6§l<<<");
            case 10 -> Bukkit.broadcastMessage("§c§l>>> §4" + player.getName() + " §cполучил серию из 10 убийств! §c§l<<<");
            case 20 -> Bukkit.broadcastMessage("§5§l>>> §d" + player.getName() + " §5получил серию из 20 убийств! §5§l<<<");
        }
    }

    // ------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------

    public int getKillStreak(Player player) {
        return killStreaks.getOrDefault(player.getUniqueId(), 0);
    }

    public int getKills(Player player) {
        return kills.getOrDefault(player.getUniqueId(), 0);
    }

    public int getDeaths(Player player) {
        return deaths.getOrDefault(player.getUniqueId(), 0);
    }

    public double getKD(Player player) {
        int d = getDeaths(player);
        return d == 0 ? getKills(player) : (double) getKills(player) / d;
    }
}
