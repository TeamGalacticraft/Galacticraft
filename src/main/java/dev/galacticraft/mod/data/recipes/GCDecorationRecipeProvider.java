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

import dev.galacticraft.mod.api.data.recipe.*;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.data.model.GCBlockFamilies;
import dev.galacticraft.mod.tag.GCItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

/**
 * Decoration block recipes
 */
public class GCDecorationRecipeProvider extends FabricRecipeProvider {
    public GCDecorationRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        GCBlockFamilies.getAllFamilies()
                .filter(BlockFamily::shouldGenerateRecipe)
                .forEach(blockFamily -> generateBlockFamilyRecipes(output, blockFamily));

        baseDecorationBlocks(output, GCItems.COMPRESSED_ALUMINUM, GCBlockFamilies.ALUMINUM_DECORATIONS);
        baseDecorationBlocks(output, GCItems.COMPRESSED_BRONZE, GCBlockFamilies.BRONZE_DECORATIONS);
        baseDecorationBlocks(output, GCItems.COMPRESSED_COPPER, GCBlockFamilies.COPPER_DECORATIONS);
        baseDecorationBlocks(output, GCItems.COMPRESSED_IRON, GCBlockFamilies.IRON_DECORATIONS);
        baseDecorationBlocks(output, GCItems.COMPRESSED_METEORIC_IRON, GCBlockFamilies.METEORIC_IRON_DECORATIONS);
        baseDecorationBlocks(output, GCItems.COMPRESSED_STEEL, GCBlockFamilies.STEEL_DECORATIONS);
        baseDecorationBlocks(output, GCItems.COMPRESSED_TIN, GCBlockFamilies.TIN_DECORATIONS);
        baseDecorationBlocks(output, GCItems.COMPRESSED_TITANIUM, GCBlockFamilies.TITANIUM_DECORATIONS);

        smeltBuildingBlock(output, GCBlocks.MOON_ROCK, GCBlocks.COBBLED_MOON_ROCK);
        smeltBuildingBlock(output, GCBlocks.CRACKED_MOON_ROCK_BRICK, GCBlocks.MOON_ROCK_BRICK);
        squareStone(output, GCBlocks.MOON_ROCK_BRICK, GCBlocks.MOON_ROCK);
        squareStone(output, GCBlocks.POLISHED_MOON_ROCK, GCBlocks.MOON_ROCK_BRICK);
        chiseledStone(output, GCBlocks.CHISELED_MOON_ROCK_BRICK, GCBlocks.MOON_ROCK_BRICK_SLAB, GCBlocks.MOON_ROCK, GCBlocks.MOON_ROCK_BRICK);
        pillar(output, GCBlocks.MOON_ROCK_PILLAR, GCBlocks.MOON_ROCK);

        smeltBuildingBlock(output, GCBlocks.LUNASLATE, GCBlocks.COBBLED_LUNASLATE);

        squareStone(output, GCBlocks.MOON_BASALT_BRICK, GCBlocks.MOON_BASALT);
        smeltBuildingBlock(output, GCBlocks.CRACKED_MOON_BASALT_BRICK, GCBlocks.MOON_BASALT_BRICK);

        smeltBuildingBlock(output, GCBlocks.MARS_STONE, GCBlocks.MARS_COBBLESTONE);

