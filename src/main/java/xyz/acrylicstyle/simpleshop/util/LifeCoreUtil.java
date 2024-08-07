package xyz.acrylicstyle.simpleshop.util;

import xyz.acrylicstyle.simpleshop.util.external.LifeCoreBridge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class LifeCoreUtil {
    public static boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("LifeCore");
    }

    public static void promptSign(Player player, @NotNull Consumer<List<String>> consumer) {
        if (!isAvailable()) {
            throw new IllegalStateException("LifeCore is not available");
        }
        LifeCoreBridge.promptSign(player, consumer);
    }
}
