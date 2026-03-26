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

package dev.galacticraft.mod.compat.rei.common.display;

import dev.galacticraft.mod.content.item.FlagItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FlagRecipe;
import dev.galacticraft.mod.tag.GCItemTags;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapedDisplay;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.*;

public class FlagDisplayGenerator implements DynamicDisplayGenerator<DefaultCraftingDisplay<?>> {
    @Override
    public Optional<List<DefaultCraftingDisplay<?>>> getRecipeFor(EntryStack<?> entry) {
        if (entry.getValue() instanceof ItemStack stack) {
            if (stack.is(GCItemTags.FLAGS)) {
                return Optional.of(List.of(createDisplay(FlagItem.toBanner(stack), stack)));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<DefaultCraftingDisplay<?>>> getUsageFor(EntryStack<?> entry) {
        if (entry.getValue() instanceof ItemStack stack) {
            if (stack.is(ItemTags.BANNERS)) {
                return Optional.of(List.of(createDisplay(stack, FlagItem.fromBanner(stack))));
            } else if (stack.is(GCItems.STEEL_POLE)) {
                return randomFlagRecipe();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<DefaultCraftingDisplay<?>>> generate(ViewSearchBuilder builder) {
        if (builder.getRecipesFor().isEmpty() && builder.getUsagesFor().isEmpty()) {
            return randomFlagRecipe();
        }
        return Optional.empty();
    }

    public static DefaultCustomShapedDisplay createDisplay(ItemStack banner, ItemStack flag) {
        return DefaultCustomShapedDisplay.simple(
                ingredientsFromBanner(EntryIngredients.of(banner.copyWithCount(1))),
                List.of(EntryIngredients.of(flag)),
                2, 3,
                Optional.empty()
        );
    }

    public static List<EntryIngredient> ingredientsFromBanner(EntryIngredient banner) {
        return List.of(
                EntryIngredients.of(GCItems.STEEL_POLE), banner,
                EntryIngredients.of(GCItems.STEEL_POLE), EntryIngredient.empty(),
                EntryIngredients.of(GCItems.STEEL_POLE), EntryIngredient.empty()
        );
    }

    public static Optional<List<DefaultCraftingDisplay<?>>> randomFlagRecipe() {
        Random random = new Random();
        List<Holder.Reference<BannerPattern>> availablePatterns = BasicDisplay.registryAccess().registry(Registries.BANNER_PATTERN)
                .map(registry -> registry.holders().toList())
                .orElseGet(List::of);

        if (availablePatterns.isEmpty()) {
            return Optional.empty();
        }

        List<DefaultCraftingDisplay<?>> recipes = new ArrayList<>(16);

        for (DyeColor color : DyeColor.values()) {
            EntryIngredient.Builder banners = EntryIngredient.builder(3);
            EntryIngredient.Builder flags = EntryIngredient.builder(3);

            ItemStack plainBanner = new ItemStack(BannerBlock.byColor(color));
            banners.add(EntryStacks.of(plainBanner));
            flags.add(EntryStacks.of(FlagItem.fromBanner(plainBanner)));

            for (int i = 0; i < 2; i++) {
                ItemStack banner = FlagRecipe.randomBanner(color, availablePatterns, random);
                ItemStack flag = FlagItem.fromBanner(banner);

                banners.add(EntryStacks.of(banner));
                flags.add(EntryStacks.of(flag));
            }

            EntryIngredient banner = banners.build();
            EntryIngredient flag = flags.build();

            EntryIngredient.unifyFocuses(banner, flag);

            recipes.add(DefaultCustomShapedDisplay.simple(ingredientsFromBanner(banner), List.of(flag), 2, 3, Optional.empty()));
        }

        return Optional.of(recipes);
    }
}
