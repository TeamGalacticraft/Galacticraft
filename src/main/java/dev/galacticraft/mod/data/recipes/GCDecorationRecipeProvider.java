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
import dev.galacticraft.mod.data.model.GCBlockFamilies;
import dev.galacticraft.mod.tag.GCItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
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

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.GLOWSTONE_TORCH, 4)
                .define('G', ConventionalItemTags.GLOWSTONE_DUSTS)
                .define('S', ConventionalItemTags.WOODEN_RODS)
                .pattern("G")
                .pattern("S")
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(ConventionalItemTags.GLOWSTONE_DUSTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.GLOWSTONE_LANTERN)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('I', ConventionalItemTags.IRON_NUGGETS)
                .pattern("III")
                .pattern("IGI")
                .pattern("III")
                .unlockedBy(getHasName(GCItems.GLOWSTONE_TORCH), has(GCItems.GLOWSTONE_TORCH))
                .save(output);

        // Vacuum glass
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.VACUUM_GLASS)
                .define('G', ConventionalItemTags.GLASS_BLOCKS_COLORLESS)
                .define('T', GCItemTags.TIN_INGOTS)
                .pattern("TGT")
                .pattern("GGG")
                .pattern("TGT")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItemTags.TIN_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.CLEAR_VACUUM_GLASS)
                .define('G', ConventionalItemTags.GLASS_BLOCKS_COLORLESS)
                .define('A', GCItemTags.ALUMINUM_INGOTS)
                .pattern("AGA")
                .pattern("GGG")
                .pattern("AGA")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItemTags.ALUMINUM_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.STRONG_VACUUM_GLASS)
                .define('G', ConventionalItemTags.GLASS_BLOCKS_COLORLESS)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("AGA")
                .pattern("GGG")
                .pattern("AGA")
                .unlockedBy(getHasName(GCItems.COMPRESSED_ALUMINUM), has(GCItems.COMPRESSED_ALUMINUM))
                .save(output);

        // Misc decoration blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.IRON_GRATING, 4)
                .define('I', Items.IRON_BARS)
                .pattern("II")
                .pattern("II")
                .unlockedBy(getHasName(Items.IRON_BARS), has(Items.IRON_BARS))
                .save(output);

        // Metal Ladder Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.TIN_LADDER, 6)
                .define('T', GCItemTags.TIN_INGOTS)
                .pattern("T T")
                .pattern("TTT")
                .pattern("T T")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItemTags.TIN_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.ALUMINUM_LADDER, 6)
                .define('A', GCItemTags.ALUMINUM_INGOTS)
                .pattern("A A")
                .pattern("AAA")
                .pattern("A A")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItemTags.ALUMINUM_INGOTS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.TITANIUM_LADDER, 6)
                .define('T', GCItemTags.TITANIUM_INGOTS)
                .pattern("T T")
                .pattern("TTT")
                .pattern("T T")
                .unlockedBy(getHasName(GCItems.TITANIUM_INGOT), has(GCItemTags.TITANIUM_INGOTS))
                .save(output);

