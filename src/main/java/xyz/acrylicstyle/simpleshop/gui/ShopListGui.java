package xyz.acrylicstyle.simpleshop.gui;

import xyz.acrylicstyle.simpleshop.ShopData;
import xyz.acrylicstyle.simpleshop.SimpleShop;
import xyz.acrylicstyle.simpleshop.util.ItemUtil;
import xyz.acrylicstyle.simpleshop.util.LifeCoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShopListGui implements InventoryHolder {
    private final Inventory inventory = Bukkit.createInventory(this, 54, "ショップ");
    private final Inventory previousScreen;
    private final List<ShopData> items;
    private final List<ShopData> itemsInCurrentPage = new ArrayList<>();
    private int page = 0;

    public ShopListGui(@NotNull Inventory previousScreen, @NotNull List<ShopData> items) {
        this.previousScreen = previousScreen;
        this.items = items;
        resetItems();
    }

    public void resetItems() {
        inventory.clear();
        itemsInCurrentPage.clear();
        int index = 0;
        for (int i = page * 45; i < items.size(); i++) {
            if (index >= 45) break;
            ShopData data = items.get(i);
            inventory.setItem(index++, getItem(data));
            itemsInCurrentPage.add(data);
        }
        inventory.setItem(45, ItemUtil.createItemStack(Material.ARROW, "§e前のページ"));
        inventory.setItem(53, ItemUtil.createItemStack(Material.ARROW, "§e次のページ"));
        inventory.setItem(49, ItemUtil.createItemStack(Material.ARROW, "§a戻る"));
    }

    private @NotNull ItemStack getItem(@NotNull ShopData data) {
        ItemStack item = new ItemStack(data.getMaterial());
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (data.getBuy() == 0) {
            lore.add("§c購入不可");
        } else {
            lore.add("§a購入: " + SimpleShop.moneyAsString(data.getBuy()));
        }
        if (data.getSell() == 0) {
            lore.add("§c売却不可");
        } else {
            lore.add("§a売却: " + SimpleShop.moneyAsString(data.getSell()));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static class EventListener implements Listener {
        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (e.getInventory().getHolder() instanceof ShopListGui) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (!(e.getInventory().getHolder() instanceof ShopListGui)) {
                return;
            }
            e.setCancelled(true);
            ShopListGui gui = (ShopListGui) e.getInventory().getHolder();
            if (e.getClickedInventory() == null) return;
            if (e.getClickedInventory().getHolder() != gui) return;
            if (e.getSlot() == 53 && gui.itemsInCurrentPage.size() == 45) {
                gui.page++;
                gui.resetItems();
            }
            if (e.getSlot() == 45 && gui.page > 0) {
                gui.page--;
                gui.resetItems();
            }
            if (e.getSlot() == 49) {
                e.getWhoClicked().openInventory(gui.previousScreen);
            }
            if (e.getSlot() >= gui.itemsInCurrentPage.size()) return;
            ShopData data = gui.itemsInCurrentPage.get(e.getSlot());
            if (data.getBuy() <= 0) {
                e.getWhoClicked().sendMessage("§cこのアイテムは購入できません。");
                return;
            }
            if (e.isLeftClick() || !LifeCoreUtil.isAvailable()) {
                e.getWhoClicked().openInventory(new ShopGui(gui.getInventory(), data, 0).getInventory());
            } else if (e.isRightClick()) {
                Player player = (Player) e.getWhoClicked();
                LifeCoreUtil.promptSign(player, lines -> {
                    try {
                        String input = String.join("", lines);
                        if (input.equalsIgnoreCase("max") || input.equalsIgnoreCase("all")) {
                            Bukkit.getScheduler().runTask(SimpleShop.getInstance(), () ->
                                    e.getWhoClicked().openInventory(new ShopGui(gui.getInventory(), data, ItemUtil.countEmpty(player.getInventory()) * 64).getInventory()));
                            return;
                        }
                        int amount = Integer.parseInt(input);
                        Bukkit.getScheduler().runTask(SimpleShop.getInstance(), () ->
                                e.getWhoClicked().openInventory(new ShopGui(gui.getInventory(), data, amount).getInventory()));
                    } catch (NumberFormatException ex) {
                        e.getWhoClicked().sendMessage("§c数値を入力してください。");
                        e.getWhoClicked().openInventory(gui.getInventory());
                    }
                });
            }
        }
    }
}
