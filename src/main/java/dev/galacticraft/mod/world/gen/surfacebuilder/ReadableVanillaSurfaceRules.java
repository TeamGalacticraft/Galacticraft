/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

import static net.minecraft.world.gen.surfacebuilder.MaterialRules.*;

public class ReadableVanillaSurfaceRules {
    private static final MaterialRule AIR = block(Blocks.AIR);
    private static final MaterialRule BEDROCK = block(Blocks.BEDROCK);
    private static final MaterialRule WHITE_TERRACOTTA = block(Blocks.WHITE_TERRACOTTA);
    private static final MaterialRule ORANGE_TERRACOTTA = block(Blocks.ORANGE_TERRACOTTA);
    private static final MaterialRule TERRACOTTA = block(Blocks.TERRACOTTA);
    private static final MaterialRule RED_SAND = block(Blocks.RED_SAND);
    private static final MaterialRule RED_SANDSTONE = block(Blocks.RED_SANDSTONE);
    private static final MaterialRule STONE = block(Blocks.STONE);
    private static final MaterialRule DEEPSLATE = block(Blocks.DEEPSLATE);
    private static final MaterialRule DIRT = block(Blocks.DIRT);
    private static final MaterialRule PODZOL = block(Blocks.PODZOL);
    private static final MaterialRule COARSE_DIRT = block(Blocks.COARSE_DIRT);
    private static final MaterialRule MYCELIUM = block(Blocks.MYCELIUM);
    private static final MaterialRule GRASS_BLOCK = block(Blocks.GRASS_BLOCK);
    private static final MaterialRule CALCITE = block(Blocks.CALCITE);
    private static final MaterialRule GRAVEL = block(Blocks.GRAVEL);
    private static final MaterialRule SAND = block(Blocks.SAND);
    private static final MaterialRule SANDSTONE = block(Blocks.SANDSTONE);
    private static final MaterialRule PACKED_ICE = block(Blocks.PACKED_ICE);
    private static final MaterialRule SNOW_BLOCK = block(Blocks.SNOW_BLOCK);
    private static final MaterialRule POWDER_SNOW = block(Blocks.POWDER_SNOW);
    private static final MaterialRule ICE = block(Blocks.ICE);
    private static final MaterialRule WATER = block(Blocks.WATER);

    private static MaterialRule block(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }

    public static MaterialRule createOverworldSurfaceRule() {
        return createDefaultRule(true, false, true);
    }

