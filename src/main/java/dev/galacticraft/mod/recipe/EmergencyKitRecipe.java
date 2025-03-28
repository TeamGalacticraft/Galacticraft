/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.recipe;

import dev.galacticraft.mod.content.item.EmergencyKitItem;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.content.item.OxygenTankItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import java.util.List;

public class EmergencyKitRecipe extends CustomRecipe {
    public EmergencyKitRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        if (craftingInput.ingredientCount() != 9 || craftingInput.width() != 3 || craftingInput.height() != 3) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                ItemStack itemStack = EmergencyKitItem.EMERGENCY_ITEMS.get(j + i * 3).copy();
                if (itemStack.getItem() instanceof OxygenTankItem tankItem) {
                    itemStack = OxygenTankItem.getFullTank(tankItem);
                } else if (itemStack.getComponents().has(DataComponents.FOOD)) {
                    ItemStack itemStack2 = itemStack.copyWithCount(CannedFoodItem.MAX_FOOD);
                    itemStack = GCItems.CANNED_FOOD.getDefaultInstance();
                    CannedFoodItem.add(itemStack, itemStack2);
                }

                if (ItemStack.isSameItemSameComponents(craftingInput.getItem(j, i), itemStack)) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        return GCItems.EMERGENCY_KIT.getDefaultInstance();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GCRecipes.EMERGENCY_KIT_SERIALIZER;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n >= 3 && n2 >= 3;
    }
}
