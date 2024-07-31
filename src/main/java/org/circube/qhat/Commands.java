package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

import static org.circube.qhat.TaskHandler.*;

public class Commands implements CommandExecutor, TabCompleter {
    private final Map<UUID, Long> confirmationMap = new HashMap<>();
    private final QHat plugin;

    public Commands(QHat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("qhat")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("只有玩家可以执行此命令。");
                return true;
            }

            UUID uuid = ((Player) sender).getUniqueId();

            if (Objects.equals(args[0].toLowerCase(), "start")) {
                if (getStatus()) {
                    sender.sendMessage(ChatColor.RED + "已经开始了。");
                    return true;
                } else {
                    startActivity();
                    return true;
                }

            }

            if (Objects.equals(args[0].toLowerCase(), "reset")) {
                confirmationMap.putIfAbsent(uuid, System.currentTimeMillis());
                sender.sendMessage("请在15秒内输入/qhat confirm来确认重置。如果你不想重置数据应该使用/qhat pause。");
                Bukkit.getScheduler().runTaskLater(plugin, () -> confirmationMap.remove(uuid), 300);
                return true;
            }

            if (Objects.equals(args[0].toLowerCase(), "pause")) {
                stopActivity();
                Bukkit.broadcastMessage(ChatColor.YELLOW + "管理员中止了游戏。");
                return true;
            }

            if (Objects.equals(args[0], "confirm")) {
                if (confirmationMap.containsKey(uuid)) {
                    clearPersistedData();
                    stopActivity();
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "管理员重置了游戏。");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "当前没有重置指令，请先输入/qhat reset。");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("qhat")) {
            List<String> autoCompletes = new ArrayList<>();
            if (args.length == 1) {
                autoCompletes.add("start");
                autoCompletes.add("reset");
                autoCompletes.add("confirm");
                autoCompletes.add("pause");
            }
            return autoCompletes;
        }
        return null;
    }
}
