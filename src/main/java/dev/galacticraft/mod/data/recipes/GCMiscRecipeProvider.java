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

import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

/**
 * Intermediate crafting materials and food recipes.
 */
public class GCMiscRecipeProvider extends FabricRecipeProvider {
    public GCMiscRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        // Gear
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_MASK)
                .define('G', Items.GLASS_PANE)
                .define('H', Items.IRON_HELMET)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .unlockedBy(getHasName(Items.GLASS_PANE), has(Items.GLASS_PANE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_GEAR)
                .define('Y', GCItems.GLASS_FLUID_PIPE)
                .define('X', GCItems.OXYGEN_CONCENTRATOR)
                .pattern(" Y ")
                .pattern("YXY")
                .pattern("Y Y")
                .unlockedBy(getHasName(GCItems.GLASS_FLUID_PIPE), has(GCItems.GLASS_FLUID_PIPE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_CONCENTRATOR)
                .define('Z', GCTags.COMPRESSED_STEEL)
                .define('W', GCTags.COMPRESSED_TIN)
                .define('Y', GCItems.TIN_CANISTER)
                .define('X', GCItems.OXYGEN_VENT)
                .pattern("ZWZ")
                .pattern("WYW")
                .pattern("ZXZ")
                .unlockedBy(getHasName(GCItems.OXYGEN_VENT), has(GCItems.OXYGEN_VENT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SMALL_OXYGEN_TANK)
                .define('W', Items.LIME_WOOL)
                .define('T', GCItems.TIN_CANISTER)
                .define('C', GCItems.COMPRESSED_COPPER)
                .pattern("W")
                .pattern("T")
                .pattern("C")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.MEDIUM_OXYGEN_TANK)
                .define('W', Items.ORANGE_WOOL)
                .define('T', GCItems.TIN_CANISTER)
                .define('C', GCItems.COMPRESSED_TIN)
                .pattern("WW")
                .pattern("TT")
                .pattern("CC")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.LARGE_OXYGEN_TANK)
                .define('W', Items.RED_WOOL)
                .define('T', GCItems.TIN_CANISTER)
                .define('C', GCItems.COMPRESSED_STEEL)
                .pattern("WWW")
                .pattern("TTT")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.PARACHUTE.get(DyeColor.WHITE))
                .define('S', Items.STRING)
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


        // Misc crafting materials
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_STICK, 4)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D")
                .pattern("D")
                .unlockedBy(getHasName(GCItems.DESH_STICK), has(GCItems.DESH_STICK))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GCItems.CARBON_FRAGMENTS, 3)
                .requires(ConventionalItemTags.COAL)
                .unlockedBy("has_coal", has(ConventionalItemTags.COAL))
                .save(output, BuiltInRegistries.ITEM.getKey(GCItems.CARBON_FRAGMENTS).withSuffix("_from_coal"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GCItems.CARBON_FRAGMENTS, 3)
                .requires(Items.CHARCOAL)
                .unlockedBy(getHasName(Items.CHARCOAL), has(Items.CHARCOAL))
                .save(output, BuiltInRegistries.ITEM.getKey(GCItems.CARBON_FRAGMENTS).withSuffix("_from_charcoal"));

        SimpleCookingRecipeBuilder.generic(Ingredient.of(ItemTags.PLANKS), RecipeCategory.MISC, GCItems.CARBON_FRAGMENTS, 0.1f, 200, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output, getItemName(GCItems.CARBON_FRAGMENTS) + "_from_smelting_planks");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.CANVAS)
                .define('S', Items.STRING)
                .define('I', Items.STICK)
                .pattern("SSI")
                .pattern("SSS")
                .pattern("ISS")
                .unlockedBy(getHasName(Items.STRING), has(Items.STRING))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.FLUID_MANIPULATOR)
                .define('M', GCItems.METEORIC_IRON_INGOT)
                .define('S', Items.SLIME_BALL)
                .define('F', GCItems.OXYGEN_FAN)
                .define('W', GCItems.ADVANCED_WAFER)
                .pattern("MFM")
                .pattern("SWS")
                .pattern("MFM")
                .unlockedBy(getHasName(GCItems.METEORIC_IRON_INGOT), has(GCItems.METEORIC_IRON_INGOT))
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
                .define('S', GCItems.SOLAR_ARRAY_WAFER)
                .define('W', GCItems.ALUMINUM_WIRE)
                .pattern("GGG")
                .pattern("SSS")
                .pattern("WWW")
                .unlockedBy(getHasName(GCItems.SOLAR_ARRAY_WAFER), has(GCItems.SOLAR_ARRAY_WAFER))
                .save(output);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.FULL_SOLAR_PANEL)
                .define('M', GCItems.SINGLE_SOLAR_MODULE)
                .define('W', GCItems.ALUMINUM_WIRE)
                .pattern("MMM")
                .pattern("WWW")
                .pattern("MMM")
                .unlockedBy(getHasName(GCItems.SINGLE_SOLAR_MODULE), has(GCItems.SINGLE_SOLAR_MODULE))
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
                .define('T', GCItems.COMPRESSED_TIN)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('C', ConventionalItemTags.COAL)
                .pattern(" T ")
                .pattern("TRT")
                .pattern("TCT")
                .unlockedBy(getHasName(GCItems.COMPRESSED_TIN), has(GCItems.COMPRESSED_TIN))
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
                .define('I', GCItems.ILMENITE_ORE)
                .define('S', GCItems.DESH_ORE)
                .define('E', GCItems.MOON_CHEESE_WHEEL) //todo: add cheese ore?
                .define('R', Items.REDSTONE_ORE)
                .define('B', GCItems.BEAM_CORE)
                .pattern("DLG")
                .pattern("RBC")
                .pattern("ESI")
                .unlockedBy(getHasName(GCItems.ILMENITE_ORE), has(GCItems.ILMENITE_ORE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.TIN_CANISTER, 2)
                .define('X', GCTags.TIN_INGOTS)
                .pattern("X X")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItems.TIN_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.COPPER_CANISTER, 2)
                .define('X', Items.COPPER_INGOT)
                .pattern("X X")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, GCItems.ROCKET_LAUNCH_PAD, 9)
                .define('C', GCItems.COMPRESSED_IRON)
                .define('I', Items.IRON_BLOCK)
                .pattern("CCC")
                .pattern("III")
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
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
                .define('I', GCTags.COMPRESSED_IRON)
                .define('G', ConventionalItemTags.GLASS_PANES)
                .pattern("RIR")
                .pattern("IGI")
                .pattern("RIR")
                .unlockedBy(getHasName(GCItems.COMPRESSED_IRON), has(GCItems.COMPRESSED_IRON))
                .save(output);

        // Food
        cookingRecipes(output, 100, GCItems.GROUND_BEEF, GCItems.BEEF_PATTY, 1.0F);

