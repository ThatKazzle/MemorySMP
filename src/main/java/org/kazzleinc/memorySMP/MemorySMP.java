package org.kazzleinc.memorySMP;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;
import org.kazzleinc.memorySMP.commands.SwitchPowerCommand;
import org.kazzleinc.memorySMP.commands.TestMembraneCommand;
import org.kazzleinc.memorySMP.commands.UsePowerCommand;
import org.kazzleinc.memorySMP.membranes.*;

public final class MemorySMP extends JavaPlugin implements Listener {

    public NamespacedKey membraneItemKey = new NamespacedKey(this, "membrane_item");

    TestMembraneCommand testMembraneCommand = new TestMembraneCommand(this);

    SwitchPowerCommand switchPowerCommand = new SwitchPowerCommand(this);
    UsePowerCommand usePowerCommand = new UsePowerCommand(this);

    //membrane classes
    GambleMembraneClass gambleMembraneClass = new GambleMembraneClass(this);
    LockedMembraneClass lockedMembraneClass = new LockedMembraneClass(this);
    MobilityMembraneClass mobilityMembraneClass = new MobilityMembraneClass(this);
    ScorchingMembraneClass scorchingMembraneClass = new ScorchingMembraneClass(this);
    SpiritMembraneClass spiritMembraneClass = new SpiritMembraneClass(this);
    VoidMembraneClass voidMembraneClass = new VoidMembraneClass(this);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        getServer().getPluginManager().registerEvents(gambleMembraneClass, this);
        getServer().getPluginManager().registerEvents(lockedMembraneClass, this);
        getServer().getPluginManager().registerEvents(mobilityMembraneClass, this);
        getServer().getPluginManager().registerEvents(scorchingMembraneClass, this);
        getServer().getPluginManager().registerEvents(spiritMembraneClass, this);
        getServer().getPluginManager().registerEvents(voidMembraneClass, this);

        getCommand("testmembrane").setExecutor(testMembraneCommand);

        getCommand("power1").setExecutor(usePowerCommand);
        getCommand("power2").setExecutor(switchPowerCommand);

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getHand() == EquipmentSlot.HAND) {
            if (item.getItemMeta().getPersistentDataContainer().has(membraneItemKey)) {
                switch (item.getItemMeta().getPersistentDataContainer().get(membraneItemKey, PersistentDataType.STRING)) {
                    case "locked" -> {

                    }
                    case "scorching" -> {

                    }
                }

                getConfig().set("players." + player.getName() + ".membranes." + item.getItemMeta().getPersistentDataContainer().get(membraneItemKey, PersistentDataType.STRING), true);
                saveConfig();
            }
        }
    }
}
