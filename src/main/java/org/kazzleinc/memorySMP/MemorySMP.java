package org.kazzleinc.memorySMP;

import org.bukkit.*;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static org.kazzleinc.memorySMP.MemoryUtils.*;

public final class MemorySMP extends JavaPlugin implements Listener {
    private final Random random = new Random();

    public NamespacedKey membraneItemKey = new NamespacedKey(this, "membrane_item");
    public NamespacedKey upgraderItemKey = new NamespacedKey(this, "upgrader_item");
    public NamespacedKey randomizerItemKey = new NamespacedKey(this, "randomizer_item");

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
                        if (player.getOpenInventory().getTopInventory().getItem(12) != null && player.getOpenInventory().getTopInventory().getItem(12).getItemMeta() != null) {
                            if (player.getOpenInventory().getTopInventory().getItem(12).getItemMeta().getPersistentDataContainer().has(upgraderItemKey)) {
                                if (getConfig().getInt("players." + player.getName() + ".membranes.primLevel", 1) < 3) {
                                    player.getOpenInventory().setItem(40, getConfirmStack());
                                } else {
                                    player.getOpenInventory().setItem(40, getMaxLevelReachedStack());
                                }
                            } else {
                                player.getOpenInventory().setItem(40, getUnavailableStack());
                            }
                        } else if (player.getOpenInventory().getTopInventory().getItem(14) != null && player.getOpenInventory().getTopInventory().getItem(14).getItemMeta() != null) {
                            //player.getOpenInventory().setItem(40, getUnavailableStack());
                            if (player.getOpenInventory().getTopInventory().getItem(14).getItemMeta().getPersistentDataContainer().has(upgraderItemKey)) {
                                if (getConfig().getInt("players." + player.getName() + ".membranes.secLevel", 1) < 3) {
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
            if (item != null) {
                if (item.getItemMeta().getPersistentDataContainer().has(membraneItemKey)) {

                }

                if (item.getType() != Material.STICK) return; // Replace with your custom item

                List<String> options = List.of();
                for (String key : getConfig().getConfigurationSection("available-membranes").getKeys(false)) {
                    options.add(toTitleCase(key));
                }

                startRandomizing(event.getPlayer().getName(), options);
            }

        }

    }

    private void startRandomizing(String playerName, List<String> options) {
        new BukkitRunnable() {
            private int ticks = 0;
            private int prevTick = 100; // Total duration in ticks (~5 seconds)
            private int delay = 2;      // Initial delay in ticks
            private int step = 1;       // Initial step between updates

            @Override
            public void run() {
                // Get the player
                Player player = Bukkit.getPlayer(playerName);
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                // Calculate current index and send the title
                int index = random.nextInt(options.size());
                String title = ChatColor.GOLD + "Randomizing...";
                String subtitle = options.get(index);

                player.sendTitle(title, subtitle, 0, 20, 0); // Updated to ensure subtitle refreshes

                if (ticks > 20) {
                    player.sendTitle(ChatColor.GOLD + "You now have: ", subtitle, 10, 80, 20);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.f, 2.f);

                    getConfig().set("players." + playerName + ".membranes." + getPlayerMembrane(playerName), false);
                    getConfig().set("players." + playerName + ".membranes." + subtitle, true);

                    saveConfig();

                    this.cancel();
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.f, 0.7f);
                }
                ticks++;
            }
        }.runTaskTimer(this, 0, 5);
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

    public String getPlayerMembrane(String playerName) {
        // Navigate to the player's membranes section in the config
        String path = "players." + playerName + ".membranes";
        if (!getConfig().isConfigurationSection(path)) {
            return null; // No membranes section for this player
        }

        // Retrieve the player's membranes configuration
        Map<String, Object> membranes = getConfig().getConfigurationSection(path).getValues(false);

        // Iterate through the membranes to find the one set to true
        for (Map.Entry<String, Object> entry : membranes.entrySet()) {
            if (entry.getValue() instanceof Boolean && (Boolean) entry.getValue()) {
                return entry.getKey(); // Return the key of the true membrane
            }
        }

        // No membrane is set to true
        return null;
    }
}
