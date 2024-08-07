package xyz.acrylicstyle.simpleshop.commands;

import xyz.acrylicstyle.simpleshop.SimpleShop;
import xyz.acrylicstyle.simpleshop.gui.CategoryGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ShopCommand implements TabExecutor {
    private final SimpleShop plugin;

    public ShopCommand(SimpleShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("simpleshop.admin")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.reload();
                    sender.sendMessage("Reloaded SimpleShop configuration.");
                    return true;
                }
            }
        }
        ((Player) sender).openInventory(new CategoryGui(plugin.getShops()).getInventory());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("simpleshop.admin")) {
            if (args.length == 1) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }
}
