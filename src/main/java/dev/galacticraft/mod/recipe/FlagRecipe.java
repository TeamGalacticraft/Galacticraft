/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.mod.content.item.FlagItem;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FlagRecipe extends CustomRecipe {
    public FlagRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != 4) {
            return false;
        }

        boolean flipped = false;
        ItemStack banner = input.getItem(1, 0);
        if (invalidBanner(banner)) {
            banner = input.getItem(0, 0);
            if (invalidBanner(banner)) {
                return false;
            }

            flipped = true;
        }

        int poleX = flipped ? 1 : 0;
        for (int y = 0; y < 3; y++) {
            if (!input.getItem(poleX, y).is(GCItems.STEEL_POLE)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
        ItemStack banner = input.getItem(1, 0);
        if (invalidBanner(banner)) {
            banner = input.getItem(0, 0);
        }

        return FlagItem.fromBanner(banner);
    }

    private static boolean invalidBanner(ItemStack stack) {
        return !(stack.getItem() instanceof BannerItem) || !stack.has(DataComponents.BANNER_PATTERNS);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return GCRecipes.FLAG_SERIALIZER;
    }
}
