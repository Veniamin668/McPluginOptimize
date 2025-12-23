/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.donate;

import org.bukkit.Material;

public class DonateItem {
    private final String id;
    private final String displayName;
    private final Material material;
    private final int defaultAmount;
    private final double price;

    public DonateItem(String id, String displayName, Material material, int defaultAmount, double price) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.defaultAmount = defaultAmount;
        this.price = price;
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

    public int getDefaultAmount() {
        return this.defaultAmount;
    }

    public double getPrice() {
        return this.price;
    }
}

