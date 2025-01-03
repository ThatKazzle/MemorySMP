package org.kazzleinc.memorySMP.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kazzleinc.memorySMP.MemorySMP;
import org.kazzleinc.memorySMP.MemoryUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MemoryCommand implements TabExecutor, Listener {
    MemorySMP plugin;

    public NamespacedKey inventoryKey = new NamespacedKey("inventories", "membrane_inventory");
    public NamespacedKey confirmInvKey = new NamespacedKey("inventories", "membrane_inventory_confirm");

    public MemoryCommand(MemorySMP plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                if (args[0].equals("upgrade")) {
                    openMembraneUpgradeGui(player);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> options = Arrays.asList("upgrade", "info");

            return filterSuggestions(options, args[0]);
        } else {
            return List.of();
        }
    }

    private List<String> filterSuggestions(List<String> options, String input) {
        if (input == null || input.isEmpty()) {
            return options;
        }
        String lowerInput = input.toLowerCase();
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(lowerInput))
                .collect(Collectors.toList());
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

    private void openMembraneUpgradeGui(Player player) {
        // Create a 9x6 inventory (54 slots)
        Inventory gui = Bukkit.createInventory(player, 54, "" + ChatColor.BOLD + ChatColor.RED + "Upgrade your Membrane: ");

        // Create the red stained glass pane item
        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = redPane.getItemMeta();

        // Fill the inventory with red stained-glass panes
        for (int i = 0; i < gui.getSize(); i++) {

            if (meta != null) {
                meta.setDisplayName("" + ChatColor.RESET + ChatColor.BOLD + ChatColor.RED + ChatColor.MAGIC + "A"); // Set empty display name
                meta.getPersistentDataContainer().set(inventoryKey, PersistentDataType.BOOLEAN, true);
                //meta.setDisplayName("Index " + i);
                redPane.setItemMeta(meta);
            }

            gui.setItem(i, redPane);
        }

        // Leave specific slots empty
        int centerSlot = 4; // Middle slot of the first row
        int secondSlotDown = centerSlot + 9; // Slot in the second row
        int thirdSlotDown = centerSlot + 9 * 2; // Slot in the fifth row

        int fifthSlotDown = centerSlot + 9 * 4;
//4 to 13 to 22
        ItemStack membraneStack = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta membraneItemMeta = membraneStack.getItemMeta();

        membraneItemMeta.setEnchantmentGlintOverride(true);
        membraneItemMeta.getPersistentDataContainer().set(inventoryKey, PersistentDataType.BOOLEAN, true);

        for (String powerName : plugin.getConfig().getConfigurationSection("players." + player.getName() + ".membranes").getKeys(false)) {
            if (plugin.getConfig().getBoolean("players." + player.getName() + ".membranes." + powerName)) {
                membraneItemMeta.setDisplayName("" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + MemoryUtils.toTitleCase(powerName) + ChatColor.RESET + ChatColor.GREEN + ChatColor.BOLD + " Membrane");
                plugin.getLogger().info("!! - the printout is " + plugin.getConfig().getList("available-membranes." + powerName + ".power-names"));
                membraneItemMeta.setLore(List.of(
                        "" + ChatColor.LIGHT_PURPLE + "- " + ChatColor.BOLD + plugin.getConfig().getStringList("available-membranes." + powerName + ".power-names").getFirst() + ChatColor.RESET + ChatColor.LIGHT_PURPLE + " level " + plugin.getConfig().getInt("players." + player.getName() + ".membranes.primLevel", 1),
                        "" + ChatColor.LIGHT_PURPLE + "- " + ChatColor.BOLD + plugin.getConfig().getStringList("available-membranes." + powerName + ".power-names").getLast() + ChatColor.RESET + ChatColor.LIGHT_PURPLE + " level " + plugin.getConfig().getInt("players." + player.getName() + ".membranes.secLevel", 1)));

                membraneStack.setItemMeta(membraneItemMeta);

                gui.setItem(thirdSlotDown, membraneStack);

            }
        }

        gui.setItem(secondSlotDown - 1, null);

        gui.setItem(secondSlotDown + 1, null);

        gui.setItem(fifthSlotDown, getUnavailableStack());

        // Open the inventory for the player
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("" + ChatColor.BOLD + ChatColor.RED + "Upgrade your Membrane: ")) {
            if (event.getCurrentItem() != null) {
                String playerMembraneName = "";
                for (String powerName : plugin.getConfig().getConfigurationSection("players." + player.getName() + ".membranes").getKeys(false)) {
                    if (plugin.getConfig().getBoolean("players." + player.getName() + ".membranes." + powerName)) {
                        playerMembraneName = powerName;
                    }
                }

                ItemMeta clickedMeta = event.getCurrentItem().getItemMeta();

                if (clickedMeta.getPersistentDataContainer().has(inventoryKey)) {
                    event.setCancelled(true);
                }

                if (clickedMeta.getPersistentDataContainer().has(confirmInvKey)) {
                    plugin.saveConfig();
                    if (event.getView().getTopInventory().getItem(12) != null && plugin.getConfig().getInt("players." + player.getName() + ".membranes.primLevel") < 3) {
                        event.getView().getTopInventory().getItem(12).setAmount(event.getView().getTopInventory().getItem(12).getAmount() - 1);
                        if (event.getView().getTopInventory().getItem(12) != null) {
                            player.getInventory().addItem(Objects.requireNonNull(event.getView().getTopInventory().getItem(12)));
                        }
                        plugin.getConfig().set("players." + player.getName() + ".membranes.primLevel", plugin.getConfig().getInt("players." + player.getName() + ".membranes.primLevel", 1) + 1);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + plugin.getConfig().getStringList("available-membranes." + playerMembraneName + ".power-names").getFirst() + ChatColor.RESET + ChatColor.LIGHT_PURPLE + " has been upgraded to " + ChatColor.BOLD + "Level " + plugin.getConfig().getInt("players." + player.getName() + ".membranes.primLevel", 1) + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ".");
                    }

                    if (event.getView().getTopInventory().getItem(14) != null && plugin.getConfig().getInt("players." + player.getName() + ".membranes.secLevel") < 3) {
                        event.getView().getTopInventory().getItem(14).setAmount(event.getView().getTopInventory().getItem(14).getAmount() - 1);
                        if (event.getView().getTopInventory().getItem(14) != null) {
                            player.getInventory().addItem(Objects.requireNonNull(event.getView().getTopInventory().getItem(14)));
                        }
                        plugin.getConfig().set("players." + player.getName() + ".membranes.secLevel", plugin.getConfig().getInt("players." + player.getName() + ".membranes.secLevel", 1) + 1);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + plugin.getConfig().getStringList("available-membranes." + playerMembraneName + ".power-names").getLast() + ChatColor.RESET + ChatColor.LIGHT_PURPLE + " has been upgraded to " + ChatColor.BOLD + "Level " + plugin.getConfig().getInt("players." + player.getName() + ".membranes.secLevel", 1) + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ".");
                    }

                    plugin.saveConfig();

                    event.getView().close();

                    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.f, 1.f);
                }
            }

            if (event.getView().getTopInventory().getItem(12) != null && event.getView().getTopInventory().getItem(12).getItemMeta() != null) {
                if (event.getView().getTopInventory().getItem(12).getItemMeta().getPersistentDataContainer().has(plugin.upgraderItemKey)) {
                    if (plugin.getConfig().getInt("players." + player.getName() + ".membranes.primLevel", 1) < 3) {
                        event.getInventory().setItem(40, getConfirmStack());
                    } else {
                        event.getInventory().setItem(40, getMaxLevelReachedStack());
                    }
                } else {
                    event.getInventory().setItem(40, getUnavailableStack());
                }
            } else if (event.getView().getTopInventory().getItem(14) != null && event.getView().getTopInventory().getItem(14).getItemMeta() != null) {
                if (event.getView().getTopInventory().getItem(14).getItemMeta().getPersistentDataContainer().has(plugin.upgraderItemKey)) {
                    if (plugin.getConfig().getInt("players." + player.getName() + ".membranes.secLevel", 1) < 3) {
                        event.getInventory().setItem(40, getConfirmStack());
                    } else {
                        if (plugin.getConfig().getInt("players." + player.getName() + ".membranes.primLevel", 1) < 3 || plugin.getConfig().getInt("players." + player.getName() + ".membranes.secLevel", 1) < 3)
                        event.getInventory().setItem(40, getMaxLevelReachedStack());
                    }
                } else {
                    event.getInventory().setItem(40, getUnavailableStack());
                }
            } else {
                event.getInventory().setItem(40, getUnavailableStack());
            }
        }
    }
}
