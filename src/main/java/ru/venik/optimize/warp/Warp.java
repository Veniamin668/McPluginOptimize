/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.warp;

import org.bukkit.Location;

public class Warp {
    private final String name;
    private final Location location;

    public Warp(String name, Location location) {
        this.name = name;
        this.location = location.clone();
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.location.clone();
    }
}

