/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.cases;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import ru.venik.optimize.cases.CaseReward;

public class Case {
    private final String id;
    private final String displayName;
    private final Material material;
    private final double rarity;
    private final List<CaseReward> rewards = new ArrayList<CaseReward>();

    public Case(String id, String displayName, Material material, double rarity) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.rarity = rarity;
    }

    public void addReward(CaseReward reward) {
        this.rewards.add(reward);
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Material getMaterial() {
        return this.material;
    }

    public double getRarity() {
        return this.rarity;
    }

    public List<CaseReward> getRewards() {
        return new ArrayList<CaseReward>(this.rewards);
    }
}

