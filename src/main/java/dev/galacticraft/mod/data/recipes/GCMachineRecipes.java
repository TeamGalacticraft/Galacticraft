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

import dev.galacticraft.mod.api.data.recipe.CircuitFabricatorRecipeBuilder;
import dev.galacticraft.mod.api.data.recipe.ShapedCompressorRecipeBuilder;
import dev.galacticraft.mod.api.data.recipe.ShapelessCompressorRecipeBuilder;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

public class GCMachineRecipes extends FabricRecipeProvider {
    public GCMachineRecipes(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        // Machine Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ADVANCED_SOLAR_PANEL)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.FULL_SOLAR_PANEL)
                .define('P', GCItems.STEEL_POLE)
                .define('W', GCItems.HEAVY_SEALABLE_ALUMINUM_WIRE)
                .define('A', GCItems.ADVANCED_WAFER)
                .pattern("SFS")
                .pattern("SPS")
                .pattern("WAW")
                .unlockedBy(getHasName(GCItems.ADVANCED_WAFER), has(GCItems.ADVANCED_WAFER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.BASIC_SOLAR_PANEL)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.FULL_SOLAR_PANEL)
                .define('P', GCItems.STEEL_POLE)
                .define('W', GCItems.ALUMINUM_WIRE)
                .define('B', GCItems.BASIC_WAFER)
                .pattern("SFS")
                .pattern("SPS")
                .pattern("WBW")
                .unlockedBy(getHasName(GCItems.BASIC_WAFER), has(GCItems.BASIC_WAFER))
                .save(output);

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
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.COAL_GENERATOR)
                .define('C', ConventionalItemTags.COPPER_INGOTS)
                .define('A', GCItems.ALUMINUM_INGOT)
                .define('F', Items.FURNACE)
                .define('W', GCItems.ALUMINUM_WIRE)
                .pattern("CCC")
                .pattern("AFA")
                .pattern("AWA")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItems.ALUMINUM_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.COMPRESSOR)
                .define('I', GCItems.ALUMINUM_INGOT)
                .define('A', Items.ANVIL)
                .define('C', ConventionalItemTags.COPPER_INGOTS)
                .define('W', GCItems.BASIC_WAFER)
                .pattern("IAI")
                .pattern("ICI")
                .pattern("IWI")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItems.ALUMINUM_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ELECTRIC_ARC_FURNACE)
                .define('H', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .define('E', GCItems.ELECTRIC_FURNACE)
                .define('M', GCItems.METEORIC_IRON_INGOT)
                .define('W', GCItems.ADVANCED_WAFER)
                .pattern("HHH")
                .pattern("HEH")
                .pattern("MWM")
                .unlockedBy(getHasName(GCItems.ADVANCED_WAFER), has(GCItems.ADVANCED_WAFER))
                .save(output);

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
                .save(output);
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
                .save(output, BuiltInRegistries.ITEM.getKey(GCItems.ELECTRIC_COMPRESSOR).withSuffix("_upgrade"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ELECTRIC_FURNACE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', Items.FURNACE)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('W', GCItems.BASIC_WAFER)
                .pattern("SSS")
                .pattern("SFS")
                .pattern("AWA")
                .unlockedBy(getHasName(GCItems.BASIC_WAFER), has(GCItems.BASIC_WAFER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ENERGY_STORAGE_MODULE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('B', GCItems.BATTERY)
                .pattern("SSS")
                .pattern("BBB")
                .pattern("SSS")
                .unlockedBy(getHasName(GCItems.BATTERY), has(GCItems.BATTERY))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_BUBBLE_DISTRIBUTOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.OXYGEN_FAN)
                .define('V', GCItems.OXYGEN_VENT)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("SFS")
                .pattern("VAV")
                .pattern("SFS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_COLLECTOR)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_COMPRESSOR)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('C', GCItems.OXYGEN_CONCENTRATOR)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("SAS")
                .pattern("ACA")
                .pattern("SBS")
                .unlockedBy(getHasName(GCItems.OXYGEN_CONCENTRATOR), has(GCItems.OXYGEN_CONCENTRATOR))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_DECOMPRESSOR)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_SEALER)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('F', GCItems.OXYGEN_FAN)
                .define('V', GCItems.OXYGEN_VENT)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("ASA")
                .pattern("VFV")
                .pattern("ASA")
                .unlockedBy(getHasName(GCItems.OXYGEN_FAN), has(GCItems.OXYGEN_FAN))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.OXYGEN_STORAGE_MODULE)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('T', GCItems.LARGE_OXYGEN_TANK)
                .pattern("SSS")
                .pattern("TTT")
                .pattern("SSS")
                .unlockedBy(getHasName(GCItems.LARGE_OXYGEN_TANK), has(GCItems.LARGE_OXYGEN_TANK))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.REFINERY)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('C', GCItems.COPPER_CANISTER)
                .define('F', Items.BLAST_FURNACE)
                .pattern("SCS")
                .pattern("SCS")
                .pattern("SFS")
                .unlockedBy(getHasName(GCItems.CRUDE_OIL_BUCKET), has(GCItems.CRUDE_OIL_BUCKET))
                .save(output);

        // Wires + Pipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.GLASS_FLUID_PIPE, 6)
                .define('X', Items.GLASS_PANE)
                .pattern("XXX")
                .pattern("   ")
                .pattern("XXX")
                .unlockedBy(getHasName(Items.GLASS_PANE), has(Items.GLASS_PANE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ALUMINUM_WIRE, 6)
                .define('W', Items.WHITE_WOOL)
                .define('A', GCItems.ALUMINUM_INGOT)
                .pattern("WWW")
                .pattern("AAA")
                .pattern("WWW")
                .unlockedBy(getHasName(Items.GLASS_PANE), has(Items.GLASS_PANE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.SEALABLE_ALUMINUM_WIRE, 6)
                .define('T', GCBlocks.TIN_DECORATION)
                .define('W', GCItems.ALUMINUM_WIRE)
                .pattern("TWT")
                .unlockedBy(getHasName(GCItems.ALUMINUM_WIRE), has(GCItems.ALUMINUM_WIRE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.WALKWAY, 5)
                .define('T', GCItems.COMPRESSED_TITANIUM)
                .pattern("TTT")
                .pattern(" T ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_TITANIUM), has(GCItems.COMPRESSED_TITANIUM))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.WIRE_WALKWAY, 5)
                .define('T', GCItems.COMPRESSED_TITANIUM)
                .define('W', GCItems.ALUMINUM_WIRE)
                .pattern("TTT")
                .pattern("WTW")
                .pattern("WWW")
                .unlockedBy(getHasName(GCItems.WALKWAY), has(GCItems.WALKWAY))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GCItems.WIRE_WALKWAY)
                .requires(GCItems.COMPRESSED_TITANIUM)
                .requires(GCItems.ALUMINUM_WIRE)
                .unlockedBy(getHasName(GCItems.WALKWAY), has(GCItems.WALKWAY))
                .save(output, BuiltInRegistries.ITEM.getKey(GCItems.WIRE_WALKWAY).withSuffix("_shapeless"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.FLUID_PIPE_WALKWAY, 5)
                .define('T', GCItems.COMPRESSED_TITANIUM)
                .define('P', GCItems.GLASS_FLUID_PIPE)
                .pattern("TTT")
                .pattern("PTP")
                .pattern("PPP")
                .unlockedBy(getHasName(GCItems.WALKWAY), has(GCItems.WALKWAY))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GCItems.FLUID_PIPE_WALKWAY)
                .requires(GCItems.COMPRESSED_TITANIUM)
                .requires(GCItems.GLASS_FLUID_PIPE)
                .unlockedBy(getHasName(GCItems.WALKWAY), has(GCItems.WALKWAY))
                .save(output, BuiltInRegistries.ITEM.getKey(GCItems.FLUID_PIPE_WALKWAY).withSuffix("_shapeless"));

        // Compressor
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_ALUMINUM)
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItems.ALUMINUM_INGOT))
                .requires(GCTags.ALUMINUM_INGOTS)
                .requires(GCTags.ALUMINUM_INGOTS)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_BRONZE)
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
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_IRON)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(ConventionalItemTags.IRON_INGOTS))
                .requires(ConventionalItemTags.IRON_INGOTS)
                .requires(ConventionalItemTags.IRON_INGOTS)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_METEORIC_IRON)
                .requires(GCItems.METEORIC_IRON_INGOT)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_STEEL)
                .unlockedBy(getHasName(GCItems.COMPRESSED_IRON), has(GCItems.COMPRESSED_IRON))
                .requires(ConventionalItemTags.COAL)
                .requires(GCItems.COMPRESSED_IRON)
                .requires(ConventionalItemTags.COAL)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_STEEL)
                .requires(GCTags.STEEL_INGOTS)
                .requires(GCTags.STEEL_INGOTS)
                .save(output, BuiltInRegistries.ITEM.getKey(GCItems.COMPRESSED_STEEL).withSuffix("_from_ingots"));
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_TIN)
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItems.TIN_INGOT))
                .requires(GCTags.TIN_INGOTS)
                .requires(GCTags.TIN_INGOTS)
                .save(output);
        ShapelessCompressorRecipeBuilder.shapeless(GCItems.COMPRESSED_TITANIUM)
                .unlockedBy(getHasName(GCItems.TITANIUM_INGOT), has(GCItems.TITANIUM_INGOT))
                .requires(GCItems.TITANIUM_INGOT)
                .requires(GCItems.TITANIUM_INGOT)
                .save(output);

        ShapedCompressorRecipeBuilder.create(GCItems.TIER_1_HEAVY_DUTY_PLATE, 2)
                .unlockedBy(getHasName(GCItems.COMPRESSED_BRONZE), has(GCItems.COMPRESSED_BRONZE))
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("BAS")
                .pattern("BAS")
                .save(output);

        ShapedCompressorRecipeBuilder.create(GCItems.TIER_1_HEAVY_DUTY_PLATE, 2)
                .unlockedBy(getHasName(GCItems.COMPRESSED_BRONZE), has(GCItems.COMPRESSED_BRONZE))
                .define('B', GCItems.COMPRESSED_BRONZE)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .define('S', GCItems.COMPRESSED_STEEL)
                .pattern("SAB")
                .pattern("SAB")
                .save(output, BuiltInRegistries.ITEM.getKey(GCItems.TIER_1_HEAVY_DUTY_PLATE).withSuffix("_flipped"));

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
                .requires(Items.LAPIS_LAZULI)
                .save(output);
        CircuitFabricatorRecipeBuilder.create(GCItems.SOLAR_ARRAY_WAFER, 3)
                .requires(GCItems.SOLAR_DUST)
                .save(output);
    }

    @Override
    public String getName() {
        return "Machine Recipes";
    }
}
