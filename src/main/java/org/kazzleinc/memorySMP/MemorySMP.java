package org.kazzleinc.memorySMP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.kazzleinc.memorySMP.commands.MemoryCommand;
import org.kazzleinc.memorySMP.commands.SwitchPowerCommand;
import org.kazzleinc.memorySMP.commands.TestMembraneCommand;
import org.kazzleinc.memorySMP.commands.UsePowerCommand;
import org.kazzleinc.memorySMP.membranes.*;

import java.util.Objects;

import static org.kazzleinc.memorySMP.MemoryUtils.*;

public final class MemorySMP extends JavaPlugin implements Listener {

    public NamespacedKey membraneItemKey = new NamespacedKey(this, "membrane_item");
    public NamespacedKey upgraderItemKey = new NamespacedKey(this, "upgrader_item");

    public NamespacedKey inventoryKey = new NamespacedKey("inventories", "membrane_inventory");
    public NamespacedKey confirmInvKey = new NamespacedKey("inventories", "membrane_inventory_confirm");

    TestMembraneCommand testMembraneCommand = new TestMembraneCommand(this);

    SwitchPowerCommand switchPowerCommand = new SwitchPowerCommand(this);
    UsePowerCommand usePowerCommand = new UsePowerCommand(this);
    MemoryCommand memoryCommand = new MemoryCommand(this);

    //membrane classes
    public GambleMembraneClass gambleMembraneClass = new GambleMembraneClass(this);
    public LockedMembraneClass lockedMembraneClass = new LockedMembraneClass(this);
    public MobilityMembraneClass mobilityMembraneClass = new MobilityMembraneClass(this);
    public ScorchingMembraneClass scorchingMembraneClass = new ScorchingMembraneClass(this);
    public SpiritMembraneClass spiritMembraneClass = new SpiritMembraneClass(this);
    public VoidMembraneClass voidMembraneClass = new VoidMembraneClass(this);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        getServer().getPluginManager().registerEvents(gambleMembraneClass, this);
        getServer().getPluginManager().registerEvents(lockedMembraneClass, this);
        getServer().getPluginManager().registerEvents(mobilityMembraneClass, this);
        getServer().getPluginManager().registerEvents(scorchingMembraneClass, this);
        getServer().getPluginManager().registerEvents(spiritMembraneClass, this);
        getServer().getPluginManager().registerEvents(voidMembraneClass, this);
        getServer().getPluginManager().registerEvents(memoryCommand, this);

        getCommand("testmembrane").setExecutor(testMembraneCommand);

        getCommand("power1").setExecutor(usePowerCommand);
        getCommand("power2").setExecutor(switchPowerCommand);
        getCommand("memory").setExecutor(memoryCommand);

        saveDefaultConfig();

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (String powerName : getConfig().getConfigurationSection("players." + player.getName() + ".membranes").getKeys(false)) {
                        if (getConfig().getBoolean("players." + player.getName() + ".membranes." + powerName)) {
                            switch (powerName) {
                                case "locked" -> lockedMembraneClass.cooldownDisplay(player);
                                case "mobility" -> mobilityMembraneClass.cooldownDisplay(player);
                                case "void" -> voidMembraneClass.cooldownDisplay(player);
                                case "spirit" -> spiritMembraneClass.cooldownDisplay(player);
                                case "scorching" -> scorchingMembraneClass.cooldownDisplay(player);
                                case "gamble" -> gambleMembraneClass.cooldownDisplay(player);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 20);

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getOpenInventory().getTitle().equals("" + ChatColor.BOLD + ChatColor.RED + "Upgrade your Membrane: ")) {
                        if (player.getOpenInventory().getTopInventory().getItem(13) != null && player.getOpenInventory().getTopInventory().getItem(13).getItemMeta() != null) {
                            if (player.getOpenInventory().getTopInventory().getItem(13).getItemMeta().getPersistentDataContainer().has(upgraderItemKey)) {
                                if (getConfig().getInt("players." + player.getName() + ".membranes.level", 1) < 3) {
                                    player.getOpenInventory().setItem(40, getConfirmStack());
                                } else {
                                    player.getOpenInventory().setItem(40, getMaxLevelReachedStack());
                                }
                            } else {
                                player.getOpenInventory().setItem(40, getUnavailableStack());
                            }
                        } else {
                            player.getOpenInventory().setItem(40, getUnavailableStack());
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 10);
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
                switch (Objects.requireNonNull(item.getItemMeta().getPersistentDataContainer().get(membraneItemKey, PersistentDataType.STRING))) {
                    case "locked" -> {

                    }
                    case "scorching" -> {

                    }
                }

                getConfig().set("players." + player.getName() + ".membranes." + item.getItemMeta().getPersistentDataContainer().get(membraneItemKey, PersistentDataType.STRING), true);
                item.setAmount(item.getAmount() - 1);
                saveConfig();
            }
        }
    }

    private ItemStack getConfirmStack() {
        ItemStack confirmStack = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        ItemMeta confirmStackMeta = confirmStack.getItemMeta();

        confirmStackMeta.setItemName("" + ChatColor.GOLD + ChatColor.BOLD + "Confirm?");
        confirmStackMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        confirmStackMeta.getPersistentDataContainer().set(inventoryKey, PersistentDataType.BOOLEAN, true);
        confirmStackMeta.getPersistentDataContainer().set(confirmInvKey, PersistentDataType.BOOLEAN, true);

        confirmStack.setItemMeta(confirmStackMeta);

        return confirmStack;
    }

    private ItemStack getMaxLevelReachedStack() {
        ItemStack unavailStack = new ItemStack(Material.BARRIER);
        ItemMeta unavailStackMeta = unavailStack.getItemMeta();

        unavailStackMeta.setItemName("" + ChatColor.RED + ChatColor.BOLD + "Max level membrane reached.");
        unavailStackMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        unavailStackMeta.getPersistentDataContainer().set(inventoryKey, PersistentDataType.BOOLEAN, true);

        unavailStack.setItemMeta(unavailStackMeta);

        return unavailStack;
    }

    private ItemStack getUnavailableStack() {
        ItemStack unavailStack = new ItemStack(Material.BARRIER);
        ItemMeta unavailStackMeta = unavailStack.getItemMeta();

        unavailStackMeta.setItemName("" + ChatColor.RED + ChatColor.BOLD + "No upgrader has been given.");
        unavailStackMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        unavailStackMeta.getPersistentDataContainer().set(inventoryKey, PersistentDataType.BOOLEAN, true);

        unavailStack.setItemMeta(unavailStackMeta);

        return unavailStack;
    }
}
