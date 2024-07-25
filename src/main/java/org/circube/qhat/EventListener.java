package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
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
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
                victim.getInventory().setHelmet(null);
                attacker.setGlowing(true);
                victim.setGlowing(false);
                Bukkit.broadcastMessage(ChatColor.YELLOW + attacker.getDisplayName() + "§f夺取了" + ChatColor.YELLOW + victim.getDisplayName() + "§f的终极绿帽！");
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && helmet.getType() == Material.TURTLE_HELMET) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();
        if (player.getGameMode() == GameMode.SURVIVAL) {
            if (blockType != Material.SLIME_BLOCK &&
                    blockType != Material.WHITE_WOOL &&
                    blockType != Material.ORANGE_WOOL &&
                    blockType != Material.MAGENTA_WOOL &&
                    blockType != Material.LIGHT_BLUE_WOOL &&
                    blockType != Material.YELLOW_WOOL &&
                    blockType != Material.LIME_WOOL &&
                    blockType != Material.PINK_WOOL &&
                    blockType != Material.GRAY_WOOL &&
                    blockType != Material.LIGHT_GRAY_WOOL &&
                    blockType != Material.CYAN_WOOL &&
                    blockType != Material.PURPLE_WOOL &&
                    blockType != Material.BLUE_WOOL &&
                    blockType != Material.BROWN_WOOL &&
                    blockType != Material.GREEN_WOOL &&
                    blockType != Material.RED_WOOL &&
                    blockType != Material.BLACK_WOOL) {

                event.setCancelled(true);
            }
        }
    }
}
