package org.kazzleinc.memorySMP.membranes;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.kazzleinc.memorySMP.MemorySMP;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class LockedMembraneClass extends ParentMembraneClass implements Listener {
    public final HashMap<UUID, Long> cooldowns = new HashMap<>();
    public final HashSet<UUID> lockedSlotList = new HashSet<>();

    MemorySMP plugin;

    public LockedMembraneClass(MemorySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void action1(Player player) {
        if (!isOnCooldown(player.getUniqueId(), cooldowns)) {
            if (getTargetPlayer(player, 3.5f) != null) {
                setCooldown(player.getUniqueId(), cooldowns, 120);
                Player targetPlayer = getTargetPlayer(player, 3.5f);

                lockedSlotList.add(targetPlayer.getUniqueId());

                new BukkitRunnable() {
                    Player playerCheck = targetPlayer;
                    @Override
                    public void run() {
                        lockedSlotList.remove(playerCheck.getUniqueId());
                    }
                }.runTaskLater(plugin, 20 * 5);
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.f, 1.f);
                player.sendMessage(ChatColor.RED + "No players hit.");
            }
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
        player.sendActionBar(ChatColor.GREEN + "Slot Lock: " + getCooldownTimeLeft(player.getUniqueId(), cooldowns) + ChatColor.RESET + ChatColor.BOLD + ChatColor.GOLD + " | " + ChatColor.RESET + ChatColor.GREEN + "Cooldown 2: " + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "NOT IMP");
    }

    @EventHandler
    public void onPlayerChangeSlotEvent(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (lockedSlotList.contains(player.getUniqueId())) {
            player.getInventory().setHeldItemSlot(event.getPreviousSlot());
        }

    }
}
