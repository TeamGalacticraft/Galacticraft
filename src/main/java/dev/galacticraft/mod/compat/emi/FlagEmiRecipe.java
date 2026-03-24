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

package dev.galacticraft.mod.compat.emi;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.FlagItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FlagRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.List;
import java.util.Random;

public class FlagEmiRecipe extends EmiPatternCraftingRecipe {
    public FlagEmiRecipe() {
        super(
                List.of(EmiIngredient.of(ItemTags.BANNERS), EmiStack.of(GCItems.STEEL_POLE)),
                EmiStack.EMPTY,
                Constant.id('/' + Constant.Recipe.FLAG),
                true
        );
    }

    @Override
    public List<EmiStack> getOutputs() {
        return GCItems.FLAGS.colorMap().values().stream().map(EmiStack::of).toList();
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        if (slot == 1) {
            return new GeneratedSlotWidget(random -> EmiStack.of(randomBanner(random)), this.unique, x, y);
        } else if (slot % 3 == 0) {
            return new SlotWidget(EmiStack.of(GCItems.STEEL_POLE), x, y);
        }
        return new SlotWidget(EmiStack.EMPTY, x, y);
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget(random -> EmiStack.of(FlagItem.fromBanner(randomBanner(random))), this.unique, x, y);
    }

    public static ItemStack randomBanner(Random random) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return ItemStack.EMPTY;
        }

        List<Holder.Reference<BannerPattern>> availablePatterns = level.registryAccess().registry(Registries.BANNER_PATTERN)
                .map(registry -> registry.holders().toList())
                .orElseGet(List::of);

        if (availablePatterns.isEmpty()) {
            return ItemStack.EMPTY;
        }

        DyeColor baseColor = DyeColor.byId(random.nextInt(16));
        return FlagRecipe.randomBanner(baseColor, availablePatterns, random);
    }
}
