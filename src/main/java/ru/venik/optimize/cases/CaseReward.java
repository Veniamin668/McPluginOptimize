package ru.venik.optimize.cases;

import org.bukkit.Material;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Награда из кейса.
 */
public class CaseReward {

    private final Material material;
    private final int minAmount;
    private final int maxAmount;
    private final double chance;

    public CaseReward(Material material, int minAmount, int maxAmount, double chance) {
        this.material = material;

        // Защита от некорректных значений
        if (minAmount < 1) minAmount = 1;
        if (maxAmount < minAmount) maxAmount = minAmount;

        this.minAmount = minAmount;
        this.maxAmount = maxAmount;

        // Шанс не может быть отрицательным
        this.chance = Math.max(0, chance);
    }

    public Material getMaterial() {
        return material;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getChance() {
        return chance;
    }

    /**
     * Получить случайное количество предметов в диапазоне.
     */
    public int getRandomAmount() {
        if (minAmount == maxAmount) {
            return minAmount;
        }
        return ThreadLocalRandom.current().nextInt(minAmount, maxAmount + 1);
    }
}
