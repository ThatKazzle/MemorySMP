package org.kazzleinc.memorySMP;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class UpgraderItemClass {
    MemorySMP plugin;

    public UpgraderItemClass(MemorySMP plugin) {
        this.plugin = plugin;
    }

    public ItemStack createUpgraderItemStack() {

        ItemStack upgraderStack = new ItemStack(Material.TRIAL_KEY);
        ItemMeta upgraderItemMeta = upgraderStack.getItemMeta();

        upgraderItemMeta.getPersistentDataContainer().set(plugin.upgraderItemKey, PersistentDataType.BOOLEAN, true);

        upgraderItemMeta.setDisplayName("" + ChatColor.GOLD + "Membrane Upgrader");
        upgraderItemMeta.setLore(List.of(" " + "" + ChatColor.AQUA + "Use this to upgrade your " + ChatColor.GREEN + ChatColor.BOLD + "Membrane", "" + ChatColor.AQUA + " to the next level."));

        upgraderItemMeta.setFireResistant(true);

        upgraderStack.setItemMeta(upgraderItemMeta);

        return upgraderStack;
    }
}
