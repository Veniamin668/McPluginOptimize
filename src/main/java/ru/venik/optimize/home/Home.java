package ru.venik.optimize.home;

import org.bukkit.Location;
import java.util.Objects;

/**
 * Модель дома игрока.
 * Полностью неизменяемая и безопасная.
 */
public class Home {

    private final String name;
    private final Location location;

    public Home(String name, Location location) {
        this.name = name.toLowerCase();
        this.location = location.clone();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location.clone();
    }

    // ------------------------------------------------------------
    // Удобные фабрики
    // ------------------------------------------------------------

    public static Home of(String name, Location location) {
        return new Home(name, location);
    }

    // ------------------------------------------------------------
    // equals / hashCode / toString
    // ------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Home)) return false;
        Home home = (Home) o;
        return name.equals(home.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Home{name='" + name + "', location=" + location + "}";
    }
}
