/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class ScoreboardManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<UUID, Scoreboard>();
    private final Map<UUID, Objective> playerObjectives = new HashMap<UUID, Objective>();
    private boolean running = false;

    public ScoreboardManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        if (this.running || !this.configManager.getConfig().getBoolean("scoreboard.enabled", true)) {
            return;
        }
        this.running = true;
        Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.updateScoreboard(player);
            }
        }, 0L, this.configManager.getConfig().getLong("scoreboard.update-interval", 20L));
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.createScoreboard(player);
        }
    }

    public void createScoreboard(Player player) {
        if (!this.configManager.getConfig().getBoolean("scoreboard.enabled", true)) {
            return;
        }
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("venikcraft", Criteria.DUMMY, (Component)Component.text((String)this.configManager.getConfig().getString("scoreboard.title", "\u00a76\u00a7lVenikCraft").replace("&", "\u00a7")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.playerScoreboards.put(player.getUniqueId(), scoreboard);
        this.playerObjectives.put(player.getUniqueId(), objective);
        player.setScoreboard(scoreboard);
        this.updateScoreboard(player);
    }

    private void updateScoreboard(Player player) {
        Objective objective = this.playerObjectives.get(player.getUniqueId());
        if (objective == null) {
            this.createScoreboard(player);
            return;
        }
        Scoreboard scoreboard = this.playerScoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            return;
        }
        scoreboard.getEntries().forEach(arg_0 -> ((Scoreboard)scoreboard).resetScores(arg_0));
        List<String> lines = this.configManager.getConfig().getStringList("scoreboard.lines");
        lines = lines.isEmpty() ? List.of("\u00a77\u00a7m----------------", "\u00a7e\u041e\u043d\u043b\u0430\u0439\u043d: \u00a7f" + Bukkit.getOnlinePlayers().size(), "", "\u00a7e\u0412\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441: \u00a7f0", "", "\u00a7eTPS: \u00a7f" + String.format("%.2f", this.plugin.getPerformanceMonitor().getCurrentTps()), "", "\u00a77\u00a7m----------------") : lines.stream().map(line -> line.replace("{donate_balance}", String.valueOf(this.plugin.getDonationManager().getBalance(player)))).collect(Collectors.toList());
        int lineNumber = lines.size();
        for (String line2 : lines) {
            line2 = this.replacePlaceholders(player, line2);
            Team team = scoreboard.getTeam("line" + lineNumber);
            if (team == null) {
                team = scoreboard.registerNewTeam("line" + lineNumber);
            }
            String entry = this.getEntryByIndex(lineNumber);
            team.addEntry(entry);
            team.prefix((Component)Component.text((String)line2));
            objective.getScore(entry).setScore(lineNumber);
            --lineNumber;
        }
    }

    private String replacePlaceholders(Player player, String text) {
        text = text.replace("{player}", player.getName());
        text = text.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("{tps}", String.format("%.2f", this.plugin.getPerformanceMonitor().getCurrentTps()));
        text = text.replace("{ping}", String.valueOf(player.getPing()));
        text = text.replace("{world}", player.getWorld().getName());
        text = text.replace("{x}", String.valueOf(player.getLocation().getBlockX()));
        text = text.replace("{y}", String.valueOf(player.getLocation().getBlockY()));
        text = text.replace("{z}", String.valueOf(player.getLocation().getBlockZ()));
        return text.replace("&", "\u00a7");
    }

    private String getEntryByIndex(int index) {
        String[] entries = new String[]{"\u00a70", "\u00a71", "\u00a72", "\u00a73", "\u00a74", "\u00a75", "\u00a76", "\u00a77", "\u00a78", "\u00a79", "\u00a7a", "\u00a7b", "\u00a7c", "\u00a7d", "\u00a7e", "\u00a7f"};
        return entries[index % entries.length] + "\u00a7r";
    }

    public void removeScoreboard(Player player) {
        this.playerObjectives.remove(player.getUniqueId());
        this.playerScoreboards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void stop() {
        this.running = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.removeScoreboard(player);
        }
        this.playerScoreboards.clear();
        this.playerObjectives.clear();
    }

    public boolean isRunning() {
        return this.running;
    }
}

