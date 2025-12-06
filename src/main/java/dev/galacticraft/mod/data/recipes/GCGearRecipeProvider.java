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

package dev.galacticraft.mod.data.recipes;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.data.recipe.GCCookingRecipeBuilder;
import dev.galacticraft.mod.api.data.recipe.GCShapedRecipeBuilder;
import dev.galacticraft.mod.api.data.recipe.GCShapelessRecipeBuilder;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.data.EmiDefaultRecipeProvider;
import dev.galacticraft.mod.tag.GCItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

/**
 * Armor, tools, and other gear recipes
 */
public class GCGearRecipeProvider extends FabricRecipeProvider {
    public GCGearRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        // Armor
        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_HELMET)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("PPP")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_CHESTPLATE)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("P P")
                .pattern("PPP")
                .pattern("PPP")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_LEGGINGS)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("PPP")
                .pattern("P P")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_BOOTS)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("P P")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.DESH_HELMET)
                .define('D', GCItems.DESH_INGOT)
                .pattern("DDD")
                .pattern("D D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.DESH_CHESTPLATE)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D D")
                .pattern("DDD")
                .pattern("DDD")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.DESH_LEGGINGS)
                .define('D', GCItems.DESH_INGOT)
                .pattern("DDD")
                .pattern("D D")
                .pattern("D D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.DESH_BOOTS)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D D")
                .pattern("D D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.THERMAL_PADDING_HELMET)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("CCC")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.THERMAL_PADDING_CHESTPIECE)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("C C")
                .pattern("CCC")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.THERMAL_PADDING_LEGGINGS)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("CCC")
                .pattern("C C")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.THERMAL_PADDING_BOOTS)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("C C")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.ISOTHERMAL_PADDING_HELMET)
                .define('C', GCItems.ISOTHERMAL_FABRIC)
                .pattern("CCC")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.ISOTHERMAL_FABRIC), has(GCItems.ISOTHERMAL_FABRIC))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.ISOTHERMAL_PADDING_CHESTPIECE)
                .define('C', GCItems.ISOTHERMAL_FABRIC)
                .pattern("C C")
                .pattern("CCC")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.ISOTHERMAL_FABRIC), has(GCItems.ISOTHERMAL_FABRIC))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.ISOTHERMAL_PADDING_LEGGINGS)
                .define('C', GCItems.ISOTHERMAL_FABRIC)
                .pattern("CCC")
                .pattern("C C")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.ISOTHERMAL_FABRIC), has(GCItems.ISOTHERMAL_FABRIC))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.ISOTHERMAL_PADDING_BOOTS)
                .define('C', GCItems.ISOTHERMAL_FABRIC)
                .pattern("C C")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.ISOTHERMAL_FABRIC), has(GCItems.ISOTHERMAL_FABRIC))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.SENSOR_GLASSES)
                .define('D', ConventionalItemTags.DIAMOND_GEMS)
                .define('S', ConventionalItemTags.STRINGS)
                .define('L', GCItems.SENSOR_LENS)
                .define('M', GCItems.METEORIC_IRON_INGOT)
                .pattern("SDS")
                .pattern("S S")
                .pattern("LML")
                .unlockedBy(getHasName(GCItems.SENSOR_LENS), has(GCItems.SENSOR_LENS))
                .emiDefaultRecipe(true)
                .save(output);

        // Heavy-Duty Tools
        GCShapedRecipeBuilder.crafting(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_SHOVEL)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .define('S', ConventionalItemTags.WOODEN_RODS)
                .pattern("P")
                .pattern("S")
                .pattern("S")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_PICKAXE)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .define('S', ConventionalItemTags.WOODEN_RODS)
                .pattern("PPP")
                .pattern(" S ")
                .pattern(" S ")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_AXE)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .define('S', ConventionalItemTags.WOODEN_RODS)
                .pattern("PP")
                .pattern("PS")
                .pattern(" S")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_HOE)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .define('S', ConventionalItemTags.WOODEN_RODS)
                .pattern("PP")
                .pattern(" S")
                .pattern(" S")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_SWORD)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .define('S', ConventionalItemTags.WOODEN_RODS)
                .pattern("P")
                .pattern("P")
                .pattern("S")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .emiDefaultRecipe(true)
                .save(output);

        // Desh Tools
        GCShapedRecipeBuilder.crafting(RecipeCategory.TOOLS, GCItems.DESH_SHOVEL)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("D")
                .pattern("S")
                .pattern("S")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.TOOLS, GCItems.DESH_PICKAXE)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("DDD")
                .pattern(" S ")
                .pattern(" S ")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.TOOLS, GCItems.DESH_AXE)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("DD")
                .pattern("DS")
                .pattern(" S")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.TOOLS, GCItems.DESH_HOE)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("DD")
                .pattern(" S")
                .pattern(" S")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);
        GCShapedRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.DESH_SWORD)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("D")
                .pattern("D")
                .pattern("S")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);

        // Smithing Recipes
        titaniumSmithing(output, RecipeCategory.COMBAT, GCItems.DESH_CHESTPLATE, GCItems.TITANIUM_CHESTPLATE);
        titaniumSmithing(output, RecipeCategory.COMBAT, GCItems.DESH_LEGGINGS, GCItems.TITANIUM_LEGGINGS);
        titaniumSmithing(output, RecipeCategory.COMBAT, GCItems.DESH_HELMET, GCItems.TITANIUM_HELMET);
        titaniumSmithing(output, RecipeCategory.COMBAT, GCItems.DESH_BOOTS, GCItems.TITANIUM_BOOTS);
        titaniumSmithing(output, RecipeCategory.COMBAT, GCItems.DESH_SWORD, GCItems.TITANIUM_SWORD);
        titaniumSmithing(output, RecipeCategory.TOOLS, GCItems.DESH_AXE, GCItems.TITANIUM_AXE);
        titaniumSmithing(output, RecipeCategory.TOOLS, GCItems.DESH_PICKAXE, GCItems.TITANIUM_PICKAXE);
        titaniumSmithing(output, RecipeCategory.TOOLS, GCItems.DESH_HOE, GCItems.TITANIUM_HOE);
        titaniumSmithing(output, RecipeCategory.TOOLS, GCItems.DESH_SHOVEL, GCItems.TITANIUM_SHOVEL);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.TITANTIUM_UPGRADE_SMITHING_TEMPLATE, 2)
                .define('D', ConventionalItemTags.DIAMOND_GEMS)
                .define('T', GCItems.TITANTIUM_UPGRADE_SMITHING_TEMPLATE)
                .define('A', GCItemTags.ASTEROID_ROCKS)
                .pattern("DTD")
                .pattern("DAD")
                .pattern("DDD")
                .unlockedBy(getHasName(GCItems.TITANTIUM_UPGRADE_SMITHING_TEMPLATE), has(GCItems.TITANTIUM_UPGRADE_SMITHING_TEMPLATE))
                .save(output);

        // Other Items
        GCShapelessRecipeBuilder.crafting(RecipeCategory.COMBAT, GCItems.THROWABLE_METEOR_CHUNK, 3)
                .requires(GCItems.RAW_METEORIC_IRON)
                .unlockedBy(getHasName(GCItems.RAW_METEORIC_IRON), has(GCItems.RAW_METEORIC_IRON))
                .emiDefaultRecipe(true)
                .save(output);

        oreSmeltingAndBlasting(output, RecipeCategory.COMBAT, GCItems.THROWABLE_METEOR_CHUNK, GCItems.HOT_THROWABLE_METEOR_CHUNK, 0.1F, 100, true);

        GCShapedRecipeBuilder.crafting(RecipeCategory.TOOLS, GCItems.STANDARD_WRENCH)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("  S")
                .pattern(" B ")
                .pattern("B  ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .emiDefaultRecipe(true)
                .save(output);
    }

    private static void titaniumSmithing(RecipeOutput output, RecipeCategory category, Item input, Item result) {
        String path = getItemName(result) + "_smithing";

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(GCItems.TITANTIUM_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(input), Ingredient.of(GCItems.COMPRESSED_TITANIUM), category, result
                )
                .unlocks(getHasName(GCItems.COMPRESSED_TITANIUM), has(GCItems.COMPRESSED_TITANIUM))
                .save(output, path);

        EmiDefaultRecipeProvider.add(Constant.id(path));
    }

    private static void oreSmeltingAndBlasting(RecipeOutput output, RecipeCategory category, ItemLike input, ItemLike result, float experience, int cookingTime, boolean emiDefaultRecipe) {
        Ingredient ingredient = Ingredient.of(input);
        String hasName = RecipeProvider.getHasName(input);
        var criterion = RecipeProvider.has(input);
        String itemName = RecipeProvider.getItemName(result);

        GCCookingRecipeBuilder.smelting(ingredient, category, result, experience, cookingTime * 2)
                .unlockedBy(hasName, criterion)
                .emiDefaultRecipe(emiDefaultRecipe)
                .save(output, itemName + "_from_smelting");
        GCCookingRecipeBuilder.blasting(ingredient, category, result, experience, cookingTime)
                .unlockedBy(hasName, criterion)
                .save(output, itemName + "_from_blasting");
    }

    @Override
    public String getName() {
        return "Gear Recipes";
    }
}
