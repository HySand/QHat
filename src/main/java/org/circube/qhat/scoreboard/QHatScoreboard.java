package org.circube.qhat.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.circube.qhat.QHat;

public class QHatScoreboard {
    private final QHat plugin;
    private Scoreboard scoreboard;
    private Objective objective;

    public QHatScoreboard(QHat plugin) {
        this.plugin = plugin;
    }

    public void setupScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("qHatScore", "dummy", ChatColor.GREEN + "帽子王得分");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void addScore(Player player) {
        objective.getScore(player.getName()).setScore(objective.getScore(player.getName()).getScore() + 1);
    }

    public void applyToPlayer(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void resetScore(Player player) {
        objective.getScore(player.getName()).setScore(0);
    }
}
