package ru.venik.optimize.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardManager {

    private final JavaPlugin plugin;

    private final Map<UUID, Scoreboard> boards = new HashMap<>();
    private final Map<UUID, Objective> objectives = new HashMap<>();

    private boolean running = false;

    private final List<String> defaultLines = List.of(
            "§7§m----------------",
            "§eОнлайн: §f{online}",
            "",
            "§eБаланс: §f{balance}",
            "",
            "§eTPS: §f{tps}",
            "",
            "§7§m----------------"
    );

    public ScoreboardManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    // START / STOP
    // ------------------------------------------------------------

    public void start(long intervalTicks) {
        if (running) return;
        running = true;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                update(p);
            }
        }, 0L, intervalTicks);

        for (Player p : Bukkit.getOnlinePlayers()) {
            create(p);
        }
    }

    public void stop() {
        running = false;

        for (Player p : Bukkit.getOnlinePlayers()) {
            remove(p);
        }

        boards.clear();
        objectives.clear();
    }

    // ------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------

    public void create(Player player) {

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective obj = board.registerNewObjective(
                "sidebar",
                Criteria.DUMMY,
                Component.text("§6§lVenikCraft")
        );

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        boards.put(player.getUniqueId(), board);
        objectives.put(player.getUniqueId(), obj);

        player.setScoreboard(board);

        update(player);
    }

    // ------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------

    public void update(Player player) {

        Scoreboard board = boards.get(player.getUniqueId());
        Objective obj = objectives.get(player.getUniqueId());

        if (board == null || obj == null) {
            create(player);
            return;
        }

        // Удаляем старые команды
        for (Team t : board.getTeams()) {
            t.unregister();
        }

        // Получаем строки
        List<String> lines = getLines(player);

        int score = lines.size();

        for (String line : lines) {

            String entry = getUniqueEntry(score);

            Team team = board.registerNewTeam("line_" + score);
            team.addEntry(entry);

            team.prefix(Component.text(line));

            obj.getScore(entry).setScore(score);

            score--;
        }
    }

    // ------------------------------------------------------------
    // LINES
    // ------------------------------------------------------------

    private List<String> getLines(Player p) {

        List<String> lines = new ArrayList<>(defaultLines);

        return lines.stream()
                .map(line -> line
                        .replace("{player}", p.getName())
                        .replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("{balance}", "0") // подставишь свой баланс
                        .replace("{tps}", String.format("%.2f", getTps()))
                        .replace("&", "§")
                )
                .toList();
    }

    // ------------------------------------------------------------
    // UTILS
    // ------------------------------------------------------------

    private String getUniqueEntry(int index) {
        return "§" + Integer.toHexString(index) + "§r";
    }

    private double getTps() {
        return 20.0; // сюда подставишь свой PerformanceMonitor
    }

    public void remove(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        boards.remove(player.getUniqueId());
        objectives.remove(player.getUniqueId());
    }

    public boolean isRunning() {
        return running;
    }
}
