package org.kazzleinc.memorySMP.membranes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.kazzleinc.memorySMP.MemorySMP;

public class LockedMembraneClass extends ParentMembraneClass implements Listener {
    MemorySMP plugin;

    public LockedMembraneClass(MemorySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void action1(Player player) {
        player.sendMessage("AH ! ACTION ONE !");
    }

    @Override
    public void action2(Player player) {
        player.sendMessage("AH ! ACTION TWO !");
    }

    @Override
    public void cooldownDisplay(Player player) {
        player.sendActionBar(ChatColor.GREEN + "Cooldown 1: " + ChatColor.RESET + ChatColor.BOLD + ChatColor.RED + "NOT IMP" + ChatColor.RESET + ChatColor.BOLD + ChatColor.GOLD + " | " + ChatColor.RESET + ChatColor.GREEN + "Cooldown 2: " + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "NOT IMP");
    }
}
