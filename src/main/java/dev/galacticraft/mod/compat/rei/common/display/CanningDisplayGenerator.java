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

package dev.galacticraft.mod.compat.rei.common.display;

import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.galacticraft.mod.content.GCBlocks.FOOD_CANNER;
import static dev.galacticraft.mod.content.item.GCItems.EMPTY_CAN;

public class CanningDisplayGenerator implements DynamicDisplayGenerator<DefaultCanningDisplay> {

    @Override
    public Optional<List<DefaultCanningDisplay>> getRecipeFor(EntryStack<?> entry) {
        if (entry.getValue() instanceof ItemStack itemStack && CannedFoodItem.isCannedFoodItem(itemStack)) {
            return Optional.of(List.of(new DefaultCanningDisplay(itemStack)));
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<DefaultCanningDisplay>> getUsageFor(EntryStack<?> entry) {
        if (entry.getValue() instanceof ItemStack itemStack) {
            if (itemStack.getItem() == EMPTY_CAN || itemStack.getItem() == FOOD_CANNER.asItem()) {
                return this.defaultCanningDisplays();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<DefaultCanningDisplay>> generate(ViewSearchBuilder builder) {
        if (builder.getCategories().contains(GalacticraftREIServerPlugin.CANNING)) {
            return this.defaultCanningDisplays();
        }
        return Optional.empty();
    }

    private Optional<List<DefaultCanningDisplay>> defaultCanningDisplays() {
        List<DefaultCanningDisplay> displays = new ArrayList<>();
        for (ItemStack cannedFoodItem : CannedFoodItem.getDefaultCannedFoods()) {
            List<EntryIngredient> entries = CannedFoodItem.getContents(cannedFoodItem).stream().map(stack -> EntryIngredients.of(stack)).collect(Collectors.toList());
            displays.add(new DefaultCanningDisplay(entries, List.of(EntryIngredients.of(cannedFoodItem)), Optional.empty()));
        }
        return Optional.of(displays);
    }
}