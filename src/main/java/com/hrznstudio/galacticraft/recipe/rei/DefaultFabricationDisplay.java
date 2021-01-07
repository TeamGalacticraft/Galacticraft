/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.recipe.rei;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.recipe.FabricationRecipe;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class DefaultFabricationDisplay implements RecipeDisplay {
    private final List<List<EntryStack>> input;
    private final List<EntryStack> output;

    public DefaultFabricationDisplay(FabricationRecipe recipe) {
        this.input = Lists.newArrayList();
        recipe.getPreviewInputs().forEach((ingredient) -> {
            List<EntryStack> stacks = new ArrayList<>();
            for (ItemStack stack : ingredient.getMatchingStacksClient()) {
                stacks.add(EntryStack.create(stack));
            }
            this.input.add(stacks);
        });

        this.output = Collections.singletonList(EntryStack.create(recipe.getOutput()));
    }

    @Override
    public List<List<EntryStack>> getInputEntries() {
        return this.input;
    }

    @Override
    public List<EntryStack> getOutputEntries() {
        return this.output;
    }

    public List<List<EntryStack>> getInput() {
        return this.input;
    }

    @Override
    public Identifier getRecipeCategory() {
        return GalacticraftREIPlugin.CIRCUIT_FABRICATION;
    }
}