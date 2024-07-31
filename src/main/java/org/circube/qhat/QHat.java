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
    private final Map<UUID, Long> confirmationMap = new HashMap<>();
    private boolean ACTIVATED = false;
    private final Map<UUID, ItemStack> extraItems = new HashMap<>();
    private final Set<UUID> selectedUUID = new HashSet<>();

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

    public Map<UUID, Long> getConfirmationMap() {
        return confirmationMap;
    }

    public boolean getStatus() {
        return ACTIVATED;
    }

    public void setStatus(boolean status) {
        ACTIVATED = status;
    }

    public void addExtraItem(UUID uuid, ItemStack itemStack) {
        extraItems.putIfAbsent(uuid, itemStack);
    }

    public void removeExtraItem(UUID uuid) {
        extraItems.remove(uuid);
    }

    public ItemStack getExtraItem(UUID uuid) {
        return extraItems.getOrDefault(uuid, null);
    }

    public void addAsSelected(UUID uuid) {
        selectedUUID.add(uuid);
    }

    public boolean isSelected(UUID uuid) {
        return selectedUUID.contains(uuid);
    }

    public void clearSelected() {
        selectedUUID.clear();
    }

    public CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 10) {
            return null;
        }

        return CoreProtect;
    }
}
