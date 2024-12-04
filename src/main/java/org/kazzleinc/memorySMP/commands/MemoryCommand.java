package org.kazzleinc.memorySMP.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kazzleinc.memorySMP.MemorySMP;
import org.kazzleinc.memorySMP.MemoryUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryCommand implements TabExecutor, Listener {
    MemorySMP plugin;

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

        confirmStack.setItemMeta(confirmStackMeta);

        return confirmStack;
    }

    private ItemStack getMaxLevelReacedStack() {
        ItemStack unavailStack = new ItemStack(Material.BARRIER);
        ItemMeta unavailStackMeta = unavailStack.getItemMeta();

        unavailStackMeta.setItemName("" + ChatColor.RED + ChatColor.BOLD + "Max level membrane reached.");
        unavailStackMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        unavailStack.setItemMeta(unavailStackMeta);

        return unavailStack;
    }

    private ItemStack getUnavailableStack() {
        ItemStack unavailStack = new ItemStack(Material.BARRIER);
        ItemMeta unavailStackMeta = unavailStack.getItemMeta();

        unavailStackMeta.setItemName("" + ChatColor.RED + ChatColor.BOLD + "No upgrader has been given.");
        unavailStackMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        unavailStack.setItemMeta(unavailStackMeta);

        return unavailStack;
    }

    private void openMembraneUpgradeGui(Player player) {
        // Create a 9x6 inventory (54 slots)
        Inventory gui = Bukkit.createInventory(player, 54, "" + ChatColor.BOLD + ChatColor.RED + "Upgrade your Membrane: ");

        // Create the red stained glass pane item


        // Fill the inventory with red stained-glass panes
        for (int i = 0; i < gui.getSize(); i++) {
            ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = redPane.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("" + ChatColor.RESET + ChatColor.BOLD + ChatColor.RED + ChatColor.MAGIC + "A"); // Set empty display name
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

        for (String powerName : plugin.getConfig().getConfigurationSection("players." + player.getName() + ".membranes").getKeys(false)) {
            if (plugin.getConfig().getBoolean("players." + player.getName() + ".membranes." + powerName)) {
                membraneItemMeta.setDisplayName("" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + MemoryUtils.toTitleCase(powerName) + ChatColor.RESET + ChatColor.GREEN + ChatColor.BOLD + " Membrane");
                membraneItemMeta.setLore(List.of(ChatColor.LIGHT_PURPLE + "- Level " + plugin.getConfig().getInt("players." + player.getName() + ".membranes.level", 1)));

                membraneStack.setItemMeta(membraneItemMeta);

                gui.setItem(thirdSlotDown, membraneStack);

            }
        }


        gui.setItem(secondSlotDown, null);

        gui.setItem(fifthSlotDown, getUnavailableStack());

        // Open the inventory for the player
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        // Prevent players from picking up items in the "Memory Upgrade" inventory
        if (event.getView().getTitle().equals("" + ChatColor.BOLD + ChatColor.RED + "Upgrade your Membrane: ")) {
            ItemMeta clickedMeta = event.getCurrentItem().getItemMeta();
//            if (
//                    clickedMeta.getDisplayName().equals("" + ChatColor.RESET + ChatColor.BOLD + ChatColor.RED + ChatColor.MAGIC + "A")
//                    || clickedMeta.getDisplayName().equals("" + ChatColor.GOLD + ChatColor.BOLD + "Confirm?")
//                    || clickedMeta.getDisplayName().contains("" + ChatColor.RESET + ChatColor.GREEN + ChatColor.BOLD + " Membrane"))
//            {
//                event.setCancelled(true);
//            }

            if (event.getClickedInventory() == event.getView().getTopInventory()) {
                event.setCancelled(true);

                if (clickedMeta.getDisplayName().equals("" + ChatColor.GOLD + ChatColor.BOLD + "Confirm?")) {
                    plugin.getConfig().set("players." + player.getName() + ".membranes.level", plugin.getConfig().getInt("players." + player.getName() + ".membranes.level", 1) + 1);
                    plugin.saveConfig();

                    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.f, 1.f);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Your membrane has been upgraded to " + ChatColor.BOLD + "Level " + plugin.getConfig().getInt("players." + player.getName() + ".membranes.level", 1) + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ".");

                }
            }
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        Player player = (Player) event.getWhoClicked();

        player.sendMessage("view title: " + event.getView().getTitle());
        player.sendMessage("clicked item title: " + player.getItemOnCursor());
        player.sendMessage("item 13: " + event.getInventory().getItem(13).getItemMeta().getDisplayName());
        player.sendMessage("has upgraderItemKey: " + String.valueOf(event.getInventory().getItem(13).getItemMeta().getPersistentDataContainer().has(plugin.upgraderItemKey)));
        if (event.getView().getTitle().equals("" + ChatColor.BOLD + ChatColor.RED + "Upgrade your Membrane: ")) {
            if (event.getInventory().getItem(13).getItemMeta().getPersistentDataContainer().has(plugin.upgraderItemKey)) {
                if (plugin.getConfig().getInt("players." + player.getName() + ".membranes.level", 1) < 2) {
                    event.getInventory().setItem(49, getConfirmStack());
                } else {
                    event.getInventory().setItem(49, getMaxLevelReacedStack());
                }
            } else {
                event.getInventory().setItem(49, getUnavailableStack());
            }
        }
    }
}
