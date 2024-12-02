package org.kazzleinc.memorySMP.membranes;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.kazzleinc.memorySMP.MemorySMP;

import java.util.HashMap;
import java.util.UUID;

public class LockedMembraneClass extends ParentMembraneClass implements Listener {
    public final HashMap<UUID, Long> cooldowns = new HashMap<>();

    MemorySMP plugin;

    public LockedMembraneClass(MemorySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void action1(Player player) {
        if (!isOnCooldown(player.getUniqueId(), cooldowns)) {
            setCooldown(player.getUniqueId(), cooldowns, 120);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.f, 1.f);
        }

    }

    @Override
    public void action2(Player player) {
        player.sendMessage("AH ! ACTION TWO !");
    }

    @Override
    public void cooldownDisplay(Player player) {
        player.sendActionBar(ChatColor.GREEN + "Cooldown 1: " + getCooldownTimeLeft(player.getUniqueId(), cooldowns) + ChatColor.RESET + ChatColor.BOLD + ChatColor.GOLD + " | " + ChatColor.RESET + ChatColor.GREEN + "Cooldown 2: " + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "NOT IMP");
    }
}
