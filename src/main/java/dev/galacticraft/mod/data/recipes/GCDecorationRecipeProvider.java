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
                GCItems.TIN_DECORATION,
                GCItems.TIN_DECORATION_SLAB,
                GCItems.TIN_DECORATION_STAIRS,
                GCItems.TIN_DECORATION_WALL
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_TIN,
                GCItems.DETAILED_TIN_DECORATION,
                GCItems.DETAILED_TIN_DECORATION_SLAB,
                GCItems.DETAILED_TIN_DECORATION_STAIRS,
                GCItems.DETAILED_TIN_DECORATION_WALL
        );

        decorationBlock(output, GCItems.COMPRESSED_COPPER,
                GCItems.COPPER_DECORATION,
                GCItems.COPPER_DECORATION_SLAB,
                GCItems.COPPER_DECORATION_STAIRS,
                GCItems.COPPER_DECORATION_WALL
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_COPPER,
                GCItems.DETAILED_COPPER_DECORATION,
                GCItems.DETAILED_COPPER_DECORATION_SLAB,
                GCItems.DETAILED_COPPER_DECORATION_STAIRS,
                GCItems.DETAILED_COPPER_DECORATION_WALL
        );

        decorationBlock(output, GCItems.COMPRESSED_IRON,
                GCItems.IRON_DECORATION,
                GCItems.IRON_DECORATION_SLAB,
                GCItems.IRON_DECORATION_STAIRS,
                GCItems.IRON_DECORATION_WALL
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_IRON,
                GCItems.DETAILED_IRON_DECORATION,
                GCItems.DETAILED_IRON_DECORATION_SLAB,
                GCItems.DETAILED_IRON_DECORATION_STAIRS,
                GCItems.DETAILED_IRON_DECORATION_WALL
        );

        decorationBlock(output, GCItems.COMPRESSED_ALUMINUM,
                GCItems.ALUMINUM_DECORATION,
                GCItems.ALUMINUM_DECORATION_SLAB,
                GCItems.ALUMINUM_DECORATION_STAIRS,
                GCItems.ALUMINUM_DECORATION_WALL
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_ALUMINUM,
                GCItems.DETAILED_ALUMINUM_DECORATION,
                GCItems.DETAILED_ALUMINUM_DECORATION_SLAB,
                GCItems.DETAILED_ALUMINUM_DECORATION_STAIRS,
                GCItems.DETAILED_ALUMINUM_DECORATION_WALL
        );

        decorationBlock(output, GCItems.COMPRESSED_STEEL,
                GCItems.STEEL_DECORATION,
                GCItems.STEEL_DECORATION_SLAB,
                GCItems.STEEL_DECORATION_STAIRS,
                GCItems.STEEL_DECORATION_WALL
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_STEEL,
                GCItems.DETAILED_STEEL_DECORATION,
                GCItems.DETAILED_STEEL_DECORATION_SLAB,
                GCItems.DETAILED_STEEL_DECORATION_STAIRS,
                GCItems.DETAILED_STEEL_DECORATION_WALL
        );

        decorationBlock(output, GCItems.COMPRESSED_BRONZE,
                GCItems.BRONZE_DECORATION,
                GCItems.BRONZE_DECORATION_SLAB,
                GCItems.BRONZE_DECORATION_STAIRS,
                GCItems.BRONZE_DECORATION_WALL
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_BRONZE,
                GCItems.DETAILED_BRONZE_DECORATION,
                GCItems.DETAILED_BRONZE_DECORATION_SLAB,
                GCItems.DETAILED_BRONZE_DECORATION_STAIRS,
                GCItems.DETAILED_BRONZE_DECORATION_WALL
        );

        decorationBlock(output, GCItems.COMPRESSED_METEORIC_IRON,
                GCItems.METEORIC_IRON_DECORATION,
                GCItems.METEORIC_IRON_DECORATION_SLAB,
                GCItems.METEORIC_IRON_DECORATION_STAIRS,
                GCItems.METEORIC_IRON_DECORATION_WALL
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_METEORIC_IRON,
                GCItems.DETAILED_METEORIC_IRON_DECORATION,
                GCItems.DETAILED_METEORIC_IRON_DECORATION_SLAB,
                GCItems.DETAILED_METEORIC_IRON_DECORATION_STAIRS,
                GCItems.DETAILED_METEORIC_IRON_DECORATION_WALL
        );

        decorationBlock(output, GCItems.COMPRESSED_TITANIUM,
                GCItems.TITANIUM_DECORATION,
                GCItems.TITANIUM_DECORATION_SLAB,
                GCItems.TITANIUM_DECORATION_STAIRS,
                GCItems.TITANIUM_DECORATION_WALL
        );
        detailedDecorationBlock(output, GCItems.COMPRESSED_TITANIUM,
                GCItems.DETAILED_TITANIUM_DECORATION,
                GCItems.DETAILED_TITANIUM_DECORATION_SLAB,
                GCItems.DETAILED_TITANIUM_DECORATION_STAIRS,
                GCItems.DETAILED_TITANIUM_DECORATION_WALL
        );

        decorationBlockVariants(output, GCItems.DARK_DECORATION,
                GCItems.DARK_DECORATION_SLAB,
                GCItems.DARK_DECORATION_STAIRS,
                GCItems.DARK_DECORATION_WALL
        );
        decorationBlockVariants(output, GCItems.DETAILED_DARK_DECORATION,
                GCItems.DETAILED_DARK_DECORATION_SLAB,
                GCItems.DETAILED_DARK_DECORATION_STAIRS,
                GCItems.DETAILED_DARK_DECORATION_WALL
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
