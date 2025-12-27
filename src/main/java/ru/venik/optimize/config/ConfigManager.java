package ru.venik.optimize.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Универсальный менеджер конфигурации.
 * Поддерживает:
 *  - config.yml
 *  - кастомные файлы (messages.yml, warps.yml, kits.yml и т.д.)
 *  - безопасную загрузку
 *  - автоматическое создание файлов
 */
public class ConfigManager {

    private final Plugin plugin;
    private FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    // ------------------------------------------------------------
    // Основной config.yml
    // ------------------------------------------------------------

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        loadConfig();
    }

    // ------------------------------------------------------------
    // Загрузка кастомных YAML-файлов
    // ------------------------------------------------------------

    public FileConfiguration loadCustomConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        FileConfiguration cfg = new YamlConfiguration();

        try {
            cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Не удалось загрузить " + fileName);
            e.printStackTrace();
        }

        return cfg;
    }

    public void saveCustomConfig(String fileName, FileConfiguration cfg) {
        File file = new File(plugin.getDataFolder(), fileName);

        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить " + fileName);
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------
    // Геттеры для config.yml
    // ------------------------------------------------------------

    public boolean isPerformanceEnabled() {
        return config.getBoolean("performance.enabled", true);
    }

    public int getTpsCheckInterval() {
        return config.getInt("performance.tps-check-interval", 20);
    }

    public double getAlertLowTps() {
        return config.getDouble("performance.alert-low-tps", 18.0);
    }

    public double getAlertHighMspt() {
        return config.getDouble("performance.alert-high-mspt", 50.0);
    }

    public boolean isBlockProfilerEnabled() {
        return config.getBoolean("block-profiler.enabled", true);
    }

    public int getSamplingInterval() {
        return config.getInt("block-profiler.sampling-interval", 100);
    }

    public int getMaxSamples() {
        return config.getInt("block-profiler.max-samples", 120);
    }

    public int getTrackTopBlocks() {
        return config.getInt("block-profiler.track-top-blocks", 20);
    }

    public boolean isEntityOptimizationEnabled() {
        return config.getBoolean("entity.enabled", true);
    }

    public int getMaxEntitiesPerChunk() {
        return config.getInt("entity.max-entities-per-chunk", 50);
    }

    public boolean isChunkOptimizationEnabled() {
        return config.getBoolean("chunk.enabled", true);
    }

    public boolean isRedstoneOptimizationEnabled() {
        return config.getBoolean("redstone.enabled", true);
    }

    public boolean isCleanupEnabled() {
        return config.getBoolean("cleanup.enabled", true);
    }

    public int getCleanupInterval() {
        return config.getInt("cleanup.cleanup-interval", 36000);
    }
}
