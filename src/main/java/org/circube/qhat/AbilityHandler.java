package org.circube.qhat;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class AbilityHandler {
    private static final QHat plugin = QHat.getPlugin(QHat.class);
    private static final Map<UUID, ItemStack> extraItems = new HashMap<>();
    private static final Set<UUID> selectedUUID = new HashSet<>();

    private static final List<String> ATTRIBUTES = Arrays.asList(
            "增加3点生命值",
            "增加7%移速",
            "增加12%击退",
            "增加1.5攻击力"
    );
    private static final List<String> TOOLS = Arrays.asList(
            "下回合获得16支箭",
            "下回合获得4颗末影珍珠",
            "下回合获得剪刀",
            "下回合获得蜘蛛网",
            "下回合获得钓鱼竿",
            "下回合获得3支潜影箭",
            "下回合获得2支末影箭"
    );
    static List<String> selectedAttributes = new ArrayList<>();
    static List<String> selectedTools = new ArrayList<>();

    public static void resetAttributes(Player player) {
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

        removeExtraItem(player.getUniqueId());
    }

    public static void openAbilityGUI(Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.RECORDS, 1f, 1f);
            Inventory gui = Bukkit.createInventory(null, 27, "选择一项加成");

            for (int i = 0; i < selectedAttributes.size(); i++) {
                String attribute = selectedAttributes.get(i);
                ItemStack button = createAttributeButton(attribute);
                gui.setItem(13, button);
            }

            for (int i = 0; i < selectedTools.size(); i++) {
                String tool = selectedTools.get(i);
                ItemStack button = createAttributeButton(tool);
                gui.setItem(11 + i * 4, button);
            }

            player.openInventory(gui);
        }

    }

    public static void getRandomAttributes() {
        Collections.shuffle(ATTRIBUTES);
        selectedAttributes = ATTRIBUTES.subList(0, 1);
    }

    public static void getRandomTools() {
        Collections.shuffle(TOOLS);
        selectedTools = TOOLS.subList(0, 2);
    }

    private static ItemStack createAttributeButton(String attributeName) {
        Material material;
        String displayName;
        String flag;
        switch (attributeName) {
            case "增加3点生命值":
                material = Material.APPLE;
                displayName = ChatColor.LIGHT_PURPLE + attributeName;
                flag = "health";
                break;
            case "增加7%移速":
                material = Material.FEATHER;
                displayName = ChatColor.LIGHT_PURPLE + attributeName;
                flag = "speed";
                break;
            case "增加12%击退":
                material = Material.STICK;
                displayName = ChatColor.LIGHT_PURPLE + attributeName;
                flag = "knockback";
                break;
            case "增加1.5攻击力":
                material = Material.IRON_AXE;
                displayName = ChatColor.LIGHT_PURPLE + attributeName;
                flag = "damage";
                break;
            case "下回合获得16支箭":
                material = Material.ARROW;
                displayName = ChatColor.GOLD + attributeName;
                flag = "arrow";
                break;
            case "下回合获得4颗末影珍珠":
                material = Material.ENDER_PEARL;
                displayName = ChatColor.GOLD + attributeName;
                flag = "ender_pearl";
                break;
            case "下回合获得剪刀":
                material = Material.SHEARS;
                displayName = ChatColor.GOLD + attributeName;
                flag = "shear";
                break;
            case "下回合获得蜘蛛网":
                material = Material.COBWEB;
                displayName = ChatColor.GOLD + attributeName;
                flag = "cobweb";
                break;
            case "下回合获得钓鱼竿":
                material = Material.FISHING_ROD;
                displayName = ChatColor.GOLD + attributeName;
                flag = "fish_rod";
                break;
            case "下回合获得3支潜影箭":
                material = Material.valueOf("ARCHERS_PARADOX_SHULKER_ARROW");
                displayName = ChatColor.GOLD + attributeName;
                flag = "shulker_arrow";
                break;
            case "下回合获得2支末影箭":
                material = Material.valueOf("ARCHERS_PARADOX_ENDER_ARROW");
                displayName = ChatColor.GOLD + attributeName;
                flag = "ender_arrow";
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

    public static void addExtraItem(UUID uuid, ItemStack itemStack) {
        extraItems.putIfAbsent(uuid, itemStack);
    }

    public static void removeExtraItem(UUID uuid) {
        extraItems.remove(uuid);
    }

    public static ItemStack getExtraItem(UUID uuid) {
        return extraItems.getOrDefault(uuid, null);
    }

    public static void addAsSelected(UUID uuid) {
        selectedUUID.add(uuid);
    }

    public static boolean isSelected(UUID uuid) {
        return selectedUUID.contains(uuid);
    }

    public static void clearSelected() {
        selectedUUID.clear();
    }
}
