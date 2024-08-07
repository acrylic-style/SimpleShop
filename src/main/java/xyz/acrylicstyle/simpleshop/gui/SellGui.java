package xyz.acrylicstyle.simpleshop.gui;

import xyz.acrylicstyle.simpleshop.ShopData;
import xyz.acrylicstyle.simpleshop.SimpleShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SellGui implements InventoryHolder, Listener {
    public static final SellGui INSTANCE = new SellGui();
    private final Inventory inventory = Bukkit.createInventory(this, 54, "売却");

    private SellGui() {}

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof SellGui)) return;
        long money = 0;
        List<ItemStack> reject = new ArrayList<>();
        for (ItemStack itemStack : e.getInventory()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            if (itemStack.hasItemMeta()) {
                reject.add(itemStack);
                continue;
            }
            ShopData data = SimpleShop.getInstance().findShopData(itemStack.getType());
            if (data == null || data.getSell() <= 0) {
                reject.add(itemStack);
                continue;
            }
            long sell = data.getSell();
            money += sell * itemStack.getAmount();
        }
        SimpleShop.getEconomy().depositPlayer((Player) e.getPlayer(), money);
        for (ItemStack itemStack : reject) {
            for (ItemStack stack : e.getPlayer().getInventory().addItem(itemStack).values()) {
                e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), stack);
            }
        }
        e.getPlayer().sendMessage("§aアイテムを売却し、" + SimpleShop.moneyAsString(money) + "§aが振り込まれました。");
        if (!reject.isEmpty()) {
            e.getPlayer().sendMessage("§a売却できなかったアイテムはインベントリに返却されました。");
        }
    }
}
