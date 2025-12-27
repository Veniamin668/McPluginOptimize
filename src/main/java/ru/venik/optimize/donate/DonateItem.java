package ru.venik.optimize.donate;

import org.bukkit.Material;

/**
 * Модель донат-предмета.
 * Полностью автономная, без конфигов.
 */
public class DonateItem {

    private final String id;
    private final String displayName;
    private final Material material;
    private final int amount;
    private final double price;

    public DonateItem(String id, String displayName, Material material, int amount, double price) {
        this.id = id.toLowerCase();
        this.displayName = displayName;
        this.material = material;
        this.amount = Math.max(1, amount);
        this.price = Math.max(0, price);
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

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    // ------------------------------------------------------------
    // Удобные фабрики
    // ------------------------------------------------------------

    /** Создать предмет с amount = 1 */
    public static DonateItem of(String id, String name, Material material, double price) {
        return new DonateItem(id, name, material, 1, price);
    }

    /** Создать предмет с кастомным количеством */
    public static DonateItem stack(String id, String name, Material material, int amount, double price) {
        return new DonateItem(id, name, material, amount, price);
    }
}
