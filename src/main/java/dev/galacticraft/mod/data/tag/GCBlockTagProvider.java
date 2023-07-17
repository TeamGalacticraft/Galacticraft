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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class GCBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public GCBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(GCTags.INFINIBURN_MOON)
                .add(this.reverseLookup(GCBlocks.MOON_BASALT_BRICK));
        this.tag(GCTags.BASE_STONE_MOON)
                .add(this.reverseLookup(GCBlocks.MOON_ROCK));
        this.tag(GCTags.MOON_CARVER_REPLACEABLES)
                .add(this.reverseLookup(GCBlocks.MOON_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_SURFACE_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT))
                .add(this.reverseLookup(GCBlocks.MOON_DIRT))
                .add(this.reverseLookup(GCBlocks.MOON_TURF));
        this.tag(GCTags.MOON_CRATER_CARVER_REPLACEABLES)
                .add(this.reverseLookup(GCBlocks.MOON_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_SURFACE_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT))
                .add(this.reverseLookup(GCBlocks.MOON_DIRT))
                .add(this.reverseLookup(GCBlocks.MOON_TURF));
        this.tag(GCTags.MOON_STONE_ORE_REPLACABLES)
                .add(this.reverseLookup(GCBlocks.MOON_ROCK))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT));
        this.tag(GCTags.LUNASLATE_ORE_REPLACABLES)
                .add(this.reverseLookup(GCBlocks.LUNASLATE));

        this.tag(BlockTags.CLIMBABLE)
                .add(this.reverseLookup(GCBlocks.TIN_LADDER));

        this.tag(BlockTags.WALLS)
                .add(this.reverseLookup(GCBlocks.TIN_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.COPPER_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.BRONZE_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.STEEL_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.TITANIUM_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.IRON_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.ALUMINUM_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.METEORIC_IRON_DECORATION_WALL))

                .add(this.reverseLookup(GCBlocks.DETAILED_TIN_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_COPPER_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_BRONZE_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_STEEL_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_TITANIUM_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_IRON_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_ALUMINUM_DECORATION_WALL))
                .add(this.reverseLookup(GCBlocks.DETAILED_METEORIC_IRON_DECORATION_WALL))

                .add(this.reverseLookup(GCBlocks.MOON_ROCK_WALL))
                .add(this.reverseLookup(GCBlocks.COBBLED_MOON_ROCK_WALL))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT_WALL))
                .add(this.reverseLookup(GCBlocks.MOON_BASALT_BRICK_WALL));

        // MINING TAGS
        this.tag(ConventionalBlockTags.ORES)
                .add(this.reverseLookup(GCBlocks.SILICON_ORE))
                .add(this.reverseLookup(GCBlocks.DEEPSLATE_SILICON_ORE))

                .add(this.reverseLookup(GCBlocks.MOON_COPPER_ORE))
                .add(this.reverseLookup(GCBlocks.LUNASLATE_COPPER_ORE))

                .add(this.reverseLookup(GCBlocks.TIN_ORE))
                .add(this.reverseLookup(GCBlocks.DEEPSLATE_TIN_ORE))
                .add(this.reverseLookup(GCBlocks.MOON_TIN_ORE))
                .add(this.reverseLookup(GCBlocks.LUNASLATE_TIN_ORE))

                .add(this.reverseLookup(GCBlocks.ALUMINUM_ORE))
                .add(this.reverseLookup(GCBlocks.DEEPSLATE_ALUMINUM_ORE))

                .add(this.reverseLookup(GCBlocks.DESH_ORE))

                .add(this.reverseLookup(GCBlocks.ILMENITE_ORE))

                .add(this.reverseLookup(GCBlocks.GALENA_ORE));
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(this.reverseLookup(GCBlocks.SILICON_ORE))
                .add(this.reverseLookup(GCBlocks.DEEPSLATE_SILICON_ORE))

                .add(this.reverseLookup(GCBlocks.MOON_COPPER_ORE))
                .add(this.reverseLookup(GCBlocks.LUNASLATE_COPPER_ORE))

                .add(this.reverseLookup(GCBlocks.TIN_ORE))
                .add(this.reverseLookup(GCBlocks.DEEPSLATE_TIN_ORE))
                .add(this.reverseLookup(GCBlocks.MOON_TIN_ORE))
                .add(this.reverseLookup(GCBlocks.LUNASLATE_TIN_ORE))

                .add(this.reverseLookup(GCBlocks.ALUMINUM_ORE))
                .add(this.reverseLookup(GCBlocks.DEEPSLATE_ALUMINUM_ORE))

                .add(this.reverseLookup(GCBlocks.DESH_ORE))

                .add(this.reverseLookup(GCBlocks.ILMENITE_ORE))

                .add(this.reverseLookup(GCBlocks.GALENA_ORE));
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(this.reverseLookup(GCBlocks.MOON_COPPER_ORE))
                .add(this.reverseLookup(GCBlocks.LUNASLATE_COPPER_ORE))

                .add(this.reverseLookup(GCBlocks.TIN_ORE))
                .add(this.reverseLookup(GCBlocks.DEEPSLATE_TIN_ORE))
                .add(this.reverseLookup(GCBlocks.MOON_TIN_ORE))
                .add(this.reverseLookup(GCBlocks.LUNASLATE_TIN_ORE))

                .add(this.reverseLookup(GCBlocks.ALUMINUM_ORE))
                .add(this.reverseLookup(GCBlocks.DEEPSLATE_ALUMINUM_ORE))

                .add(this.reverseLookup(GCBlocks.GALENA_ORE));
        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(this.reverseLookup(GCBlocks.SILICON_ORE))
                .add(this.reverseLookup(GCBlocks.DEEPSLATE_SILICON_ORE));
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(this.reverseLookup(GCBlocks.DESH_ORE))
                .add(this.reverseLookup(GCBlocks.ILMENITE_ORE));

        // Cheese Candle Tags
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
