package org.circube.qhat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
            player.sendMessage("不要啊啊啊啊啊啊啊啊啊");
            event.setCancelled(true);
        }
    }
}
