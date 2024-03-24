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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;

/**
 * Armor, tools, and other gear recipes
 */
public class GCGearRecipeProvider extends FabricRecipeProvider {
    public GCGearRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        // Armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_HELMET)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("PPP")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_CHESTPLATE)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("P P")
                .pattern("PPP")
                .pattern("PPP")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_LEGGINGS)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("PPP")
                .pattern("P P")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_BOOTS)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("P P")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_HELMET)
                .define('D', GCItems.DESH_INGOT)
                .pattern("DDD")
                .pattern("D D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_CHESTPLATE)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D D")
                .pattern("DDD")
                .pattern("DDD")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_LEGGINGS)
                .define('D', GCItems.DESH_INGOT)
                .pattern("DDD")
                .pattern("D D")
                .pattern("D D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_BOOTS)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D D")
                .pattern("D D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_PADDING_HELMET)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("CCC")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_PADDING_CHESTPIECE)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("C C")
                .pattern("CCC")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_PADDING_LEGGINGS)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("CCC")
                .pattern("C C")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_PADDING_BOOTS)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("C C")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SENSOR_GLASSES)
                .define('D', ConventionalItemTags.DIAMONDS)
                .define('S', Items.STRING)
                .define('L', GCItems.SENSOR_LENS)
                .define('M', GCItems.METEORIC_IRON_INGOT)
                .pattern("SDS")
                .pattern("S S")
                .pattern("LML")
                .unlockedBy(getHasName(GCItems.SENSOR_LENS), has(GCItems.SENSOR_LENS))
                .save(output);

        // Heavy-Duty Tools
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_SHOVEL)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("S")
                .pattern("W")
                .pattern("W")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_PICKAXE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("SSS")
                .pattern(" W ")
                .pattern(" W ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_AXE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("SS")
                .pattern("SW")
                .pattern(" W")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_AXE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("SS")
                .pattern("WS")
                .pattern("W ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output, Constant.id("heavy_duty_axe_flipped"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_HOE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("SS")
                .pattern(" W")
                .pattern(" W")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_HOE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("SS")
                .pattern("W ")
                .pattern("W ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output, Constant.id("heavy_duty_hoe_flipped"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_SWORD)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("S")
                .pattern("S")
                .pattern("W")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        // Desh Tools
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.DESH_SHOVEL)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("D")
                .pattern("S")
                .pattern("S")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.DESH_PICKAXE)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("DDD")
                .pattern(" S ")
                .pattern(" S ")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.DESH_AXE)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("DD")
                .pattern("DS")
                .pattern(" S")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.DESH_AXE)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("DD")
                .pattern("SD")
                .pattern("S ")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output, Constant.id("desh_axe_flipped"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.DESH_HOE)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("DD")
                .pattern(" S")
                .pattern(" S")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.DESH_HOE)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("DD")
                .pattern("S ")
                .pattern("S ")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output, Constant.id("desh_hoe_flipped"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.DESH_SWORD)
                .define('D', GCItems.DESH_INGOT)
                .define('S', GCItems.DESH_STICK)
                .pattern("D")
                .pattern("S")
                .pattern("S")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);
        
        // Smithing Recipes
        titaniumSmithing(output, GCItems.DESH_CHESTPLATE, RecipeCategory.COMBAT, GCItems.TITANIUM_CHESTPLATE);
        titaniumSmithing(output, GCItems.DESH_LEGGINGS, RecipeCategory.COMBAT, GCItems.TITANIUM_LEGGINGS);
        titaniumSmithing(output, GCItems.DESH_HELMET, RecipeCategory.COMBAT, GCItems.TITANIUM_HELMET);
        titaniumSmithing(output, GCItems.DESH_BOOTS, RecipeCategory.COMBAT, GCItems.TITANIUM_BOOTS);
        titaniumSmithing(output, GCItems.DESH_SWORD, RecipeCategory.COMBAT, GCItems.TITANIUM_SWORD);
        titaniumSmithing(output, GCItems.DESH_AXE, RecipeCategory.TOOLS, GCItems.TITANIUM_AXE);
        titaniumSmithing(output, GCItems.DESH_PICKAXE, RecipeCategory.TOOLS, GCItems.TITANIUM_PICKAXE);
        titaniumSmithing(output, GCItems.DESH_HOE, RecipeCategory.TOOLS, GCItems.TITANIUM_HOE);
        titaniumSmithing(output, GCItems.DESH_SHOVEL, RecipeCategory.TOOLS, GCItems.TITANIUM_SHOVEL);

        // Other Items

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, GCItems.THROWABLE_METEOR_CHUNK, 3)
                .requires(GCItems.RAW_METEORIC_IRON)
                .unlockedBy(getHasName(GCItems.RAW_METEORIC_IRON), has(GCItems.RAW_METEORIC_IRON))
                .save(output);

        simpleCookingRecipe(output, "smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 200, GCItems.THROWABLE_METEOR_CHUNK, GCItems.HOT_THROWABLE_METEOR_CHUNK, 0.7F);
        simpleCookingRecipe(output, "blasting", RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, 100, GCItems.THROWABLE_METEOR_CHUNK, GCItems.HOT_THROWABLE_METEOR_CHUNK, 0.7F);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.STANDARD_WRENCH)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("  S")
                .pattern(" B ")
                .pattern("B  ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);
    }

    private static void titaniumSmithing(RecipeOutput output, Item input, RecipeCategory category, Item result) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(GCItems.TITANTIUM_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(input), Ingredient.of(GCItems.COMPRESSED_TITANIUM), category, result
                )
                .unlocks(getHasName(GCItems.COMPRESSED_TITANIUM), has(GCItems.COMPRESSED_TITANIUM))
                .save(output, getItemName(result) + "_smithing");
    }

    @Override
    public String getName() {
        return "Gear Recipes";
    }
}
