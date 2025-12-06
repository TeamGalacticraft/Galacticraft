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

import dev.galacticraft.mod.api.data.recipe.GCCookingRecipeBuilder;
import dev.galacticraft.mod.api.data.recipe.GCShapedRecipeBuilder;
import dev.galacticraft.mod.api.data.recipe.GCShapelessRecipeBuilder;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.EmergencyKitRecipe;
import dev.galacticraft.mod.tag.GCItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

/**
 * Intermediate crafting materials and food recipes.
 */
public class GCMiscRecipeProvider extends FabricRecipeProvider {
    public GCMiscRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        // Gear
        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.OXYGEN_MASK)
                .define('G', ConventionalItemTags.GLASS_PANES_COLORLESS)
                .define('H', Items.IRON_HELMET)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .unlockedBy(getHasName(Items.GLASS_PANE), has(ConventionalItemTags.GLASS_PANES_COLORLESS))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.OXYGEN_GEAR)
                .define('Y', GCItemTags.GLASS_FLUID_PIPES)
                .define('X', GCItems.OXYGEN_CONCENTRATOR)
                .pattern(" Y ")
                .pattern("YXY")
                .pattern("Y Y")
                .unlockedBy(getHasName(GCBlocks.GLASS_FLUID_PIPE), has(GCBlocks.GLASS_FLUID_PIPE))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.OXYGEN_CONCENTRATOR)
                .define('Z', GCItems.COMPRESSED_STEEL)
                .define('W', GCItems.COMPRESSED_TIN)
                .define('Y', GCItemTags.TIN_CANISTERS)
                .define('X', GCItems.OXYGEN_VENT)
                .pattern("ZWZ")
                .pattern("WYW")
                .pattern("ZXZ")
                .unlockedBy(getHasName(GCItems.OXYGEN_VENT), has(GCItems.OXYGEN_VENT))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.SMALL_OXYGEN_TANK)
                .define('W', Items.LIME_WOOL)
                .define('T', GCItemTags.TIN_CANISTERS)
                .define('C', GCItems.COMPRESSED_COPPER)
                .pattern("W")
                .pattern("T")
                .pattern("C")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItemTags.TIN_CANISTERS))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.MEDIUM_OXYGEN_TANK)
                .define('W', Items.ORANGE_WOOL)
                .define('T', GCItemTags.TIN_CANISTERS)
                .define('C', GCItems.COMPRESSED_TIN)
                .pattern("WW")
                .pattern("TT")
                .pattern("CC")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItemTags.TIN_CANISTERS))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.LARGE_OXYGEN_TANK)
                .define('W', Items.RED_WOOL)
                .define('T', GCItemTags.TIN_CANISTERS)
                .define('C', GCItems.COMPRESSED_STEEL)
                .pattern("WWW")
                .pattern("TTT")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItemTags.TIN_CANISTERS))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.PARACHUTE)
                .define('S', ConventionalItemTags.STRINGS)
                .define('C', GCItems.CANVAS)
                .pattern("CCC")
                .pattern("S S")
                .pattern(" S ")
                .unlockedBy(getHasName(GCItems.CANVAS), has(GCItems.CANVAS))
                .emiDefaultRecipe(true)
                .save(output);
        GCRecipeHelper.dyeColoring(output, RecipeCategory.MISC, GCItems.PARACHUTE, GCItems.DYED_PARACHUTES, true);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.FREQUENCY_MODULE)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('I', GCItems.COMPRESSED_IRON)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('P', Items.REPEATER)
                .define('W', GCItems.BASIC_WAFER)
                .pattern(" A ")
                .pattern("IPI")
                .pattern("RWR")
                .unlockedBy(getHasName(GCItems.COMPRESSED_ALUMINUM), has(GCItems.COMPRESSED_ALUMINUM))
                .emiDefaultRecipe(true)
                .save(output);

        SpecialRecipeBuilder.special(EmergencyKitRecipe::new).save(output, "emergency_kit");


        // Misc crafting materials
        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.DESH_STICK, 4)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D")
                .pattern("D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapelessRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.CARBON_FRAGMENTS, 3)
                .requires(ItemTags.COALS)
                .unlockedBy("has_coal", has(ItemTags.COALS))
                .save(output);

        GCCookingRecipeBuilder.generic(Ingredient.of(ItemTags.PLANKS), RecipeCategory.MISC, GCItems.CARBON_FRAGMENTS, 0.1f, 200, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output, getSmeltingRecipeName(GCItems.CARBON_FRAGMENTS));

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.CANVAS)
                .define('S', ConventionalItemTags.STRINGS)
                .define('I', ConventionalItemTags.WOODEN_RODS)
                .pattern("SSI")
                .pattern("SSS")
                .pattern("ISS")
                .unlockedBy(getHasName(Items.STRING), has(ConventionalItemTags.STRINGS))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.FLUID_MANIPULATOR)
                .define('M', GCItems.COMPRESSED_METEORIC_IRON)
                .define('S', ConventionalItemTags.SLIME_BALLS)
                .define('F', GCItems.OXYGEN_FAN)
                .define('W', GCItems.ADVANCED_WAFER)
                .pattern("MFM")
                .pattern("SWS")
                .pattern("MFM")
                .unlockedBy(getHasName(GCItems.METEORIC_IRON_INGOT), has(GCItems.COMPRESSED_METEORIC_IRON))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.SENSOR_LENS)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('P', ConventionalItemTags.GLASS_PANES)
                .define('F', GCItems.COMPRESSED_METEORIC_IRON)
                .pattern("RPR")
                .pattern("PFP")
                .pattern("RPR")
                .unlockedBy(getHasName(GCItems.COMPRESSED_METEORIC_IRON), has(GCItems.COMPRESSED_METEORIC_IRON))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.SINGLE_SOLAR_MODULE, 2)
                .define('G', ConventionalItemTags.GLASS_BLOCKS)
                .define('S', GCItems.BLUE_SOLAR_WAFER)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .pattern("GGG")
                .pattern("SSS")
                .pattern("WWW")
                .unlockedBy(getHasName(GCItems.BLUE_SOLAR_WAFER), has(GCItems.BLUE_SOLAR_WAFER))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.FULL_SOLAR_PANEL)
                .define('M', GCItems.SINGLE_SOLAR_MODULE)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .pattern("MMM")
                .pattern("WWW")
                .pattern("MMM")
                .unlockedBy(getHasName(GCItems.SINGLE_SOLAR_MODULE), has(GCItems.SINGLE_SOLAR_MODULE))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.SOLAR_ARRAY_PANEL, 3)
                .define('G', ConventionalItemTags.GLASS_BLOCKS)
                .define('S', GCItems.SOLAR_ARRAY_WAFER)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .pattern("GGG")
                .pattern("SSS")
                .pattern("WWW")
                .unlockedBy(getHasName(GCItems.SOLAR_ARRAY_WAFER), has(GCItems.SOLAR_ARRAY_WAFER))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.STEEL_POLE)
                .define('I', GCItems.COMPRESSED_STEEL)
                .pattern("I")
                .pattern("I")
                .pattern("I")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.THERMAL_CLOTH)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('W', ItemTags.WOOL)
                .pattern(" W ")
                .pattern("WRW")
                .pattern(" W ")
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.BATTERY)
                .define('P', GCItems.COMPRESSED_COPPER)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('C', ItemTags.COALS)
                .pattern(" P ")
                .pattern("ARA")
                .pattern("ACA")
                .unlockedBy(getHasName(GCItems.COMPRESSED_ALUMINUM), has(GCItems.COMPRESSED_ALUMINUM))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.AMBIENT_THERMAL_CONTROLLER)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('W', GCItems.BASIC_WAFER)
                .define('V', GCItems.OXYGEN_VENT)
                .pattern("RVR")
                .pattern("BSB")
                .pattern("BWB")
                .unlockedBy(getHasName(GCItems.OXYGEN_VENT), has(GCItems.OXYGEN_VENT))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.ATMOSPHERIC_VALVE)
                .define('D', GCItems.DESH_INGOT)
                .define('V', GCItems.OXYGEN_VENT)
                .pattern("DVD")
                .pattern(" D ")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.ISOTHERMAL_FABRIC)
                .define('D', GCItems.DESH_INGOT)
                .define('T', GCItems.THERMAL_CLOTH)
                .pattern("TDT")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.ORION_DRIVE)
                .define('D', Items.DIAMOND_ORE)
                .define('L', Items.LAPIS_ORE)
                .define('G', Items.GOLD_ORE)
                .define('C', Items.COAL_ORE)
                .define('I', GCBlocks.ILMENITE_ORE)
                .define('S', GCBlocks.DESH_ORE)
                .define('E', GCBlocks.MOON_CHEESE_ORE)
                .define('R', Items.REDSTONE_ORE)
                .define('B', GCItems.BEAM_CORE)
                .pattern("DLG")
                .pattern("RBC")
                .pattern("ESI")
                .unlockedBy(getHasName(GCBlocks.ILMENITE_ORE), has(GCBlocks.ILMENITE_ORE))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.TIN_CANISTER, 2)
                .define('X', GCItemTags.TIN_INGOTS)
                .pattern("X X")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItemTags.TIN_INGOTS))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.COPPER_CANISTER, 2)
                .define('X', ConventionalItemTags.COPPER_INGOTS)
                .pattern("X X")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(ConventionalItemTags.COPPER_INGOTS))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.EMPTY_CAN, 2)
                .define('X', GCItemTags.ALUMINUM_INGOTS)
                .pattern("X X")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItemTags.ALUMINUM_INGOTS))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.TRANSPORTATION, GCBlocks.ROCKET_LAUNCH_PAD, 9)
                .define('C', GCItems.COMPRESSED_IRON)
                .define('I', ConventionalItemTags.STORAGE_BLOCKS_IRON)
                .pattern("CCC")
                .pattern("III")
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(ConventionalItemTags.STORAGE_BLOCKS_IRON))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.TRANSPORTATION, GCBlocks.FUELING_PAD, 9)
                .define('C', GCItems.COMPRESSED_STEEL)
                .define('I', ConventionalItemTags.STORAGE_BLOCKS_IRON)
                .pattern("CCC")
                .pattern("III")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.OXYGEN_FAN)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', GCItems.BASIC_WAFER)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .pattern("S S")
                .pattern(" W ")
                .pattern("SRS")
                .unlockedBy(getHasName(GCItems.BASIC_WAFER), has(GCItems.BASIC_WAFER))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.OXYGEN_VENT)
                .define('T', GCItems.COMPRESSED_TIN)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("TT")
                .pattern("TS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_TIN), has(GCItems.COMPRESSED_TIN))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.MISC, GCItems.BEAM_CORE)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('I', GCItems.COMPRESSED_IRON)
                .define('G', ConventionalItemTags.GLASS_PANES)
                .pattern("RIR")
                .pattern("IGI")
                .pattern("RIR")
                .unlockedBy(getHasName(GCItems.COMPRESSED_IRON), has(GCItems.COMPRESSED_IRON))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.TRANSPORTATION, GCItems.BUGGY_WHEEL)
                .define('L', ConventionalItemTags.LEATHERS)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern(" L ")
                .pattern("LSL")
                .pattern(" L ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.TRANSPORTATION, GCItems.BUGGY_SEAT)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('I', GCItems.COMPRESSED_IRON)
                .pattern("  S")
                .pattern(" IS")
                .pattern("SSS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.TRANSPORTATION, GCItems.BUGGY_STORAGE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('I', GCItems.COMPRESSED_IRON)
                .define('C', ConventionalItemTags.WOODEN_CHESTS)
                .pattern("SSS")
                .pattern("ICI")
                .pattern("SSS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .emiDefaultRecipe(true)
                .save(output);

        // Food
        cookingRecipes(output, GCItems.GROUND_BEEF, GCItems.BEEF_PATTY, 0.35F, 100, true);

        GCShapelessRecipeBuilder.crafting(RecipeCategory.FOOD, GCItems.CHEESE_CRACKER)
                .requires(GCItems.CRACKER)
                .requires(GCItems.MOON_CHEESE_SLICE)
                .unlockedBy(getHasName(GCItems.CRACKER), has(GCItems.CRACKER))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapelessRecipeBuilder.crafting(RecipeCategory.FOOD, GCItems.BURGER_BUN, 2)
                .requires(ConventionalItemTags.WHEAT_CROPS)
                .requires(ConventionalItemTags.WHEAT_CROPS)
                .requires(ConventionalItemTags.EGGS)
                .requires(ConventionalItemTags.MILK_BUCKETS)
                .requires(GCItemTags.BURGER_BUN_SEEDS)
                .unlockedBy(getHasName(Items.EGG), has(ConventionalItemTags.EGGS))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapelessRecipeBuilder.crafting(RecipeCategory.FOOD, GCItems.GROUND_BEEF, 2)
                .requires(Items.BEEF)
                .unlockedBy(getHasName(Items.BEEF), has(Items.BEEF))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapelessRecipeBuilder.crafting(RecipeCategory.FOOD, GCItems.CHEESEBURGER)
                .requires(GCItems.MOON_CHEESE_SLICE)
                .requires(GCItems.BEEF_PATTY)
                .requires(GCItems.BURGER_BUN)
                .unlockedBy(getHasName(GCItems.GROUND_BEEF), has(GCItems.GROUND_BEEF))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapelessRecipeBuilder.crafting(RecipeCategory.FOOD, GCItems.MOON_CHEESE_SLICE, 6)
                .requires(GCBlocks.MOON_CHEESE_WHEEL)
                .unlockedBy(getHasName(GCBlocks.MOON_CHEESE_WHEEL), has(GCBlocks.MOON_CHEESE_WHEEL))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.FOOD, GCBlocks.MOON_CHEESE_WHEEL)
                .define('C', GCItems.MOON_CHEESE_CURD)
                .define('M', ConventionalItemTags.MILK_BUCKETS)
                .pattern("CCC")
                .pattern("CMC")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.MOON_CHEESE_CURD), has(GCItems.MOON_CHEESE_CURD))
                .emiDefaultRecipe(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.FOOD, GCBlocks.MOON_CHEESE_LOG)
                .define('C', GCItems.MOON_CHEESE_WHEEL)
                .pattern("C")
                .pattern("C")
                .unlockedBy(getHasName(GCItems.MOON_CHEESE_WHEEL), has(GCItems.MOON_CHEESE_WHEEL))
                .emiDefaultRecipe(true)
                .save(output);
    }

    @Override
    public String getName() {
        return "Misc Recipes";
    }

    private static void cookingRecipes(RecipeOutput output, ItemLike input, ItemLike result, float experience, int cookingTime, boolean emiDefaultRecipe) {
        Ingredient ingredient = Ingredient.of(input);
        String hasName = RecipeProvider.getHasName(input);
        var criterion = RecipeProvider.has(input);
        String itemName = RecipeProvider.getItemName(result);

        GCCookingRecipeBuilder.smelting(ingredient, RecipeCategory.FOOD, result, experience, cookingTime * 2)
                .unlockedBy(hasName, criterion)
                .emiDefaultRecipe(emiDefaultRecipe)
                .save(output, itemName + "_from_smelting");
        GCCookingRecipeBuilder.smoking(ingredient, RecipeCategory.FOOD, result, experience, cookingTime)
                .unlockedBy(hasName, criterion)
                .save(output, itemName + "_from_smoking");
        GCCookingRecipeBuilder.campfireCooking(ingredient, RecipeCategory.FOOD, result, experience, cookingTime * 6)
                .unlockedBy(hasName, criterion)
                .save(output, itemName + "_from_campfire_cooking");
    }
}
