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

package dev.galacticraft.mod.data;

import dev.galacticraft.mod.content.item.GCItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class GCRecipeProvider extends FabricRecipeProvider {
    public GCRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<FinishedRecipe> exporter) {
        // Special
        ShapedRecipeBuilder.shaped(GCItem.ROCKET_LAUNCH_PAD, 9)
                .define('C', GCItem.COMPRESSED_IRON)
                .define('I', Items.IRON_BLOCK)
                .pattern("CCC")
                .pattern("III")
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
                .save(exporter);
        ShapedRecipeBuilder.shaped(GCItem.CRYOGENIC_CHAMBER, 1)
                .define('D', GCItem.COMPRESSED_DESH)
                .define('P', GCItem.TIER_1_HEAVY_DUTY_PLATE) // change to tag?
                .define('B', ItemTags.BEDS)
                .pattern("DPD")
                .pattern("DBD")
                .pattern("DPD")
                .unlockedBy(getHasName(GCItem.COMPRESSED_DESH), has(GCItem.COMPRESSED_DESH))
                .save(exporter);

        // Ingots
        RecipeProvider.nineBlockStorageRecipes(exporter, GCItem.ALUMINUM_NUGGET, GCItem.ALUMINUM_INGOT);
        RecipeProvider.nineBlockStorageRecipes(exporter, GCItem.DESH_NUGGET, GCItem.DESH_INGOT);
        RecipeProvider.nineBlockStorageRecipes(exporter, GCItem.LEAD_NUGGET, GCItem.LEAD_INGOT);
        RecipeProvider.nineBlockStorageRecipes(exporter, GCItem.METEORIC_IRON_NUGGET, GCItem.METEORIC_IRON_INGOT);
        RecipeProvider.nineBlockStorageRecipes(exporter, GCItem.TIN_NUGGET, GCItem.TIN_INGOT);
        RecipeProvider.nineBlockStorageRecipes(exporter, GCItem.TITANIUM_NUGGET, GCItem.TITANIUM_INGOT);

        // Ores
        // this.ingotSmelting(exporter, Items.COPPER_INGOT, GCItem.MOON_COPPER_ORE, GCItem.LUNASLATE_COPPER_ORE); // tags would complicate things here
        // this.ingotSmelting(exporter, GCItem.DESH_INGOT, null);
    }

    private void ingotSmelting(Consumer<FinishedRecipe> exporter, Item ingot, TagKey<Item> tag) { // better than minecraft implementation since we can use tags
        Ingredient ingredient = Ingredient.of(tag);
        SimpleCookingRecipeBuilder.smelting(ingredient, ingot, (float)0.1, 200)
                .unlockedBy("has_" + tag.location().getPath(), has(tag)) // fabric should extract the middle
                .save(exporter, RecipeProvider.getSmeltingRecipeName(ingot));
        SimpleCookingRecipeBuilder.smelting(ingredient, ingot, (float)0.05, 100)
                .unlockedBy("has_" + tag.location().getPath(), has(tag))
                .save(exporter, RecipeProvider.getBlastingRecipeName(ingot));
    }
    private void ingotSmelting(Consumer<FinishedRecipe> exporter, Item ingot, ItemLike... items) { // ItemLike or Items
        Ingredient ingredient = Ingredient.of(items);
        SimpleCookingRecipeBuilder smelt = SimpleCookingRecipeBuilder.smelting(ingredient, ingot, (float)0.1, 200);
        SimpleCookingRecipeBuilder blast = SimpleCookingRecipeBuilder.smelting(ingredient, ingot, (float)0.05, 100);
        
        for (ItemLike item : items) {
            String hasName = getHasName(item);
            TriggerInstance hasTrigger = has(item);
            smelt.unlockedBy(hasName, hasTrigger);
            blast.unlockedBy(hasName, hasTrigger);
        }

        smelt.save(exporter, RecipeProvider.getSmeltingRecipeName(ingot));
        blast.save(exporter, RecipeProvider.getBlastingRecipeName(ingot));
    }
}
