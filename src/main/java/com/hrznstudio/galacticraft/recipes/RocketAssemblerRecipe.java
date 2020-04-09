/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.recipes;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerRecipe implements Recipe<Inventory> {
    private final Identifier id;

    private final Identifier output;
    private final DefaultedList<ItemStack> input;

    public RocketAssemblerRecipe(Identifier id, Identifier output, DefaultedList<ItemStack> input) {
        this.id = id;
        this.output = output;
        this.input = input;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipes.ROCKET_ASSEMBLER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return GalacticraftRecipes.ROCKET_ASSEMBLER_TYPE;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    public Identifier getPartOutput() {
        return output;
    }

    @Override
    public boolean matches(Inventory inv, World world_1) {
        RecipeFinder finder = new RecipeFinder();
        int found = 0;

        for (int i = 0; i < inv.getInvSize(); ++i) {
            ItemStack itemStack_1 = inv.getInvStack(i);
            if (!itemStack_1.isEmpty()) {
                ++found;
                finder.addItem(itemStack_1);
            }
        }

        return found == this.input.size() && finder.findRecipe(this, null);
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return ItemStack.EMPTY;
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int int_1, int int_2) {
        return false;
    }

    public DefaultedList<ItemStack> getInput() {
        return this.input;
    }
}