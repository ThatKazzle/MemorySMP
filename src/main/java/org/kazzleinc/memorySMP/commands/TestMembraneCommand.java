package org.kazzleinc.memorySMP.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.kazzleinc.memorySMP.MemorySMP;
import org.kazzleinc.memorySMP.UpgraderItemClass;

public class TestMembraneCommand implements CommandExecutor {
    MemorySMP plugin;

    public TestMembraneCommand(MemorySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;

            ItemStack testStack = new ItemStack(Material.NETHER_STAR);
            ItemMeta testStackMeta = testStack.getItemMeta();

            testStackMeta.getPersistentDataContainer().set(plugin.membraneItemKey, PersistentDataType.STRING,  strings[0]);
            testStackMeta.setDisplayName(strings[0] + " membrane");
            testStack.setItemMeta(testStackMeta);

            sender.getInventory().addItem(UpgraderItemClass.createUpgraderItemStack());

            return true;
        } else {
            return false;
        }
    }
}