        GCShapedRecipeBuilder.crafting(RecipeCategory.DECORATIONS, GCItems.GLOWSTONE_TORCH, 4)
                .define('G', ConventionalItemTags.GLOWSTONE_DUSTS)
                .define('S', ConventionalItemTags.WOODEN_RODS)
                .pattern("G")
                .pattern("S")
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(ConventionalItemTags.GLOWSTONE_DUSTS))
                .emiDefault(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.DECORATIONS, GCBlocks.GLOWSTONE_LANTERN)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('I', ConventionalItemTags.IRON_NUGGETS)
                .pattern("III")
                .pattern("IGI")
                .pattern("III")
                .unlockedBy(getHasName(GCItems.GLOWSTONE_TORCH), has(GCItems.GLOWSTONE_TORCH))
                .emiDefault(true)
                .save(output);

        // Vacuum glass
        GCShapedRecipeBuilder.crafting(RecipeCategory.DECORATIONS, GCBlocks.VACUUM_GLASS)
                .define('G', ConventionalItemTags.GLASS_BLOCKS_COLORLESS)
                .define('T', GCItemTags.TIN_INGOTS)
                .pattern("TGT")
                .pattern("GGG")
                .pattern("TGT")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItemTags.TIN_INGOTS))
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.DECORATIONS, GCBlocks.CLEAR_VACUUM_GLASS)
                .define('G', ConventionalItemTags.GLASS_BLOCKS_COLORLESS)
                .define('A', GCItemTags.ALUMINUM_INGOTS)
                .pattern("AGA")
                .pattern("GGG")
                .pattern("AGA")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItemTags.ALUMINUM_INGOTS))
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.DECORATIONS, GCBlocks.STRONG_VACUUM_GLASS)
                .define('G', ConventionalItemTags.GLASS_BLOCKS_COLORLESS)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("AGA")
                .pattern("GGG")
                .pattern("AGA")
                .unlockedBy(getHasName(GCItems.COMPRESSED_ALUMINUM), has(GCItems.COMPRESSED_ALUMINUM))
                .save(output);

        // Misc decoration blocks
        GCShapedRecipeBuilder.crafting(RecipeCategory.DECORATIONS, GCBlocks.IRON_GRATING, 4)
                .define('I', Items.IRON_BARS)
                .pattern("II")
                .pattern("II")
                .unlockedBy(getHasName(Items.IRON_BARS), has(Items.IRON_BARS))
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.DECORATIONS, GCBlocks.TIN_LADDER, 6)
                .define('T', GCItemTags.TIN_INGOTS)
                .pattern("T T")
                .pattern("TTT")
                .pattern("T T")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItemTags.TIN_INGOTS))
                .save(output);
    }

    private static void generateBlockFamilyRecipes(RecipeOutput output, BlockFamily blockFamily) {
        Block base = blockFamily.getBaseBlock();
        blockFamily.getVariants().forEach((variant, block) -> {
            switch (variant) {
                case SLAB: {
                    slab(output, RecipeCategory.BUILDING_BLOCKS, block, base);
                    stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, block, base, 2);
                    break;
                }
                case STAIRS: {
                    stairs(output, block, base);
                    stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, block, base);
                    break;
                }
                case WALL: {
                    wall(output, RecipeCategory.DECORATIONS, block, base);
                    stonecutterResultFromBase(output, RecipeCategory.DECORATIONS, block, base);
                    break;
                }
            }
        });
    }

    public static void pillar(RecipeOutput output, ItemLike pillar, ItemLike base) {
        GCShapedRecipeBuilder.crafting(RecipeCategory.BUILDING_BLOCKS, pillar, 2)
                .define('#', base)
                .pattern("#")
                .pattern("#")
                .unlockedBy(getHasName(base), has(base))
                .save(output);

        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, pillar, base);
    }

    public static void squareStone(RecipeOutput output, ItemLike brick, ItemLike base) {
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, brick, base);
        GCShapedRecipeBuilder.crafting(RecipeCategory.BUILDING_BLOCKS, brick, 4)
                .define('#', base)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getHasName(base), has(base))
                .save(output);
    }

    public static void smeltBuildingBlock(RecipeOutput output, ItemLike result, ItemLike ingredient) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, 0.1F, 200)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(output);
    }

    public static void chiseledStone(RecipeOutput output, ItemLike chiseled, ItemLike brickSlab, ItemLike base, ItemLike brick) {
        chiseled(output, RecipeCategory.BUILDING_BLOCKS, chiseled, brickSlab);
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, chiseled, base);
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, chiseled, brick);
    }

    public static void baseDecorationBlocks(RecipeOutput output, ItemLike input, GCBlockFamilies.DecorationFamily family) {
        GCShapedRecipeBuilder.crafting(RecipeCategory.BUILDING_BLOCKS, family.original().getBaseBlock(), 4)
                .define('#', Items.STONE)
                .define('X', input)
                .pattern("## ")
                .pattern("##X")
                .unlockedBy(getHasName(input), has(input))
                .emiDefault(true)
                .save(output);

        GCShapedRecipeBuilder.crafting(RecipeCategory.BUILDING_BLOCKS, family.detailed().getBaseBlock(), 4)
                .define('#', Items.STONE)
                .define('X', input)
                .pattern("##")
                .pattern("##")
                .pattern(" X")
                .unlockedBy(getHasName(input), has(input))
                .emiDefault(true)
                .save(output);
    }

    public static void stairs(RecipeOutput output, ItemLike stairs, ItemLike base) {
        stairBuilder(stairs, Ingredient.of(base))
                .unlockedBy(getHasName(base), has(base))
                .save(output);
    }

    @Override
    public String getName() {
        return "Decoration Recipes";
    }
}
