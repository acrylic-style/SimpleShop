package xyz.acrylicstyle.simpleshop.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ItemUtil {
    public static @NotNull ItemStack createItemStack(@NotNull Material type, @NotNull String name, @NotNull String @NotNull ... lore) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static int countEmpty(@NotNull PlayerInventory inventory) {
        int count = 0;
        for (ItemStack stack : inventory) {
            if (stack == null || stack.getType() == Material.AIR) count++;
        }
        // don't count armor and offhand slots
        if (inventory.getHelmet() == null || inventory.getHelmet().getType() == Material.AIR) count--;
        if (inventory.getChestplate() == null || inventory.getChestplate().getType() == Material.AIR) count--;
        if (inventory.getLeggings() == null || inventory.getLeggings().getType() == Material.AIR) count--;
        if (inventory.getBoots() == null || inventory.getBoots().getType() == Material.AIR) count--;
        if (inventory.getItemInOffHand() == null || inventory.getItemInOffHand().getType() == Material.AIR) count--;
        return count;
    }
}
