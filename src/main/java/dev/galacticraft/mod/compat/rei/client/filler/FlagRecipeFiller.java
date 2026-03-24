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

import dev.galacticraft.mod.content.item.FlagItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FlagRecipe;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.categories.crafting.filler.CraftingRecipeFiller;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapedDisplay;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class FlagRecipeFiller implements CraftingRecipeFiller<FlagRecipe> {
    protected static final int BANNERS_PER_COLOR = 2;
    protected static final int PATTERNS_PER_BANNER = 2;

    @Override
    public Collection<Display> apply(RecipeHolder<FlagRecipe> flagRecipeRecipeHolder) {
        System.out.println(flagRecipeRecipeHolder.value());

        Random random = new Random();
        List<Holder.Reference<BannerPattern>> availablePatterns = BasicDisplay.registryAccess().registry(Registries.BANNER_PATTERN)
                .map(registry -> registry.holders().toList())
                .orElseGet(List::of);

        if (availablePatterns.isEmpty()) {
            return List.of();
        }

        EntryIngredient.Builder banners = EntryIngredient.builder(16 * BANNERS_PER_COLOR * PATTERNS_PER_BANNER);
        EntryIngredient.Builder flags = EntryIngredient.builder(16 * BANNERS_PER_COLOR * PATTERNS_PER_BANNER);
        for (DyeColor color : DyeColor.values()) {
            for (int i = 0; i < BANNERS_PER_COLOR; i++) {
                ItemStack banner = generateRandomBanner(color, availablePatterns, random);
                ItemStack flag = FlagItem.fromBanner(banner);

                banners.add(EntryStacks.of(banner));
                flags.add(EntryStacks.of(flag));
            }
        }

        EntryIngredient banner = banners.build();
        EntryIngredient flag = flags.build();

        EntryIngredient.unifyFocuses(banner, flag);

        List<EntryIngredient> inputs = List.of(
                EntryIngredients.of(GCItems.STEEL_POLE), banner,
                EntryIngredients.of(GCItems.STEEL_POLE), EntryIngredient.empty(),
                EntryIngredients.of(GCItems.STEEL_POLE), EntryIngredient.empty()
        );

        return List.of(new DefaultCustomShapedDisplay(flagRecipeRecipeHolder, inputs, List.of(flag), 2, 3));
    }

    public static ItemStack generateRandomBanner(DyeColor baseColor, List<Holder.Reference<BannerPattern>> availablePatterns, Random random) {
        final int layerCount = 2;

        BannerPatternLayers layers = new BannerPatternLayers(new ArrayList<>(layerCount));
        for (int i = 0; i < layerCount; i++) {
            layers.layers().add(new BannerPatternLayers.Layer(
                    availablePatterns.get(random.nextInt(availablePatterns.size())),
                    DyeColor.byId(random.nextInt(16))
            ));
        }

        ItemStack banner = new ItemStack(BannerBlock.byColor(baseColor).asItem());
        banner.set(DataComponents.BANNER_PATTERNS, layers);

        return banner;
    }

    @Override
    public Class<FlagRecipe> getRecipeClass() {
        return FlagRecipe.class;
    }
}
