package ru.venik.optimize.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Warp {

    private final String name;
    private final Location location;

    public Warp(String name, Location location) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Warp name cannot be null or empty");

        if (location == null)
            throw new IllegalArgumentException("Warp location cannot be null");

        this.name = name;
        this.location = location.clone();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location.clone();
    }

    // ------------------------------------------------------------
    // SERIALIZATION SUPPORT
    // ------------------------------------------------------------

    public String serialize() {
        return name + ";" +
                location.getWorld().getName() + ";" +
                location.getX() + ";" +
                location.getY() + ";" +
                location.getZ() + ";" +
                location.getYaw() + ";" +
                location.getPitch();
    }

    public static Warp deserialize(String data) {
        String[] parts = data.split(";");
        if (parts.length < 7) return null;

        String name = parts[0];
        World world = Bukkit.getWorld(parts[1]);
        if (world == null) return null;

        double x = Double.parseDouble(parts[2]);
        double y = Double.parseDouble(parts[3]);
        double z = Double.parseDouble(parts[4]);
        float yaw = Float.parseFloat(parts[5]);
        float pitch = Float.parseFloat(parts[6]);

        return new Warp(name, new Location(world, x, y, z, yaw, pitch));
    }

    @Override
    public String toString() {
        return "Warp{name='" + name + "', location=" + location + "}";
    }
}