//        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.BRONZE_LADDER, 6)
//                .define('B', GCItemTags.BRONZE_INGOTS)
//                .pattern("B B")
//                .pattern("BBB")
//                .pattern("B B")
//                .unlockedBy(getHasName(GCItems.BRONZE_INGOT), has(GCItemTags.BRONZE_INGOTS))
//                .save(output);
//
//        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCBlocks.STEEL_LADDER, 6)
//                .define('S', GCItemTags.STEEL_INGOTS)
//                .pattern("S S")
//                .pattern("SSS")
//                .pattern("S S")
//                .unlockedBy(getHasName(GCItems.STEEL_INGOT), has(GCItemTags.STEEL_INGOTS))
//                .save(output);

        // Metal decoration blocks
        decorationBlock(output, GCItems.COMPRESSED_TIN,
                GCBlocks.TIN_DECORATION.item(),
                GCBlocks.TIN_DECORATION.slabItem(),
                GCBlocks.TIN_DECORATION.stairsItem(),
                GCBlocks.TIN_DECORATION.wallItem()
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_TIN,
                GCBlocks.TIN_DECORATION.detailedItem(),
                GCBlocks.TIN_DECORATION.detailedSlabItem(),
                GCBlocks.TIN_DECORATION.detailedStairsItem(),
                GCBlocks.TIN_DECORATION.detailedWallItem()
        );

        decorationBlock(output, GCItems.COMPRESSED_COPPER,
                GCBlocks.COPPER_DECORATION.item(),
                GCBlocks.COPPER_DECORATION.slabItem(),
                GCBlocks.COPPER_DECORATION.stairsItem(),
                GCBlocks.COPPER_DECORATION.wallItem()
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_COPPER,
                GCBlocks.COPPER_DECORATION.detailedItem(),
                GCBlocks.COPPER_DECORATION.detailedSlabItem(),
                GCBlocks.COPPER_DECORATION.detailedStairsItem(),
                GCBlocks.COPPER_DECORATION.detailedWallItem()
        );

        decorationBlock(output, GCItems.COMPRESSED_IRON,
                GCBlocks.IRON_DECORATION.item(),
                GCBlocks.IRON_DECORATION.slabItem(),
                GCBlocks.IRON_DECORATION.stairsItem(),
                GCBlocks.IRON_DECORATION.wallItem()
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_IRON,
                GCBlocks.IRON_DECORATION.detailedItem(),
                GCBlocks.IRON_DECORATION.detailedSlabItem(),
                GCBlocks.IRON_DECORATION.detailedStairsItem(),
                GCBlocks.IRON_DECORATION.detailedWallItem()
        );

        decorationBlock(output, GCItems.COMPRESSED_ALUMINUM,
                GCBlocks.ALUMINUM_DECORATION.item(),
                GCBlocks.ALUMINUM_DECORATION.slabItem(),
                GCBlocks.ALUMINUM_DECORATION.stairsItem(),
                GCBlocks.ALUMINUM_DECORATION.wallItem()
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_ALUMINUM,
                GCBlocks.ALUMINUM_DECORATION.detailedItem(),
                GCBlocks.ALUMINUM_DECORATION.detailedSlabItem(),
                GCBlocks.ALUMINUM_DECORATION.detailedStairsItem(),
                GCBlocks.ALUMINUM_DECORATION.detailedWallItem()
        );

        decorationBlock(output, GCItems.COMPRESSED_STEEL,
                GCBlocks.STEEL_DECORATION.item(),
                GCBlocks.STEEL_DECORATION.slabItem(),
                GCBlocks.STEEL_DECORATION.stairsItem(),
                GCBlocks.STEEL_DECORATION.wallItem()
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_STEEL,
                GCBlocks.STEEL_DECORATION.detailedItem(),
                GCBlocks.STEEL_DECORATION.detailedSlabItem(),
                GCBlocks.STEEL_DECORATION.detailedStairsItem(),
                GCBlocks.STEEL_DECORATION.detailedWallItem()
        );

        decorationBlock(output, GCItems.COMPRESSED_BRONZE,
                GCBlocks.BRONZE_DECORATION.item(),
                GCBlocks.BRONZE_DECORATION.slabItem(),
                GCBlocks.BRONZE_DECORATION.stairsItem(),
                GCBlocks.BRONZE_DECORATION.wallItem()
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_BRONZE,
                GCBlocks.BRONZE_DECORATION.detailedItem(),
                GCBlocks.BRONZE_DECORATION.detailedSlabItem(),
                GCBlocks.BRONZE_DECORATION.detailedStairsItem(),
                GCBlocks.BRONZE_DECORATION.detailedWallItem()
        );

        decorationBlock(output, GCItems.COMPRESSED_METEORIC_IRON,
                GCBlocks.METEORIC_IRON_DECORATION.item(),
                GCBlocks.METEORIC_IRON_DECORATION.slabItem(),
                GCBlocks.METEORIC_IRON_DECORATION.stairsItem(),
                GCBlocks.METEORIC_IRON_DECORATION.wallItem()
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_METEORIC_IRON,
                GCBlocks.METEORIC_IRON_DECORATION.detailedItem(),
                GCBlocks.METEORIC_IRON_DECORATION.detailedSlabItem(),
                GCBlocks.METEORIC_IRON_DECORATION.detailedStairsItem(),
                GCBlocks.METEORIC_IRON_DECORATION.detailedWallItem()
        );

        decorationBlock(output, GCItems.COMPRESSED_TITANIUM,
                GCBlocks.TITANIUM_DECORATION.item(),
                GCBlocks.TITANIUM_DECORATION.slabItem(),
                GCBlocks.TITANIUM_DECORATION.stairsItem(),
                GCBlocks.TITANIUM_DECORATION.wallItem()
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_TITANIUM,
                GCBlocks.TITANIUM_DECORATION.detailedItem(),
                GCBlocks.TITANIUM_DECORATION.detailedSlabItem(),
                GCBlocks.TITANIUM_DECORATION.detailedStairsItem(),
                GCBlocks.TITANIUM_DECORATION.detailedWallItem()
        );

        decorationBlockVariants(output, GCBlocks.DARK_DECORATION.item(),
                GCBlocks.DARK_DECORATION.slabItem(),
                GCBlocks.DARK_DECORATION.stairsItem(),
                GCBlocks.DARK_DECORATION.wallItem()
        );
        decorationBlockVariants(output, GCBlocks.DARK_DECORATION.detailedItem(),
                GCBlocks.DARK_DECORATION.detailedSlabItem(),
                GCBlocks.DARK_DECORATION.detailedStairsItem(),
                GCBlocks.DARK_DECORATION.detailedWallItem()
        );

        // Rock decoration blocks
        decorationBlockVariants(output, GCBlocks.MOON_ROCK,
                GCBlocks.MOON_ROCK_SLAB,
                GCBlocks.MOON_ROCK_STAIRS,
                GCBlocks.MOON_ROCK_WALL
        );
        decorationBlockVariants(output, GCBlocks.MOON_ROCK_BRICK,
                GCBlocks.MOON_ROCK_BRICK_SLAB,
                GCBlocks.MOON_ROCK_BRICK_STAIRS,
                GCBlocks.MOON_ROCK_BRICK_WALL
        );
        decorationBlockVariants(output, GCBlocks.CRACKED_MOON_ROCK_BRICK,
                GCBlocks.CRACKED_MOON_ROCK_BRICK_SLAB,
                GCBlocks.CRACKED_MOON_ROCK_BRICK_STAIRS,
                GCBlocks.CRACKED_MOON_ROCK_BRICK_WALL
        );
        decorationBlockVariants(output, GCBlocks.POLISHED_MOON_ROCK,
                GCBlocks.POLISHED_MOON_ROCK_SLAB,
                GCBlocks.POLISHED_MOON_ROCK_STAIRS,
                GCBlocks.POLISHED_MOON_ROCK_WALL
        );
        smeltBuildingBlock(output, GCBlocks.MOON_ROCK, GCBlocks.COBBLED_MOON_ROCK);
        smeltBuildingBlock(output, GCBlocks.CRACKED_MOON_ROCK_BRICK, GCBlocks.MOON_ROCK_BRICK);
        squareStone(output, GCBlocks.MOON_ROCK_BRICK, GCBlocks.MOON_ROCK);
        squareStone(output, GCBlocks.POLISHED_MOON_ROCK, GCBlocks.MOON_ROCK_BRICK);
        chiseledStone(output, GCBlocks.CHISELED_MOON_ROCK_BRICK, GCBlocks.MOON_ROCK_BRICK_SLAB, GCBlocks.MOON_ROCK, GCBlocks.MOON_ROCK_BRICK);
        pillar(output, GCBlocks.MOON_ROCK_PILLAR, GCBlocks.MOON_ROCK);

        decorationBlockVariants(output, GCBlocks.LUNASLATE,
                GCBlocks.LUNASLATE_SLAB,
                GCBlocks.LUNASLATE_STAIRS,
                GCBlocks.LUNASLATE_WALL
        );
        decorationBlockVariants(output, GCBlocks.COBBLED_LUNASLATE,
                GCBlocks.COBBLED_LUNASLATE_SLAB,
                GCBlocks.COBBLED_LUNASLATE_STAIRS,
                GCBlocks.COBBLED_LUNASLATE_WALL
        );
        smeltBuildingBlock(output, GCBlocks.LUNASLATE, GCBlocks.COBBLED_LUNASLATE);

        decorationBlockVariants(output, GCBlocks.MOON_BASALT,
                GCBlocks.MOON_BASALT_SLAB,
                GCBlocks.MOON_BASALT_STAIRS,
                GCBlocks.MOON_BASALT_WALL
        );
        decorationBlockVariants(output, GCBlocks.MOON_BASALT_BRICK,
                GCBlocks.MOON_BASALT_BRICK_SLAB,
                GCBlocks.MOON_BASALT_BRICK_STAIRS,
                GCBlocks.MOON_BASALT_BRICK_WALL
        );
        decorationBlockVariants(output, GCBlocks.CRACKED_MOON_BASALT_BRICK,
                GCBlocks.CRACKED_MOON_BASALT_BRICK_SLAB,
                GCBlocks.CRACKED_MOON_BASALT_BRICK_STAIRS,
                GCBlocks.CRACKED_MOON_BASALT_BRICK_WALL
        );
        squareStone(output, GCBlocks.MOON_BASALT_BRICK, GCBlocks.MOON_BASALT);
        smeltBuildingBlock(output, GCBlocks.CRACKED_MOON_BASALT_BRICK, GCBlocks.MOON_BASALT_BRICK);

        decorationBlockVariants(output, GCBlocks.MARS_STONE,
                GCBlocks.MARS_STONE_SLAB,
                GCBlocks.MARS_STONE_STAIRS,
                GCBlocks.MARS_STONE_WALL
        );
        decorationBlockVariants(output, GCBlocks.MARS_COBBLESTONE,
                GCBlocks.MARS_COBBLESTONE_SLAB,
                GCBlocks.MARS_COBBLESTONE_STAIRS,
                GCBlocks.MARS_COBBLESTONE_WALL
        );
        smeltBuildingBlock(output, GCBlocks.MARS_STONE, GCBlocks.MARS_COBBLESTONE);
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
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pillar, 2)
                .define('#', base)
                .pattern("#")
                .pattern("#")
                .unlockedBy(getHasName(base), has(base))
                .save(output);

        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, pillar, base);
    }

    public static void squareStone(RecipeOutput output, ItemLike brick, ItemLike base) {
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, brick, base);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, brick, 4)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, family.original().getBaseBlock(), 4)
                .define('#', Items.STONE)
                .define('X', input)
                .pattern("## ")
                .pattern("##X")
                .unlockedBy(getHasName(input), has(input))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, family.detailed().getBaseBlock(), 4)
                .define('#', Items.STONE)
                .define('X', input)
                .pattern("##")
                .pattern("##")
                .pattern(" X")
                .unlockedBy(getHasName(input), has(input))
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
