package ru.venik.optimize.kit;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Модель набора (кита).
 * Полностью неизменяемая, безопасная и готовая к использованию в KitManager.
 */
public class Kit {

    private final String id;
    private final String displayName;
    private final long cooldown;
    private final List<ItemStack> items;

    public Kit(String id, String displayName, long cooldown, List<ItemStack> items) {
        this.id = id.toLowerCase();
        this.displayName = displayName;
        this.cooldown = Math.max(0, cooldown);

        // Глубокая копия предметов
        List<ItemStack> copy = new ArrayList<>();
        if (items != null) {
            for (ItemStack item : items) {
                if (item != null) {
                    copy.add(item.clone());
                }
            }
        }
        this.items = Collections.unmodifiableList(copy);
    }

    // ------------------------------------------------------------
    // Фабрики
    // ------------------------------------------------------------

    public static Kit empty(String id, String name, long cooldown) {
        return new Kit(id, name, cooldown, new ArrayList<>());
    }

    public static Kit of(String id, String name, long cooldown, List<ItemStack> items) {
        return new Kit(id, name, cooldown, items);
    }

    // ------------------------------------------------------------
    // Геттеры
    // ------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getCooldown() {
        return cooldown;
    }

    public List<ItemStack> getItems() {
        // Возвращаем копию, чтобы никто не мог изменить оригинал
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack item : items) {
            copy.add(item.clone());
        }
        return copy;
    }

    // ------------------------------------------------------------
    // equals / hashCode / toString
    // ------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kit)) return false;
        Kit kit = (Kit) o;
        return id.equals(kit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Kit{id='" + id + "', items=" + items.size() + "}";
    }
}
