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
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Ingot and raw ore recipes
 */
public class GCOreRecipeProvider extends FabricRecipeProvider {

    public GCOreRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        // Ore Smelting and Blasting
        oreSmeltingAndBlasting(output, List.of(GCBlocks.SILICON_ORE, GCBlocks.DEEPSLATE_SILICON_ORE), GCItems.SILICON, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCBlocks.MOON_COPPER_ORE, GCBlocks.LUNASLATE_COPPER_ORE), Items.COPPER_INGOT, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCItems.RAW_ALUMINUM, GCBlocks.ALUMINUM_ORE, GCBlocks.DEEPSLATE_ALUMINUM_ORE), GCItems.ALUMINUM_INGOT, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCItems.RAW_TIN, GCBlocks.TIN_ORE, GCBlocks.DEEPSLATE_TIN_ORE, GCBlocks.MOON_TIN_ORE), GCItems.TIN_INGOT, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCItems.RAW_METEORIC_IRON), GCItems.METEORIC_IRON_INGOT, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCBlocks.MOON_CHEESE_ORE), GCItems.MOON_CHEESE_CURD, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCBlocks.LUNAR_SAPPHIRE_ORE), GCItems.LUNAR_SAPPHIRE, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCItems.RAW_DESH, GCBlocks.DESH_ORE), GCItems.DESH_INGOT, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCItems.RAW_TITANIUM, GCBlocks.ILMENITE_ORE), GCItems.TITANIUM_INGOT, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCItems.RAW_LEAD, GCBlocks.GALENA_ORE), GCItems.LEAD_INGOT, 0.7f, 100);
        oreSmeltingAndBlasting(output, List.of(GCBlocks.SOLAR_ORE), GCItems.SOLAR_DUST, 0.7f, 100);

        
        // Nuggets <-> Ingots
        nineBlockStoragePackingRecipe(output, RecipeCategory.MISC, GCItems.METEORIC_IRON_NUGGET, RecipeCategory.MISC, GCItems.METEORIC_IRON_INGOT, "meteoric_iron_ingot_from_nuggets", "meteoric_iron_ingot");
        nineBlockStoragePackingRecipe(output, RecipeCategory.MISC, GCItems.DESH_NUGGET, RecipeCategory.MISC, GCItems.DESH_INGOT, "desh_ingot_from_nuggets", "desh_ingot");
        nineBlockStoragePackingRecipe(output, RecipeCategory.MISC, GCItems.LEAD_NUGGET, RecipeCategory.MISC, GCItems.LEAD_INGOT, "lead_ingot_from_nuggets", "lead_ingot");
        nineBlockStoragePackingRecipe(output, RecipeCategory.MISC, GCItems.ALUMINUM_NUGGET, RecipeCategory.MISC, GCItems.ALUMINUM_INGOT, "aluminum_ingot_from_nuggets", "aluminum_ingot");
        nineBlockStoragePackingRecipe(output, RecipeCategory.MISC, GCItems.TIN_NUGGET, RecipeCategory.MISC, GCItems.TIN_INGOT, "tin_ingot_from_nuggets", "tin_ingot");
        nineBlockStoragePackingRecipe(output, RecipeCategory.MISC, GCItems.TITANIUM_NUGGET, RecipeCategory.MISC, GCItems.TITANIUM_INGOT, "titanium_ingot_from_nuggets", "titanium_ingot");

        // Ingots <-> Blocks
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.METEORIC_IRON_INGOT, RecipeCategory.BUILDING_BLOCKS, GCBlocks.METEORIC_IRON_BLOCK, "meteoric_iron_ingot_from_block", "meteoric_iron_ingot");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.DESH_INGOT, RecipeCategory.BUILDING_BLOCKS, GCBlocks.DESH_BLOCK, "desh_ingot_from_block", "desh_ingot");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.LEAD_INGOT, RecipeCategory.BUILDING_BLOCKS, GCBlocks.LEAD_BLOCK, "lead_ingot_from_block", "lead_ingot");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.ALUMINUM_INGOT, RecipeCategory.BUILDING_BLOCKS, GCBlocks.ALUMINUM_BLOCK, "aluminum_ingot_from_block", "aluminum_ingot");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.TIN_INGOT, RecipeCategory.BUILDING_BLOCKS, GCBlocks.TIN_BLOCK, "tin_ingot_from_block", "tin_ingot");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.TITANIUM_INGOT, RecipeCategory.BUILDING_BLOCKS, GCBlocks.TITANIUM_BLOCK, "titanium_ingot_from_block", "titanium_ingot");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.LUNAR_SAPPHIRE, RecipeCategory.BUILDING_BLOCKS, GCBlocks.LUNAR_SAPPHIRE_BLOCK, "lunar_sapphire_from_block", "lunar_sapphire");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.SILICON, RecipeCategory.BUILDING_BLOCKS, GCBlocks.SILICON_BLOCK, "silicon_from_block", "silicon");
        crystalBlockStorageRecipe(output, GCItems.OLIVINE_SHARD, RecipeCategory.MISC, GCBlocks.OLIVINE_BLOCK, "olivine_block_from_shards", null);
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.RAW_METEORIC_IRON, RecipeCategory.BUILDING_BLOCKS, GCBlocks.RAW_METEORIC_IRON_BLOCK, "raw_meteoric_iron_from_block", "raw_meteoric_iron");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.RAW_DESH, RecipeCategory.BUILDING_BLOCKS, GCBlocks.RAW_DESH_BLOCK, "raw_desh_from_block", "raw_desh");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.RAW_LEAD, RecipeCategory.BUILDING_BLOCKS, GCBlocks.RAW_LEAD_BLOCK, "raw_lead_from_block", "raw_lead");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.RAW_ALUMINUM, RecipeCategory.BUILDING_BLOCKS, GCBlocks.RAW_ALUMINUM_BLOCK, "raw_aluminum_from_block", "raw_aluminum");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.RAW_TIN, RecipeCategory.BUILDING_BLOCKS, GCBlocks.RAW_TIN_BLOCK, "raw_tin_from_block", "raw_tin");
        nineBlockStorageUnpackingRecipe(output, RecipeCategory.MISC, GCItems.RAW_TITANIUM, RecipeCategory.BUILDING_BLOCKS, GCBlocks.RAW_TITANIUM_BLOCK, "raw_titanium_from_block", "raw_titanium");
    }

    @Override
    public String getName() {
        return "Ore Recipes";
    }

    private static void oreSmeltingAndBlasting(RecipeOutput output, List<ItemLike> input, ItemLike result, float experience, int time) {
        oreSmelting(output, input, RecipeCategory.MISC, result, experience, time * 2, BuiltInRegistries.ITEM.getKey(result.asItem()).getPath());
        oreBlasting(output, input, RecipeCategory.MISC, result, experience, time, BuiltInRegistries.ITEM.getKey(result.asItem()).getPath());
    }

    public static void nineBlockStoragePackingRecipe(RecipeOutput exporter, RecipeCategory reverseCategory, ItemLike baseItem, RecipeCategory compactingCategory, ItemLike compactItem, String compactingId, String compactingGroup) {
        nineBlockStorageRecipe(exporter, reverseCategory, baseItem, compactingCategory, compactItem, compactingId, compactingGroup, getSimpleRecipeName(baseItem), null);
    }

    public static void nineBlockStorageUnpackingRecipe(RecipeOutput exporter, RecipeCategory reverseCategory, ItemLike baseItem, RecipeCategory compactingCategory, ItemLike compactItem, String reverseId, String reverseGroup) {
        nineBlockStorageRecipe(exporter, reverseCategory, baseItem, compactingCategory, compactItem, getSimpleRecipeName(compactItem), null, reverseId, reverseGroup);
    }

    public static void nineBlockStorageRecipe(RecipeOutput exporter, RecipeCategory reverseCategory, ItemLike baseItem, RecipeCategory compactingCategory, ItemLike compactItem, String compactingId, @Nullable String compactingGroup, String reverseId, @Nullable String reverseGroup) {
        ShapelessRecipeBuilder.shapeless(reverseCategory, baseItem, 9)
                .requires(compactItem)
                .group(reverseGroup)
                .unlockedBy(getHasName(compactItem), has(compactItem))
                .save(exporter, Constant.id(reverseId));
        ShapedRecipeBuilder.shaped(compactingCategory, compactItem)
                .define('#', baseItem)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(compactingGroup)
                .unlockedBy(getHasName(baseItem), has(baseItem))
                .save(exporter, Constant.id(compactingId));
    }

    public static void crystalBlockStorageRecipe(RecipeOutput exporter, ItemLike baseItem, RecipeCategory compactingCategory, ItemLike compactItem, String compactingId, @Nullable String compactingGroup) {
        ShapedRecipeBuilder.shaped(compactingCategory, compactItem)
                .define('#', baseItem)
                .pattern("##")
                .pattern("##")
                .group(compactingGroup)
                .unlockedBy(getHasName(baseItem), has(baseItem))
                .save(exporter, Constant.id(compactingId));
    }
}
