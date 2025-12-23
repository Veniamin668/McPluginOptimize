/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.menu;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class Menu {
    private final String id;
    private final String title;
    private final int size;
    private final Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
    private final Map<Integer, String> actions = new HashMap<Integer, String>();

    public Menu(String id, String title, int size) {
        this.id = id;
        this.title = title;
        this.size = size;
    }

    public void addItem(int slot, ItemStack item) {
        this.items.put(slot, item);
    }

    public void addItem(int slot, ItemStack item, String action) {
        this.items.put(slot, item);
        this.actions.put(slot, action);
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public int getSize() {
        return this.size;
    }

    public Map<Integer, ItemStack> getItems() {
        return new HashMap<Integer, ItemStack>(this.items);
    }

    public String getAction(int slot) {
        return this.actions.get(slot);
    }
}

