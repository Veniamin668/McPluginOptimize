package ru.venik.optimize.cases;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Представляет кейс с наградами.
 */
public class Case {

    private final String id;
    private final String displayName;
    private final Material material;
    private final double rarity;

    // Потокобезопасность не нужна — менеджер кейсов работает в основном потоке
    private final List<CaseReward> rewards = new ArrayList<>();

    public Case(String id, String displayName, Material material, double rarity) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.rarity = rarity;
    }

    /**
     * Добавить награду в кейс.
     */
    public void addReward(CaseReward reward) {
        if (reward != null) {
            rewards.add(reward);
        }
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public double getRarity() {
        return rarity;
    }

    /**
     * Возвращает копию списка наград, чтобы нельзя было изменить оригинал.
     */
    public List<CaseReward> getRewards() {
        return Collections.unmodifiableList(new ArrayList<>(rewards));
    }
}
