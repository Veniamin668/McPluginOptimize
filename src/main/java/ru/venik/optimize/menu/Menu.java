package ru.venik.optimize.menu;

import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Модель GUI-меню.
 * Полностью безопасная, неизменяемая и готовая к MenuManager.
 */
public class Menu {

    private final String id;
    private final String title;
    private final int size;

    private final Map<Integer, ItemStack> items;
    private final Map<Integer, String> actions;

    public Menu(String id, String title, int size,
                Map<Integer, ItemStack> items,
                Map<Integer, String> actions) {

        this.id = id.toLowerCase();
        this.title = title;
        this.size = size;

        // Глубокая копия предметов
        Map<Integer, ItemStack> itemCopy = new HashMap<>();
        if (items != null) {
            for (Map.Entry<Integer, ItemStack> e : items.entrySet()) {
                if (e.getValue() != null) {
                    itemCopy.put(e.getKey(), e.getValue().clone());
                }
            }
        }
        this.items = Collections.unmodifiableMap(itemCopy);

        // Копия действий
        this.actions = actions == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(actions));
    }

    // ------------------------------------------------------------
    // Фабрики
    // ------------------------------------------------------------

    public static Menu empty(String id, String title, int size) {
        return new Menu(id, title, size, new HashMap<>(), new HashMap<>());
    }

    public static Builder builder(String id, String title, int size) {
        return new Builder(id, title, size);
    }

    // ------------------------------------------------------------
    // Геттеры
    // ------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, ItemStack> getItems() {
        // Возвращаем копию, чтобы никто не мог изменить оригинал
        Map<Integer, ItemStack> copy = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> e : items.entrySet()) {
            copy.put(e.getKey(), e.getValue().clone());
        }
        return copy;
    }

    public String getAction(int slot) {
        return actions.get(slot);
    }

    public boolean hasAction(int slot) {
        return actions.containsKey(slot);
    }

    // ------------------------------------------------------------
    // Builder — удобное создание меню
    // ------------------------------------------------------------

    public static class Builder {

        private final String id;
        private final String title;
        private final int size;

        private final Map<Integer, ItemStack> items = new HashMap<>();
        private final Map<Integer, String> actions = new HashMap<>();

        public Builder(String id, String title, int size) {
            this.id = id;
            this.title = title;
            this.size = size;
        }

        public Builder item(int slot, ItemStack item) {
            items.put(slot, item.clone());
            return this;
        }

        public Builder item(int slot, ItemStack item, String action) {
            items.put(slot, item.clone());
            actions.put(slot, action);
            return this;
        }

        public Menu build() {
            return new Menu(id, title, size, items, actions);
        }
    }

    // ------------------------------------------------------------
    // equals / hashCode / toString
    // ------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Menu)) return false;
        Menu menu = (Menu) o;
        return id.equals(menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Menu{id='" + id + "', size=" + size + "}";
    }
}