    public static MaterialRule createDefaultRule(boolean surface, boolean bedrockRoof, boolean bedrockFloor) {
        MaterialCondition ABOVE_97 = aboveY(YOffset.fixed(97), 2);
        MaterialCondition ABOVE_256 = aboveY(YOffset.fixed(256), 0);
        MaterialCondition aboveStoneInvert63 = aboveYWithStoneDepth(YOffset.fixed(63), -1);
        MaterialCondition aboveStone74 = aboveYWithStoneDepth(YOffset.fixed(74), 1);
        MaterialCondition ABOVE62 = aboveY(YOffset.fixed(62), 0);
        MaterialCondition ABOUVE_63 = aboveY(YOffset.fixed(63), 0);
        MaterialCondition NO_WATER_UP_ADJ_BEL = water(-1, 0);
        MaterialCondition NO_WATER_ADJ_BEL = water(0, 0);
        MaterialCondition WATER_ANDSTONE6 = waterWithStoneDepth(-6, -1);
        MaterialCondition HOLE = hole();
        MaterialCondition IS_FROZEN_OCEAN = biome(BiomeKeys.FROZEN_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN);
        MaterialCondition IS_STEEP = steepSlope();
        MaterialCondition IS_WARM_SANDY_PLACE = biome(BiomeKeys.WARM_OCEAN, BiomeKeys.DESERT, BiomeKeys.BEACH, BiomeKeys.SNOWY_BEACH);
        MaterialCondition LOW_SURFACE = noiseThreshold(NoiseParametersKeys.SURFACE, -0.909, -0.5454);
        MaterialCondition MID_SURFACE = noiseThreshold(NoiseParametersKeys.SURFACE, -0.1818, 0.1818);
        MaterialCondition HIGH_SURFACE = noiseThreshold(NoiseParametersKeys.SURFACE, 0.5454, 0.909);
        MaterialRule SURFACE_BLOCK_IF_NO_WATER = sequence(condition(NO_WATER_UP_ADJ_BEL, GRASS_BLOCK), DIRT);
        MaterialRule SANDSTONE_REP_SAND = sequence(condition(STONE_DEPTH_CEILING, SANDSTONE), SAND);
        MaterialRule STONE_REP_GRAVEL = sequence(condition(STONE_DEPTH_CEILING, STONE), GRAVEL);
        MaterialRule SPECIAL_SECONDARY = sequence(
                condition(biome(BiomeKeys.STONY_PEAKS), sequence(condition(noiseThreshold(NoiseParametersKeys.CALCITE, -0.0125, 0.0125), CALCITE), STONE)),
                condition(biome(BiomeKeys.STONY_SHORE), sequence(condition(noiseThreshold(NoiseParametersKeys.GRAVEL, -0.05, 0.05), STONE_REP_GRAVEL), STONE)),
                condition(biome(BiomeKeys.WINDSWEPT_HILLS), condition(surfaceNoiseThreshold(1.0), STONE)),
                condition(IS_WARM_SANDY_PLACE, SANDSTONE_REP_SAND),
                condition(biome(BiomeKeys.DRIPSTONE_CAVES), STONE));
        MaterialRule POW_SNOW = condition(noiseThreshold(NoiseParametersKeys.POWDER_SNOW, 0.45, 0.58), POWDER_SNOW);
        MaterialRule MORE_POW_SNOW = condition(noiseThreshold(NoiseParametersKeys.POWDER_SNOW, 0.35, 0.6), POWDER_SNOW);
        MaterialRule SECONDARY_MATERIAL = sequence(
                condition(biome(BiomeKeys.FROZEN_PEAKS), sequence(condition(IS_STEEP, PACKED_ICE), condition(noiseThreshold(NoiseParametersKeys.PACKED_ICE, -0.5, 0.2), PACKED_ICE), condition(noiseThreshold(NoiseParametersKeys.ICE, -0.0625, 0.025), ICE), SNOW_BLOCK)),
                condition(biome(BiomeKeys.SNOWY_SLOPES), sequence(condition(IS_STEEP, STONE), POW_SNOW, SNOW_BLOCK)),
                condition(biome(BiomeKeys.JAGGED_PEAKS), STONE),
                condition(biome(BiomeKeys.GROVE), sequence(POW_SNOW, DIRT)),
                SPECIAL_SECONDARY,
                condition(biome(BiomeKeys.WINDSWEPT_SAVANNA), condition(surfaceNoiseThreshold(1.75), STONE)),
                condition(biome(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS), sequence(condition(surfaceNoiseThreshold(2.0), STONE_REP_GRAVEL), condition(surfaceNoiseThreshold(1.0), STONE), condition(surfaceNoiseThreshold(-1.0), DIRT), STONE_REP_GRAVEL)),
                DIRT);
        MaterialRule SURFACE_MATERIAL = sequence(
                condition(biome(BiomeKeys.FROZEN_PEAKS), sequence(
                        condition(IS_STEEP, PACKED_ICE),
                        condition(noiseThreshold(NoiseParametersKeys.PACKED_ICE, 0.0, 0.2), PACKED_ICE),
                        condition(noiseThreshold(NoiseParametersKeys.ICE, 0.0, 0.025), ICE), SNOW_BLOCK)
                ),
                condition(biome(BiomeKeys.SNOWY_SLOPES), sequence(
                        condition(IS_STEEP, STONE),
                        MORE_POW_SNOW,
                        SNOW_BLOCK
                )),
                condition(biome(BiomeKeys.JAGGED_PEAKS), sequence(
                        condition(IS_STEEP, STONE),
                        SNOW_BLOCK
                )),
                condition(biome(BiomeKeys.GROVE), sequence(
                        MORE_POW_SNOW,
                        SNOW_BLOCK
                )),
                SPECIAL_SECONDARY,
                condition(biome(BiomeKeys.WINDSWEPT_SAVANNA), sequence(
                        condition(surfaceNoiseThreshold(1.75), STONE),
                        condition(surfaceNoiseThreshold(-0.5), COARSE_DIRT))),
                condition(biome(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS), sequence(
                        condition(surfaceNoiseThreshold(2.0), STONE_REP_GRAVEL),
                        condition(surfaceNoiseThreshold(1.0), STONE),
                        condition(surfaceNoiseThreshold(-1.0), SURFACE_BLOCK_IF_NO_WATER),
                        STONE_REP_GRAVEL
                )),
                condition(biome(BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA), sequence(condition(surfaceNoiseThreshold(1.75), COARSE_DIRT), condition(surfaceNoiseThreshold(-0.95), PODZOL))),
                condition(biome(BiomeKeys.ICE_SPIKES), SNOW_BLOCK),
                condition(biome(BiomeKeys.MUSHROOM_FIELDS), MYCELIUM),
                SURFACE_BLOCK_IF_NO_WATER);
        MaterialRule SURFACE_GENERATION = sequence(
                condition(STONE_DEPTH_FLOOR, sequence(
                        condition(biome(BiomeKeys.WOODED_BADLANDS), condition(ABOVE_97, sequence(
                                condition(LOW_SURFACE, COARSE_DIRT),
                                condition(MID_SURFACE, COARSE_DIRT),
                                condition(HIGH_SURFACE, COARSE_DIRT),
                                SURFACE_BLOCK_IF_NO_WATER))),
                        condition(biome(BiomeKeys.SWAMP), condition(ABOVE62, condition(not(ABOUVE_63), condition(noiseThreshold(NoiseParametersKeys.SURFACE_SWAMP, 0.0), WATER)))))),
                condition(biome(BiomeKeys.BADLANDS, BiomeKeys.ERODED_BADLANDS, BiomeKeys.WOODED_BADLANDS), sequence(
                        condition(STONE_DEPTH_FLOOR, sequence(
                                condition(ABOVE_256, ORANGE_TERRACOTTA),
                                condition(aboveStone74, sequence(
                                condition(LOW_SURFACE, TERRACOTTA),
                                condition(MID_SURFACE, TERRACOTTA),
                                condition(HIGH_SURFACE, TERRACOTTA),
                                terracottaBands())),
                                condition(NO_WATER_UP_ADJ_BEL, sequence(
                                        condition(STONE_DEPTH_CEILING, RED_SANDSTONE), RED_SAND)
                                ), condition(not(HOLE), ORANGE_TERRACOTTA), condition(WATER_ANDSTONE6, WHITE_TERRACOTTA), STONE_REP_GRAVEL)),
                        condition(aboveStoneInvert63, sequence(
                                condition(ABOUVE_63, condition(not(aboveStone74), ORANGE_TERRACOTTA)),
                                terracottaBands()
                        )),
                        condition(STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH, condition(WATER_ANDSTONE6, WHITE_TERRACOTTA)))),
                condition(STONE_DEPTH_FLOOR, condition(NO_WATER_UP_ADJ_BEL, sequence(
                        condition(IS_FROZEN_OCEAN, condition(HOLE, sequence(
                                condition(NO_WATER_ADJ_BEL, AIR),
                                condition(temperature(), ICE), WATER
                        ))),
                        SURFACE_MATERIAL))),
                condition(WATER_ANDSTONE6, sequence(
                        condition(STONE_DEPTH_FLOOR, condition(IS_FROZEN_OCEAN, condition(HOLE, WATER))),
                        condition(STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH, SECONDARY_MATERIAL),
                        condition(IS_WARM_SANDY_PLACE, condition(stoneDepth(0, true, 0, VerticalSurfaceType.FLOOR), SANDSTONE)))),
                condition(STONE_DEPTH_FLOOR, sequence(
                        condition(biome(BiomeKeys.FROZEN_PEAKS, BiomeKeys.JAGGED_PEAKS), STONE),
                        condition(biome(BiomeKeys.WARM_OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN), SANDSTONE_REP_SAND),
                        STONE_REP_GRAVEL)));
        Builder<MaterialRule> builder = ImmutableList.builder();
        if (bedrockRoof) {
            builder.add(condition(not(verticalGradient("bedrock_roof", YOffset.belowTop(5), YOffset.getTop())), BEDROCK));
        }

        if (bedrockFloor) {
            builder.add(condition(verticalGradient("bedrock_floor", YOffset.getBottom(), YOffset.aboveBottom(5)), BEDROCK));
        }

        MaterialRule materialRule10 = condition(surface(), SURFACE_GENERATION);
        builder.add(surface ? materialRule10 : SURFACE_GENERATION);
        builder.add(condition(verticalGradient("deepslate", YOffset.fixed(0), YOffset.fixed(8)), DEEPSLATE));
        return sequence(builder.build().toArray(MaterialRule[]::new));
    }

    private static MaterialCondition surfaceNoiseThreshold(double min) {
        return noiseThreshold(NoiseParametersKeys.SURFACE, min / 8.25, Double.MAX_VALUE);
    }
}
