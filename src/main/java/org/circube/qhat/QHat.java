package org.circube.qhat;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.circube.qhat.scoreboard.QHatScoreboard;
import org.circube.qhat.scoreboard.QHatScoreboardTask;

import java.util.*;

public final class QHat extends JavaPlugin {
    private QHatScoreboard scoreboard;
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        this.getCommand("qhat").setExecutor(new Commands(this));
        this.getCommand("qhat").setTabCompleter(new Commands(this));

        scoreboard = new QHatScoreboard();
        scoreboard.setupScoreboard();

        new QHatScoreboardTask(this, scoreboard).runTaskTimer(this, 0, 10);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public QHatScoreboard getScoreboard() {
        return scoreboard;
    }
}
