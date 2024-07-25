package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("qhat")) {
            if (Objects.equals(args[0], "start")) {
                removeAllHelmets();
                giveRandomPlayerHelmet(sender);
                return true;
            }
            if (Objects.equals(args[0], "reset")) {
                removeAllHelmets();
                return true;
            }
        }
        return false;
    }

    private void removeAllHelmets() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet != null && helmet.getType() == Material.TURTLE_HELMET) {
                player.getInventory().setHelmet(null);
            }
        }
    }

    private void giveRandomPlayerHelmet(CommandSender sender) {
        List<Player> adventureModePlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                adventureModePlayers.add(player);
            }
        }

        if (!adventureModePlayers.isEmpty()) {
            Random random = new Random();
            Player selectedPlayer = adventureModePlayers.get(random.nextInt(adventureModePlayers.size()));
            selectedPlayer.getInventory().setHelmet(new ItemStack(Material.TURTLE_HELMET));
            Bukkit.broadcastMessage(selectedPlayer + "获得了终极绿帽!");
        } else {
            sender.sendMessage("没有处于冒险模式的玩家!");
        }
    }
}
