package org.circube.qhat;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Commands implements CommandExecutor {
    private final QHat plugin;
    private BukkitTask timerTask;
    private BukkitTask rageTask;

    private static final Material[] WOOL_TYPES = {
            Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.MAGENTA_WOOL, Material.LIGHT_BLUE_WOOL,
            Material.YELLOW_WOOL, Material.LIME_WOOL, Material.PINK_WOOL, Material.GRAY_WOOL,
            Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL,
            Material.BROWN_WOOL, Material.GREEN_WOOL, Material.RED_WOOL, Material.BLACK_WOOL
    };

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

            Map<UUID, Long> confirmationMap = plugin.getConfirmationMap();
            UUID uuid = ((Player) sender).getUniqueId();

            if (Objects.equals(args[0], "start")) {
                if (plugin.getStatus()) {
                    sender.sendMessage(ChatColor.RED + "已经开始了");
                    return true;
                } else {
                    initInventory();
                    removeAllHelmets();
                    giveRandomPlayerHelmet(sender);
                    plugin.setStatus(true);
                    stopBGM();
                    playBGM();
                    broadcastTitle("游戏开始", "抓住戴帽子的胖揍他", 15, 45, 30);
                    timerTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.setStatus(false);
                            clearInventory();
                            removeAllHelmets();
                            stopBGM();
                            broadcastTitle("时间到", "先休息一下吧", 15, 45, 30);
                        }
                    }.runTaskLater(plugin, 3630);
                    rageTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            addEffect();
                            broadcastTitle("!狂暴模式!", "", 3, 15, 5);
                        }
                    }.runTaskLater(plugin, 2410);
                    return true;
                }

            }

            if (Objects.equals(args[0], "stop")) {
                if (plugin.getStatus()) {
                    confirmationMap.putIfAbsent(uuid, System.currentTimeMillis());
                    sender.sendMessage("请在15秒内输入 /qhat confirm 来确认停止。");
                    Bukkit.getScheduler().runTaskLater(plugin, () -> confirmationMap.remove(uuid), 300);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "还没有开始");
                    return true;
                }

            }

            if (Objects.equals(args[0], "confirm")) {
                if (confirmationMap.containsKey(uuid)) {
                    stopActivity();
                    sender.sendMessage("结束了游戏");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "当前没有结束任务");
                    return true;
                }

            }
        }
        return false;
    }

    private void removeAllHelmets() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet != null && helmet.getType() == Material.TURTLE_HELMET) {
                player.setGlowing(false);
                player.getInventory().setHelmet(null);
            }
        }
    }

    private void giveRandomPlayerHelmet(CommandSender sender) {
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
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
            itemStack.setItemMeta(itemMeta);
            selectedPlayer.getInventory().setHelmet(itemStack);
            selectedPlayer.setGlowing(true);
            Bukkit.broadcastMessage(ChatColor.YELLOW + selectedPlayer.getDisplayName() + "§f获得了终极绿帽!");
        } else {
            stopActivity();
            sender.sendMessage("没有处于生存模式的玩家!");
        }
    }

    private void stopActivity() {
        clearInventory();
        removeAllHelmets();
        plugin.getScoreboard().resetScore();
        timerTask.cancel();
        rageTask.cancel();
        plugin.setStatus(false);
        stopBGM();
        clearEffect();
    }

    private void initInventory() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                player.getInventory().clear();
                 Inventory inventory = player.getInventory();
                Random random = new Random();

                ItemStack bow = new ItemStack(Material.BOW);
                inventory.addItem(bow);

                ItemStack arrows = new ItemStack(Material.ARROW, 8);
                inventory.addItem(arrows);

                ItemStack enderPearls = new ItemStack(Material.ENDER_PEARL, 2);
                inventory.addItem(enderPearls);

                Material randomWoolType = WOOL_TYPES[random.nextInt(WOOL_TYPES.length)];
                ItemStack woolStack = new ItemStack(randomWoolType, 16);
                inventory.addItem(woolStack);

                ItemStack slimeBlockStack = new ItemStack(Material.SLIME_BLOCK, 2);
                inventory.addItem(slimeBlockStack);
            }
        }
    }

    private void clearInventory() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                player.getInventory().clear();

                for (int i = 0; i < 9; i++) {
                    ItemStack firework = createFancyFirework();
                    player.getInventory().addItem(firework);
                }
            }
        }
    }

    private ItemStack createFancyFirework() {
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) firework.getItemMeta();

        if (meta != null) {
            Random random = new Random();
            FireworkEffect.Builder builder = FireworkEffect.builder();

            builder.with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]);

            builder.withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            builder.withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));

            builder.withFlicker();
            builder.withTrail();

            meta.addEffect(builder.build());

            meta.setPower(random.nextInt(3) + 1);

            firework.setItemMeta(meta);
        }

        return firework;
    }

    private void playBGM() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, "activity:now_or_never", SoundCategory.NEUTRAL, 1f, 1f);
        }
    }

    private void stopBGM() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.stopAllSounds();
        }
    }

    private void broadcastTitle(String title, String subtitle, int fadeIn, int last, int fadeOut) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, fadeIn, last, fadeOut);
        }
    }

    private void addEffect() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1220, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1220, 1));
        }
    }

    private void clearEffect() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.JUMP);
        }
    }
}
