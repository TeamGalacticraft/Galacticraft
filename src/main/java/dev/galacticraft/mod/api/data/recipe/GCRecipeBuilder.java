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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.data.EmiDefaultRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class GCRecipeBuilder<T extends GCRecipeBuilder<T>> implements RecipeBuilder {
    protected final String prefix;
    @Nullable
    protected final RecipeCategory category;
    protected final Item result;
    protected final int count;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    protected String group = "";
    protected boolean emiDefaultRecipe = false;

    protected GCRecipeBuilder(String prefix, RecipeCategory category, ItemLike result, int count) {
        this.prefix = prefix;
        this.category = category;
        this.result = result.asItem();
        this.count = count;
    }

    @Override
    public T unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return (T) this;
    }

    @Override
    public T group(@Nullable String group) {
        this.group = group;
        return (T) this;
    }

    public T emiDefaultRecipe(boolean emiDefaultRecipe) {
        this.emiDefaultRecipe = emiDefaultRecipe;
        return (T) this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        Advancement.Builder builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        ResourceLocation resourceLocation = id.withPrefix(this.prefix);
        ResourceLocation resourceLocation2;
        if (this.category != null) {
            resourceLocation2 = id.withPrefix("recipes/" + this.category.getFolderName() + "/");
        } else {
            resourceLocation2 = id.withPrefix("recipes/");
        }
        output.accept(resourceLocation, createRecipe(id), builder.build(resourceLocation2));

        if (this.emiDefaultRecipe) {
            EmiDefaultRecipeProvider.add(resourceLocation);
        }
    }

    @Override
    public void save(RecipeOutput output, String path) {
        this.save(output, Constant.id(path));
    }

    public abstract Recipe<?> createRecipe(ResourceLocation id);
}
