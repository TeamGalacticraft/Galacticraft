package dev.galacticraft.mod.compat.omnishape;

import dev.omnishape.api.OmnishapeData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;

public class OmnishapeCompat {
    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded("omnishape");
    }

    public static boolean canExtractFromItem(ItemStack stack) {
        if (isLoaded()) {
            return OmnishapeData.canExtractFromItem(stack);
        }
        return false;
    }
}