package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.circube.qhat.scoreboard.QHatScoreboard;
import org.circube.qhat.scoreboard.QHatScoreboardTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class QHat extends JavaPlugin {
    private QHatScoreboard scoreboard;
    private final Map<UUID, Long> confirmationMap = new HashMap<>();
    private boolean ACTIVATED = false;
    private static final Map<UUID, ItemStack> extraItems = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        this.getCommand("qhat").setExecutor(new Commands(this));
        this.getCommand("qhat").setTabCompleter(new Commands(this));

        scoreboard = new QHatScoreboard(this);
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

    public Map<UUID, Long> getConfirmationMap() {
        return confirmationMap;
    }

    public boolean getStatus() {
        return ACTIVATED;
    }

    public void setStatus(boolean status) {
        ACTIVATED = status;
    }

    public static void addExtraItem(UUID uuid, ItemStack itemStack) {
        extraItems.putIfAbsent(uuid, itemStack);
    }

    public static void removeExtraItem(UUID uuid) {
        extraItems.remove(uuid);
    }

    public static ItemStack getExtraItem(UUID uuid) {
        return extraItems.getOrDefault(uuid, null);
    }
}
