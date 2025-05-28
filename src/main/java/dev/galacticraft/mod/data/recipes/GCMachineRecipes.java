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
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.api.data.recipe.CircuitFabricatorRecipeBuilder;
import dev.galacticraft.mod.api.data.recipe.ShapedCompressorRecipeBuilder;
import dev.galacticraft.mod.api.data.recipe.ShapelessCompressorRecipeBuilder;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GCMachineRecipes extends FabricRecipeProvider {
    public GCMachineRecipes(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        // Machine Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.ADVANCED_SOLAR_PANEL)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.FULL_SOLAR_PANEL)
                .define('P', GCItems.STEEL_POLE)
                .define('W', GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE)
                .define('A', GCItems.ADVANCED_WAFER)
                .pattern("SFS")
                .pattern("SPS")
                .pattern("WAW")
                .unlockedBy(getHasName(GCItems.ADVANCED_WAFER), has(GCItems.ADVANCED_WAFER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.BASIC_SOLAR_PANEL)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.FULL_SOLAR_PANEL)
                .define('P', GCItems.STEEL_POLE)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .define('B', GCItems.BASIC_WAFER)
                .pattern("SFS")
                .pattern("SPS")
                .pattern("WBW")
                .unlockedBy(getHasName(GCItems.BASIC_WAFER), has(GCItems.BASIC_WAFER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.CIRCUIT_FABRICATOR)
                .define('A', GCItemTags.ALUMINUM_INGOTS)
                .define('L', Items.LEVER)
                .define('B', ItemTags.STONE_BUTTONS)
                .define('F', ConventionalItemTags.PLAYER_WORKSTATIONS_FURNACES)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .define('R', Items.REDSTONE_TORCH)
                .pattern("ALA")
                .pattern("BFB")
                .pattern("WRW")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItemTags.ALUMINUM_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.COAL_GENERATOR)
                .define('C', ConventionalItemTags.COPPER_INGOTS)
                .define('A', GCItemTags.ALUMINUM_INGOTS)
                .define('F', ConventionalItemTags.PLAYER_WORKSTATIONS_FURNACES)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .pattern("CCC")
                .pattern("AFA")
                .pattern("AWA")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItemTags.ALUMINUM_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.COMPRESSOR)
                .define('I', GCItemTags.ALUMINUM_INGOTS)
                .define('A', Items.ANVIL)
                .define('C', ConventionalItemTags.COPPER_INGOTS)
                .define('W', GCItems.BASIC_WAFER)
                .pattern("IAI")
                .pattern("ICI")
                .pattern("IWI")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItemTags.ALUMINUM_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.ELECTRIC_ARC_FURNACE)
                .define('H', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .define('E', GCBlocks.ELECTRIC_FURNACE)
                .define('M', GCItems.METEORIC_IRON_INGOT)
                .define('W', GCItems.ADVANCED_WAFER)
                .pattern("HHH")
                .pattern("HEH")
                .pattern("MWM")
                .unlockedBy(getHasName(GCItems.METEORIC_IRON_INGOT), has(GCItems.METEORIC_IRON_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.ELECTRIC_COMPRESSOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('A', Items.ANVIL)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('W', GCItems.ADVANCED_WAFER)
                .define('I', GCBlocks.ALUMINUM_WIRE)
                .pattern("SAS")
                .pattern("SBS")
                .pattern("IWI")
                .unlockedBy(getHasName(GCItems.ADVANCED_WAFER), has(GCItems.ADVANCED_WAFER))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.ELECTRIC_COMPRESSOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('T', GCItems.COMPRESSED_TIN)
                .define('C', GCBlocks.COMPRESSOR)
                .define('W', GCItems.ADVANCED_WAFER)
                .define('I', GCBlocks.ALUMINUM_WIRE)
                .pattern("STS")
                .pattern("SCS")
                .pattern("IWI")
                .unlockedBy(getHasName(GCItems.ADVANCED_WAFER), has(GCItems.ADVANCED_WAFER))
                .save(output, BuiltInRegistries.ITEM.getKey(GCBlocks.ELECTRIC_COMPRESSOR.asItem()).withSuffix("_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.ELECTRIC_FURNACE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', ConventionalItemTags.PLAYER_WORKSTATIONS_FURNACES)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('W', GCItems.BASIC_WAFER)
                .pattern("SSS")
                .pattern("SFS")
                .pattern("AWA")
                .unlockedBy(getHasName(GCItems.BASIC_WAFER), has(GCItems.BASIC_WAFER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.ENERGY_STORAGE_MODULE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('B', GCItems.BATTERY)
                .pattern("SSS")
                .pattern("BBB")
                .pattern("SSS")
                .unlockedBy(getHasName(GCItems.BATTERY), has(GCItems.BATTERY))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.OXYGEN_FAN)
                .define('V', GCItems.OXYGEN_VENT)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("SFS")
                .pattern("VAV")
                .pattern("SFS")
                .unlockedBy(getHasName(GCItems.OXYGEN_FAN), has(GCItems.OXYGEN_FAN))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.OXYGEN_COLLECTOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.OXYGEN_FAN)
                .define('T', GCItems.TIN_CANISTER)
                .define('V', GCItems.OXYGEN_VENT)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('C', GCItems.OXYGEN_CONCENTRATOR)
                .pattern("SSS")
                .pattern("FTV")
                .pattern("ACA")
                .unlockedBy(getHasName(GCItems.OXYGEN_CONCENTRATOR), has(GCItems.OXYGEN_CONCENTRATOR))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.OXYGEN_COMPRESSOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('C', GCItems.OXYGEN_CONCENTRATOR)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("SAS")
                .pattern("ACA")
                .pattern("SBS")
                .unlockedBy(getHasName(GCItems.OXYGEN_CONCENTRATOR), has(GCItems.OXYGEN_CONCENTRATOR))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.OXYGEN_DECOMPRESSOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('R', Items.REDSTONE_TORCH)
                .define('C', GCItems.OXYGEN_CONCENTRATOR)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('F', GCItems.OXYGEN_FAN)
                .pattern("SFS")
                .pattern("ACA")
                .pattern("SRS")
                .unlockedBy(getHasName(GCItems.OXYGEN_CONCENTRATOR), has(GCItems.OXYGEN_CONCENTRATOR))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.FOOD_CANNER)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('P', Items.PISTON)
                .define('C', GCItems.EMPTY_CAN)
                .define('B', GCItems.BASIC_WAFER)
                .define('A', GCBlocks.ALUMINUM_WIRE)
                .pattern("SPS")
                .pattern("SCS")
                .pattern("ABA")
                .unlockedBy(getHasName(GCItems.EMPTY_CAN), has(GCItems.EMPTY_CAN))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.OXYGEN_SEALER)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.OXYGEN_FAN)
                .define('V', GCItems.OXYGEN_VENT)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("ASA")
                .pattern("VFV")
                .pattern("ASA")
                .unlockedBy(getHasName(GCItems.OXYGEN_FAN), has(GCItems.OXYGEN_FAN))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.OXYGEN_STORAGE_MODULE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('T', GCItems.LARGE_OXYGEN_TANK)
                .pattern("SSS")
                .pattern("TTT")
                .pattern("SSS")
                .unlockedBy(getHasName(GCItems.LARGE_OXYGEN_TANK), has(GCItems.LARGE_OXYGEN_TANK))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.REFINERY)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('C', GCItems.COPPER_CANISTER)
                .define('F', Items.BLAST_FURNACE)
                .pattern("SCS")
                .pattern("SCS")
                .pattern("SFS")
                .unlockedBy(getHasName(GCItems.CRUDE_OIL_BUCKET), has(GCItems.CRUDE_OIL_BUCKET))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.FUEL_LOADER)
                .define('U', GCItems.COMPRESSED_COPPER)
                .define('C', GCItems.TIN_CANISTER)
                .define('T', GCItems.COMPRESSED_TIN)
                .define('W', GCItems.BASIC_WAFER)
                .pattern("UUU")
                .pattern("UCU")
                .pattern("TWT")
                .unlockedBy(getHasName(GCItems.FUEL_BUCKET), has(GCItems.FUEL_BUCKET))
                .save(output);

        // Pipes from panes
        for (PipeColor color : PipeColor.values()) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.GLASS_FLUID_PIPES.get(color), 6)
                    .define('X', color.glassPane())
                    .pattern("XXX")
                    .pattern("   ")
                    .pattern("XXX")
                    .unlockedBy(getHasName(Items.GLASS_PANE), has(Items.GLASS_PANE))
                    .save(output);
        }

        // Dye fluid pipes
        for (DyeColor dye : DyeColor.values()) {
            PipeColor color = PipeColor.fromDye(dye);
            ItemLike pipe = GCBlocks.GLASS_FLUID_PIPES.get(color);
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, pipe, 8)
                    .define('P', GCBlocks.GLASS_FLUID_PIPE)
                    .define('D', color.dye())
                    .pattern("PPP")
                    .pattern("PDP")
                    .pattern("PPP")
                    .unlockedBy(getHasName(GCBlocks.GLASS_FLUID_PIPE), has(GCBlocks.GLASS_FLUID_PIPE))
                    .save(output, RecipeBuilder.getDefaultRecipeId(pipe).withPrefix("dye_"));
        }

        // Wash fluid pipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.GLASS_FLUID_PIPE, 8)
                .define('P', GCItemTags.STAINED_GLASS_FLUID_PIPES)
                .define('W', Items.WATER_BUCKET)
                .pattern("PPP")
                .pattern("PWP")
                .pattern("PPP")
                .unlockedBy(getHasName(GCBlocks.GLASS_FLUID_PIPE), has(GCBlocks.GLASS_FLUID_PIPE))
                .save(output, Constant.id("_wash_stained_glass_fluid_pipe")); // Leading _ here makes REI show it after the pane recipe

        // Wires
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.ALUMINUM_WIRE, 6)
                .define('W', Items.WHITE_WOOL)
                .define('A', GCItemTags.ALUMINUM_INGOTS)
                .pattern("WWW")
                .pattern("AAA")
                .pattern("WWW")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItemTags.ALUMINUM_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.SEALABLE_ALUMINUM_WIRE, 6)
                .define('T', GCBlocks.TIN_DECORATION)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .pattern("TWT")
                .unlockedBy(getHasName(GCBlocks.ALUMINUM_WIRE), has(GCBlocks.ALUMINUM_WIRE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.WALKWAY, 5)
                .define('T', GCItems.COMPRESSED_TITANIUM)
                .pattern("TTT")
                .pattern(" T ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_TITANIUM), has(GCItems.COMPRESSED_TITANIUM))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.WIRE_WALKWAY, 5)
                .define('T', GCItems.COMPRESSED_TITANIUM)
                .define('W', GCBlocks.ALUMINUM_WIRE)
                .pattern("TTT")
                .pattern("WTW")
                .pattern("WWW")
                .unlockedBy(getHasName(GCItems.COMPRESSED_TITANIUM), has(GCItems.COMPRESSED_TITANIUM))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GCBlocks.WIRE_WALKWAY)
                .requires(GCBlocks.WALKWAY)
                .requires(GCBlocks.ALUMINUM_WIRE)
                .unlockedBy(getHasName(GCBlocks.WALKWAY), has(GCBlocks.WALKWAY))
                .save(output, BuiltInRegistries.ITEM.getKey(GCBlocks.WIRE_WALKWAY.asItem()).withSuffix("_shapeless"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.FLUID_PIPE_WALKWAY, 5)
                .define('T', GCItems.COMPRESSED_TITANIUM)
                .define('P', GCItemTags.GLASS_FLUID_PIPES)
                .pattern("TTT")
                .pattern("PTP")
                .pattern("PPP")
                .unlockedBy(getHasName(GCItems.COMPRESSED_TITANIUM), has(GCItems.COMPRESSED_TITANIUM))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GCBlocks.FLUID_PIPE_WALKWAY)
                .requires(GCBlocks.WALKWAY)
                .requires(GCItemTags.GLASS_FLUID_PIPES)
                .unlockedBy(getHasName(GCBlocks.WALKWAY), has(GCBlocks.WALKWAY))
                .save(output, BuiltInRegistries.ITEM.getKey(GCBlocks.FLUID_PIPE_WALKWAY.asItem()).withSuffix("_shapeless"));

        // Compressor
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_ALUMINUM)
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItemTags.ALUMINUM_INGOTS))
                .requires(GCItemTags.ALUMINUM_INGOTS)
                .requires(GCItemTags.ALUMINUM_INGOTS)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_BRONZE)
                .unlockedBy(getHasName(GCItems.COMPRESSED_COPPER), has(GCItems.COMPRESSED_COPPER))
                .requires(GCItems.COMPRESSED_COPPER)
                .requires(GCItems.COMPRESSED_TIN)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_COPPER)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(ConventionalItemTags.COPPER_INGOTS))
                .requires(ConventionalItemTags.COPPER_INGOTS)
                .requires(ConventionalItemTags.COPPER_INGOTS)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_DESH)
                .unlockedBy(getHasName(GCItems.DESH_INGOT), has(GCItems.DESH_INGOT))
                .requires(GCItems.DESH_INGOT)
                .requires(GCItems.DESH_INGOT)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_IRON)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(ConventionalItemTags.IRON_INGOTS))
                .requires(ConventionalItemTags.IRON_INGOTS)
                .requires(ConventionalItemTags.IRON_INGOTS)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_METEORIC_IRON)
                .unlockedBy(getHasName(GCItems.METEORIC_IRON_INGOT), has(GCItems.METEORIC_IRON_INGOT))
                .requires(GCItems.METEORIC_IRON_INGOT)
                .requires(GCItems.METEORIC_IRON_INGOT)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_STEEL)
                .unlockedBy(getHasName(GCItems.COMPRESSED_IRON), has(GCItems.COMPRESSED_IRON))
                .requires(ItemTags.COALS)
                .requires(GCItems.COMPRESSED_IRON)
                .requires(ItemTags.COALS)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_STEEL)
                .unlockedBy("has_steel_ingot", has(GCItemTags.STEEL_INGOTS))
                .requires(GCItemTags.STEEL_INGOTS)
                .requires(GCItemTags.STEEL_INGOTS)
                .save(output, BuiltInRegistries.ITEM.getKey(GCItems.COMPRESSED_STEEL).withSuffix("_from_ingots"));
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_TIN)
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItemTags.TIN_INGOTS))
                .requires(GCItemTags.TIN_INGOTS)
                .requires(GCItemTags.TIN_INGOTS)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_TITANIUM)
                .unlockedBy(getHasName(GCItems.TITANIUM_INGOT), has(GCItems.TITANIUM_INGOT))
                .requires(GCItems.TITANIUM_INGOT)
                .requires(GCItems.TITANIUM_INGOT)
                .save(output);

        ShapedCompressorRecipeBuilder.create(GCItems.TIER_1_HEAVY_DUTY_PLATE, 2)
                .unlockedBy(getHasName(GCItems.COMPRESSED_BRONZE), has(GCItems.COMPRESSED_BRONZE))
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .pattern("SAB")
                .pattern("SAB")
                .save(output);

        ShapelessCompressorRecipeBuilder.shapeless(GCItems.TIER_2_HEAVY_DUTY_PLATE)
                .unlockedBy(getHasName(GCItems.COMPRESSED_METEORIC_IRON), has(GCItems.COMPRESSED_METEORIC_IRON))
                .requires(GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .requires(GCItems.COMPRESSED_METEORIC_IRON)
                .save(output);

        ShapelessCompressorRecipeBuilder.shapeless(GCItems.TIER_3_HEAVY_DUTY_PLATE)
                .unlockedBy(getHasName(GCItems.COMPRESSED_METEORIC_IRON), has(GCItems.COMPRESSED_METEORIC_IRON))
                .requires(GCItems.TIER_2_HEAVY_DUTY_PLATE)
                .requires(GCItems.COMPRESSED_DESH)
                .save(output);

        // Circuit Fabricator
        CircuitFabricatorRecipeBuilder.create(GCItems.BASIC_WAFER, 3)
                .requires(Items.REDSTONE_TORCH)
                .save(output);
        CircuitFabricatorRecipeBuilder.create(GCItems.ADVANCED_WAFER)
                .requires(Items.REPEATER)
                .save(output);

        CircuitFabricatorRecipeBuilder.create(GCItems.BLUE_SOLAR_WAFER, 9)
                .requires(ConventionalItemTags.LAPIS_GEMS)
                .save(output);
        CircuitFabricatorRecipeBuilder.create(GCItems.SOLAR_ARRAY_WAFER, 3)
                .requires(GCItemTags.SOLAR_DUSTS)
                .save(output);
    }

    @Override
    public @NotNull String getName() {
        return "Machine Recipes";
    }
}
