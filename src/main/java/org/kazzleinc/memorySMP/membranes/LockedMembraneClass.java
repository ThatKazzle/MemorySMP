package org.kazzleinc.memorySMP.membranes;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.kazzleinc.memorySMP.MemorySMP;
import org.kazzleinc.memorySMP.ParticleUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class LockedMembraneClass extends ParentMembraneClass implements Listener {
    public final HashMap<UUID, Long> cooldowns = new HashMap<>();
    public final HashMap<UUID, Long> playerHookCooldowns = new HashMap<>();
    public final HashSet<UUID> lockedSlotList = new HashSet<>();

    public final HashMap<UUID, Boolean> isFrozen = new HashMap<>();

    MemorySMP plugin;

    public LockedMembraneClass(MemorySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void action1(Player player) {
        if (!isOnCooldown(player.getUniqueId(), cooldowns)) {
            if (getTargetPlayer(player, 3.5f) != null) {
                int primaryPlayerLevel = getPrimaryLevel(player.getName(), plugin.getConfig());
                int delayTime = 0;

                if (primaryPlayerLevel == 1) {
                    delayTime = 5;
                } else if (primaryPlayerLevel == 2) {
                    delayTime = 7;
                } else if (primaryPlayerLevel == 3) {
                    delayTime = 10;
                }

                setCooldown(player.getUniqueId(), cooldowns, 120);
                Player targetPlayer = getTargetPlayer(player, 3.5f);

                lockedSlotList.add(targetPlayer.getUniqueId());

                new BukkitRunnable() {
                    Player playerCheck = targetPlayer;
                    @Override
                    public void run() {
                        lockedSlotList.remove(playerCheck.getUniqueId());
                    }
                }.runTaskLater(plugin, 20 * delayTime);
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
        int playerSecondaryLevel = getSecondaryLevel(player.getName(), plugin.getConfig());
        int distanceToCheckPlayer = 0;

        if (playerSecondaryLevel == 1) {
            distanceToCheckPlayer = 15;
        } else if (playerSecondaryLevel == 2) {
            distanceToCheckPlayer = 20;
        } else if (playerSecondaryLevel == 3) {
            distanceToCheckPlayer = 25;
        }

        if (!isOnCooldown(player.getUniqueId(), playerHookCooldowns)) {
            if (getTargetPlayer(player, distanceToCheckPlayer) != null) {
                setCooldown(player.getUniqueId(), playerHookCooldowns, 90);
                Player hitPlayer = getTargetPlayer(player, distanceToCheckPlayer);

                new BukkitRunnable() {
                    Player localHitPlayer = hitPlayer;
                    double alpha = 0;

                    Vector startPos = hitPlayer.getLocation().clone().toVector();
                    Vector endPos = player.getLocation().toVector();
                    @Override
                    public void run() {
                        if (alpha >= 1) {
                            freezeAction(player, localHitPlayer);
                            this.cancel();
                        }

                        alpha += 0.05;
                        localHitPlayer.teleport(lerp(startPos, endPos, alpha).toLocation(player.getWorld()));
                    }
                }.runTaskTimer(plugin, 0, 5);
            }
        }
    }

    public void freezeAction(Player player, Player hitPlayer) {
        isFrozen.put(hitPlayer.getUniqueId(), true);
        hitPlayer.setAllowFlight(true);
        BukkitTask soundRunner = new BukkitRunnable() {
            @Override
            public void run() {
                //ParticleUtils.createParticleRing(hitPlayer.getEyeLocation(), 1.5, 40, Particle.DUST, Color.WHITE, 1);
                hitPlayer.getWorld().playSound(hitPlayer.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1.f, 1.f);

                hitPlayer.setSaturation(0);
                hitPlayer.damage(0.000001);

                if (hitPlayer.getHealth() - 0.75 < 0) {
                    hitPlayer.setHealth(0);
                } else {
                    hitPlayer.setHealth(hitPlayer.getHealth() - 0.75);
                }
            }
        }.runTaskTimer(plugin, 0, 20);

        BukkitTask particleRunner = new BukkitRunnable() {
            int freeze = 0;
            @Override
            public void run() {

                freeze += 1;
                double freezeDiv = (double) freeze / 100;
                //player.sendMessage(String.valueOf(freezeDiv));

                ParticleUtils.createParticleRing(lerp(hitPlayer.getLocation().toVector(), hitPlayer.getEyeLocation().toVector(), freezeDiv).toLocation(player.getWorld()), 1.5, 30, Particle.DUST, Color.WHITE, 1);
                ParticleUtils.createParticleRing(lerp(hitPlayer.getLocation().toVector(), hitPlayer.getEyeLocation().toVector(), 1 - freezeDiv).toLocation(player.getWorld()), 1.5, 30, Particle.DUST, Color.WHITE, 1);
            }
        }.runTaskTimer(plugin, 0, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage("unfroze " + hitPlayer.getName());
                isFrozen.remove(hitPlayer.getUniqueId());
                hitPlayer.setAllowFlight(false);
                soundRunner.cancel();
                particleRunner.cancel();
                this.cancel();
            }
        }.runTaskLater(plugin, 20 * 5);
    }

    @Override
    public void cooldownDisplay(Player player) {
        player.sendActionBar(getPowerLevelColor(player.getName(), true, plugin.getConfig()) + "Slot Lock: " + getCooldownTimeLeft(player.getUniqueId(), cooldowns) + ChatColor.RESET + ChatColor.BOLD + ChatColor.GOLD + " | " + ChatColor.RESET + getPowerLevelColor(player.getName(), false, plugin.getConfig()) + "Player Hook: " + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + getCooldownTimeLeft(player.getUniqueId(), playerHookCooldowns));
    }

    @EventHandler
    public void onPlayerChangeSlotEvent(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (lockedSlotList.contains(player.getUniqueId())) {
            player.getInventory().setHeldItemSlot(event.getPreviousSlot());
        }

    }

    @EventHandler
    public void onPlayerJumpEvent(PlayerJumpEvent event) {
        Player player = event.getPlayer();

        if (isFrozen.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (isFrozen.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (isFrozen.containsKey(player.getUniqueId()) && event.getItem().getType() == Material.CHORUS_FRUIT || event.getItem().getType() == Material.ENDER_PEARL) {
            event.setCancelled(true);
        }
    }
}
