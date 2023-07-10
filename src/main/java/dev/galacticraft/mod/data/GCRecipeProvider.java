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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.data.recipes.CircuitFabricatorRecipeBuilder;
import dev.galacticraft.mod.data.recipes.ShapedCompressorRecipeBuilder;
import dev.galacticraft.mod.data.recipes.ShapelessCompressorRecipeBuilder;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class GCRecipeProvider extends FabricRecipeProvider {

    public GCRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, GCItems.ROCKET_LAUNCH_PAD, 9)
                .define('C', GCItems.COMPRESSED_IRON)
                .define('I', Items.IRON_BLOCK)
                .pattern("CCC")
                .pattern("III")
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
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
                .save(exporter, Constant.id("compressed_steel_from_ingots"));
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
                .save(exporter, Constant.id("electric_compressor_alt"));
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

        // Fabricator
        CircuitFabricatorRecipeBuilder.create(GCItems.BASIC_WAFER, 3)
                .requires(Items.REDSTONE_TORCH)
                .save(exporter);
        CircuitFabricatorRecipeBuilder.create(GCItems.ADVANCED_WAFER)
                .requires(Items.REPEATER)
                .save(exporter);

        CircuitFabricatorRecipeBuilder.create(GCItems.BLUE_SOLAR_WAFER, 9)
                .requires(Items.LAPIS_LAZULI)
                .save(exporter);
        CircuitFabricatorRecipeBuilder.create(GCItems.SOLAR_ARRAY_WAFER, 3)
                .requires(GCItems.SOLAR_DUST)
                .save(exporter);
    }
}