//        SimpleCookingRecipeBuilder.smelting(Ingredient.of(GCBlocks.CHEESE_ORE), RecipeCategory.FOOD, GCItems.CHEESE_CURD, 0.35F, 200)
//                .unlockedBy(getHasName(GCBlocks.CHEESE_ORE), has(GCBlocks.CHEESE_ORE))
//                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.BURGER_BUN, 2)
                .requires(Items.WHEAT)
                .requires(Items.WHEAT)
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .unlockedBy(getHasName(Items.EGG), has(Items.EGG))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.GROUND_BEEF, 2)
                .requires(Items.BEEF)
                .unlockedBy(getHasName(Items.BEEF), has(Items.BEEF))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CHEESEBURGER)
                .requires(GCItems.CHEESE_SLICE)
                .requires(GCItems.BEEF_PATTY)
                .requires(GCItems.BURGER_BUN)
                .unlockedBy(getHasName(GCItems.GROUND_BEEF), has(GCItems.GROUND_BEEF))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CHEESE_SLICE, 6)
                .requires(GCItems.MOON_CHEESE_WHEEL)
                .unlockedBy(getHasName(GCItems.MOON_CHEESE_WHEEL), has(GCItems.MOON_CHEESE_WHEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, GCItems.MOON_CHEESE_WHEEL)
                .define('C', GCItems.CHEESE_CURD)
                .define('M', Items.MILK_BUCKET)
                .pattern("CCC")
                .pattern("CMC")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.CHEESE_CURD), has(GCItems.CHEESE_CURD))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CANNED_DEHYDRATED_APPLE)
                .requires(GCItems.TIN_CANISTER)
                .requires(Items.APPLE)
                .requires(Items.APPLE)
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CANNED_DEHYDRATED_POTATO)
                .requires(GCItems.TIN_CANISTER)
                .requires(Items.POTATO)
                .requires(Items.POTATO)
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CANNED_DEHYDRATED_CARROT)
                .requires(GCItems.TIN_CANISTER)
                .requires(Items.CARROT)
                .requires(Items.CARROT)
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CANNED_DEHYDRATED_MELON)
                .requires(GCItems.TIN_CANISTER)
                .requires(Items.MELON_SLICE)
                .requires(Items.MELON_SLICE)
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CANNED_BEEF)
                .requires(GCItems.TIN_CANISTER)
                .requires(GCItems.GROUND_BEEF)
                .requires(GCItems.GROUND_BEEF)
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
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
