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
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import static net.minecraft.world.level.levelgen.SurfaceRules.*;

public class ReadableVanillaSurfaceRules {
    private static final RuleSource AIR = block(Blocks.AIR);
    private static final RuleSource BEDROCK = block(Blocks.BEDROCK);
    private static final RuleSource WHITE_TERRACOTTA = block(Blocks.WHITE_TERRACOTTA);
    private static final RuleSource ORANGE_TERRACOTTA = block(Blocks.ORANGE_TERRACOTTA);
    private static final RuleSource TERRACOTTA = block(Blocks.TERRACOTTA);
    private static final RuleSource RED_SAND = block(Blocks.RED_SAND);
    private static final RuleSource RED_SANDSTONE = block(Blocks.RED_SANDSTONE);
    private static final RuleSource STONE = block(Blocks.STONE);
    private static final RuleSource DEEPSLATE = block(Blocks.DEEPSLATE);
    private static final RuleSource DIRT = block(Blocks.DIRT);
    private static final RuleSource PODZOL = block(Blocks.PODZOL);
    private static final RuleSource COARSE_DIRT = block(Blocks.COARSE_DIRT);
    private static final RuleSource MYCELIUM = block(Blocks.MYCELIUM);
    private static final RuleSource GRASS_BLOCK = block(Blocks.GRASS_BLOCK);
    private static final RuleSource CALCITE = block(Blocks.CALCITE);
    private static final RuleSource GRAVEL = block(Blocks.GRAVEL);
    private static final RuleSource SAND = block(Blocks.SAND);
    private static final RuleSource SANDSTONE = block(Blocks.SANDSTONE);
    private static final RuleSource PACKED_ICE = block(Blocks.PACKED_ICE);
    private static final RuleSource SNOW_BLOCK = block(Blocks.SNOW_BLOCK);
    private static final RuleSource POWDER_SNOW = block(Blocks.POWDER_SNOW);
    private static final RuleSource ICE = block(Blocks.ICE);
    private static final RuleSource WATER = block(Blocks.WATER);

