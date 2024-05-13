/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.google.common.collect.Lists;
import dev.galacticraft.mod.recipe.ShapelessCompressingRecipe;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class DefaultShapelessCompressingDisplay implements DefaultCompressingDisplay {
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;

    public DefaultShapelessCompressingDisplay(RecipeHolder<ShapelessCompressingRecipe> recipe) {
        this.input = Lists.newArrayList();
        recipe.value().getIngredients().forEach((ingredient) -> {
            for (ItemStack stack : ingredient.getItems()) {
                input.add(EntryIngredients.of(stack));
            }
        });
        this.output = Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(BasicDisplay.registryAccess())));
    }

    public DefaultShapelessCompressingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        this.input = inputs;
        this.output = outputs;
    }

    @Override
    public @NotNull List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public @NotNull List<EntryIngredient> getOutputEntries() {
        return output;
    }
}