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

package dev.galacticraft.mod.data;

import com.google.common.collect.ImmutableList;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.data.recipes.FabricationRecipeBuilder;
import dev.galacticraft.mod.data.recipes.ShapedCompressorRecipeBuilder;
import dev.galacticraft.mod.data.recipes.ShapelessCompressorRecipeBuilder;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class GCRecipeProvider extends FabricRecipeProvider {

    public GCRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        // Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.GLOWSTONE_TORCH, 4)
                .define('G', Items.GLOWSTONE_DUST)
                .define('S', Items.STICK)
                .pattern("G")
                .pattern("S")
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.GLOWSTONE_LANTERN)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('I', Items.IRON_NUGGET)
                .pattern("III")
                .pattern("IGI")
                .pattern("III")
                .unlockedBy(getHasName(GCItems.GLOWSTONE_TORCH), has(GCItems.GLOWSTONE_TORCH))
                .save(exporter);

        decorationBlock(exporter, GCItems.COMPRESSED_TIN, GCBlocks.TIN_DECORATION);
        stairs(exporter, GCBlocks.TIN_DECORATION_STAIRS, GCBlocks.TIN_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.TIN_DECORATION_SLAB, GCBlocks.TIN_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.TIN_DECORATION_WALL, GCBlocks.TIN_DECORATION);
        detailedDecorationBlock(exporter, GCItems.COMPRESSED_TIN, GCBlocks.DETAILED_TIN_DECORATION);
        stairs(exporter, GCBlocks.DETAILED_TIN_DECORATION_STAIRS, GCBlocks.DETAILED_TIN_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_TIN_DECORATION_SLAB, GCBlocks.DETAILED_TIN_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_TIN_DECORATION_WALL, GCBlocks.DETAILED_TIN_DECORATION);

        decorationBlock(exporter, GCItems.COMPRESSED_COPPER, GCBlocks.COPPER_DECORATION);
        stairs(exporter, GCBlocks.COPPER_DECORATION_STAIRS, GCBlocks.COPPER_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.COPPER_DECORATION_SLAB, GCBlocks.COPPER_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.COPPER_DECORATION_WALL, GCBlocks.COPPER_DECORATION);
        detailedDecorationBlock(exporter, GCItems.COMPRESSED_COPPER, GCBlocks.DETAILED_COPPER_DECORATION);
        stairs(exporter, GCBlocks.DETAILED_COPPER_DECORATION_STAIRS, GCBlocks.DETAILED_COPPER_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_COPPER_DECORATION_SLAB, GCBlocks.DETAILED_COPPER_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_COPPER_DECORATION_WALL, GCBlocks.DETAILED_COPPER_DECORATION);

        decorationBlock(exporter, GCItems.COMPRESSED_ALUMINUM, GCBlocks.ALUMINUM_DECORATION);
        stairs(exporter, GCBlocks.ALUMINUM_DECORATION_STAIRS, GCBlocks.ALUMINUM_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.ALUMINUM_DECORATION_SLAB, GCBlocks.ALUMINUM_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.ALUMINUM_DECORATION_WALL, GCBlocks.ALUMINUM_DECORATION);
        detailedDecorationBlock(exporter, GCItems.COMPRESSED_ALUMINUM, GCBlocks.DETAILED_ALUMINUM_DECORATION);
        stairs(exporter, GCBlocks.DETAILED_ALUMINUM_DECORATION_STAIRS, GCBlocks.DETAILED_ALUMINUM_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_ALUMINUM_DECORATION_SLAB, GCBlocks.DETAILED_ALUMINUM_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_ALUMINUM_DECORATION_WALL, GCBlocks.DETAILED_ALUMINUM_DECORATION);

        decorationBlock(exporter, GCItems.COMPRESSED_STEEL, GCBlocks.STEEL_DECORATION);
        stairs(exporter, GCBlocks.STEEL_DECORATION_STAIRS, GCBlocks.STEEL_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.STEEL_DECORATION_SLAB, GCBlocks.STEEL_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.STEEL_DECORATION_WALL, GCBlocks.STEEL_DECORATION);
        detailedDecorationBlock(exporter, GCItems.COMPRESSED_STEEL, GCBlocks.DETAILED_STEEL_DECORATION);
        stairs(exporter, GCBlocks.DETAILED_STEEL_DECORATION_STAIRS, GCBlocks.DETAILED_STEEL_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_STEEL_DECORATION_SLAB, GCBlocks.DETAILED_STEEL_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_STEEL_DECORATION_WALL, GCBlocks.DETAILED_STEEL_DECORATION);

        decorationBlock(exporter, GCItems.COMPRESSED_BRONZE, GCBlocks.BRONZE_DECORATION);
        stairs(exporter, GCBlocks.BRONZE_DECORATION_STAIRS, GCBlocks.BRONZE_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.BRONZE_DECORATION_SLAB, GCBlocks.BRONZE_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.BRONZE_DECORATION_WALL, GCBlocks.BRONZE_DECORATION);
        detailedDecorationBlock(exporter, GCItems.COMPRESSED_BRONZE, GCBlocks.DETAILED_BRONZE_DECORATION);
        stairs(exporter, GCBlocks.DETAILED_BRONZE_DECORATION_STAIRS, GCBlocks.DETAILED_BRONZE_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_BRONZE_DECORATION_SLAB, GCBlocks.DETAILED_BRONZE_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_BRONZE_DECORATION_WALL, GCBlocks.DETAILED_BRONZE_DECORATION);

        decorationBlock(exporter, GCItems.COMPRESSED_METEORIC_IRON, GCBlocks.METEORIC_IRON_DECORATION);
        stairs(exporter, GCBlocks.METEORIC_IRON_DECORATION_STAIRS, GCBlocks.METEORIC_IRON_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.METEORIC_IRON_DECORATION_SLAB, GCBlocks.METEORIC_IRON_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.METEORIC_IRON_DECORATION_WALL, GCBlocks.METEORIC_IRON_DECORATION);
        detailedDecorationBlock(exporter, GCItems.COMPRESSED_METEORIC_IRON, GCBlocks.DETAILED_METEORIC_IRON_DECORATION);
        stairs(exporter, GCBlocks.DETAILED_METEORIC_IRON_DECORATION_STAIRS, GCBlocks.DETAILED_METEORIC_IRON_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_METEORIC_IRON_DECORATION_SLAB, GCBlocks.DETAILED_METEORIC_IRON_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_METEORIC_IRON_DECORATION_WALL, GCBlocks.DETAILED_METEORIC_IRON_DECORATION);

        decorationBlock(exporter, GCItems.COMPRESSED_TITANIUM, GCBlocks.TITANIUM_DECORATION);
        stairs(exporter, GCBlocks.TITANIUM_DECORATION_STAIRS, GCBlocks.TITANIUM_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.TITANIUM_DECORATION_SLAB, GCBlocks.TITANIUM_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.TITANIUM_DECORATION_WALL, GCBlocks.TITANIUM_DECORATION);
        detailedDecorationBlock(exporter, GCItems.COMPRESSED_TITANIUM, GCBlocks.DETAILED_TITANIUM_DECORATION);
        stairs(exporter, GCBlocks.DETAILED_TITANIUM_DECORATION_STAIRS, GCBlocks.DETAILED_TITANIUM_DECORATION);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_TITANIUM_DECORATION_SLAB, GCBlocks.DETAILED_TITANIUM_DECORATION);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DETAILED_TITANIUM_DECORATION_WALL, GCBlocks.DETAILED_TITANIUM_DECORATION);

        // Gear
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_MASK)
                .define('G', Items.GLASS_PANE)
                .define('H', Items.IRON_HELMET)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .unlockedBy(getHasName(Items.GLASS_PANE), has(Items.GLASS_PANE))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_GEAR)
                .define('Y', GCBlocks.GLASS_FLUID_PIPE)
                .define('X', GCItems.OXYGEN_CONCENTRATOR)
                .pattern(" Y ")
                .pattern("YXY")
                .pattern("Y Y")
                .unlockedBy(getHasName(GCBlocks.GLASS_FLUID_PIPE), has(GCItems.GLASS_FLUID_PIPE))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_CONCENTRATOR)
                .define('Z', GCTags.COMPRESSED_STEEL)
                .define('W', GCTags.COMPRESSED_TIN)
                .define('Y', GCItems.TIN_CANISTER)
                .define('X', GCItems.OXYGEN_VENT)
                .pattern("ZWZ")
                .pattern("WYW")
                .pattern("ZXZ")
                .unlockedBy(getHasName(GCItems.OXYGEN_VENT), has(GCItems.OXYGEN_VENT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.TIN_CANISTER, 2)
                .define('X', GCTags.TIN_INGOTS)
                .pattern("X X")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItems.TIN_INGOT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.COPPER_CANISTER, 2)
                .define('X', Items.COPPER_INGOT)
                .pattern("X X")
                .pattern("X X")
                .pattern("XXX")
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, GCItems.ROCKET_LAUNCH_PAD, 9)
                .define('C', GCItems.COMPRESSED_IRON)
                .define('I', Items.IRON_BLOCK)
                .pattern("CCC")
                .pattern("III")
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_FAN)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', GCItems.BASIC_WAFER)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .pattern("S S")
                .pattern(" W ")
                .pattern("SRS")
                .unlockedBy(getHasName(GCItems.BASIC_WAFER), has(GCItems.BASIC_WAFER))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_VENT)
                .define('T', GCItems.COMPRESSED_TIN)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("TT")
                .pattern("TS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_TIN), has(GCItems.COMPRESSED_TIN))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SMALL_OXYGEN_TANK)
                .define('W', Items.LIME_WOOL)
                .define('T', GCItems.TIN_CANISTER)
                .define('C', GCItems.COMPRESSED_COPPER)
                .pattern("W")
                .pattern("T")
                .pattern("C")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.MEDIUM_OXYGEN_TANK)
                .define('W', Items.ORANGE_WOOL)
                .define('T', GCItems.TIN_CANISTER)
                .define('C', GCItems.COMPRESSED_TIN)
                .pattern("WW")
                .pattern("TT")
                .pattern("CC")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.LARGE_OXYGEN_TANK)
                .define('W', Items.RED_WOOL)
                .define('T', GCItems.TIN_CANISTER)
                .define('C', GCItems.COMPRESSED_STEEL)
                .pattern("WWW")
                .pattern("TTT")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(exporter);

        // Misc crafting materials
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_STICK, 4)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D")
                .pattern("D")
                .unlockedBy(getHasName(GCItems.DESH_STICK), has(GCItems.DESH_STICK))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GCItems.CARBON_FRAGMENTS, 3)
                .requires(ConventionalItemTags.COAL)
                .unlockedBy("has_coal", has(ConventionalItemTags.COAL))
                .save(exporter, BuiltInRegistries.ITEM.getKey(GCItems.CARBON_FRAGMENTS).withSuffix("from_coal"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GCItems.CARBON_FRAGMENTS, 3)
                .requires(Items.CHARCOAL)
                .unlockedBy(getHasName(Items.CHARCOAL), has(Items.CHARCOAL))
                .save(exporter, BuiltInRegistries.ITEM.getKey(GCItems.CARBON_FRAGMENTS).withSuffix("from_charcoal"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.CANVAS)
                .define('S', Items.STRING)
                .define('I', Items.STICK)
                .pattern("SSI")
                .pattern("SSS")
                .pattern("ISS")
                .unlockedBy(getHasName(Items.STRING), has(Items.STRING))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.FLUID_MANIPULATOR)
                .define('M', GCItems.METEORIC_IRON_INGOT)
                .define('S', Items.SLIME_BALL)
                .define('F', GCItems.OXYGEN_FAN)
                .define('W', GCItems.ADVANCED_WAFER)
                .pattern("MFM")
                .pattern("SWS")
                .pattern("MFM")
                .unlockedBy(getHasName(GCItems.METEORIC_IRON_INGOT), has(GCItems.METEORIC_IRON_INGOT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SENSOR_LENS)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('P', ConventionalItemTags.GLASS_PANES)
                .define('F', GCItems.COMPRESSED_METEORIC_IRON)
                .pattern("RPR")
                .pattern("PFP")
                .pattern("RPR")
                .unlockedBy(getHasName(GCItems.COMPRESSED_METEORIC_IRON), has(GCItems.COMPRESSED_METEORIC_IRON))
                .save(exporter);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SINGLE_SOLAR_MODULE, 2)
                .define('G', ConventionalItemTags.GLASS_BLOCKS)
                .define('S', GCItems.SOLAR_ARRAY_WAFER)
                .define('W', GCItems.ALUMINUM_WIRE)
                .pattern("GGG")
                .pattern("SSS")
                .pattern("WWW")
                .unlockedBy(getHasName(GCItems.SOLAR_ARRAY_WAFER), has(GCItems.SOLAR_ARRAY_WAFER))
                .save(exporter);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.FULL_SOLAR_PANEL)
                .define('M', GCItems.SINGLE_SOLAR_MODULE)
                .define('W', GCItems.ALUMINUM_WIRE)
                .pattern("MMM")
                .pattern("WWW")
                .pattern("MMM")
                .unlockedBy(getHasName(GCItems.SINGLE_SOLAR_MODULE), has(GCItems.SINGLE_SOLAR_MODULE))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.STEEL_POLE)
                .define('I', GCItems.COMPRESSED_STEEL)
                .pattern("I")
                .pattern("I")
                .pattern("I")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_CLOTH)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('W', ItemTags.WOOL)
                .pattern(" W ")
                .pattern("WRW")
                .pattern(" W ")
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.PARACHUTE)
                .define('S', Items.STRING)
                .define('C', GCItems.CANVAS)
                .pattern("CCC")
                .pattern("S S")
                .pattern(" S ")
                .unlockedBy(getHasName(GCItems.CANVAS), has(GCItems.CANVAS))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.BATTERY)
                .define('T', GCItems.COMPRESSED_TIN)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('C', ConventionalItemTags.COAL)
                .pattern(" T ")
                .pattern("TRT")
                .pattern("TCT")
                .unlockedBy(getHasName(GCItems.COMPRESSED_TIN), has(GCItems.COMPRESSED_TIN))
                .save(exporter);

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
                .save(exporter);

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
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ATMOSPHERIC_VALVE)
                .define('D', GCItems.DESH_INGOT)
                .define('V', GCItems.OXYGEN_VENT)
                .pattern("DVD")
                .pattern(" D ")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ISOTHERMAL_FABRIC)
                .define('D', GCItems.DESH_INGOT)
                .define('T', GCItems.THERMAL_CLOTH)
                .pattern("TDT")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ORION_DRIVE)
                .define('D', Items.DIAMOND_ORE)
                .define('L', Items.LAPIS_ORE)
                .define('G', Items.GOLD_ORE)
                .define('C', Items.COAL_ORE)
                .define('I', GCItems.ILMENITE_ORE)
                .define('S', GCItems.DESH_ORE)
                .define('E', GCItems.MOON_CHEESE_BLOCK) //todo: add cheese ore?
                .define('R', Items.REDSTONE_ORE)
                .define('B', GCItems.BEAM_CORE)
                .pattern("DLG")
                .pattern("RBC")
                .pattern("ESI")
                .unlockedBy(getHasName(GCItems.ILMENITE_ORE), has(GCItems.ILMENITE_ORE))
                .save(exporter);

        // Food
        cookingRecipes(exporter, 100, GCItems.GROUND_BEEF, GCItems.BEEF_PATTY, 1.0F);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.BURGER_BUN, 2)
                .requires(Items.WHEAT)
                .requires(Items.WHEAT)
                .requires(Items.EGG)
                .requires(Items.MILK_BUCKET)
                .unlockedBy(getHasName(Items.EGG), has(Items.EGG))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.GROUND_BEEF, 2)
                .requires(Items.BEEF)
                .unlockedBy(getHasName(Items.BEEF), has(Items.BEEF))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CHEESEBURGER)
                .requires(GCItems.CHEESE_SLICE)
                .requires(GCItems.BEEF_PATTY)
                .requires(GCItems.BURGER_BUN)
                .unlockedBy(getHasName(GCItems.GROUND_BEEF), has(GCItems.GROUND_BEEF))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CHEESE_SLICE, 6)
                .requires(GCItems.MOON_CHEESE_BLOCK)
                .unlockedBy(getHasName(GCItems.MOON_CHEESE_BLOCK), has(GCItems.MOON_CHEESE_BLOCK))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, GCItems.MOON_CHEESE_BLOCK)
                .define('Y', GCItems.CHEESE_CURD)
                .define('X', Items.MILK_BUCKET)
                .pattern("YYY")
                .pattern("YXY")
                .pattern("YYY")
                .unlockedBy(getHasName(GCItems.CHEESE_CURD), has(GCItems.CHEESE_CURD))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CANNED_DEHYDRATED_APPLE)
                .requires(GCItems.TIN_CANISTER)
                .requires(Items.APPLE)
                .requires(Items.APPLE)
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CANNED_DEHYDRATED_CARROT)
                .requires(GCItems.TIN_CANISTER)
                .requires(Items.CARROT)
                .requires(Items.CARROT)
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CANNED_DEHYDRATED_MELON)
                .requires(GCItems.TIN_CANISTER)
                .requires(Items.MELON_SLICE)
                .requires(Items.MELON_SLICE)
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, GCItems.CANNED_BEEF)
                .requires(GCItems.TIN_CANISTER)
                .requires(GCItems.GROUND_BEEF)
                .requires(GCItems.GROUND_BEEF)
                .unlockedBy(getHasName(GCItems.TIN_CANISTER), has(GCItems.TIN_CANISTER))
                .save(exporter);

        // Armor
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_HELMET)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("PPP")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_CHESTPLATE)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("P P")
                .pattern("PPP")
                .pattern("PPP")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_LEGGINGS)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("PPP")
                .pattern("P P")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_BOOTS)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern("P P")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_HELMET)
                .define('D', GCItems.DESH_INGOT)
                .pattern("DDD")
                .pattern("D D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_CHESTPLATE)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D D")
                .pattern("DDD")
                .pattern("DDD")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_LEGGINGS)
                .define('D', GCItems.DESH_INGOT)
                .pattern("DDD")
                .pattern("D D")
                .pattern("D D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.DESH_BOOTS)
                .define('D', GCItems.DESH_INGOT)
                .pattern("D D")
                .pattern("D D")
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_PADDING_HELMET)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("CCC")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_PADDING_CHESTPIECE)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("C C")
                .pattern("CCC")
                .pattern("CCC")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_PADDING_LEGGINGS)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("CCC")
                .pattern("C C")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.THERMAL_PADDING_BOOTS)
                .define('C', GCItems.THERMAL_CLOTH)
                .pattern("C C")
                .pattern("C C")
                .unlockedBy(getHasName(GCItems.THERMAL_CLOTH), has(GCItems.THERMAL_CLOTH))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SENSOR_GLASSES)
                .define('D', ConventionalItemTags.DIAMONDS)
                .define('S', Items.STRING)
                .define('L', GCItems.SENSOR_LENS)
                .define('M', GCItems.METEORIC_IRON_INGOT)
                .pattern("SDS")
                .pattern("S S")
                .pattern("LML")
                .unlockedBy(getHasName(GCItems.SENSOR_LENS), has(GCItems.SENSOR_LENS))
                .save(exporter);

        // Tools
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_SHOVEL)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("S")
                .pattern("W")
                .pattern("W")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_PICKAXE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("SSS")
                .pattern(" W ")
                .pattern(" W ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_AXE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("SS")
                .pattern("SW")
                .pattern(" W")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, GCItems.HEAVY_DUTY_HOE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("SS")
                .pattern(" W")
                .pattern(" W")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, GCItems.HEAVY_DUTY_SWORD)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('W', Items.STICK)
                .pattern("S")
                .pattern("S")
                .pattern("W")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.STANDARD_WRENCH)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("  S")
                .pattern(" B ")
                .pattern("B  ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(exporter);

        // Pipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.GLASS_FLUID_PIPE, 6)
                .define('X', Items.GLASS_PANE)
                .pattern("XXX")
                .pattern("   ")
                .pattern("XXX")
                .unlockedBy(getHasName(Items.GLASS_PANE), has(Items.GLASS_PANE))
                .save(exporter);

        // Compressing
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_ALUMINUM)
                .requires(GCTags.ALUMINUM_INGOTS)
                .requires(GCTags.ALUMINUM_INGOTS)
                .save(exporter);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_BRONZE)
                .requires(GCItems.COMPRESSED_COPPER)
                .requires(GCItems.COMPRESSED_TIN)
                .save(exporter);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_COPPER)
                .requires(ConventionalItemTags.COPPER_INGOTS)
                .requires(ConventionalItemTags.COPPER_INGOTS)
                .save(exporter);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_DESH)
                .requires(GCItems.DESH_INGOT)
                .save(exporter);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_IRON)
                .requires(ConventionalItemTags.IRON_INGOTS)
                .requires(ConventionalItemTags.IRON_INGOTS)
                .save(exporter);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_METEORIC_IRON)
                .requires(GCItems.METEORIC_IRON_INGOT)
                .save(exporter);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_STEEL)
                .requires(ConventionalItemTags.COAL)
                .requires(GCItems.COMPRESSED_IRON)
                .requires(ConventionalItemTags.COAL)
                .save(exporter);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_STEEL)
                .requires(GCTags.STEEL_INGOTS)
                .requires(GCTags.STEEL_INGOTS)
                .save(exporter, BuiltInRegistries.ITEM.getKey(GCItems.COMPRESSED_STEEL).withSuffix("_from_ingots"));
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_TIN)
                .requires(GCTags.TIN_INGOTS)
                .requires(GCTags.TIN_INGOTS)
                .save(exporter);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_TITANIUM)
                .requires(GCItems.TITANIUM_INGOT)
                .requires(GCItems.TITANIUM_INGOT)
                .save(exporter);

        ShapedCompressorRecipeBuilder.create(GCItems.TIER_1_HEAVY_DUTY_PLATE, 2)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("BAS")
                .pattern("BAS")
                .save(exporter);

        ShapedCompressorRecipeBuilder.create(GCItems.TIER_1_HEAVY_DUTY_PLATE, 2)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("SAB")
                .pattern("SAB")
                .save(exporter, BuiltInRegistries.ITEM.getKey(GCItems.TIER_1_HEAVY_DUTY_PLATE).withSuffix("_flipped"));

        ShapelessCompressorRecipeBuilder.shapeless(GCItems.TIER_2_HEAVY_DUTY_PLATE, 1)
                .requires(GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .requires(GCItems.COMPRESSED_METEORIC_IRON)
                .save(exporter);

        ShapelessCompressorRecipeBuilder.shapeless(GCItems.TIER_3_HEAVY_DUTY_PLATE, 1)
                .requires(GCItems.TIER_2_HEAVY_DUTY_PLATE)
                .requires(GCItems.COMPRESSED_DESH)
                .save(exporter);

        // Machines
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.CIRCUIT_FABRICATOR)
                .define('A', GCItems.ALUMINUM_INGOT)
                .define('L', Items.LEVER)
                .define('B', Items.STONE_BUTTON)
                .define('F', Items.FURNACE)
                .define('W', GCItems.ALUMINUM_WIRE)
                .define('R', Items.REDSTONE_TORCH)
                .pattern("ALA")
                .pattern("BFB")
                .pattern("WRW")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItems.ALUMINUM_INGOT))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.COMPRESSOR)
                .define('I', GCItems.ALUMINUM_INGOT)
                .define('A', Items.ANVIL)
                .define('C', ConventionalItemTags.COPPER_INGOTS)
                .define('W', GCItems.BASIC_WAFER)
                .pattern("IAI")
                .pattern("ICI")
                .pattern("IWI")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItems.ALUMINUM_INGOT))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ELECTRIC_COMPRESSOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('A', Items.ANVIL)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('W', GCItems.ADVANCED_WAFER)
                .define('I', GCItems.ALUMINUM_WIRE)
                .pattern("SAS")
                .pattern("SBS")
                .pattern("IWI")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItems.ALUMINUM_INGOT))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ELECTRIC_COMPRESSOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('T', GCItems.COMPRESSED_TIN)
                .define('C', GCItems.COMPRESSOR)
                .define('W', GCItems.ADVANCED_WAFER)
                .define('I', GCItems.ALUMINUM_WIRE)
                .pattern("STS")
                .pattern("SCS")
                .pattern("IWI")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItems.ALUMINUM_INGOT))
                .save(exporter, BuiltInRegistries.ITEM.getKey(GCItems.ELECTRIC_COMPRESSOR).withSuffix("_alt"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_BUBBLE_DISTRIBUTOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.OXYGEN_FAN)
                .define('V', GCItems.OXYGEN_VENT)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("SFS")
                .pattern("VAV")
                .pattern("SFS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(exporter);

        // Rockets
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ROCKET_WORKBENCH)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('C', Items.CRAFTING_TABLE)
                .define('L', Items.LEVER)
                .define('W', GCItems.ADVANCED_WAFER)
                .define('R', Items.REDSTONE_TORCH)
                .pattern("SCS")
                .pattern("LWL")
                .pattern("SRS")
                .unlockedBy(getHasName(GCItems.ADVANCED_WAFER), has(GCItems.ADVANCED_WAFER))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.NOSE_CONE)
                .define('Y', Items.REDSTONE_TORCH)
                .define('X', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern(" Y ")
                .pattern(" X ")
                .pattern("X X")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ROCKET_FINS)
                .define('Y', GCTags.COMPRESSED_STEEL)
                .define('X', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern(" Y ")
                .pattern("XYX")
                .pattern("X X")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.BEAM_CORE)
                .define('X', ConventionalItemTags.REDSTONE_DUSTS)
                .define('Y', GCTags.COMPRESSED_IRON)
                .define('Z', ConventionalItemTags.GLASS_PANES)
                .pattern("XYX")
                .pattern("YZY")
                .pattern("XYX")
                .unlockedBy(getHasName(GCItems.COMPRESSED_IRON), has(GCItems.COMPRESSED_IRON))
                .save(exporter);

        // Fabricator
        FabricationRecipeBuilder.create(GCItems.BASIC_WAFER, 3)
                .requires(Items.REDSTONE_TORCH)
                .save(exporter);
        FabricationRecipeBuilder.create(GCItems.ADVANCED_WAFER)
                .requires(Items.REPEATER)
                .save(exporter);

        FabricationRecipeBuilder.create(GCItems.BLUE_SOLAR_WAFER, 9)
                .requires(Items.LAPIS_LAZULI)
                .save(exporter);
        FabricationRecipeBuilder.create(GCItems.SOLAR_ARRAY_WAFER, 3)
                .requires(GCItems.SOLAR_DUST)
                .save(exporter);

        // Nuggets <-> Ingots
        GCRecipeProvider.nineBlockStorageRecipesWithCustomPacking(exporter, RecipeCategory.MISC, GCItems.METEORIC_IRON_NUGGET, RecipeCategory.MISC, GCItems.METEORIC_IRON_INGOT, "meteoric_iron_ingot_from_nuggets", "meteoric_iron_ingot");
        GCRecipeProvider.nineBlockStorageRecipesWithCustomPacking(exporter, RecipeCategory.MISC, GCItems.DESH_NUGGET, RecipeCategory.MISC, GCItems.DESH_INGOT, "desh_ingot_from_nuggets", "desh_ingot");
        GCRecipeProvider.nineBlockStorageRecipesWithCustomPacking(exporter, RecipeCategory.MISC, GCItems.LEAD_NUGGET, RecipeCategory.MISC, GCItems.LEAD_INGOT, "lead_ingot_from_nuggets", "lead_ingot");
        GCRecipeProvider.nineBlockStorageRecipesWithCustomPacking(exporter, RecipeCategory.MISC, GCItems.ALUMINUM_NUGGET, RecipeCategory.MISC, GCItems.ALUMINUM_INGOT, "aluminum_ingot_from_nuggets", "aluminum_ingot");
        GCRecipeProvider.nineBlockStorageRecipesWithCustomPacking(exporter, RecipeCategory.MISC, GCItems.TIN_NUGGET, RecipeCategory.MISC, GCItems.TIN_INGOT, "tin_ingot_from_nuggets", "tin_ingot");
        GCRecipeProvider.nineBlockStorageRecipesWithCustomPacking(exporter, RecipeCategory.MISC, GCItems.TITANIUM_NUGGET, RecipeCategory.MISC, GCItems.TITANIUM_INGOT, "titanium_ingot_from_nuggets", "titanium_ingot");

        // Ingots <-> Blocks
        GCRecipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(exporter, RecipeCategory.MISC, GCItems.METEORIC_IRON_INGOT, RecipeCategory.BUILDING_BLOCKS, GCItems.METEORIC_IRON_BLOCK, "meteoric_iron_ingot_from_block", "meteoric_iron_ingot");
        GCRecipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(exporter, RecipeCategory.MISC, GCItems.DESH_INGOT, RecipeCategory.BUILDING_BLOCKS, GCItems.DESH_BLOCK, "desh_ingot_from_block", "desh_ingot");
        GCRecipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(exporter, RecipeCategory.MISC, GCItems.LEAD_INGOT, RecipeCategory.BUILDING_BLOCKS, GCItems.LEAD_BLOCK, "lead_ingot_from_block", "lead_ingot");

        // skips aluminum and tin blocks
        GCRecipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(exporter, RecipeCategory.MISC, GCItems.TITANIUM_INGOT, RecipeCategory.BUILDING_BLOCKS, GCItems.TITANIUM_BLOCK, "titanium_ingot_from_block", "titanium_ingot");

        // Ore Smelting
        oreSmeltingAndBlasting(exporter, ImmutableList.of(GCItems.ALUMINUM_ORE, GCItems.DEEPSLATE_ALUMINUM_ORE, GCItems.RAW_ALUMINUM), RecipeCategory.MISC, GCItems.ALUMINUM_INGOT, 0.7F, 200, "aluminum_ingot");
        oreSmeltingAndBlasting(exporter, ImmutableList.of(GCItems.DESH_ORE, GCItems.RAW_DESH), RecipeCategory.MISC, GCItems.DESH_INGOT, 0.7F, 200, "desh_ingot");
        oreSmeltingAndBlasting(exporter, ImmutableList.of(GCItems.GALENA_ORE, GCItems.RAW_LEAD), RecipeCategory.MISC, GCItems.LEAD_INGOT, 0.7F, 200, "lead_ingot");
        oreSmeltingAndBlasting(exporter, ImmutableList.of(GCItems.MOON_COPPER_ORE), RecipeCategory.MISC, Items.COPPER_INGOT, 0.7F, 200, "copper_ingot");
        oreSmeltingAndBlasting(exporter, ImmutableList.of(GCItems.TIN_ORE, GCItems.DEEPSLATE_TIN_ORE, GCItems.MOON_TIN_ORE, GCItems.RAW_TIN), RecipeCategory.MISC, GCItems.TIN_INGOT, 0.7F, 200, "tin_ingot");
        oreSmeltingAndBlasting(exporter, ImmutableList.of(GCItems.ILMENITE_ORE, GCItems.RAW_TITANIUM), RecipeCategory.MISC, GCItems.TITANIUM_INGOT, 0.7F, 200, "titanium_ingot");
        oreSmeltingAndBlasting(exporter, ImmutableList.of(GCItems.RAW_METEORIC_IRON), RecipeCategory.MISC, GCItems.METEORIC_IRON_INGOT, 0.7F, 200, "meteoric_iron_ingot");
        oreSmeltingAndBlasting(exporter, ImmutableList.of(GCItems.IRON_SHARD), RecipeCategory.MISC, Items.IRON_INGOT, 0.7F, 200, "iron_ingot");
    }

    // Code copied from RecipeProvider class with changes to save with GC mod id
    public static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> consumer, RecipeCategory recipeCategory, ItemLike itemLike, RecipeCategory recipeCategory2, ItemLike itemLike2, String string, String string2) {
        GCRecipeProvider.nineBlockStorageRecipes(consumer, recipeCategory, itemLike, recipeCategory2, itemLike2, string, string2, RecipeProvider.getSimpleRecipeName(itemLike), null);
    }

    public static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> consumer, RecipeCategory recipeCategory, ItemLike itemLike, RecipeCategory recipeCategory2, ItemLike itemLike2, String string, String string2) {
        GCRecipeProvider.nineBlockStorageRecipes(consumer, recipeCategory, itemLike, recipeCategory2, itemLike2, RecipeProvider.getSimpleRecipeName(itemLike2), null, string, string2);
    }

    public static void nineBlockStorageRecipes(Consumer<FinishedRecipe> consumer, RecipeCategory recipeCategory, ItemLike itemLike, RecipeCategory recipeCategory2, ItemLike itemLike2, String string, @Nullable String string2, String string3, @Nullable String string4) {
        ShapelessRecipeBuilder.shapeless(recipeCategory, itemLike, 9).requires(itemLike2).group(string4).unlockedBy(RecipeProvider.getHasName(itemLike2), RecipeProvider.has(itemLike2)).save(consumer, Constant.id(string3));
        ShapedRecipeBuilder.shaped(recipeCategory2, itemLike2).define('#', itemLike).pattern("###").pattern("###").pattern("###").group(string2).unlockedBy(RecipeProvider.getHasName(itemLike), RecipeProvider.has(itemLike)).save(consumer, Constant.id(string));
    }

    public static void stairs(Consumer<FinishedRecipe> exporter, ItemLike stairs, ItemLike base) {
        stairBuilder(stairs, Ingredient.of(base))
                .unlockedBy(getHasName(base), has(base))
                .save(exporter);
    }

    public static void decorationBlock(Consumer<FinishedRecipe> exporter, ItemLike input, ItemLike block) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 1)
                .define('#', Items.STONE)
                .define('X', input)
                .pattern("## ")
                .pattern("##X")
                .unlockedBy(getHasName(input), has(input))
                .save(exporter);
    }

    public static void detailedDecorationBlock(Consumer<FinishedRecipe> exporter, ItemLike input, ItemLike block) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 1)
                .define('#', Items.STONE)
                .define('X', input)
                .pattern("##")
                .pattern("##")
                .pattern(" X")
                .unlockedBy(getHasName(input), has(input))
                .save(exporter);
    }

    private static void cookingRecipes(Consumer<FinishedRecipe> exporter, int cookingTime, ItemLike input, ItemLike result, float experience) {
        simpleCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING_RECIPE, cookingTime, input, result, experience);
        simpleCookingRecipe(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, cookingTime * 6, input, result, experience);
    }

    public static void oreSmeltingAndBlasting(
            Consumer<FinishedRecipe> finishedRecipeConsumer,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group
    ) {
        oreSmelting(finishedRecipeConsumer, ingredients, category, result, experience, cookingTime, group);
        oreBlasting(finishedRecipeConsumer, ingredients, category, result, experience, cookingTime / 2, group);
    }

}