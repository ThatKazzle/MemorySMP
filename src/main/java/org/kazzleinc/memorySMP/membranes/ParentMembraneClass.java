package org.kazzleinc.memorySMP.membranes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public abstract class ParentMembraneClass {
    public abstract void action1(Player player);

    public abstract void action2(Player player);

    public abstract void cooldownDisplay(Player player);

    public boolean isOnCooldown(UUID playerId, HashMap<UUID, Long> map) {
        return map.containsKey(playerId) && map.get(playerId) > System.currentTimeMillis();
    }

    /**
     * Sets a cooldown on a list, making it easier to do multiple powers.
     *
     * @param playerId
     * @param map
     * @param cooldown MAKE SURE THIS IS IN SECONDS!
     */
    public void setCooldown(UUID playerId, HashMap<UUID, Long> map, int cooldown) {
        map.put(playerId, System.currentTimeMillis() + (cooldown * 1000));
    }

    public String getCooldownTimeLeft(UUID playerId, HashMap<UUID, Long> cooldown) {
        if (cooldown.get(playerId) != null) {
            long timeLeft = (cooldown.get(playerId) - System.currentTimeMillis()) / 1000;

            long seconds = timeLeft % 60;
            long minutes = timeLeft / 60;

            return ChatColor.RED + formatCooldownTime(timeLeft);
        } else {
            return "" + ChatColor.GREEN + ChatColor.BOLD + "Ready!";
        }
    }

    public String formatCooldownTime(long totalSeconds) {
        //long totalSeconds = ticks / 20;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        if (totalSeconds <= 0) {
            return "" + ChatColor.GREEN + ChatColor.BOLD + "Ready!";
        } else if (minutes > 0) {
            return "" + ChatColor.RED + minutes + "m " + seconds + "s";
        } else {
            return "" + ChatColor.RED + seconds + "s";
        }
    }

    public static Player getTargetPlayer(Player player, double range) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        // Perform ray tracing to find entities in the player's line of sight
        if (player.getWorld().rayTraceEntities(
                eyeLocation,
                direction,
                range,
                entity -> entity instanceof Player && !entity.equals(player) // Ignore self
        ) != null) {
            Entity target = player.getWorld().rayTraceEntities(
                    eyeLocation,
                    direction,
                    range,
                    entity -> entity instanceof Player && !entity.equals(player) // Ignore self
            ).getHitEntity();

            return target instanceof Player ? (Player) target : null;
        } else {
            return null;
        }
    }
}
