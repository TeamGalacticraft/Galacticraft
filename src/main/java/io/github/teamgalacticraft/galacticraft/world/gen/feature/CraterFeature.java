package io.github.teamgalacticraft.galacticraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;
import java.util.function.Function;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CraterFeature extends Feature<CraterFeatureConfig> {
    public CraterFeature(Function<Dynamic<?>, ? extends CraterFeatureConfig> function_1) {
        super(function_1);
    }

    public boolean generate(IWorld iWorld_1, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator_1, Random random_1, BlockPos blockPos_1, CraterFeatureConfig lakeFeatureConfig_1) {
        while (blockPos_1.getY() > 5 && iWorld_1.isAir(blockPos_1)) {
            blockPos_1 = blockPos_1.down();
        }

        if (blockPos_1.getY() <= 4) {
            return false;
        } else {
            blockPos_1 = blockPos_1.down(4);
            ChunkPos chunkPos_1 = new ChunkPos(blockPos_1);
            if (!iWorld_1.getChunk(chunkPos_1.x, chunkPos_1.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(Feature.VILLAGE.getName()).isEmpty()) {
                return false;
            } else {
                boolean[] booleans_1 = new boolean[2048];
                int int_1 = random_1.nextInt(4) + 4;

                int int_15;
                for (int_15 = 0; int_15 < int_1; ++int_15) {
                    double double_1 = random_1.nextDouble() * 6.0D + 3.0D;
                    double double_2 = random_1.nextDouble() * 4.0D + 2.0D;
                    double double_3 = random_1.nextDouble() * 6.0D + 3.0D;
                    double double_4 = random_1.nextDouble() * (16.0D - double_1 - 2.0D) + 1.0D + double_1 / 2.0D;
                    double double_5 = random_1.nextDouble() * (8.0D - double_2 - 4.0D) + 2.0D + double_2 / 2.0D;
                    double double_6 = random_1.nextDouble() * (16.0D - double_3 - 2.0D) + 1.0D + double_3 / 2.0D;

                    for (int int_3 = 1; int_3 < 15; ++int_3) {
                        for (int int_4 = 1; int_4 < 15; ++int_4) {
                            for (int int_5 = 1; int_5 < 7; ++int_5) {
                                double double_7 = ((double) int_3 - double_4) / (double_1 / 2.0D);
                                double double_8 = ((double) int_5 - double_5) / (double_2 / 2.0D);
                                double double_9 = ((double) int_4 - double_6) / (double_3 / 2.0D);
                                double double_10 = double_7 * double_7 + double_8 * double_8 + double_9 * double_9;
                                if (double_10 < 1.0D) {
                                    booleans_1[(int_3 * 16 + int_4) * 8 + int_5] = true;
                                }
                            }
                        }
                    }
                }

                int int_17;
                int int_16;
                boolean boolean_2;

                for (int_15 = 0; int_15 < 16; ++int_15) {
                    for (int_16 = 0; int_16 < 16; ++int_16) {
                        for (int_17 = 0; int_17 < 8; ++int_17) {
                            if (booleans_1[(int_15 * 16 + int_16) * 8 + int_17]) {
                                iWorld_1.setBlockState(blockPos_1.add(int_15, int_17, int_16), int_17 >= 4 ? Blocks.AIR.getDefaultState() : Blocks.AIR.getDefaultState(), 2);
                            }
                        }
                    }
                }

                BlockPos blockPos_3;
                for (int_15 = 0; int_15 < 16; ++int_15) {
                    for (int_16 = 0; int_16 < 16; ++int_16) {
                        for (int_17 = 4; int_17 < 8; ++int_17) {
                            if (booleans_1[(int_15 * 16 + int_16) * 8 + int_17]) {
                                blockPos_3 = blockPos_1.add(int_15, int_17 - 1, int_16);
                                if (Block.isNaturalDirt(iWorld_1.getBlockState(blockPos_3).getBlock()) && iWorld_1.getLightLevel(LightType.SKY, blockPos_1.add(int_15, int_17, int_16)) > 0) {
                                    iWorld_1.setBlockState(blockPos_3, GalacticraftBlocks.MOON_TURF_BLOCK.getDefaultState(), 2);
                                }
                            }
                        }
                    }
                }

                    for (int_15 = 0; int_15 < 16; ++int_15) {
                        for (int_16 = 0; int_16 < 16; ++int_16) {
                            for (int_17 = 0; int_17 < 8; ++int_17) {
                                boolean_2 = !booleans_1[(int_15 * 16 + int_16) * 8 + int_17] && (int_15 < 15 && booleans_1[((int_15 + 1) * 16 + int_16) * 8 + int_17] || int_15 > 0 && booleans_1[((int_15 - 1) * 16 + int_16) * 8 + int_17] || int_16 < 15 && booleans_1[(int_15 * 16 + int_16 + 1) * 8 + int_17] || int_16 > 0 && booleans_1[(int_15 * 16 + (int_16 - 1)) * 8 + int_17] || int_17 < 7 && booleans_1[(int_15 * 16 + int_16) * 8 + int_17 + 1] || int_17 > 0 && booleans_1[(int_15 * 16 + int_16) * 8 + (int_17 - 1)]);
                                if (boolean_2 && (int_17 < 4 || random_1.nextInt(2) != 0) && iWorld_1.getBlockState(blockPos_1.add(int_15, int_17, int_16)).getMaterial().method_15799()) {
                                    iWorld_1.setBlockState(blockPos_1.add(int_15, int_17, int_16), GalacticraftBlocks.MOON_TURF_BLOCK.getDefaultState(), 2);
                                }
                            }
                        }
                    }


                return true;
            }
        }
    }
}
