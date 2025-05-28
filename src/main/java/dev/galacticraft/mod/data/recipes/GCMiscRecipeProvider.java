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
import net.minecraft.world.item.DyeColor;
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_MASK)
                .define('G', ConventionalItemTags.GLASS_PANES_COLORLESS)
                .define('H', Items.IRON_HELMET)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .unlockedBy(getHasName(Items.GLASS_PANE), has(ConventionalItemTags.GLASS_PANES_COLORLESS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_GEAR)
                .define('Y', GCItemTags.GLASS_FLUID_PIPES)
                .define('X', GCItems.OXYGEN_CONCENTRATOR)
                .pattern(" Y ")
                .pattern("YXY")
                .pattern("Y Y")
                .unlockedBy(getHasName(GCBlocks.GLASS_FLUID_PIPE), has(GCBlocks.GLASS_FLUID_PIPE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_CONCENTRATOR)
                .define('Z', GCItems.COMPRESSED_STEEL)
                .define('W', GCItems.COMPRESSED_TIN)
                .define('Y', GCItemTags.TIN_CANISTERS)
                .define('X', GCItems.OXYGEN_VENT)
                .pattern("ZWZ")
                .pattern("WYW")
                .pattern("ZXZ")
                .unlockedBy(getHasName(GCItems.OXYGEN_VENT), has(GCItems.OXYGEN_VENT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SMALL_OXYGEN_TANK)
                .define('W', Items.LIME_WOOL)
                .define('T', GCItemTags.TIN_CANISTERS)
                .define('C', GCItems.COMPRESSED_COPPER)
                .pattern("W")
                .pattern("T")
                .pattern("C")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItemTags.TIN_CANISTERS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.MEDIUM_OXYGEN_TANK)
                .define('W', Items.ORANGE_WOOL)
                .define('T', GCItemTags.TIN_CANISTERS)
                .define('C', GCItems.COMPRESSED_TIN)
                .pattern("WW")
                .pattern("TT")
                .pattern("CC")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItemTags.TIN_CANISTERS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.LARGE_OXYGEN_TANK)
                .define('W', Items.RED_WOOL)
                .define('T', GCItemTags.TIN_CANISTERS)
                .define('C', GCItems.COMPRESSED_STEEL)
                .pattern("WWW")
                .pattern("TTT")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItemTags.TIN_CANISTERS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.PARACHUTE.get(DyeColor.WHITE))
                .define('S', ConventionalItemTags.STRINGS)
                .define('C', GCItems.CANVAS)
                .pattern("CCC")
                .pattern("S S")
                .pattern(" S ")
                .unlockedBy(getHasName(GCItems.CANVAS), has(GCItems.CANVAS))
                .save(output);
        GCRecipeHelper.dyeColoring(output, RecipeCategory.MISC, GCItems.PARACHUTE);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.FREQUENCY_MODULE)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('I', GCItems.COMPRESSED_IRON)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('P', Items.REPEATER)
                .define('W', GCItems.BASIC_WAFER)
                .pattern(" A ")
                .pattern("IPI")
                .pattern("RWR")
                .unlockedBy(getHasName(GCItems.COMPRESSED_ALUMINUM), has(GCItems.COMPRESSED_ALUMINUM))
                .save(output);

        SpecialRecipeBuilder.special(EmergencyKitRecipe::new).save(output, "emergency_kit");


        // Misc crafting materials
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_STICK, 4)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D")
                .pattern("D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GCItems.CARBON_FRAGMENTS, 3)
                .requires(ConventionalItemTags.COAL)
                .unlockedBy("has_coal", has(ConventionalItemTags.COAL))
                .save(output);

        SimpleCookingRecipeBuilder.generic(Ingredient.of(ItemTags.PLANKS), RecipeCategory.MISC, GCItems.CARBON_FRAGMENTS, 0.1f, 200, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output, getSmeltingRecipeName(GCItems.CARBON_FRAGMENTS));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.CANVAS)
                .define('S', ConventionalItemTags.STRINGS)
                .define('I', ConventionalItemTags.WOODEN_RODS)
                .pattern("SSI")
                .pattern("SSS")
                .pattern("ISS")
                .unlockedBy(getHasName(Items.STRING), has(ConventionalItemTags.STRINGS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.FLUID_MANIPULATOR)
                .define('M', GCItems.COMPRESSED_METEORIC_IRON)
                .define('S', ConventionalItemTags.SLIME_BALLS)
                .define('F', GCItems.OXYGEN_FAN)
                .define('W', GCItems.ADVANCED_WAFER)
                .pattern("MFM")
                .pattern("SWS")
                .pattern("MFM")
                .unlockedBy(getHasName(GCItems.METEORIC_IRON_INGOT), has(GCItems.COMPRESSED_METEORIC_IRON))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SENSOR_LENS)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('P', ConventionalItemTags.GLASS_PANES)
                .define('F', GCItems.COMPRESSED_METEORIC_IRON)
                .pattern("RPR")
                .pattern("PFP")
                .pattern("RPR")
                .unlockedBy(getHasName(GCItems.COMPRESSED_METEORIC_IRON), has(GCItems.COMPRESSED_METEORIC_IRON))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SINGLE_SOLAR_MODULE, 2)
                .define('G', ConventionalItemTags.GLASS_BLOCKS)
                .define('S', GCItems.BLUE_SOLAR_WAFER)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .pattern("GGG")
                .pattern("SSS")
                .pattern("WWW")
                .unlockedBy(getHasName(GCItems.BLUE_SOLAR_WAFER), has(GCItems.BLUE_SOLAR_WAFER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.FULL_SOLAR_PANEL)
                .define('M', GCItems.SINGLE_SOLAR_MODULE)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .pattern("MMM")
                .pattern("WWW")
                .pattern("MMM")
                .unlockedBy(getHasName(GCItems.SINGLE_SOLAR_MODULE), has(GCItems.SINGLE_SOLAR_MODULE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SOLAR_ARRAY_PANEL, 3)
                .define('G', ConventionalItemTags.GLASS_BLOCKS)
                .define('S', GCItems.SOLAR_ARRAY_WAFER)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .pattern("GGG")
                .pattern("SSS")
                .pattern("WWW")
                .unlockedBy(getHasName(GCItems.SOLAR_ARRAY_WAFER), has(GCItems.SOLAR_ARRAY_WAFER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.STEEL_POLE)
                .define('I', GCItems.COMPRESSED_STEEL)
                .pattern("I")
                .pattern("I")
                .pattern("I")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_CLOTH)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('W', ItemTags.WOOL)
                .pattern(" W ")
                .pattern("WRW")
                .pattern(" W ")
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.BATTERY)
                .define('P', GCItems.COMPRESSED_COPPER)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('C', ConventionalItemTags.COAL)
                .pattern(" P ")
                .pattern("ARA")
                .pattern("ACA")
                .unlockedBy(getHasName(GCItems.COMPRESSED_ALUMINUM), has(GCItems.COMPRESSED_ALUMINUM))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.AMBIENT_THERMAL_CONTROLLER)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('W', GCItems.BASIC_WAFER)
                .define('V', GCItems.OXYGEN_VENT)
                .pattern("RVR")
                .pattern("BSB")
                .pattern("BWB")
                .unlockedBy(getHasName(GCItems.OXYGEN_VENT), has(GCItems.OXYGEN_VENT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ATMOSPHERIC_VALVE)
                .define('D', GCItems.DESH_INGOT)
                .define('V', GCItems.OXYGEN_VENT)
                .pattern("DVD")
                .pattern(" D ")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ISOTHERMAL_FABRIC)
                .define('D', GCItems.DESH_INGOT)
                .define('T', GCItems.THERMAL_CLOTH)
                .pattern("TDT")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ORION_DRIVE)
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
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.TIN_CANISTER, 2)
                .define('X', GCItemTags.TIN_INGOTS)
                .pattern("X X")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItemTags.TIN_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.COPPER_CANISTER, 2)
                .define('X', ConventionalItemTags.COPPER_INGOTS)
                .pattern("X X")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(ConventionalItemTags.COPPER_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, GCBlocks.ROCKET_LAUNCH_PAD, 9)
                .define('C', GCItems.COMPRESSED_IRON)
                .define('I', ConventionalItemTags.STORAGE_BLOCKS_IRON)
                .pattern("CCC")
                .pattern("III")
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(ConventionalItemTags.STORAGE_BLOCKS_IRON))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, GCBlocks.FUELING_PAD, 9)
                .define('C', GCItems.COMPRESSED_STEEL)
                .define('I', ConventionalItemTags.STORAGE_BLOCKS_IRON)
                .pattern("CCC")
                .pattern("III")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_FAN)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', GCItems.BASIC_WAFER)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .pattern("S S")
                .pattern(" W ")
                .pattern("SRS")
                .unlockedBy(getHasName(GCItems.BASIC_WAFER), has(GCItems.BASIC_WAFER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_VENT)
                .define('T', GCItems.COMPRESSED_TIN)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("TT")
                .pattern("TS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_TIN), has(GCItems.COMPRESSED_TIN))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.BEAM_CORE)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('I', GCItems.COMPRESSED_IRON)
                .define('G', ConventionalItemTags.GLASS_PANES)
                .pattern("RIR")
                .pattern("IGI")
                .pattern("RIR")
                .unlockedBy(getHasName(GCItems.COMPRESSED_IRON), has(GCItems.COMPRESSED_IRON))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.BUGGY_WHEEL)
                .define('L', ConventionalItemTags.LEATHERS)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern(" L ")
                .pattern("LSL")
                .pattern(" L ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.BUGGY_SEAT)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('I', GCItems.COMPRESSED_IRON)
                .pattern("  S")
                .pattern(" IS")
                .pattern("SSS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.BUGGY_STORAGE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('I', GCItems.COMPRESSED_IRON)
                .define('C', ConventionalItemTags.WOODEN_CHESTS)
                .pattern("SSS")
                .pattern("ICI")
                .pattern("SSS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        // Food
        cookingRecipes(output, 100, GCItems.GROUND_BEEF, GCItems.BEEF_PATTY, 0.35F);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.BURGER_BUN, 2)
                .requires(ConventionalItemTags.WHEAT_CROPS)
                .requires(ConventionalItemTags.WHEAT_CROPS)
                .requires(ConventionalItemTags.EGGS)
                .requires(ConventionalItemTags.MILK_BUCKETS)
                .unlockedBy(getHasName(Items.EGG), has(ConventionalItemTags.EGGS))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.GROUND_BEEF, 2)
                .requires(Items.BEEF)
                .unlockedBy(getHasName(Items.BEEF), has(Items.BEEF))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CHEESEBURGER)
                .requires(GCItems.MOON_CHEESE_SLICE)
                .requires(GCItems.BEEF_PATTY)
                .requires(GCItems.BURGER_BUN)
                .unlockedBy(getHasName(GCItems.GROUND_BEEF), has(GCItems.GROUND_BEEF))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.MOON_CHEESE_SLICE, 7)
                .requires(GCBlocks.MOON_CHEESE_WHEEL)
                .unlockedBy(getHasName(GCBlocks.MOON_CHEESE_WHEEL), has(GCBlocks.MOON_CHEESE_WHEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, GCBlocks.MOON_CHEESE_WHEEL)
                .define('C', GCItems.MOON_CHEESE_CURD)
                .define('M', ConventionalItemTags.MILK_BUCKETS)
                .pattern("CCC")
                .pattern("CMC")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.MOON_CHEESE_CURD), has(GCItems.MOON_CHEESE_CURD))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, GCBlocks.MOON_CHEESE_LOG)
                .define('C', GCItems.MOON_CHEESE_WHEEL)
                .pattern("C")
                .pattern("C")
                .unlockedBy(getHasName(GCItems.MOON_CHEESE_WHEEL), has(GCItems.MOON_CHEESE_WHEEL))
                .save(output);
    }

    @Override
    public String getName() {
        return "Misc Recipes";
    }

    private static void cookingRecipes(RecipeOutput output, int cookingTime, ItemLike input, ItemLike result, float experience) {
        simpleCookingRecipe(output, "smoking", RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, cookingTime, input, result, experience);
        simpleCookingRecipe(output, "smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, cookingTime * 2, input, result, experience);
        simpleCookingRecipe(output, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new, cookingTime * 6, input, result, experience);
    }
}
