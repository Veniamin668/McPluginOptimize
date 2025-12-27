package ru.venik.optimize.optimization;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Оптимизация ферм:
 *  - отключение хопперов вдали от игроков
 *  - замедление роста культур в пустых чанках
 */
public class FarmOptimizer {

    private final JavaPlugin plugin;

    private final boolean optimizeHoppers;
    private final boolean optimizeCrops;
    private final int checkIntervalTicks;
    private final double playerRange;

    private boolean running = false;

    public FarmOptimizer(JavaPlugin plugin,
                         boolean optimizeHoppers,
                         boolean optimizeCrops,
                         int checkIntervalTicks,
                         double playerRange) {

        this.plugin = plugin;
        this.optimizeHoppers = optimizeHoppers;
        this.optimizeCrops = optimizeCrops;
        this.checkIntervalTicks = checkIntervalTicks;
        this.playerRange = playerRange;
    }

    // ------------------------------------------------------------
    // START / STOP
    // ------------------------------------------------------------

    public void start() {
        if (running) return;
        running = true;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (optimizeHoppers) optimizeHoppers();
            if (optimizeCrops) optimizeCrops();
        }, 0L, checkIntervalTicks);
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    // ------------------------------------------------------------
    // ОПТИМИЗАЦИЯ ХОППЕРОВ
    // ------------------------------------------------------------

    private void optimizeHoppers() {

        int affected = 0;

        for (World world : Bukkit.getWorlds()) {

            for (Chunk chunk : world.getLoadedChunks()) {

                boolean hasPlayers = hasNearbyPlayers(world, chunk);

                // Paper API: отключение тиков хопперов
                if (chunk.isForceLoaded()) continue;

                chunk.setForceLoaded(hasPlayers);

                if (!hasPlayers) affected++;
            }
        }

        if (affected > 0) {
            plugin.getLogger().info("Paused " + affected + " hopper chunks");
        }
    }

    // ------------------------------------------------------------
    // ОПТИМИЗАЦИЯ РОСТА КУЛЬТУР
    // ------------------------------------------------------------

    private void optimizeCrops() {

        int slowed = 0;

        for (World world : Bukkit.getWorlds()) {

            for (Chunk chunk : world.getLoadedChunks()) {

                boolean hasPlayers = hasNearbyPlayers(world, chunk);

                if (hasPlayers) continue;

                // Замедляем рост культур в чанке
                slowCropGrowth(chunk);
                slowed++;
            }
        }

        if (slowed > 0) {
            plugin.getLogger().info("Slowed crop growth in " + slowed + " chunks");
        }
    }

    private void slowCropGrowth(Chunk chunk) {

        for (Block block : getChunkBlocks(chunk)) {

            if (!(block.getBlockData() instanceof Ageable ageable)) continue;

            int age = ageable.getAge();

            if (age > 0 && Math.random() < 0.5) { // 50% шанс "заморозить"
                ageable.setAge(age - 1);
                block.setBlockData(ageable, false);
            }
        }
    }

    // ------------------------------------------------------------
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ------------------------------------------------------------

    private boolean hasNearbyPlayers(World world, Chunk chunk) {

        int cx = chunk.getX();
        int cz = chunk.getZ();

        for (Player p : world.getPlayers()) {

            Chunk pc = p.getLocation().getChunk();

            if (Math.abs(pc.getX()) - cx <= playerRange &&
                Math.abs(pc.getZ()) - cz <= playerRange) {
                return true;
            }
        }

        return false;
    }

    private Set<Block> getChunkBlocks(Chunk chunk) {

        Set<Block> blocks = new HashSet<>();

        int bx = chunk.getX() << 4;
        int bz = chunk.getZ() << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < chunk.getWorld().getMaxHeight(); y++) {
                    blocks.add(chunk.getWorld().getBlockAt(bx + x, y, bz + z));
                }
            }
        }

        return blocks;
    }
}
