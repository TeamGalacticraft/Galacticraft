/*
 * Copyright (c) 2018-2019 Horizon Studio
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

package com.hrznstudio.galacticraft.recipes.rei;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.recipes.FabricationRecipe;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class DefaultFabricationDisplay implements RecipeDisplay {
    private FabricationRecipe display;
    private List<List<ItemStack>> input;
    private List<ItemStack> output;

    public DefaultFabricationDisplay(FabricationRecipe recipe) {
        this.display = recipe;
        this.input = Lists.newArrayList();
        recipe.getPreviewInputs().forEach((ingredient) -> {
            this.input.add(Arrays.asList(ingredient.getStackArray()));
        });
        this.output = Arrays.asList(recipe.getOutput());
    }

    @Override
    public List<List<ItemStack>> getInput() {
        return this.input;
    }

    @Override
    public List<ItemStack> getOutput() {
        return this.output;
    }

    @Override
    public List<List<ItemStack>> getRequiredItems() {
        return this.input;
    }

    @Override
    public Identifier getRecipeCategory() {
        return GalacticraftREIPlugin.CIRCUIT_FABRICATION;
    }
}