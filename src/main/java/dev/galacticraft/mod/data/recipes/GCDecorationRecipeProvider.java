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

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * Decoration block recipes
 */
public class GCDecorationRecipeProvider extends FabricRecipeProvider {
    public GCDecorationRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.GLOWSTONE_TORCH, 4)
                .define('G', Items.GLOWSTONE_DUST)
                .define('S', Items.STICK)
                .pattern("G")
                .pattern("S")
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.GLOWSTONE_LANTERN)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('I', Items.IRON_NUGGET)
                .pattern("III")
                .pattern("IGI")
                .pattern("III")
                .unlockedBy(getHasName(GCItems.GLOWSTONE_TORCH), has(GCItems.GLOWSTONE_TORCH))
                .save(output);

        // Vacuum glass
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.VACUUM_GLASS)
                .define('G', Items.GLASS)
                .define('T', GCItems.TIN_INGOT)
                .pattern("TGT")
                .pattern("GGG")
                .pattern("TGT")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItems.TIN_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.CLEAR_VACUUM_GLASS)
                .define('G', Items.GLASS)
                .define('A', GCItems.ALUMINUM_INGOT)
                .pattern("AGA")
                .pattern("GGG")
                .pattern("AGA")
                .unlockedBy(getHasName(GCItems.ALUMINUM_INGOT), has(GCItems.ALUMINUM_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.STRONG_VACUUM_GLASS)
                .define('G', Items.GLASS)
                .define('A', GCItems.COMPRESSED_ALUMINUM)
                .pattern("AGA")
                .pattern("GGG")
                .pattern("AGA")
                .unlockedBy(getHasName(GCItems.COMPRESSED_ALUMINUM), has(GCItems.COMPRESSED_ALUMINUM))
                .save(output);

        // Light panels
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.DASHED_LIGHT_PANEL)
                .define('S', Items.GLASS_PANE)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('T', GCItems.COMPRESSED_STEEL)
                .pattern("SGS")
                .pattern(" T ")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.DIAGONAL_LIGHT_PANEL)
                .define('S', Items.GLASS_PANE)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('T', GCItems.COMPRESSED_STEEL)
                .pattern(" S")
                .pattern("SG")
                .pattern(" T")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.DIAGONAL_LIGHT_PANEL)
                .define('S', Items.GLASS_PANE)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('T', GCItems.COMPRESSED_STEEL)
                .pattern(" S")
                .pattern("SG")
                .pattern(" T")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output, BuiltInRegistries.ITEM.getKey(GCItems.DIAGONAL_LIGHT_PANEL).withSuffix("_flipped"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.SPOTLIGHT_LIGHT_PANEL)
                .define('S', Items.GLASS_PANE)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('T', GCItems.COMPRESSED_STEEL)
                .pattern("S S")
                .pattern(" G ")
                .pattern("STS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.SQUARE_LIGHT_PANEL)
                .define('S', Items.GLASS_PANE)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('T', GCItems.COMPRESSED_STEEL)
                .pattern("SSS")
                .pattern("SGS")
                .pattern("STS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, GCItems.LINEAR_LIGHT_PANEL)
                .define('S', Items.GLASS_PANE)
                .define('G', GCItems.GLOWSTONE_TORCH)
                .define('T', GCItems.COMPRESSED_STEEL)
                .pattern("S S")
                .pattern("SGS")
                .pattern("STS")
                .unlockedBy(getHasName(GCItems.COMPRESSED_STEEL), has(GCItems.COMPRESSED_STEEL))
                .save(output);

        // Misc decoration blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, GCItems.GRATING, 4)
                .define('I', Items.IRON_BARS)
                .pattern("II")
                .pattern("II")
                .unlockedBy(getHasName(Items.IRON_BARS), has(Items.IRON_BARS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, GCItems.TIN_LADDER, 6)
                .define('T', GCItems.TIN_INGOT)
                .pattern("T T")
                .pattern("TTT")
                .pattern("T T")
                .unlockedBy(getHasName(GCItems.TIN_INGOT), has(GCItems.TIN_INGOT))
                .save(output);

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
        decorationBlockVariants(output, GCItems.LUNASLATE,
                GCItems.LUNASLATE_SLAB,
                GCItems.LUNASLATE_STAIRS,
                GCItems.LUNASLATE_WALL
        );
        decorationBlockVariants(output, GCItems.COBBLED_LUNASLATE,
                GCItems.COBBLED_LUNASLATE_SLAB,
                GCItems.COBBLED_LUNASLATE_STAIRS,
                GCItems.COBBLED_LUNASLATE_WALL
        );
        decorationBlockVariants(output, GCItems.MOON_BASALT,
                GCItems.MOON_BASALT_SLAB,
                GCItems.MOON_BASALT_STAIRS,
                GCItems.MOON_BASALT_WALL
        );

        decorationBlockVariants(output, GCItems.MOON_BASALT_BRICK,
                GCItems.MOON_BASALT_BRICK_SLAB,
                GCItems.MOON_BASALT_BRICK_STAIRS,
                GCItems.MOON_BASALT_BRICK_WALL
        );
        decorationBlockVariants(output, GCItems.CRACKED_MOON_BASALT_BRICK,
                GCItems.CRACKED_MOON_BASALT_BRICK_SLAB,
                GCItems.CRACKED_MOON_BASALT_BRICK_STAIRS,
                GCItems.CRACKED_MOON_BASALT_BRICK_WALL
        );

        decorationBlockVariants(output, GCItems.MARS_STONE,
                GCItems.MARS_STONE_SLAB,
                GCItems.MARS_STONE_STAIRS,
                GCItems.MARS_STONE_WALL
        );
        decorationBlockVariants(output, GCItems.MARS_COBBLESTONE,
                GCItems.MARS_COBBLESTONE_SLAB,
                GCItems.MARS_COBBLESTONE_STAIRS,
                GCItems.MARS_COBBLESTONE_WALL
        );
    }

    private static void decorationBlockVariants(RecipeOutput output, ItemLike base, ItemLike slab, ItemLike stairs, ItemLike wall) {
        slab(output, RecipeCategory.BUILDING_BLOCKS, slab, base);
        stairs(output, stairs, base);
        wall(output, RecipeCategory.BUILDING_BLOCKS, wall, base);
    }

    public static void decorationBlock(RecipeOutput output, ItemLike input, ItemLike block, ItemLike slab, ItemLike stairs, ItemLike wall) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 1)
                .define('#', Items.STONE)
                .define('X', input)
                .pattern("## ")
                .pattern("##X")
                .unlockedBy(getHasName(input), has(input))
                .save(output);

        decorationBlockVariants(output, block, slab, stairs, wall);
    }

    public static void detailedDecorationBlock(RecipeOutput output, ItemLike input, ItemLike block, ItemLike slab, ItemLike stairs, ItemLike wall) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 1)
                .define('#', Items.STONE)
                .define('X', input)
                .pattern("##")
                .pattern("##")
                .pattern(" X")
                .unlockedBy(getHasName(input), has(input))
                .save(output);

        decorationBlockVariants(output, block, slab, stairs, wall);
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