    private static RuleSource block(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    public static RuleSource createOverworldSurfaceRule() {
        return createDefaultRule(true, false, true);
    }

    public static RuleSource createDefaultRule(boolean surface, boolean bedrockRoof, boolean bedrockFloor) {
        ConditionSource ABOVE_97 = yBlockCheck(VerticalAnchor.absolute(97), 2);
        ConditionSource ABOVE_256 = yBlockCheck(VerticalAnchor.absolute(256), 0);
        ConditionSource aboveStoneInvert63 = yStartCheck(VerticalAnchor.absolute(63), -1);
        ConditionSource aboveStone74 = yStartCheck(VerticalAnchor.absolute(74), 1);
        ConditionSource ABOVE62 = yBlockCheck(VerticalAnchor.absolute(62), 0);
        ConditionSource ABOUVE_63 = yBlockCheck(VerticalAnchor.absolute(63), 0);
        ConditionSource NO_WATER_UP_ADJ_BEL = waterBlockCheck(-1, 0);
        ConditionSource NO_WATER_ADJ_BEL = waterBlockCheck(0, 0);
        ConditionSource WATER_ANDSTONE6 = waterStartCheck(-6, -1);
        ConditionSource HOLE = hole();
        ConditionSource IS_FROZEN_OCEAN = isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        ConditionSource IS_STEEP = steep();
        ConditionSource IS_WARM_SANDY_PLACE = isBiome(Biomes.WARM_OCEAN, Biomes.DESERT, Biomes.BEACH, Biomes.SNOWY_BEACH);
        ConditionSource LOW_SURFACE = noiseCondition(Noises.SURFACE, -0.909, -0.5454);
        ConditionSource MID_SURFACE = noiseCondition(Noises.SURFACE, -0.1818, 0.1818);
        ConditionSource HIGH_SURFACE = noiseCondition(Noises.SURFACE, 0.5454, 0.909);
        RuleSource SURFACE_BLOCK_IF_NO_WATER = sequence(ifTrue(NO_WATER_UP_ADJ_BEL, GRASS_BLOCK), DIRT);
        RuleSource SANDSTONE_REP_SAND = sequence(ifTrue(ON_CEILING, SANDSTONE), SAND);
        RuleSource STONE_REP_GRAVEL = sequence(ifTrue(ON_CEILING, STONE), GRAVEL);
        RuleSource SPECIAL_SECONDARY = sequence(
                ifTrue(isBiome(Biomes.STONY_PEAKS), sequence(ifTrue(noiseCondition(Noises.CALCITE, -0.0125, 0.0125), CALCITE), STONE)),
                ifTrue(isBiome(Biomes.STONY_SHORE), sequence(ifTrue(noiseCondition(Noises.GRAVEL, -0.05, 0.05), STONE_REP_GRAVEL), STONE)),
                ifTrue(isBiome(Biomes.WINDSWEPT_HILLS), ifTrue(surfaceNoiseThreshold(1.0), STONE)),
                ifTrue(IS_WARM_SANDY_PLACE, SANDSTONE_REP_SAND),
                ifTrue(isBiome(Biomes.DRIPSTONE_CAVES), STONE));
        RuleSource POW_SNOW = ifTrue(noiseCondition(Noises.POWDER_SNOW, 0.45, 0.58), POWDER_SNOW);
        RuleSource MORE_POW_SNOW = ifTrue(noiseCondition(Noises.POWDER_SNOW, 0.35, 0.6), POWDER_SNOW);
        RuleSource SECONDARY_MATERIAL = sequence(
                ifTrue(isBiome(Biomes.FROZEN_PEAKS), sequence(ifTrue(IS_STEEP, PACKED_ICE), ifTrue(noiseCondition(Noises.PACKED_ICE, -0.5, 0.2), PACKED_ICE), ifTrue(noiseCondition(Noises.ICE, -0.0625, 0.025), ICE), SNOW_BLOCK)),
                ifTrue(isBiome(Biomes.SNOWY_SLOPES), sequence(ifTrue(IS_STEEP, STONE), POW_SNOW, SNOW_BLOCK)),
                ifTrue(isBiome(Biomes.JAGGED_PEAKS), STONE),
                ifTrue(isBiome(Biomes.GROVE), sequence(POW_SNOW, DIRT)),
                SPECIAL_SECONDARY,
                ifTrue(isBiome(Biomes.WINDSWEPT_SAVANNA), ifTrue(surfaceNoiseThreshold(1.75), STONE)),
                ifTrue(isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), sequence(ifTrue(surfaceNoiseThreshold(2.0), STONE_REP_GRAVEL), ifTrue(surfaceNoiseThreshold(1.0), STONE), ifTrue(surfaceNoiseThreshold(-1.0), DIRT), STONE_REP_GRAVEL)),
                DIRT);
        RuleSource SURFACE_MATERIAL = sequence(
                ifTrue(isBiome(Biomes.FROZEN_PEAKS), sequence(
                        ifTrue(IS_STEEP, PACKED_ICE),
                        ifTrue(noiseCondition(Noises.PACKED_ICE, 0.0, 0.2), PACKED_ICE),
                        ifTrue(noiseCondition(Noises.ICE, 0.0, 0.025), ICE), SNOW_BLOCK)
                ),
                ifTrue(isBiome(Biomes.SNOWY_SLOPES), sequence(
                        ifTrue(IS_STEEP, STONE),
                        MORE_POW_SNOW,
                        SNOW_BLOCK
                )),
                ifTrue(isBiome(Biomes.JAGGED_PEAKS), sequence(
                        ifTrue(IS_STEEP, STONE),
                        SNOW_BLOCK
                )),
                ifTrue(isBiome(Biomes.GROVE), sequence(
                        MORE_POW_SNOW,
                        SNOW_BLOCK
                )),
                SPECIAL_SECONDARY,
                ifTrue(isBiome(Biomes.WINDSWEPT_SAVANNA), sequence(
                        ifTrue(surfaceNoiseThreshold(1.75), STONE),
                        ifTrue(surfaceNoiseThreshold(-0.5), COARSE_DIRT))),
                ifTrue(isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), sequence(
                        ifTrue(surfaceNoiseThreshold(2.0), STONE_REP_GRAVEL),
                        ifTrue(surfaceNoiseThreshold(1.0), STONE),
                        ifTrue(surfaceNoiseThreshold(-1.0), SURFACE_BLOCK_IF_NO_WATER),
                        STONE_REP_GRAVEL
                )),
                ifTrue(isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA), sequence(ifTrue(surfaceNoiseThreshold(1.75), COARSE_DIRT), ifTrue(surfaceNoiseThreshold(-0.95), PODZOL))),
                ifTrue(isBiome(Biomes.ICE_SPIKES), SNOW_BLOCK),
                ifTrue(isBiome(Biomes.MUSHROOM_FIELDS), MYCELIUM),
                SURFACE_BLOCK_IF_NO_WATER);
        RuleSource SURFACE_GENERATION = sequence(
                ifTrue(ON_FLOOR, sequence(
                        ifTrue(isBiome(Biomes.WOODED_BADLANDS), ifTrue(ABOVE_97, sequence(
                                ifTrue(LOW_SURFACE, COARSE_DIRT),
                                ifTrue(MID_SURFACE, COARSE_DIRT),
                                ifTrue(HIGH_SURFACE, COARSE_DIRT),
                                SURFACE_BLOCK_IF_NO_WATER))),
                        ifTrue(isBiome(Biomes.SWAMP), ifTrue(ABOVE62, ifTrue(not(ABOUVE_63), ifTrue(noiseCondition(Noises.SWAMP, 0.0), WATER)))))),
                ifTrue(isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS), sequence(
                        ifTrue(ON_FLOOR, sequence(
                                ifTrue(ABOVE_256, ORANGE_TERRACOTTA),
                                ifTrue(aboveStone74, sequence(
                                ifTrue(LOW_SURFACE, TERRACOTTA),
                                ifTrue(MID_SURFACE, TERRACOTTA),
                                ifTrue(HIGH_SURFACE, TERRACOTTA),
                                bandlands())),
                                ifTrue(NO_WATER_UP_ADJ_BEL, sequence(
                                        ifTrue(ON_CEILING, RED_SANDSTONE), RED_SAND)
                                ), ifTrue(not(HOLE), ORANGE_TERRACOTTA), ifTrue(WATER_ANDSTONE6, WHITE_TERRACOTTA), STONE_REP_GRAVEL)),
                        ifTrue(aboveStoneInvert63, sequence(
                                ifTrue(ABOUVE_63, ifTrue(not(aboveStone74), ORANGE_TERRACOTTA)),
                                bandlands()
                        )),
                        ifTrue(UNDER_FLOOR, ifTrue(WATER_ANDSTONE6, WHITE_TERRACOTTA)))),
                ifTrue(ON_FLOOR, ifTrue(NO_WATER_UP_ADJ_BEL, sequence(
                        ifTrue(IS_FROZEN_OCEAN, ifTrue(HOLE, sequence(
                                ifTrue(NO_WATER_ADJ_BEL, AIR),
                                ifTrue(temperature(), ICE), WATER
                        ))),
                        SURFACE_MATERIAL))),
                ifTrue(WATER_ANDSTONE6, sequence(
                        ifTrue(ON_FLOOR, ifTrue(IS_FROZEN_OCEAN, ifTrue(HOLE, WATER))),
                        ifTrue(UNDER_FLOOR, SECONDARY_MATERIAL),
                        ifTrue(IS_WARM_SANDY_PLACE, ifTrue(stoneDepthCheck(0, true, 0, CaveSurface.FLOOR), SANDSTONE)))),
                ifTrue(ON_FLOOR, sequence(
                        ifTrue(isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE),
                        ifTrue(isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), SANDSTONE_REP_SAND),
                        STONE_REP_GRAVEL)));
        Builder<RuleSource> builder = ImmutableList.builder();
        if (bedrockRoof) {
            builder.add(ifTrue(not(verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK));
        }

        if (bedrockFloor) {
            builder.add(ifTrue(verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
        }

        RuleSource materialRule10 = ifTrue(abovePreliminarySurface(), SURFACE_GENERATION);
        builder.add(surface ? materialRule10 : SURFACE_GENERATION);
        builder.add(ifTrue(verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));
        return sequence(builder.build().toArray(RuleSource[]::new));
    }

    private static ConditionSource surfaceNoiseThreshold(double min) {
        return noiseCondition(Noises.SURFACE, min / 8.25, Double.MAX_VALUE);
    }
}
