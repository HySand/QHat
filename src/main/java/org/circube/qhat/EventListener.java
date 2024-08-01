package org.circube.qhat;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.circube.qhat.AbilityHandler.addAsSelected;
import static org.circube.qhat.AbilityHandler.addExtraItem;
import static org.circube.qhat.map.MapHandler.getCurrentMap;
import static org.circube.qhat.map.MapHandler.getCurrentSpawnLocation;

public class EventListener implements Listener {
    private final QHat plugin;
    private final Set<UUID> invinciblePlayers = new HashSet<>();

    public EventListener(QHat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if ((event.getEntity() instanceof Player victim
                && event.getDamager() instanceof Player attacker)) {
            if (invinciblePlayers.contains(victim.getUniqueId())) {
                event.setCancelled(true);
                return;
            }
            ItemStack helmet = victim.getInventory().getHelmet();
            switchHelmet(victim, attacker, helmet);
            if (attacker.getInventory().getHelmet() == null && victim.getInventory().getHelmet() == null) {
                event.setCancelled(true);
            }
        }
        if (event.getEntity() instanceof Player victim
                && event.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof Player attacker
                && victim.getHealth() - event.getFinalDamage() <= 0) {
            ItemStack helmet = victim.getInventory().getHelmet();
            switchHelmet(victim, attacker, helmet);
            if (attacker.getInventory().getHelmet() == null && victim.getInventory().getHelmet() == null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();
        if (player.getGameMode() == GameMode.SURVIVAL) {
            if (!(blockType == Material.ORANGE_WOOL ||
                    blockType == Material.MAGENTA_WOOL ||
                    blockType == Material.LIGHT_BLUE_WOOL ||
                    blockType == Material.YELLOW_WOOL ||
                    blockType == Material.LIME_WOOL ||
                    blockType == Material.PINK_WOOL ||
                    blockType == Material.GRAY_WOOL ||
                    blockType == Material.LIGHT_GRAY_WOOL ||
                    blockType == Material.CYAN_WOOL ||
                    blockType == Material.PURPLE_WOOL ||
                    blockType == Material.BLUE_WOOL ||
                    blockType == Material.BROWN_WOOL ||
                    blockType == Material.GREEN_WOOL ||
                    blockType == Material.RED_WOOL ||
                    blockType == Material.COBWEB)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDead(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getHealth() - event.getFinalDamage() <= 0) {
                respawnPlayer(player);
            }
        }
    }

    @EventHandler
    public void disableFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (getCurrentMap().isOutOfMap(event.getTo().getY())) {
            ItemStack helmet = player.getInventory().getHelmet();
            switchHelmet(player, helmet);
            respawnPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();

        switchHelmet(player, helmet);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("选择一项加成")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() != null) {
                String itemFlag = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "button"), PersistentDataType.STRING);
                switch (itemFlag) {
                    case "health":
                        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        if (healthAttribute != null) {
                            healthAttribute.setBaseValue(healthAttribute.getBaseValue() + 3.0);
                        }
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "已增加3点生命值！");
                        break;
                    case "speed":
                        AttributeInstance speedAttribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                        if (speedAttribute != null) {
                            speedAttribute.setBaseValue(speedAttribute.getBaseValue() * 1.07);
                        }
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "已增加7%移速！");
                        break;
                    case "knockback":
                        AttributeInstance knockbackAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
                        if (knockbackAttribute != null) {
                            knockbackAttribute.setBaseValue(knockbackAttribute.getBaseValue() + 0.12);
                        }
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "已增加12%击退！");
                        break;
                    case "damage":
                        AttributeInstance attackAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                        if (attackAttribute != null) {
                            attackAttribute.setBaseValue(attackAttribute.getBaseValue() + 1.5);
                        }
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "已增加1.5攻击力！");
                        break;
                    case "arrow":
                        addExtraItem(player.getUniqueId(), new ItemStack(Material.ARROW, 8));
                        player.sendMessage(ChatColor.GOLD + "你将在下回合获得16支箭！");
                        break;
                    case "ender_pearl":
                        addExtraItem(player.getUniqueId(), new ItemStack(Material.ENDER_PEARL, 2));
                        player.sendMessage(ChatColor.GOLD + "你将在下回合获得4颗末影珍珠！");
                        break;
                    case "shear":
                        ItemStack itemStack = new ItemStack(Material.SHEARS, 1);
                        itemStack.addEnchantment(Enchantment.DIG_SPEED, 3);
                        addExtraItem(player.getUniqueId(), itemStack);
                        player.sendMessage(ChatColor.GOLD + "你将在下回合获得剪刀！");
                        break;
                    case "cobweb":
                        addExtraItem(player.getUniqueId(), new ItemStack(Material.COBWEB, 1));
                        player.sendMessage(ChatColor.GOLD + "你将在下回合获得蜘蛛网！");
                        break;
                    case "fish_rod":
                        addExtraItem(player.getUniqueId(), new ItemStack(Material.FISHING_ROD, 1));
                        player.sendMessage(ChatColor.GOLD + "你将在下回合获得钓鱼竿！");
                        break;
                    case "shulker_arrow":
                        addExtraItem(player.getUniqueId(), new ItemStack(Material.valueOf("ARCHERS_PARADOX_SHULKER_ARROW"), 3));
                        player.sendMessage(ChatColor.GOLD + "你将在下回合获得3支潜影箭！");
                        break;
                    case "ender_arrow":
                        addExtraItem(player.getUniqueId(), new ItemStack(Material.valueOf("ARCHERS_PARADOX_ENDER_ARROW"), 2));
                        player.sendMessage(ChatColor.GOLD + "你将在下回合获得2支末影箭！");
                        break;
                    default:
                        player.sendMessage(ChatColor.WHITE + "未知的加成选择！");
                        break;
                }
                addAsSelected(player.getUniqueId());
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                for (int i = 0; i < 9; i++) {
                    player.getInventory().addItem(createFancyFirework());
                }
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void disableExplodeGrief(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void disableInteractive(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null) {
            Material blockType = clickedBlock.getType();
            if (isInteractive(blockType)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void disablePickupArrow(PlayerPickupArrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void suppressJoinMessage(PlayerJoinEvent e) {
        e.setJoinMessage(null);
    }

    @EventHandler
    public void suppressQuitMessage(PlayerQuitEvent e) {
        e.setQuitMessage(null);
    }

    @EventHandler
    public void disableItemDamage(PlayerItemDamageEvent e) {
        e.setCancelled(true);
    }

    private void switchHelmet(Player victim, Player attacker, ItemStack helmet) {
        if (victim == attacker) return;
        if (helmet != null) {
            attacker.getInventory().setHelmet(helmet);
            attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 2, true, false));
            attacker.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30, 1));
            victim.getInventory().setHelmet(null);
            attacker.setGlowing(true);
            victim.setGlowing(false);
            UUID uuid = attacker.getUniqueId();
            invinciblePlayers.add(uuid);
            new BukkitRunnable() {
                @Override
                public void run() {
                    invinciblePlayers.remove(uuid);
                }
            }.runTaskLater(plugin, 15);
            Bukkit.broadcastMessage(ChatColor.YELLOW + attacker.getDisplayName() + "§f夺取了" + ChatColor.YELLOW + victim.getDisplayName() + "§f的终极绿帽！");
        }
    }

    private void switchHelmet(Player victim, ItemStack helmet) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.remove(victim);
        if (helmet != null) {
            if (onlinePlayers.isEmpty()) {
                victim.getInventory().setHelmet(null);
                TaskHandler.stopActivity();
            } else {
                Player randomPlayer = onlinePlayers.get(new Random().nextInt(onlinePlayers.size()));
                switchHelmet(victim, randomPlayer, helmet);
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

    private void respawnPlayer(Player player) {
        player.setHealth(player.getMaxHealth());
        player.teleport(getCurrentSpawnLocation());
    }

    private boolean isInteractive(Material material) {
        String name = material.name();
        return !name.endsWith("_TRAPDOOR") && !name.endsWith("_FENCE_GATE") && !name.endsWith("LEVER");
    }

}
