package io.github.teamgalacticraft.galacticraft.items;

import io.github.teamgalacticraft.galacticraft.api.EnergyHolderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InfiniteBatteryItem extends Item implements EnergyHolderItem {
    public InfiniteBatteryItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isInfinite() {
        return true;
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack itemStack_1) {
        return true;
    }
}