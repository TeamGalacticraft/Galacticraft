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

import dev.galacticraft.mod.recipe.ShapelessCompressingRecipe;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ElectricShapelessCompressingDisplay implements ElectricCompressingDisplay {
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;
    private final int processingTime;

    public ElectricShapelessCompressingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, int processingTime) {
        this.input = inputs;
        this.output = outputs;
        this.processingTime = processingTime;
    }

    public ElectricShapelessCompressingDisplay(RecipeHolder<ShapelessCompressingRecipe> recipe) {
        this.input = recipe.value().getIngredients().stream().map(EntryIngredients::ofIngredient).toList();
        ItemStack stack = recipe.value().getResultItem(BasicDisplay.registryAccess());
        this.output = Collections.singletonList(EntryIngredients.of(stack));
        this.processingTime = (int) (recipe.value().getTime() / 1.5F);
    }

    @Override
    public @NotNull List<EntryIngredient> getInputEntries() {
        return this.input;
    }

    @Override
    public @NotNull List<EntryIngredient> getOutputEntries() {
        return this.output;
    }

    @Override
    public int getProcessingTime() {
        return this.processingTime;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}