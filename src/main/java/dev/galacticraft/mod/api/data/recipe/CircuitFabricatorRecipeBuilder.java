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

package dev.galacticraft.mod.api.data.recipe;

import dev.galacticraft.mod.recipe.FabricationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class CircuitFabricatorRecipeBuilder extends GCRecipeBuilder {

    private Ingredient ingredient;
    private int time;

    public CircuitFabricatorRecipeBuilder(ItemLike result, int count) {
        super("fabrication", result, count);
    }

    public static CircuitFabricatorRecipeBuilder create(ItemLike itemLike) {
        return new CircuitFabricatorRecipeBuilder(itemLike, 1);
    }

    public static CircuitFabricatorRecipeBuilder create(ItemLike itemLike, int i) {
        return new CircuitFabricatorRecipeBuilder(itemLike, i);
    }

    public CircuitFabricatorRecipeBuilder requires(TagKey<Item> tagKey) {
        this.ingredient = Ingredient.of(tagKey);
        return this;
    }

    public CircuitFabricatorRecipeBuilder requires(ItemLike itemLike) {
        this.ingredient = Ingredient.of(itemLike);
        return this;
    }

    public CircuitFabricatorRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    @Override
    public Recipe<?> createRecipe(ResourceLocation id) {
        return new FabricationRecipe(this.group, this.ingredient, new ItemStack(this.result, this.count), this.time > 1 ? this.time : 300);
    }
}