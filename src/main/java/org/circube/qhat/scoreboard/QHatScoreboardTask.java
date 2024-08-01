package org.circube.qhat.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class QHatScoreboardTask extends BukkitRunnable {
    private final QHatScoreboard scoreboard;

    public QHatScoreboardTask(QHatScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                ItemStack helmet = player.getInventory().getHelmet();
                if (helmet != null && helmet.getType() == Material.TURTLE_HELMET) {
                    scoreboard.addScore(player);
                }
                scoreboard.applyToPlayer(player);
            }
        }
    }
}
