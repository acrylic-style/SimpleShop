package xyz.acrylicstyle.simpleshop.gui;

import xyz.acrylicstyle.simpleshop.ShopData;
import xyz.acrylicstyle.simpleshop.SimpleShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryGui implements InventoryHolder {
    private final Inventory inventory = Bukkit.createInventory(this, 54, "カテゴリ選択");
    private final @NotNull Map<@NotNull String, @NotNull List<@NotNull ShopData>> shops;
    private final List<List<ShopData>> shopList = new ArrayList<>();

    public CategoryGui(@NotNull Map<@NotNull String, @NotNull List<@NotNull ShopData>> shops) {
        this.shops = shops;
        resetItems();
    }

    public void resetItems() {
        inventory.clear();
        List<Map.@Nullable Entry<String, List<ShopData>>> layout = SimpleShop.getInstance().getLayout();
        int index = 0;
        if (layout.isEmpty()) {
            // auto generated layout
            for (String category : shops.keySet()) {
                putCategoryItem(index++, category);
                shopList.add(shops.get(category));
            }
        } else {
            // custom layout
            for (Map.Entry<String, List<ShopData>> entry : layout) {
                if (entry == null) {
                    index++;
                    shopList.add(null);
                    continue;
                }
                String category = entry.getKey();
                List<ShopData> shopData = entry.getValue();
                putCategoryItem(index++, category);
                shopList.add(shopData);
            }
        }
    }

    private void putCategoryItem(int index, String category) {
        String[] split = category.split(":");
        ItemStack item = new ItemStack(split.length == 1 ? Material.NETHER_STAR : Material.valueOf(split[1]));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a" + split[0]);
        item.setItemMeta(meta);
        inventory.setItem(index, item);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static class EventListener implements Listener {
        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (e.getInventory().getHolder() instanceof CategoryGui) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (!(e.getInventory().getHolder() instanceof CategoryGui)) {
                return;
            }
            e.setCancelled(true);
            CategoryGui gui = (CategoryGui) e.getInventory().getHolder();
            if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof CategoryGui)) {
                return;
            }
            int slot = e.getSlot();
            if (slot >= gui.shopList.size()) return;
            List<ShopData> shopData = gui.shopList.get(slot);
            if (shopData == null) return;
            e.getWhoClicked().openInventory(new ShopListGui(gui.inventory, shopData).getInventory());
        }
    }
}
