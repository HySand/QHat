package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class MapHandler {
    private static String currentMap;
    private static final List<Location> OVERPASS_LOCATION = Arrays.asList(
            new Location(Bukkit.getWorld("world"), 192, 88.5, 2, 90, 0),
            new Location(Bukkit.getWorld("world"), 137, 99, 21)
    );
    private static final List<Location> HANAMOURA_LOCATION = Arrays.asList(
            new Location(Bukkit.getWorld("world"), 4146, 66.5, 11, 90, 0),
            new Location(Bukkit.getWorld("world"), 4086, 83, 0)
    );
    private static final List<Location> ANUBIS_LOCATION = Arrays.asList(
            new Location(Bukkit.getWorld("world"), -4099, 81.5, 1, 90, 0),
            new Location(Bukkit.getWorld("world"), -4140, 92, -40)
    );
    private static final List<String> MAPS = Arrays.asList(
            "overpass",
            "hanamoura",
            "train"
    );
    private static final Map<String, List<Location>> MAP_DATA = new HashMap<>() {
        {
            put("overpass", OVERPASS_LOCATION);
            put("hanamoura", HANAMOURA_LOCATION);
            put("train", ANUBIS_LOCATION);
        }
    };

    public static void getRandomMap() {
        Collections.shuffle(MAPS);
        currentMap = MAPS.get(0);
    }

    public static String getCurrentMap() {
        return currentMap;
    }

    public static Location getCurrentSpawnLocation() {
        return MAP_DATA.get(currentMap).get(0);
    }

    public static Location getCurrentCenterLocation() {
        return MAP_DATA.get(currentMap).get(0);
    }
}
