package org.circube.qhat.map;

import org.bukkit.Location;

public class QHatMap {
    private final String name;
    private final Location center;
    private final Location spawn;
    private final double ground;
    private final long time;
    private final boolean rainy;

    public QHatMap(String name, Location spawn, Location center, double ground, long time) {
        this.name = name;
        this.spawn = spawn;
        this.center = center;
        this.ground = ground;
        this.time = time;
        this.rainy = false;
    }

    public QHatMap(String name, Location spawn, Location center, double ground, long time, boolean rainy) {
        this.name = name;
        this.spawn = spawn;
        this.center = center;
        this.ground = ground;
        this.time = time;
        this.rainy = rainy;
    }

    public String getName() {
        return name;
    }

    public Location getSpawnLocation() {
        return spawn;
    }

    public Location getCenterLocation() {
        return center;
    }

    public long getMapTime() {
        return time;
    }

    public boolean isRainy() {
        return rainy;
    }

    public boolean isOutOfMap(double y) {
        return y < ground;
    }
}
