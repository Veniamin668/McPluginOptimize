/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.cases;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;

public class CaseReward {
    private final Material material;
    private final int minAmount;
    private final int maxAmount;
    private final double chance;

    public CaseReward(Material material, int minAmount, int maxAmount, double chance) {
        this.material = material;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.chance = chance;
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getMinAmount() {
        return this.minAmount;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public double getChance() {
        return this.chance;
    }

    public int getRandomAmount() {
        return ThreadLocalRandom.current().nextInt(this.minAmount, this.maxAmount + 1);
    }
}

