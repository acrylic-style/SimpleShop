package xyz.acrylicstyle.simpleshop.gui;

import xyz.acrylicstyle.simpleshop.ShopData;
import xyz.acrylicstyle.simpleshop.SimpleShop;
import xyz.acrylicstyle.simpleshop.util.ItemUtil;
import xyz.acrylicstyle.simpleshop.util.LifeCoreUtil;
import xyz.acrylicstyle.simpleshop.util.XMaterial;
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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ShopGui implements InventoryHolder {
    private final Inventory inventory = Bukkit.createInventory(this, 54, "購入画面");
    private final Inventory previousScreen;
    private final ShopData data;
    private int customAmount;

    public ShopGui(@NotNull Inventory previousScreen, @NotNull ShopData data, int customAmount) {
        this.previousScreen = previousScreen;
        this.data = data;
        this.customAmount = Math.max(1, customAmount);
        resetItems();
    }

    public void resetItems() {
        inventory.clear();
        inventory.setItem(19, getItem(1));
        inventory.setItem(20, getItem(2));
        inventory.setItem(21, getItem(4));
        inventory.setItem(22, getItem(8));
        inventory.setItem(23, getItem(16));
        inventory.setItem(24, getItem(32));
        inventory.setItem(25, getItem(64));
        if (LifeCoreUtil.isAvailable()) {
            inventory.setItem(30, ItemUtil.createItemStack(XMaterial.OAK_SIGN, "§e数を指定する"));
            if (customAmount <= 0) {
                customAmount = 1;
            }
            if (customAmount > 3000) {
                customAmount = 3000;
            }
            inventory.setItem(32, getItem(customAmount));
        }
        inventory.setItem(49, ItemUtil.createItemStack(Material.ARROW, "§a戻る"));
    }

    private @NotNull ItemStack getItem(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        ItemStack item = new ItemStack(data.getMaterial());
        item.setAmount(Math.min(64, amount));
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(
                "§aアイテム数: §e" + amount + "個",
                "§a購入: " + SimpleShop.moneyAsString((long) data.getBuy() * amount)
        ));
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
            if (e.getInventory().getHolder() instanceof ShopGui) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (!(e.getInventory().getHolder() instanceof ShopGui)) {
                return;
            }
            e.setCancelled(true);
            ShopGui gui = (ShopGui) e.getInventory().getHolder();
            if (e.getClickedInventory() == null) return;
            if (e.getClickedInventory().getHolder() != gui) return;
            if (e.getSlot() == 49) {
                e.getWhoClicked().openInventory(gui.previousScreen);
            }
            if (e.getSlot() == 30 && LifeCoreUtil.isAvailable()) {
                LifeCoreUtil.promptSign((Player) e.getWhoClicked(), lines -> {
                    String input = String.join("", lines);
                    if (input.equalsIgnoreCase("max") || input.equalsIgnoreCase("all")) {
                        gui.customAmount = ItemUtil.countEmpty(e.getWhoClicked().getInventory()) * 64;
                        gui.resetItems();
                        Bukkit.getScheduler().runTask(SimpleShop.getInstance(), () -> e.getWhoClicked().openInventory(gui.getInventory()));
                        return;
                    }
                    try {
                        gui.customAmount = Math.max(1, Integer.parseInt(input));
                        gui.resetItems();
                    } catch (NumberFormatException ex) {
                        e.getWhoClicked().sendMessage("§c数は数字で指定してください");
                    }
                    Bukkit.getScheduler().runTask(SimpleShop.getInstance(), () -> e.getWhoClicked().openInventory(gui.getInventory()));
                });
            }
            int amount = -1;
            switch (e.getSlot()) {
                case 19:
                    amount = 1;
                    break;
                case 20:
                    amount = 2;
                    break;
                case 21:
                    amount = 4;
                    break;
                case 22:
                    amount = 8;
                    break;
                case 23:
                    amount = 16;
                    break;
                case 24:
                    amount = 32;
                    break;
                case 25:
                    amount = 64;
                    break;
                case 32:
                    amount = gui.customAmount;
                    break;
            }
            if (amount != -1) {
                int maxAmount = ItemUtil.countEmpty(e.getWhoClicked().getInventory()) * 64;
                if (amount > maxAmount) {
                    amount = maxAmount;
                }
                long price = (long) gui.data.getBuy() * amount;
                if (SimpleShop.getEconomy().withdrawPlayer((Player) e.getWhoClicked(), price).transactionSuccess()) {
                    AtomicInteger droppedAmount = new AtomicInteger();
                    ItemStack item = new ItemStack(gui.data.getMaterial());
                    item.setAmount(amount);
                    e.getWhoClicked().getInventory().addItem(item).forEach((slot, dropped) -> droppedAmount.addAndGet(dropped.getAmount()));
                    if (droppedAmount.get() > 0) {
                        // drop item 64 items each
                        item.setAmount(droppedAmount.get() % 64);
                        e.getWhoClicked().getInventory().addItem(item);
                        item.setAmount(64);
                        for (int i = 0; i < droppedAmount.get() / 64; i++) {
                            e.getWhoClicked().getWorld().dropItem(e.getWhoClicked().getLocation(), item);
                        }
                    }
                    e.getWhoClicked().sendMessage("§aアイテムを" + amount + "個購入し、" + SimpleShop.moneyAsString(price) + "§aが口座から引き落とされました。");
                } else {
                    e.getWhoClicked().sendMessage("§cお金が足りません");
                }
            }
        }
    }
}
