/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.tab;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class TabListManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private BukkitTask updateTask;
    private final Map<UUID, String> customHeaders = new HashMap<UUID, String>();
    private final Map<UUID, String> customFooters = new HashMap<UUID, String>();
    private boolean running = false;

    public TabListManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void start() {
        if (this.running || !this.configManager.getConfig().getBoolean("tab.enabled", true)) {
            return;
        }
        this.running = true;
        this.updateTask = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.updateTabList(player);
            }
        }, 0L, this.configManager.getConfig().getLong("tab.update-interval", 20L));
    }

    private void updateTabList(Player player) {
        if (!player.isOnline()) {
            return;
        }
        String header = this.customHeaders.getOrDefault(player.getUniqueId(), this.configManager.getConfig().getString("tab.default-header", "\u00a76\u00a7l=== VenikOptimize ==="));
        String footer = this.customFooters.getOrDefault(player.getUniqueId(), this.configManager.getConfig().getString("tab.default-footer", "\u00a77\u0421\u0435\u0440\u0432\u0435\u0440 \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u043d\u0430 Minecraft 1.21.4\n\u00a7e\u0418\u0433\u0440\u043e\u043a\u043e\u0432 \u043e\u043d\u043b\u0430\u0439\u043d: \u00a7f{online}"));
        // replace online placeholder
        footer = footer.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        if (this.configManager.getConfig().getBoolean("tab.show-tps", true)) {
            double tps = this.plugin.getPerformanceMonitor().getCurrentTps();
            String tpsColor = tps >= 19.5 ? "\u00a7a" : (tps >= 18.0 ? "\u00a7e" : "\u00a7c");
            footer = footer + "\n\u00a77TPS: " + tpsColor + String.format("%.2f", tps);
        }
        if (this.configManager.getConfig().getBoolean("tab.custom-names", true)) {
            String prefix = this.configManager.getConfig().getString("tab.name-prefix", "\u00a77[\u00a7eVIP\u00a77] ");
            String suffix = this.configManager.getConfig().getString("tab.name-suffix", "");
            if (player.hasPermission("venikoptimize.vip")) {
                prefix = this.configManager.getConfig().getString("tab.vip-prefix", "\u00a76[\u00a7lVIP\u00a76] ");
            }
            player.setPlayerListName(prefix + player.getName() + suffix);
        }
        player.setPlayerListHeaderFooter(header.replace("&", "\u00a7"), footer.replace("&", "\u00a7"));
    }

    public void setCustomHeader(Player player, String header) {
        this.customHeaders.put(player.getUniqueId(), header);
        this.updateTabList(player);
    }

    public void setCustomFooter(Player player, String footer) {
        this.customFooters.put(player.getUniqueId(), footer);
        this.updateTabList(player);
    }

    public void resetTabList(Player player) {
        this.customHeaders.remove(player.getUniqueId());
        this.customFooters.remove(player.getUniqueId());
        this.updateTabList(player);
    }

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.updateTabList(player);
        }
    }

    public void stop() {
        this.running = false;
        if (this.updateTask != null) {
            this.updateTask.cancel();
        }
        this.customHeaders.clear();
        this.customFooters.clear();
    }

    public boolean isRunning() {
        return this.running;
    }
}

