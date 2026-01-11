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
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

import static dev.galacticraft.mod.content.GCBlocks.FOOD_CANNER;

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
            if (CannedFoodItem.isEmptyCan(itemStack) || itemStack.getItem() == FOOD_CANNER.asItem()) {
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
        return Optional.of(CannedFoodItem.getDefaultCannedFoods().stream().map(stack -> new DefaultCanningDisplay(stack)).toList());
    }
}
