package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class QHat extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
