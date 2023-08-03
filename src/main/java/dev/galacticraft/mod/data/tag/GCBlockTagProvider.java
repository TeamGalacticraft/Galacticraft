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

package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class GCBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public GCBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.getOrCreateTagBuilder(GCTags.INFINIBURN_MOON)
                .add(GCBlocks.MOON_BASALT_BRICK);
        this.getOrCreateTagBuilder(GCTags.BASE_STONE_MOON)
                .add(GCBlocks.MOON_ROCK);
        this.getOrCreateTagBuilder(GCTags.MOON_CARVER_REPLACEABLES)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.MOON_SURFACE_ROCK)
                .add(GCBlocks.MOON_BASALT)
                .add(GCBlocks.MOON_DIRT)
                .add(GCBlocks.MOON_TURF);
        this.getOrCreateTagBuilder(GCTags.MOON_CRATER_CARVER_REPLACEABLES)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.MOON_SURFACE_ROCK)
                .add(GCBlocks.MOON_BASALT)
                .add(GCBlocks.MOON_DIRT)
                .add(GCBlocks.MOON_TURF);
        this.getOrCreateTagBuilder(GCTags.MOON_STONE_ORE_REPLACABLES)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.MOON_BASALT);
        this.getOrCreateTagBuilder(GCTags.LUNASLATE_ORE_REPLACABLES)
                .add(GCBlocks.LUNASLATE);

        this.getOrCreateTagBuilder(BlockTags.CLIMBABLE)
                .add(GCBlocks.TIN_LADDER);

        this.getOrCreateTagBuilder(BlockTags.WALLS)
                .add(GCBlocks.TIN_DECORATION_WALL)
                .add(GCBlocks.COPPER_DECORATION_WALL)
                .add(GCBlocks.BRONZE_DECORATION_WALL)
                .add(GCBlocks.STEEL_DECORATION_WALL)
                .add(GCBlocks.TITANIUM_DECORATION_WALL)
                .add(GCBlocks.IRON_DECORATION_WALL)
                .add(GCBlocks.ALUMINUM_DECORATION_WALL)
                .add(GCBlocks.METEORIC_IRON_DECORATION_WALL)

                .add(GCBlocks.DETAILED_TIN_DECORATION_WALL)
                .add(GCBlocks.DETAILED_COPPER_DECORATION_WALL)
                .add(GCBlocks.DETAILED_BRONZE_DECORATION_WALL)
                .add(GCBlocks.DETAILED_STEEL_DECORATION_WALL)
                .add(GCBlocks.DETAILED_TITANIUM_DECORATION_WALL)
                .add(GCBlocks.DETAILED_IRON_DECORATION_WALL)
                .add(GCBlocks.DETAILED_ALUMINUM_DECORATION_WALL)
                .add(GCBlocks.DETAILED_METEORIC_IRON_DECORATION_WALL)

                .add(GCBlocks.MOON_ROCK_WALL)
                .add(GCBlocks.COBBLED_MOON_ROCK_WALL)
                .add(GCBlocks.MOON_BASALT_WALL)
                .add(GCBlocks.MOON_BASALT_BRICK_WALL);

        // ORE MINING TAGS
        var ores = new Block[] {
                GCBlocks.SILICON_ORE, GCBlocks.DEEPSLATE_SILICON_ORE,
                GCBlocks.MOON_COPPER_ORE, GCBlocks.LUNASLATE_COPPER_ORE,
                GCBlocks.TIN_ORE, GCBlocks.DEEPSLATE_TIN_ORE, GCBlocks.MOON_TIN_ORE, GCBlocks.LUNASLATE_TIN_ORE,
                GCBlocks.ALUMINUM_ORE, GCBlocks.DEEPSLATE_ALUMINUM_ORE,
                GCBlocks.DESH_ORE,
                GCBlocks.ILMENITE_ORE,
                GCBlocks.GALENA_ORE
        };
        this.getOrCreateTagBuilder(ConventionalBlockTags.ORES).add(ores);
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(ores);
        this.getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(
                GCBlocks.MOON_COPPER_ORE, GCBlocks.LUNASLATE_COPPER_ORE,
                GCBlocks.TIN_ORE, GCBlocks.DEEPSLATE_TIN_ORE, GCBlocks.MOON_TIN_ORE, GCBlocks.LUNASLATE_TIN_ORE,
                GCBlocks.ALUMINUM_ORE, GCBlocks.DEEPSLATE_ALUMINUM_ORE,
                GCBlocks.GALENA_ORE);
        this.getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(
                GCBlocks.SILICON_ORE, GCBlocks.DEEPSLATE_SILICON_ORE);
        this.getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(
                GCBlocks.DESH_ORE, GCBlocks.ILMENITE_ORE);

        // Cheese Candle Tags
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        GCBlocks.UNLIT_LANTERN);

        this.getOrCreateTagBuilder(BlockTags.WALL_POST_OVERRIDE)
                .add(
                        GCBlocks.GLOWSTONE_TORCH,
                        GCBlocks.UNLIT_TORCH);

        this.getOrCreateTagBuilder(BlockTags.CANDLE_CAKES)
                .add(
                        GCBlocks.CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.WHITE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.ORANGE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.MAGENTA_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.LIGHT_BLUE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.YELLOW_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.LIME_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.PINK_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.GRAY_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.LIGHT_GRAY_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.CYAN_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.PURPLE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.BLUE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.BROWN_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.GREEN_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.RED_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.BLACK_CANDLE_MOON_CHEESE_BLOCK
                );
    }
}