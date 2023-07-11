/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class GCRecipeBuilder implements RecipeBuilder, FinishedRecipe {

    private final RecipeSerializer<?> type;
    private final String subPath;

    protected final Item result;
    protected final int count;
    private String group = "";

    protected GCRecipeBuilder(RecipeSerializer<?> type, String subPath, ItemLike result, int count) {
        this.type = type;
        this.subPath = subPath;
        this.result = result.asItem();
        this.count = count;
    }

    private ResourceLocation recipeId;

    @Override
    public ResourceLocation getId() {
        return this.recipeId;
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return null;
    }

    @Override
    public RecipeBuilder unlockedBy(String string, CriterionTriggerInstance criterionTriggerInstance) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
        this.recipeId = new ResourceLocation(resourceLocation.getNamespace(), this.subPath + '/' + resourceLocation.getPath());
        consumer.accept(this);
    }

    @Override
    public JsonObject serializeRecipe() {
        JsonObject jsonRecipe = FinishedRecipe.super.serializeRecipe();
        if (!this.group.isEmpty()) {
            jsonRecipe.addProperty("group", this.group);
        }
        return jsonRecipe;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return this.type;
    }

    protected void createResult(JsonObject jsonRecipe) {
        JsonObject itemJson = new JsonObject();
        itemJson.addProperty("item", BuiltInRegistries.ITEM.getKey(getResult()).toString());
        if (this.count > 1) {
            itemJson.addProperty("count", this.count);
        }

        jsonRecipe.add("result", itemJson);
    }
}
