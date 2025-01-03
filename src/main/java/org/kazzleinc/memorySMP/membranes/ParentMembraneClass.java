package org.kazzleinc.memorySMP.membranes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.awt.*;
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

    public String getPowerLevelColor(String playerName, Boolean isFirstPower, Configuration config) {
        String colorToReturn = "";

        int primLevel = getPrimaryLevel(playerName, config);
        int secLevel = getSecondaryLevel(playerName, config);

        int numToWorkWith = isFirstPower ? primLevel : secLevel;

        switch (numToWorkWith) {
            case 1 -> colorToReturn = "" + ChatColor.GREEN;
            case 2 -> colorToReturn = "" + net.md_5.bungee.api.ChatColor.of(Color.orange);
            case 3 -> colorToReturn = "" + net.md_5.bungee.api.ChatColor.of("#FF6EFF");
        }

        return colorToReturn;
    }

    public int getPrimaryLevel(String playerName, Configuration config) {
        return config.getInt("players." + playerName + ".membranes.primLevel", 1);
    }

    public int getSecondaryLevel(String playerName, Configuration config) {
        return config.getInt("players." + playerName + ".membranes.secLevel", 1);
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

    /**
     * Linearly interpolates between two positions.
     *
     * @param start The starting position as a Vector.
     * @param end The ending position as a Vector.
     * @param alpha The interpolation factor (0.0 to 1.0).
     * @return A new Vector representing the interpolated position.
     */
    public static Vector lerp(Vector start, Vector end, double alpha) {
        // Clamp alpha to range [0, 1]
        alpha = Math.max(0, Math.min(1, alpha));

        // Interpolate each component
        double x = start.getX() + alpha * (end.getX() - start.getX());
        double y = start.getY() + alpha * (end.getY() - start.getY());
        double z = start.getZ() + alpha * (end.getZ() - start.getZ());

        return new Vector(x, y, z);
    }
}
