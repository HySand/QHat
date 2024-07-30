package org.circube.qhat;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Commands implements CommandExecutor, TabCompleter {
    private final QHat plugin;
    private BukkitTask activityTask;
    private BukkitTask endTask;
    private BukkitTask timerTask;
    private BukkitTask rageTask;
    private BukkitTask abilityTask;
    private final List<String> ABILITIES = Arrays.asList(
            "增加3点生命值",
            "增加7%移速",
            "增加12%击退",
            "增加1.5攻击力",
            "下回合获得火焰弹",
            "下回合获得16支箭",
            "下回合获得4颗末影珍珠",
            "下回合获得剪刀",
            "下回合获得蜘蛛网"
    );
    List<String> selectedAttributes = new ArrayList<>();

    private static final Material[] WOOL_TYPES = {
            Material.ORANGE_WOOL, Material.MAGENTA_WOOL, Material.LIGHT_BLUE_WOOL,
            Material.YELLOW_WOOL, Material.LIME_WOOL, Material.PINK_WOOL, Material.GRAY_WOOL,
            Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL,
            Material.BROWN_WOOL, Material.GREEN_WOOL, Material.RED_WOOL
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

            if (Objects.equals(args[0].toLowerCase(), "start")) {
                if (plugin.getStatus()) {
                    sender.sendMessage(ChatColor.RED + "已经开始了。");
                    return true;
                } else {
                    startActivity();
                    return true;
                }

            }

            if (Objects.equals(args[0].toLowerCase(), "reset")) {
                confirmationMap.putIfAbsent(uuid, System.currentTimeMillis());
                sender.sendMessage("请在15秒内输入/qhat confirm来确认重置。");
                Bukkit.getScheduler().runTaskLater(plugin, () -> confirmationMap.remove(uuid), 300);
                return true;
            }

            if (Objects.equals(args[0], "confirm")) {
                if (confirmationMap.containsKey(uuid)) {
                    stopActivity();
                    sender.sendMessage("重置了游戏");
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
            }
            return autoCompletes;
        }
        return null;
    }

    private void startActivity() {
        cancelTasks();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, "activity:opening", SoundCategory.RECORDS,1f, 1f);
        }
        startTasks();
        plugin.setStatus(true);
    }

    private void stopActivity() {
        cancelTasks();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
            clearInventory(player);
            removeHelmets(player);
            plugin.getScoreboard().resetScore(player);
            resetAttributes(player);
            player.stopAllSounds();
            clearEffect(player);
        }
        plugin.setStatus(false);
    }

    private void startTasks() {
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
                plugin.setStatus(false);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    clearInventory(player);
                    removeHelmets(player);
                    player.stopAllSounds();
                    selectedAttributes = getRandomAttributes();
                    player.playSound(player, "activity:turf_master", SoundCategory.RECORDS, 1f, 1f);
                    player.sendTitle(ChatColor.GREEN + "时间到", ChatColor.GREEN + "先休息一下吧", 15, 45, 30);
                }
            }
        }.runTaskLater(plugin, 4020);
        timerTask = new BukkitRunnable() {
            int timeLeft = 201;

            @Override
            public void run() {
                if (timeLeft < 0) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (timeLeft == 200) player.sendTitle(ChatColor.GREEN + "游戏即将开始", ChatColor.GREEN + "请做好准备", 15, 300, 0);
                    if (timeLeft <= 185 && timeLeft > 180) player.sendTitle(ChatColor.GREEN + "游戏即将开始", ChatColor.GREEN + "" + ChatColor.BOLD + (timeLeft - 181), 0, 25, 0);
                    if (timeLeft <= 180) player.setLevel(timeLeft);
                }
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0, 20);

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
                    if (plugin.isSelected(player.getUniqueId()) || player.getOpenInventory().getTitle().equals("选择一项加成")) {
                        continue;
                    }
                    openAbilityGUI(player);
                }

            }
        }.runTaskTimer(plugin, 4250, 10);
    }

    private void cancelTasks() {
        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
        }
        if (activityTask != null && !activityTask.isCancelled()) {
            activityTask.cancel();
            plugin.clearSelected();
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

    private void removeHelmets(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && helmet.getType() == Material.TURTLE_HELMET) {
            player.setGlowing(false);
            player.getInventory().setHelmet(null);
        }
    }

    private void giveRandomPlayerHelmet() {
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
            itemStack.getItemMeta().setDisplayName("终极绿帽");
            selectedPlayer.getInventory().setHelmet(itemStack);
            selectedPlayer.setGlowing(true);
            Bukkit.broadcastMessage(ChatColor.YELLOW + selectedPlayer.getDisplayName() + ChatColor.WHITE + "获得了终极绿帽!");
        } else {
            stopActivity();
        }
    }

    private void initInventory(Player player) {
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
            ItemStack woolStack = new ItemStack(randomWoolType, 16);
            inventory.addItem(woolStack);

            ItemStack arrows = new ItemStack(Material.ARROW, 8);
            inventory.addItem(arrows);

            UUID uuid = player.getUniqueId();

            if (plugin.getExtraItem(uuid) != null) {
                inventory.addItem(plugin.getExtraItem(uuid));
                plugin.removeExtraItem(uuid);
            }
        }


    }

    private void clearInventory(Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.getInventory().clear();
            player.setGlowing(false);
        }
    }

    private void addEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1220, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1220, 1));
    }

    private void clearEffect(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    public void resetAttributes(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.setBaseValue(0.1);
        }

        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.setBaseValue(20.0);
            player.setHealth(20.0);
        }

        AttributeInstance knockbackAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
        if (knockbackAttribute != null) {
            knockbackAttribute.setBaseValue(0.0);
        }

        AttributeInstance attackAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attackAttribute != null) {
            attackAttribute.setBaseValue(1.0);
        }

        plugin.removeExtraItem(player.getUniqueId());
    }

    public void openAbilityGUI(Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.RECORDS, 1f, 1f);
            Inventory gui = Bukkit.createInventory(null, 27, "选择一项加成");

            for (int i = 0; i < selectedAttributes.size(); i++) {
                String attribute = selectedAttributes.get(i);
                ItemStack button = createAttributeButton(attribute);
                gui.setItem(11 + i * 2, button);
            }

            player.openInventory(gui);
        }

    }

    private List<String> getRandomAttributes() {
        Collections.shuffle(ABILITIES);
        return ABILITIES.subList(0, 3);
    }

    private ItemStack createAttributeButton(String attributeName) {
        Material material;
        String displayName;
        String flag;
        switch (attributeName) {
            case "增加3点生命值":
                material = Material.APPLE;
                displayName = ChatColor.RED + attributeName;
                flag = "health";
                break;
            case "增加7%移速":
                material = Material.FEATHER;
                displayName = ChatColor.GREEN + attributeName;
                flag = "speed";
                break;
            case "增加12%击退":
                material = Material.STICK;
                displayName = ChatColor.AQUA + attributeName;
                flag = "knockback";
                break;
            case "增加1.5攻击力":
                material = Material.IRON_AXE;
                displayName = ChatColor.YELLOW + attributeName;
                flag = "damage";
                break;
            case "下回合获得火焰弹":
                material = Material.FIRE_CHARGE;
                displayName = ChatColor.DARK_RED + attributeName;
                flag = "fire_charge";
                break;
            case "下回合获得16支箭":
                material = Material.ARROW;
                displayName = ChatColor.DARK_GREEN + attributeName;
                flag = "arrow";
                break;
            case "下回合获得4颗末影珍珠":
                material = Material.ENDER_PEARL;
                displayName = ChatColor.DARK_PURPLE + attributeName;
                flag = "end_pearl";
                break;
            case "下回合获得剪刀":
                material = Material.SHEARS;
                displayName = ChatColor.GOLD + attributeName;
                flag = "shear";
                break;
            case "下回合获得蜘蛛网":
                material = Material.COBWEB;
                displayName = ChatColor.GRAY + attributeName;
                flag = "cobweb";
                break;
            default:
                material = Material.BARRIER;
                displayName = ChatColor.WHITE + "未知属性";
                flag = "unknown";
        }
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "button");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, flag);
        meta.setDisplayName(displayName);
        button.setItemMeta(meta);
        return button;
    }
}
