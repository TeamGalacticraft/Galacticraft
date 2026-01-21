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
import dev.galacticraft.mod.content.item.GCItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultCanningDisplay extends BasicDisplay {
    public static final BasicDisplay.Serializer<DefaultCanningDisplay> SERIALIZER = BasicDisplay.Serializer.ofSimple(DefaultCanningDisplay::createRaw);

    public DefaultCanningDisplay(ItemStack output) {
        super(getInputs(output), Collections.singletonList(EntryIngredients.of(output)), Optional.empty());
    }

    public DefaultCanningDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location) {
        super(inputs, outputs, location);
    }

    public static DefaultCanningDisplay createRaw(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location) {
        return new DefaultCanningDisplay(inputs, outputs, location);
    }

    @Override
    public CategoryIdentifier<? extends DefaultCanningDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.CANNING;
    }

    private static List<EntryIngredient> getInputs(ItemStack output) {
        List<EntryIngredient> inputs = new ArrayList<>();
        inputs.add(EntryIngredients.of(GCItems.EMPTY_CAN));
        CannedFoodItem.getContents(output).forEach(stack -> inputs.add(EntryIngredients.of(stack)));
        return inputs;
    }

    public int getWidth() {
        return 4;
    }

    public int getHeight() {
        return 4;
    }
}