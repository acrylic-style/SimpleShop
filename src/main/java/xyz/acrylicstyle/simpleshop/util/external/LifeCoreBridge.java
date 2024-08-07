package xyz.acrylicstyle.simpleshop.util.external;

import com.github.mori01231.lifecore.util.PromptSign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class LifeCoreBridge {
    public static void promptSign(Player player, @NotNull Consumer<List<String>> consumer) {
        PromptSign.promptSign(player, consumer);
    }
}
