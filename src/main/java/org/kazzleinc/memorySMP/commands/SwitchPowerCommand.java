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
            for (String powerName : plugin.getConfig().getConfigurationSection("players." + sender.getName() + ".membranes").getKeys(false)) {
                if (plugin.getConfig().getBoolean("players." + sender.getName() + ".membranes." + powerName)) {
                    switch (powerName) {
                        case "locked" -> plugin.lockedMembraneClass.action2();
                        case "mobility" -> plugin.mobilityMembraneClass.action2();
                        case "void" -> plugin.voidMembraneClass.action2();
                        case "spirit" -> plugin.spiritMembraneClass.action2();
                        case "scorching" -> plugin.scorchingMembraneClass.action2();
                        case "gamble" -> plugin.gambleMembraneClass.action2();
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
