package dev.galacticraft.mod.content;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class CannedFoodTooltip implements TooltipComponent {
    private final NonNullList<ItemStack> items;

    public CannedFoodTooltip(NonNullList<ItemStack> nonNullList)
    {
        this.items = nonNullList;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }
}
