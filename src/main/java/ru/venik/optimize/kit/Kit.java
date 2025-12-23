/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.kit;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class Kit {
    private final String id;
    private final String displayName;
    private final long cooldown;
    private final List<ItemStack> items = new ArrayList<ItemStack>();

    public Kit(String id, String displayName, long cooldown) {
        this.id = id;
        this.displayName = displayName;
        this.cooldown = cooldown;
    }

    public void addItem(ItemStack item) {
        this.items.add(item);
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public List<ItemStack> getItems() {
        return new ArrayList<ItemStack>(this.items);
    }
}

