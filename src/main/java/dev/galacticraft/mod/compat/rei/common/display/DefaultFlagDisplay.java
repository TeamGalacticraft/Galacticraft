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
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultFlagDisplay extends DefaultCraftingDisplay<FlagRecipe> {
    public static final BasicDisplay.Serializer<DefaultFlagDisplay> SERIALIZER = BasicDisplay.Serializer.ofSimpleRecipeLess(DefaultFlagDisplay::new);

    public DefaultFlagDisplay(ItemStack output) {
        super(getInputs(FlagItem.toBanner(output)), Collections.singletonList(EntryIngredients.of(output)), Optional.empty());
    }

    public DefaultFlagDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs, Optional.empty());
    }

    public static DefaultFlagDisplay fromBanner(ItemStack stack) {
        List<EntryIngredient> outputs = Collections.singletonList(EntryIngredients.of(FlagItem.fromBanner(stack)));
        return new DefaultFlagDisplay(getInputs(stack), outputs);
    }

    public static List<EntryIngredient> getInputs(ItemStack banner) {
        List<EntryIngredient> inputs = new ArrayList<>(4);
        inputs.add(EntryIngredients.of(GCItems.STEEL_POLE)); inputs.add(EntryIngredients.of(banner));
        inputs.add(EntryIngredients.of(GCItems.STEEL_POLE)); inputs.add(EntryIngredient.empty());
        inputs.add(EntryIngredients.of(GCItems.STEEL_POLE)); inputs.add(EntryIngredient.empty());

        return inputs;
    }

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 3;
    }
}
