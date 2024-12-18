package org.kazzleinc.memorySMP;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class RandomizerItemClass {
    MemorySMP plugin;

    public RandomizerItemClass(MemorySMP plugin) {
        this.plugin = plugin;
    }

    public ItemStack createRandomizerItemStack() {
        ItemStack randomizerStack = new ItemStack(Material.DIAMOND);
        ItemMeta randomizerItemMeta = randomizerStack.getItemMeta();

        randomizerItemMeta.getPersistentDataContainer().set(plugin.randomizerItemKey, PersistentDataType.BOOLEAN, true);
        randomizerItemMeta.setItemName(ChatColor.RED + "Randomizer");
        randomizerItemMeta.setLore(List.of(ChatColor.RED + "Use this to " + ChatColor.BOLD + "reroll", "" + ChatColor.RESET + ChatColor.RED + "your given " + ChatColor.BOLD + "Membrane" + ChatColor.RESET + ChatColor.RED + "."));

        randomizerStack.setItemMeta(randomizerItemMeta);
        randomizerItemMeta.setFireResistant(false);

        return randomizerStack;
    }
}
