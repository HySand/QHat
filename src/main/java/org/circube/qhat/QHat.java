package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class QHat extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        this.getCommand("qhat").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
