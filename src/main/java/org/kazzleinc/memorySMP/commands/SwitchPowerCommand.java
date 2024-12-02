package org.kazzleinc.memorySMP.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.kazzleinc.memorySMP.MemorySMP;

public class SwitchPowerCommand implements CommandExecutor {
    MemorySMP plugin;

    public SwitchPowerCommand(MemorySMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            sender.sendMessage("POWER1");
            return true;
        } else {
            return false;
        }
    }
}
