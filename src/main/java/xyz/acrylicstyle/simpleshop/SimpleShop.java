package xyz.acrylicstyle.simpleshop;

import xyz.acrylicstyle.simpleshop.commands.ShopCommand;
import xyz.acrylicstyle.simpleshop.gui.CategoryGui;
import xyz.acrylicstyle.simpleshop.gui.SellGui;
import xyz.acrylicstyle.simpleshop.gui.ShopGui;
import xyz.acrylicstyle.simpleshop.gui.ShopListGui;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SimpleShop extends JavaPlugin {
    private final Map<String, List<ShopData>> shops = new HashMap<>();
    private final List<Map.@Nullable Entry<String, List<ShopData>>> layout = new ArrayList<>();

    @Override
    public void onEnable() {
        reload();
        Bukkit.getScheduler().runTask(this, () -> {
            Objects.requireNonNull(getCommand("shop")).setExecutor(new ShopCommand(this));
            Objects.requireNonNull(getCommand("sell")).setExecutor((sender, command, s, strings) -> {
                if (sender instanceof Player) {
                    SellGui.INSTANCE.getInventory().clear();
                    ((Player) sender).openInventory(SellGui.INSTANCE.getInventory());
                }
                return true;
            });
        });
        Bukkit.getPluginManager().registerEvents(new CategoryGui.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShopListGui.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShopGui.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(SellGui.INSTANCE, this);
    }

    @SuppressWarnings("unchecked")
    public void reload() {
        reloadConfig();
        shops.clear();
        layout.clear();
        ConfigurationSection shopsSection = getConfig().getConfigurationSection("shops");
        for (String name : shopsSection.getKeys(false)) {
            for (Object o : shopsSection.getList(name)) {
                if (o instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) o;
                    Material material = Material.getMaterial((String) map.get("material"));
                    int amount = (int) map.get("amount");
                    int buy = (int) map.get("buy");
                    int sell = (int) map.get("sell");
                    String command = (String) map.get("command");
                    ShopData data = new ShopData(material, amount, buy, sell, command);
                    shops.computeIfAbsent(name, k -> new ArrayList<>()).add(data);
                } else if (o instanceof String) {
                    // material[;amount];buy;sell[;command]
                    String s = (String) o;
                    String[] split = s.split(";");
                    Material material = Material.valueOf(split[0]);
                    int amount = split.length == 3 ? 1 : Integer.parseInt(split[1]);
                    int buy = Integer.parseInt(split[split.length == 3 ? 1 : 2]);
                    int sell = Integer.parseInt(split[split.length == 3 ? 2 : 3]);
                    String command = split.length <= 4 ? null : split[4];
                    ShopData data = new ShopData(material, amount, buy, sell, command);
                    shops.computeIfAbsent(name, k -> new ArrayList<>()).add(data);
                }
            }
        }
        for (Object o : getConfig().getList("layout")) {
            if (o == null) {
                layout.add(null);
            }
            if (o instanceof String) {
                String name = (String) o;
                layout.add(new AbstractMap.SimpleEntry<>(name, shops.get(name)));
            }
        }
    }

    public @NotNull Map<@NotNull String, @NotNull List<@NotNull ShopData>> getShops() {
        return shops;
    }

    public List<Map.@Nullable Entry<String, List<ShopData>>> getLayout() {
        return layout;
    }

    public @Nullable ShopData findShopData(@NotNull Material type) {
        for (List<ShopData> list : shops.values()) {
            for (ShopData data : list) {
                if (data.getMaterial() == type) {
                    return data;
                }
            }
        }
        return null;
    }

    public static @NotNull Economy getEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) throw new RuntimeException("Economy provider is not found");
        return rsp.getProvider();
    }

    public static @NotNull SimpleShop getInstance() {
        return JavaPlugin.getPlugin(SimpleShop.class);
    }

    @Contract(pure = true)
    public static @NotNull String formatPrice(long l) {
        String preFormatted = String.format("%,d", l);
        if (l >= 1_000_000_000_000_000_000L) {
            return "§5" + preFormatted;
        } else if (l >= 1_000_000_000_000_000L) {
            return "§6" + preFormatted;
        } else if (l >= 1_000_000_000_000L) {
            return "§d" + preFormatted;
        } else if (l >= 1_000_000_000L) {
            return "§b" + preFormatted;
        } else if (l >= 1_000_000L) {
            return "§a" + preFormatted;
        } else {
            return "§f" + preFormatted;
        }
    }

    @Contract(pure = true)
    public static @NotNull String toFriendlyString(long number) {
        List<String> suffixes = Arrays.asList("", "", "万", "億", "兆", "京");
        double suffixNum = Math.ceil(("" + number).length() / 4.0);
        double shortValue = Math.floor(number / Math.pow(10000.0, suffixNum - 1) * 100) / 100;
        String suffix = suffixes.get((int) suffixNum);
        if (((long) shortValue) == shortValue) {
            return String.format("%,.0f", shortValue) + suffix;
        }
        return shortValue + suffix;
    }

    @Contract(pure = true)
    public static @NotNull String moneyAsString(long money) {
        return SimpleShop.formatPrice(money) + "円 (" + SimpleShop.toFriendlyString(money) + ")";
    }
}
