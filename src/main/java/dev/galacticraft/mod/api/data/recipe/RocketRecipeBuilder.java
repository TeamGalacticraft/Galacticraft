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

package dev.galacticraft.mod.api.data.recipe;

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.mod.recipe.RocketRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class RocketRecipeBuilder extends GCRecipeBuilder {
    private int bodyHeight;
    private Ingredient body, cone, engine, fins;
    private Ingredient boosters = Ingredient.EMPTY;
    private RocketData rocketData;

    protected RocketRecipeBuilder(ItemLike result, int count) {
        super("rocket", result, count);
    }

    public static RocketRecipeBuilder create(ItemLike result) {
        return new RocketRecipeBuilder(result, 1);
    }

    public RocketRecipeBuilder bodyHeight(int bodyHeight) {
        this.bodyHeight = bodyHeight;
        return this;
    }

    public RocketRecipeBuilder body(ItemLike body) {
        this.body = Ingredient.of(body);
        return this;
    }

    public RocketRecipeBuilder body(TagKey<Item> body) {
        this.body = Ingredient.of(body);
        return this;
    }

    public RocketRecipeBuilder cone(ItemLike cone) {
        this.cone = Ingredient.of(cone);
        return this;
    }

    public RocketRecipeBuilder cone(TagKey<Item> cone) {
        this.cone = Ingredient.of(cone);
        return this;
    }

    public RocketRecipeBuilder engine(ItemLike engine) {
        this.engine = Ingredient.of(engine);
        return this;
    }

    public RocketRecipeBuilder engine(TagKey<Item> engine) {
        this.engine = Ingredient.of(engine);
        return this;
    }

    public RocketRecipeBuilder fins(ItemLike fins) {
        this.fins = Ingredient.of(fins);
        return this;
    }

    public RocketRecipeBuilder fins(TagKey<Item> fins) {
        this.fins = Ingredient.of(fins);
        return this;
    }

    public RocketRecipeBuilder boosters(ItemLike boosters) {
        this.boosters = Ingredient.of(boosters);
        return this;
    }

    public RocketRecipeBuilder boosters(TagKey<Item> boosters) {
        this.boosters = Ingredient.of(boosters);
        return this;
    }

    public RocketRecipeBuilder rocketData(RocketData rocketData) {
        this.rocketData = rocketData;
        return this;
    }

    @Override
    public Recipe<?> createRecipe(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }

        ItemStack result = new ItemStack(this.result, this.count);
        result.set(GCDataComponents.ROCKET_DATA, this.rocketData);
        return new RocketRecipe(this.group, result, this.bodyHeight, this.body, this.cone, this.engine, this.fins, this.boosters);
    }
}
