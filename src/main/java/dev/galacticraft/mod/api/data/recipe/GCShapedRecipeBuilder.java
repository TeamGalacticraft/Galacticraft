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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.galacticraft.mod.recipe.ShapedCompressingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GCShapedRecipeBuilder extends GCCraftingRecipeBuilder {
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private int time = 200;

    protected GCShapedRecipeBuilder(RecipeCategory category, ItemLike result, int count) {
        super(category, result, count);
    }

    public static GCShapedRecipeBuilder crafting(RecipeCategory category, ItemLike itemLike) {
        return new GCShapedRecipeBuilder(category, itemLike, 1);
    }

    public static GCShapedRecipeBuilder crafting(RecipeCategory category, ItemLike itemLike, int count) {
        return new GCShapedRecipeBuilder(category, itemLike, count);
    }

    public static GCShapedRecipeBuilder compressing(ItemLike itemLike) {
        return new GCShapedRecipeBuilder(null, itemLike, 1);
    }

    public static GCShapedRecipeBuilder compressing(ItemLike itemLike, int count) {
        return new GCShapedRecipeBuilder(null, itemLike, count);
    }

    public GCShapedRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    public GCShapedRecipeBuilder define(Character character, TagKey<Item> tagKey) {
        return this.define(character, Ingredient.of(tagKey));
    }

    public GCShapedRecipeBuilder define(Character character, ItemLike itemLike) {
        return this.define(character, Ingredient.of(itemLike));
    }

    public GCShapedRecipeBuilder define(Character character, Ingredient ingredient) {
        if (this.key.containsKey(character)) {
            throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
        } else if (character == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(character, ingredient);
            return this;
        }
    }

    public GCShapedRecipeBuilder pattern(String string) {
        if (!this.rows.isEmpty() && string.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(string);
            return this;
        }
    }

    @Override
    public GCShapedRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public GCShapedRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public GCShapedRecipeBuilder emiDefault(boolean emiDefault) {
        this.emiDefault = emiDefault;
        return this;
    }

    @Override
    public Recipe<?> createRecipe(ResourceLocation id) {
        String groupName = Objects.requireNonNullElse(this.group, "");
        ShapedRecipePattern shapedRecipePattern = this.ensureValid(id);
        ItemStack itemStack = new ItemStack(this.result, this.count);
        if (this.category != null) {
            return new ShapedRecipe(groupName, RecipeBuilder.determineBookCategory(this.category), shapedRecipePattern, itemStack, true);
        } else {
            return new ShapedCompressingRecipe(groupName, shapedRecipePattern, itemStack, this.time);
        }
    }

    private ShapedRecipePattern ensureValid(ResourceLocation resourceLocation) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
        } else {
            return ShapedRecipePattern.of(this.key, this.rows);
        }
    }
}
