package org.circube.qhat.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.circube.qhat.QHat;

public class QHatScoreboardTask extends BukkitRunnable {
    private final QHat plugin;
    private final QHatScoreboard scoreboard;

    public QHatScoreboardTask(QHat plugin, QHatScoreboard scoreboard) {
        this.plugin = plugin;
        this.scoreboard = scoreboard;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                ItemStack helmet = player.getInventory().getHelmet();
                if (helmet != null && helmet.getType() == Material.TURTLE_HELMET) {
                    scoreboard.addScore(player);
                }
                scoreboard.applyToPlayer(player);
            }
        }
    }
}
