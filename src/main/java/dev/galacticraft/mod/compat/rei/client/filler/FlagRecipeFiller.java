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

package dev.galacticraft.mod.compat.rei.client.filler;

import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FlagRecipe;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.client.categories.crafting.filler.CraftingRecipeFiller;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapedDisplay;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FlagRecipeFiller implements CraftingRecipeFiller<FlagRecipe> {
    @Override
    public Collection<Display> apply(RecipeHolder<FlagRecipe> flagRecipeRecipeHolder) {
        List<Display> displays = new ArrayList<>();
        for (Holder<Item> item : BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.BANNERS)) {
            if (item.value() instanceof BannerItem banner && item.value().components().has(DataComponents.BANNER_PATTERNS)) {
                DyeColor color = banner.getColor();
                ItemStack flag = new ItemStack(GCItems.FLAGS.get(color));
                flag.set(DataComponents.BANNER_PATTERNS, banner.components().get(DataComponents.BANNER_PATTERNS));

                List<EntryIngredient> ingredients = List.of(
                        EntryIngredients.of(GCItems.STEEL_POLE), EntryIngredients.of(banner),
                        EntryIngredients.of(GCItems.STEEL_POLE), EntryIngredient.empty(),
                        EntryIngredients.of(GCItems.STEEL_POLE), EntryIngredient.empty()
                );
                Display display = new DefaultCustomShapedDisplay(
                        flagRecipeRecipeHolder,
                        ingredients,
                        List.of(EntryIngredients.of(flag)),
                        2, 3
                );

                displays.add(display);

//                CraftingRecipe recipe = new ShapedRecipe(GROUP, CraftingBookCategory.BUILDING, recipePattern, flag, false);
//                recipes.add(new RecipeHolder<>(Constant.id(GROUP + "." + color.getName()), recipe));
            }
        }

        return displays;
    }

    @Override
    public Class<FlagRecipe> getRecipeClass() {
        return FlagRecipe.class;
    }
}
