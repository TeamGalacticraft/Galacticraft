package com.hrznstudio.galacticraft.items;

import com.hrznstudio.galacticraft.api.item.EnergyHolderItem;
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
    public int getMaxEnergy(ItemStack battery) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack itemStack_1) {
        return true;
    }
}