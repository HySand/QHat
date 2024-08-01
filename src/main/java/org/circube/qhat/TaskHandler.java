package org.circube.qhat;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.circube.qhat.AbilityHandler.*;
import static org.circube.qhat.CoreProtectHandler.rollBackPlacedBlocks;
import static org.circube.qhat.MapHandler.*;

public class TaskHandler {
    private static final QHat plugin = QHat.getPlugin(QHat.class);
    private static BukkitTask activityTask;
    private static BukkitTask endTask;
    private static BukkitTask timerTask;
    private static BukkitTask rageTask;
    private static BukkitTask abilityTask;
    private static BukkitTask nextRoundTask;
    private static boolean ACTIVATED = false;
    private static final Material[] WOOL_TYPES = {
            Material.ORANGE_WOOL, Material.MAGENTA_WOOL, Material.LIGHT_BLUE_WOOL,
            Material.YELLOW_WOOL, Material.LIME_WOOL, Material.PINK_WOOL, Material.GRAY_WOOL,
            Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL,
            Material.BROWN_WOOL, Material.GREEN_WOOL, Material.RED_WOOL
    };

    public static void startActivity() {
        cancelAutoRun();
        nextRoundTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) {
                    stopActivity();
                    return;
                }
                cancelTasks();

                getRandomMap();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(getCurrentSpawnLocation());
                    player.stopAllSounds();
                    player.playSound(player, "activity:opening", SoundCategory.RECORDS, 1f, 1f);
                }
                startTasks();
                setStatus(true);
            }
        }.runTaskTimer(plugin, 0, 6000);
    }

    public static void stopActivity() {
        cancelAutoRun();
        cancelTasks();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
            clearInventory(player);
            removeHelmets(player);
            player.stopAllSounds();
            clearEffect(player);
        }
        setStatus(false);
    }

    public static void clearPersistedData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            resetAttributes(player);
            plugin.getScoreboard().resetScore(player);
        }
    }

    private static void startTasks() {
        timerTask = new BukkitRunnable() {
            int timeLeft = 201;

            @Override
            public void run() {
                if (timeLeft < 0) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (timeLeft == 201)
                        player.sendTitle(ChatColor.GREEN + "游戏即将开始", "", 25, 300, 0);
                    if (timeLeft <= 186 && timeLeft > 181)
                        player.sendTitle(ChatColor.GREEN + "游戏即将开始", ChatColor.GREEN + "" + ChatColor.BOLD + (timeLeft - 181), 0, 25, 0);
                    if (timeLeft <= 180) player.setLevel(timeLeft);
                }
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0, 20);

        activityTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    initInventory(player);
                    removeHelmets(player);
                    player.stopAllSounds();
                    player.playSound(player, "activity:now_or_never", SoundCategory.RECORDS, 1f, 1f);
                    player.closeInventory();
                    player.sendTitle(ChatColor.GREEN + "游戏开始", ChatColor.GREEN + "抓住戴帽子的胖揍他", 0, 45, 20);
                }
                giveRandomPlayerHelmet();
            }
        }.runTaskLater(plugin, 400);

        endTask = new BukkitRunnable() {
            @Override
            public void run() {
                setStatus(false);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    clearInventory(player);
                    removeHelmets(player);
                    player.stopAllSounds();
                    player.playSound(player, "activity:turf_master", SoundCategory.RECORDS, 1f, 1f);
                    getRandomAttributes();
                    getRandomTools();
                    player.sendTitle(ChatColor.GREEN + "时间到", ChatColor.GREEN + "先休息一下吧", 15, 45, 30);
                }
                rollBackPlacedBlocks(getCurrentCenterLocation());
            }
        }.runTaskLater(plugin, 4020);

        rageTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    addEffect(player);
                    player.sendTitle(ChatColor.RED + "⚠狂暴模式⚠", "", 3, 15, 5);
                }

            }
        }.runTaskLater(plugin, 2810);

        abilityTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isSelected(player.getUniqueId()) || player.getOpenInventory().getTitle().equals("选择一项加成")) {
                        continue;
                    }
                    openAbilityGUI(player);
                }

            }
        }.runTaskTimer(plugin, 4250, 10);


    }

    private static void cancelTasks() {
        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
        }
        if (activityTask != null && !activityTask.isCancelled()) {
            activityTask.cancel();
            clearSelected();
        }
        if (rageTask != null && !rageTask.isCancelled()) {
            rageTask.cancel();
        }
        if (endTask != null && !endTask.isCancelled()) {
            endTask.cancel();
        }
        if (abilityTask != null && !abilityTask.isCancelled()) {
            abilityTask.cancel();
        }
    }

    private static void cancelAutoRun() {
        if (nextRoundTask != null && !nextRoundTask.isCancelled()) {
            nextRoundTask.cancel();
        }
    }

    private static void removeHelmets(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && helmet.getType() == Material.TURTLE_HELMET) {
            player.setGlowing(false);
            player.getInventory().setHelmet(null);
        }
    }

    private static void giveRandomPlayerHelmet() {
        List<Player> survivalModePlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                survivalModePlayers.add(player);
            }
        }

        if (!survivalModePlayers.isEmpty()) {
            Random random = new Random();
            Player selectedPlayer = survivalModePlayers.get(random.nextInt(survivalModePlayers.size()));

            ItemStack itemStack = new ItemStack(Material.TURTLE_HELMET);
            itemStack.addEnchantment(Enchantment.BINDING_CURSE, 1);
            selectedPlayer.getInventory().setHelmet(itemStack);
            selectedPlayer.setGlowing(true);
            Bukkit.broadcastMessage(ChatColor.YELLOW + selectedPlayer.getDisplayName() + ChatColor.WHITE + "获得了终极绿帽!");
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "由于没有生存模式的玩家，游戏中止。");
            stopActivity();
        }
    }

    private static void initInventory(Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.setHealth(player.getMaxHealth());
            player.getInventory().clear();
            Inventory inventory = player.getInventory();
            Random random = new Random();

            ItemStack bow = new ItemStack(Material.BOW);
            inventory.addItem(bow);

            ItemStack enderPearls = new ItemStack(Material.ENDER_PEARL, 2);
            inventory.addItem(enderPearls);

            Material randomWoolType = WOOL_TYPES[random.nextInt(WOOL_TYPES.length)];
            ItemStack woolStack = new ItemStack(randomWoolType, 24);
            inventory.addItem(woolStack);

            UUID uuid = player.getUniqueId();

            if (getExtraItem(uuid) != null) {
                inventory.addItem(getExtraItem(uuid));
                removeExtraItem(uuid);
            }

            ItemStack arrows = new ItemStack(Material.ARROW, 8);
            inventory.addItem(arrows);
        }


    }

    private static void clearInventory(Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.getInventory().clear();
            player.setGlowing(false);
        }
    }

    private static void addEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1220, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1220, 1));
    }

    private static void clearEffect(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    public static void setStatus(boolean status) {
        ACTIVATED = status;
    }

    public static boolean getStatus() {
        return ACTIVATED;
    }
}
