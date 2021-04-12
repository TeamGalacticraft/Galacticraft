/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FabricationRecipe implements Recipe<Container> {
    final String group;
    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;

    public FabricationRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
    }

    @Override
    public ItemStack assemble(Container inventory) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int var1, int var2) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.input);
        return list;
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        return this.input.test(inventory.getItem(0));
    }

    @Override
    public RecipeType<?> getType() {
        return GalacticraftRecipes.FABRICATION_TYPE;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipes.FABRICATION_SERIALIZER;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public String getGroup() {
        return group;
    }
}