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

package dev.galacticraft.mod.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.galacticraft.mod.recipe.GCRecipes;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;

public class ShapedCompressorRecipeBuilder extends GCRecipeBuilder {
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();

    protected ShapedCompressorRecipeBuilder(ItemLike result, int count) {
        super(GCRecipes.SHAPED_COMPRESSING_SERIALIZER, "compressing", result, count);
    }

    public static ShapedCompressorRecipeBuilder create(ItemLike itemLike) {
        return new ShapedCompressorRecipeBuilder(itemLike, 1);
    }

    public static ShapedCompressorRecipeBuilder create(ItemLike itemLike, int i) {
        return new ShapedCompressorRecipeBuilder(itemLike, i);
    }

    public ShapedCompressorRecipeBuilder define(Character character, TagKey<Item> tagKey) {
        return this.define(character, Ingredient.of(tagKey));
    }

    public ShapedCompressorRecipeBuilder define(Character character, ItemLike itemLike) {
        return this.define(character, Ingredient.of(itemLike));
    }

    public ShapedCompressorRecipeBuilder define(Character character, Ingredient ingredient) {
        if (this.key.containsKey(character)) {
            throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
        } else if (character == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(character, ingredient);
            return this;
        }
    }

    public ShapedCompressorRecipeBuilder pattern(String string) {
        if (!this.pattern.isEmpty() && string.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.pattern.add(string);
            return this;
        }
    }

    @Override
    public void serializeRecipeData(JsonObject jsonRecipe) {
        var patternJson = new JsonArray();

        for(var string : this.pattern) {
            patternJson.add(string);
        }

        jsonRecipe.add("pattern", patternJson);
        var keyJson = new JsonObject();

        for(var entry : this.key.entrySet()) {
            keyJson.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
        }

        jsonRecipe.add("key", keyJson);
        this.createResult(jsonRecipe);
    }
}
