package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.circube.qhat.scoreboard.QHatScoreboard;
import org.circube.qhat.scoreboard.QHatScoreboardTask;

public final class QHat extends JavaPlugin {
    private QHatScoreboard scoreboard;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        this.getCommand("qhat").setExecutor(new CommandHandler(this));
        this.getCommand("qhat").setTabCompleter(new CommandHandler(this));

        scoreboard = new QHatScoreboard();
        scoreboard.setupScoreboard();

        new QHatScoreboardTask(scoreboard).runTaskTimer(this, 0, 10);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public QHatScoreboard getScoreboard() {
        return scoreboard;
    }
}
