package org.circube.qhat.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapHandler {
    private static QHatMap currentMap = null;
    private static final List<QHatMap> MAPS = new ArrayList<>();

    public static void initMaps() {
        QHatMap overpass = new QHatMap("Overpass",
                new Location(Bukkit.getWorld("world"), 192, 88.5, 2, 90, 0),
                new Location(Bukkit.getWorld("world"), 137, 99, 21),
                30,
                12000);
        MAPS.add(overpass);
        QHatMap hanamoura = new QHatMap("Hanamoura",
                new Location(Bukkit.getWorld("world"), 4164, 66.5, 11, 90, 0),
                new Location(Bukkit.getWorld("world"), 4086, 83, 0),
                55,
                1500);
        MAPS.add(hanamoura);
        QHatMap train = new QHatMap("Train",
                new Location(Bukkit.getWorld("world"), -4099, 81.5, 1, 90, 0),
                new Location(Bukkit.getWorld("world"), -4140, 92, -40),
                30,
                9000);
        MAPS.add(train);
        QHatMap summit = new QHatMap("Summit",
                new Location(Bukkit.getWorld("world"), -173, 121.5, -4094, -90, 0),
                new Location(Bukkit.getWorld("world"), -78, 133, -4104),
                95,
                13000,
                true);
        MAPS.add(summit);
        QHatMap emerald = new QHatMap("Emerald",
                new Location(Bukkit.getWorld("world"), 215, 81.5, 4255, -180, 0),
                new Location(Bukkit.getWorld("world"), 198, 100, 4154),
                30,
                11000,
                true);
        MAPS.add(emerald);
        currentMap = overpass;
    }

    public static QHatMap getCurrentMap() {
        return currentMap;
    }

    public static void getRandomMap() {
        List<QHatMap> OTHER_MAPS = new ArrayList<>(MAPS);
        OTHER_MAPS.remove(currentMap);
        Collections.shuffle(OTHER_MAPS);
        currentMap = OTHER_MAPS.get(0);
    }

    public static Location getCurrentSpawnLocation() {
        return currentMap.getSpawnLocation();
    }

    public static Location getCurrentCenterLocation() {
        return currentMap.getCenterLocation();
    }

    public static void changeMap() {
        getRandomMap();
        World world = currentMap.getCenterLocation().getWorld();
        world.setTime(currentMap.getMapTime());
        if (currentMap.isRainy()) {
            world.setStorm(true);
            world.setWeatherDuration(5950);
        }
        else {
            world.setStorm(false);
            world.setClearWeatherDuration(5950);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(currentMap.getSpawnLocation());
        }
    }
}
