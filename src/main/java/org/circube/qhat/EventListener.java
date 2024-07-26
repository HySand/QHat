package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim
                && event.getDamager() instanceof Player attacker) {
            ItemStack helmet = victim.getInventory().getHelmet();
            if (helmet != null && helmet.getType() == Material.TURTLE_HELMET) {
                attacker.getInventory().setHelmet(helmet);
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 1));
                victim.getInventory().setHelmet(null);
                attacker.setGlowing(true);
                victim.setGlowing(false);
                Bukkit.broadcastMessage(ChatColor.YELLOW + attacker.getDisplayName() + "§f夺取了" + ChatColor.YELLOW + victim.getDisplayName() + "§f的终极绿帽！");
            }
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
            if (!(blockType == Material.SLIME_BLOCK ||
                    blockType == Material.WHITE_WOOL ||
                    blockType == Material.ORANGE_WOOL ||
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
                    blockType == Material.BLACK_WOOL)) {
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
                player.setHealth(player.getMaxHealth());
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("选择一项加成")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() != null) {
                Material material = event.getCurrentItem().getType();
                switch (material) {
                    case APPLE:
                        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        if (healthAttribute != null) {
                            healthAttribute.setBaseValue(healthAttribute.getBaseValue() + 3.0);
                        }
                        player.sendMessage(ChatColor.RED + "已增加3点生命值！");
                        break;
                    case FEATHER:
                        AttributeInstance speedAttribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                        if (speedAttribute != null) {
                            speedAttribute.setBaseValue(speedAttribute.getBaseValue() * 1.07);
                        }
                        player.sendMessage(ChatColor.GREEN + "已增加7%移速！");
                        break;
                    case STICK:
                        AttributeInstance knockbackAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
                        if (knockbackAttribute != null) {
                            knockbackAttribute.setBaseValue(knockbackAttribute.getBaseValue() + 0.12);
                        }
                        player.sendMessage(ChatColor.AQUA + "已增加12%击退！");
                        break;
                    case IRON_AXE:
                        AttributeInstance attackAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                        if (attackAttribute != null) {
                            attackAttribute.setBaseValue(attackAttribute.getBaseValue() + 0.5);
                        }
                        player.sendMessage(ChatColor.YELLOW + "已增加0.5攻击力！");
                        break;
                    case TNT:
                        QHat.addExtraItem(player.getUniqueId(), new ItemStack(Material.TNT, 2));
                        player.sendMessage(ChatColor.DARK_RED + "你将在下回合获得2个TNT！");
                        break;
                    case ARROW:
                        QHat.addExtraItem(player.getUniqueId(), new ItemStack(Material.ARROW, 8));
                        player.sendMessage(ChatColor.DARK_GREEN + "你将在下回合获得16支箭！");
                        break;
                    case ENDER_PEARL:
                        QHat.addExtraItem(player.getUniqueId(), new ItemStack(Material.ENDER_PEARL, 1));
                        player.sendMessage(ChatColor.DARK_PURPLE + "你将在下回合获得3颗末影珍珠！");
                        break;
                    default:
                        player.sendMessage(ChatColor.WHITE + "未知的属性选择！");
                        break;
                }
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onPlaceTNT(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.TNT) {
            event.getBlock().setType(Material.AIR);
            ((TNTPrimed) (event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.PRIMED_TNT))).setFuseTicks(25);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }
}
