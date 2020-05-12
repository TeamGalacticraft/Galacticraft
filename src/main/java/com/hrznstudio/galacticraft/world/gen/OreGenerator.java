/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.world.gen;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.feature.GCOreGenConfig;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OreGenerator {

    private static final GCOre GC_ORE = Registry.register(Registry.FEATURE, new Identifier("gc_ore_gen"), new GCOre(GCOreGenConfig::deserialize));

    public static void registerOverworldOres() {
        for (Biome biome : Biome.BIOMES) {
            if (!biome.getCategory().equals(Biomes.NETHER_WASTES.getCategory()) && !biome.getCategory().equals(Biomes.THE_END.getCategory())) {

                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, new ConfiguredFeature<>((OreFeature) Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.ALUMINUM_ORE.getDefaultState(), 8)));
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, new ConfiguredFeature<>((OreFeature) Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.COPPER_ORE.getDefaultState(), 8)));
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, new ConfiguredFeature<>((OreFeature) Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.TIN_ORE.getDefaultState(), 8)));
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, new ConfiguredFeature<>((OreFeature) Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.SILICON_ORE.getDefaultState(), 4)));
            }
        }
    }

    public static void registerMoonOres() {
//        GalacticraftBiomes.MOON.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, new ConfiguredFeature<>(GC_ORE, new GCOreGenConfig(GalacticraftBlocks.MOON_ROCK.getDefaultState(), GalacticraftBlocks.MOON_TIN_ORE.getDefaultState(), 8)));
//        GalacticraftBiomes.MOON.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, new ConfiguredFeature<>(GC_ORE, new GCOreGenConfig(GalacticraftBlocks.MOON_ROCK.getDefaultState(), GalacticraftBlocks.MOON_COPPER_ORE.getDefaultState(), 8)));
//        GalacticraftBiomes.MOON.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, new ConfiguredFeature<>(GC_ORE, new GCOreGenConfig(GalacticraftBlocks.MOON_ROCK.getDefaultState(), GalacticraftBlocks.CHEESE_ORE.getDefaultState(), 4)));
    }

    // This code is basically just copy-pasted
    // from Feature.ORE (Target block must be of type enum Target... why Mojang?)
    public static class GCOre extends Feature<GCOreGenConfig> {

        public GCOre(Function<Dynamic<?>, ? extends GCOreGenConfig> function) {
            super(function);
        }

        @Override
        public boolean generate(IWorld iWorld, StructureAccessor accessor, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos blockPos, GCOreGenConfig featureConfig) {
            float float_1 = random.nextFloat() * 3.1415927F;
            float float_2 = (float) featureConfig.size / 8.0F;
            int size = MathHelper.ceil(((float) featureConfig.size / 16.0F * 2.0F + 1.0F) / 2.0F);

            double x1 = (float) blockPos.getX() + MathHelper.sin(float_1) * float_2;
            double x2 = (float) blockPos.getX() - MathHelper.sin(float_1) * float_2;
            double z1 = (float) blockPos.getZ() + MathHelper.cos(float_1) * float_2;
            double z2 = (float) blockPos.getZ() - MathHelper.cos(float_1) * float_2;
            double double_5 = blockPos.getY() + random.nextInt(3) - 2;
            double double_6 = blockPos.getY() + random.nextInt(3) - 2;

            int int_3 = blockPos.getX() - MathHelper.ceil(float_2) - size;
            int int_4 = blockPos.getY() - 2 - size;
            int int_5 = blockPos.getZ() - MathHelper.ceil(float_2) - size;
            int int_6 = 2 * (MathHelper.ceil(float_2) + size);
            int int_7 = 2 * (2 + size);

            for (int i = int_3; i <= int_3 + int_6; ++i) {
                for (int j = int_5; j <= int_5 + int_6; ++j) {

                    if (int_4 <= iWorld.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, i, j)) {
                        return this.generateVeinPart(iWorld, random, featureConfig, x1, x2, z1, z2, double_5, double_6, int_3, int_4, int_5, int_6, int_7);
                    }
                }
            }
            return false;
        }


        private boolean generateVeinPart(IWorld iWorld, Random random, GCOreGenConfig oreGenConfig, double double_1, double double_2, double double_3, double double_4, double double_5, double double_6, int int_1, int int_2, int int_3, int int_4, int int_5) {
            int int_6 = 0;
            BitSet bitSet_1 = new BitSet(int_4 * int_5 * int_4);
            BlockPos.Mutable blockPos$Mutable_1 = new BlockPos.Mutable();
            double[] doubles_1 = new double[oreGenConfig.size * 4];

            int int_8;
            double double_12;
            double double_13;
            double double_14;
            double double_15;
            for (int_8 = 0; int_8 < oreGenConfig.size; ++int_8) {
                float float_1 = (float) int_8 / (float) oreGenConfig.size;
                double_12 = MathHelper.lerp(float_1, double_1, double_2);
                double_13 = MathHelper.lerp(float_1, double_5, double_6);
                double_14 = MathHelper.lerp(float_1, double_3, double_4);
                double_15 = random.nextDouble() * (double) oreGenConfig.size / 16.0D;
                double double_11 = ((double) (MathHelper.sin(3.1415927F * float_1) + 1.0F) * double_15 + 1.0D) / 2.0D;
                doubles_1[int_8 * 4] = double_12;
                doubles_1[int_8 * 4 + 1] = double_13;
                doubles_1[int_8 * 4 + 2] = double_14;
                doubles_1[int_8 * 4 + 3] = double_11;
            }

            for (int_8 = 0; int_8 < oreGenConfig.size - 1; ++int_8) {
                if (doubles_1[int_8 * 4 + 3] > 0.0D) {
                    for (int int_9 = int_8 + 1; int_9 < oreGenConfig.size; ++int_9) {
                        if (doubles_1[int_9 * 4 + 3] > 0.0D) {
                            double_12 = doubles_1[int_8 * 4] - doubles_1[int_9 * 4];
                            double_13 = doubles_1[int_8 * 4 + 1] - doubles_1[int_9 * 4 + 1];
                            double_14 = doubles_1[int_8 * 4 + 2] - doubles_1[int_9 * 4 + 2];
                            double_15 = doubles_1[int_8 * 4 + 3] - doubles_1[int_9 * 4 + 3];
                            if (double_15 * double_15 > double_12 * double_12 + double_13 * double_13 + double_14 * double_14) {
                                if (double_15 > 0.0D) {
                                    doubles_1[int_9 * 4 + 3] = -1.0D;
                                } else {
                                    doubles_1[int_8 * 4 + 3] = -1.0D;
                                }
                            }
                        }
                    }
                }
            }

            for (int_8 = 0; int_8 < oreGenConfig.size; ++int_8) {
                double double_16 = doubles_1[int_8 * 4 + 3];
                if (double_16 >= 0.0D) {
                    double double_17 = doubles_1[int_8 * 4];
                    double double_18 = doubles_1[int_8 * 4 + 1];
                    double double_19 = doubles_1[int_8 * 4 + 2];
                    int int_11 = Math.max(MathHelper.floor(double_17 - double_16), int_1);
                    int int_12 = Math.max(MathHelper.floor(double_18 - double_16), int_2);
                    int int_13 = Math.max(MathHelper.floor(double_19 - double_16), int_3);
                    int int_14 = Math.max(MathHelper.floor(double_17 + double_16), int_11);
                    int int_15 = Math.max(MathHelper.floor(double_18 + double_16), int_12);
                    int int_16 = Math.max(MathHelper.floor(double_19 + double_16), int_13);

                    for (int int_17 = int_11; int_17 <= int_14; ++int_17) {
                        double double_20 = ((double) int_17 + 0.5D - double_17) / double_16;
                        if (double_20 * double_20 < 1.0D) {
                            for (int int_18 = int_12; int_18 <= int_15; ++int_18) {
                                double double_21 = ((double) int_18 + 0.5D - double_18) / double_16;
                                if (double_20 * double_20 + double_21 * double_21 < 1.0D) {
                                    for (int int_19 = int_13; int_19 <= int_16; ++int_19) {
                                        double double_22 = ((double) int_19 + 0.5D - double_19) / double_16;
                                        if (double_20 * double_20 + double_21 * double_21 + double_22 * double_22 < 1.0D) {
                                            int int_20 = int_17 - int_1 + (int_18 - int_2) * int_4 + (int_19 - int_3) * int_4 * int_5;
                                            if (!bitSet_1.get(int_20)) {
                                                bitSet_1.set(int_20);
                                                blockPos$Mutable_1.set(int_17, int_18, int_19);

                                                if (oreGenConfig.target == iWorld.getBlockState(blockPos$Mutable_1)) {
                                                    iWorld.setBlockState(blockPos$Mutable_1, oreGenConfig.state, 2);
                                                    ++int_6;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return int_6 > 0;
        }
    }
}
