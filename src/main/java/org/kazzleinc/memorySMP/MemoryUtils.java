package org.kazzleinc.memorySMP;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MemoryUtils {
    MemorySMP plugin;

    public MemoryUtils(MemorySMP plugin) {
        this.plugin = plugin;
    }

    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            } else {
                c = Character.toLowerCase(c);
            }
            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static ItemStack getConfirmStack() {
        ItemStacpublic  static rmStack = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        ItemMeta confirmStackMeta = confirmStack.getItemMeta();

        confirmStackMeta.setItemName("" + ChatColor.GOLD + ChatColor.BOLD + "Confirm?");
        confirmStackMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        confirmStackMeta.getPersistentDataContainer().set(inventoryKey, PersistentDataType.BOOLEAN, true);
        confirmStackMeta.getPersistentDataContainer().set(confirmInvKey, PersistentDataType.BOOLEAN, true);

        confirmStack.setItemMeta(confirmStackMeta);

        return confirmStack;
    }

    public static ItemStack getMaxLevelReacedStack() {
        ItemStack unavailStack = new ItemStack(Material.BARRIER);
        ItemMeta unavailStackMeta = unavailStack.getItemMeta();

        unavailStackMeta.setItemName("" + ChatColor.RED + ChatColor.BOLD + "Max level membrane reached.");
        unavailStackMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        unavailStackMeta.getPersistentDataContainer().set(inventoryKey, PersistentDataType.BOOLEAN, true);

        unavailStack.setItemMeta(unavailStackMeta);

        return unavailStack;
    }

    public static ItemStack getUnavailableStack() {
        ItemStack unavailStack = new ItemStack(Material.BARRIER);
        ItemMeta unavailStackMeta = unavailStack.getItemMeta();

        unavailStackMeta.setItemName("" + ChatColor.RED + ChatColor.BOLD + "No upgrader has been given.");
        unavailStackMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        unavailStackMeta.getPersistentDataContainer().set(inventoryKey, PersistentDataType.BOOLEAN, true);

        unavailStack.setItemMeta(unavailStackMeta);

        return unavailStack;
    }
}
