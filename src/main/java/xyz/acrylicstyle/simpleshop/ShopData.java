package xyz.acrylicstyle.simpleshop;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ShopData {
    private final @NotNull Material material;
    private final int buy;
    private final int sell;

    public ShopData(@NotNull Material material, int buy, int sell) {
        this.material = material;
        this.buy = buy;
        this.sell = sell;
    }

    public @NotNull Material getMaterial() {
        return material;
    }

    public int getBuy() {
        return buy;
    }

    public int getSell() {
        return sell;
    }
}
