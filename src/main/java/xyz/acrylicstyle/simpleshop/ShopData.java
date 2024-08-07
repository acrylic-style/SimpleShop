package xyz.acrylicstyle.simpleshop;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: remove amount, implement command
public class ShopData {
    private final @NotNull Material material;
    private final int amount;
    private final int buy;
    private final int sell;
    private final @Nullable String command;

    public ShopData(@NotNull Material material, int amount, int buy, int sell, @Nullable String command) {
        this.material = material;
        this.amount = amount;
        this.buy = buy;
        this.sell = sell;
        this.command = command;
    }

    public @NotNull Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public int getBuy() {
        return buy;
    }

    public int getSell() {
        return sell;
    }

    public @Nullable String getCommand() {
        return command;
    }
}
