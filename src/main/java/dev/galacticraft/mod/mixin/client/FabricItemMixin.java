package dev.galacticraft.mod.mixin.client;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static dev.galacticraft.mod.content.item.CannedFoodItem.getCanFoodProperties;
import static dev.galacticraft.mod.content.item.CannedFoodItem.isCannedFoodItem;

@Mixin(FabricItem.class)
public interface FabricItemMixin {
    /**
     * @author me
     * @reason allows canned food to be eaten with different amounts of nutrition for each item
     */
    @Overwrite
    default @Nullable FoodProperties getFoodComponent(ItemStack stack) {
        if (isCannedFoodItem(stack))
        {
            return getCanFoodProperties(stack);
        }else
        {
            return ((Item) this).getFoodProperties();
        }
    }
}
