package xyz.acrylicstyle.simpleshop.util;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class XMaterial {
    public static final Material OAK_SIGN = findMaterial("OAK_SIGN", "SIGN");

    public static @NotNull Material findMaterial(@NotNull String @NotNull ... names) {
        for (String name : names) {
            Material material = Material.matchMaterial(name);
            if (material != null) return material;
        }
        return Material.AIR;
    }
}
