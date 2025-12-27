package ru.venik.optimize.tab;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TabListManager {

    private final JavaPlugin plugin;

    private BukkitTask task;
    private boolean running = false;

    // Персональные header/footer
    private final Map<UUID, String> customHeaders = new ConcurrentHashMap<>();
    private final Map<UUID, String> customFooters = new ConcurrentHashMap<>();

    // Настройки
    private final long updateInterval;
    private final boolean showTps;
    private final boolean customNames;

    private final String defaultHeader;
    private final String defaultFooter;
    private final String prefix;
    private final String vipPrefix;
    private final String suffix;

    public TabListManager(JavaPlugin plugin,
                          long updateInterval,
                          boolean showTps,
                          boolean customNames,
                          String defaultHeader,
                          String defaultFooter,
                          String prefix,
                          String vipPrefix,
                          String suffix) {

        this.plugin = plugin;

        this.updateInterval = updateInterval;
        this.showTps = showTps;
        this.customNames = customNames;

        this.defaultHeader = color(defaultHeader);
        this.defaultFooter = color(defaultFooter);
        this.prefix = color(prefix);
        this.vipPrefix = color(vipPrefix);
        this.suffix = color(suffix);
    }

    // ------------------------------------------------------------
    // START / STOP
    // ------------------------------------------------------------

    public void start() {
        if (running) return;
        running = true;

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                update(p);
            }
        }, 0L, updateInterval);
    }

    public void stop() {
        running = false;

        if (task != null) task.cancel();

        customHeaders.clear();
        customFooters.clear();
    }

    // ------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------

    public void update(Player player) {

        if (!player.isOnline()) return;

        String header = customHeaders.getOrDefault(player.getUniqueId(), defaultHeader);
        String footer = customFooters.getOrDefault(player.getUniqueId(), defaultFooter);

        footer = footer.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));

        if (showTps) {
            double tps = getTps();
            String color = tps >= 19.5 ? "§a" : tps >= 18 ? "§e" : "§c";
            footer += "\n§7TPS: " + color + String.format("%.2f", tps);
        }

        if (customNames) {
            String namePrefix = player.hasPermission("venikoptimize.vip") ? vipPrefix : prefix;
            player.playerListName(Component.text(namePrefix + player.getName() + suffix));
        }

        player.sendPlayerListHeaderAndFooter(
                Component.text(header),
                Component.text(footer)
        );
    }

    // ------------------------------------------------------------
    // CUSTOM HEADER / FOOTER
    // ------------------------------------------------------------

    public void setCustomHeader(Player player, String header) {
        customHeaders.put(player.getUniqueId(), color(header));
        update(player);
    }

    public void setCustomFooter(Player player, String footer) {
        customFooters.put(player.getUniqueId(), color(footer));
        update(player);
    }

    public void reset(Player player) {
        customHeaders.remove(player.getUniqueId());
        customFooters.remove(player.getUniqueId());
        update(player);
    }

    // ------------------------------------------------------------
    // UTILS
    // ------------------------------------------------------------

    private String color(String s) {
        return s == null ? "" : s.replace("&", "§");
    }

    private double getTps() {
        // сюда подставишь свой PerformanceMonitor
        return 20.0;
    }

    public boolean isRunning() {
        return running;
    }
}
