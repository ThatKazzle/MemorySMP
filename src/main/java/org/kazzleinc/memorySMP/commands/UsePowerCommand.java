package org.kazzleinc.memorySMP.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.kazzleinc.memorySMP.MemorySMP;

public class UsePowerCommand implements CommandExecutor {
    MemorySMP plugin;

    public UsePowerCommand(MemorySMP plugin) {
        this.plugin = plugin;
        //comment to test if this is working
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
                 for (String powerName : plugin.getConfig().getConfigurationSection("players." + sender.getName() + ".membranes").getKeys(false)) {
                     if (plugin.getConfig().getBoolean("players." + sender.getName() + ".membranes." + powerName)) {
                         switch (powerName) {
                             case "locked" -> {

                             }
                             case "mobility" -> {

                             }
                             case "void" -> {

                             }
                             case "spirit" -> {

                             }
                             case "scorching" -> {

                             }
                             case "gamble" -> {

                             }
                         }
                     }
                 }
            return true;
        } else {
            return false;
        }
    }
}
