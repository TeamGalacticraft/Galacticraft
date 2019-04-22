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
    public CraterFeature(Function<Dynamic<?>, ? extends CraterFeatureConfig> function) {
        super(function);
    }

    public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGen, Random rand, BlockPos pos, CraterFeatureConfig craterConfig) {
        while (pos.getY() > 5 && world.isAir(pos)) {
            pos = pos.down();
        }

        if (pos.getY() <= 4) {
            return false;
        } else {
            pos = pos.down(4);
            ChunkPos chunkPos = new ChunkPos(pos);
            if (!world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(Feature.VILLAGE.getName()).isEmpty()) {
                return false;
            } else {
                boolean[] booleans = new boolean[2048];
                int i = rand.nextInt(4) + 4;

                int i5;
                for (i5 = 0; i5 < i; ++i5) {
                    double d1 = rand.nextDouble() * 6.0D + 3.0D;
                    double d2 = rand.nextDouble() * 4.0D + 2.0D;
                    double d3 = rand.nextDouble() * 6.0D + 3.0D;
                    double d4 = rand.nextDouble() * (16.0D - d1 - 2.0D) + 1.0D + d1 / 2.0D;
                    double d5 = rand.nextDouble() * (8.0D - d2 - 4.0D) + 2.0D + d2 / 2.0D;
                    double d6 = rand.nextDouble() * (16.0D - d3 - 2.0D) + 1.0D + d3 / 2.0D;

                    for (int i3 = 1; i3 < 15; ++i3) {
                        for (int i4 = 1; i4 < 15; ++i4) {
                            for (int i6 = 1; i6 < 7; ++i6) {
                                double d7 = ((double) i3 - d4) / (d1 / 2.0D);
                                double d8 = ((double) i6 - d5) / (d2 / 2.0D);
                                double d9 = ((double) i4 - d6) / (d3 / 2.0D);
                                double d10 = d7 * d7 + d8 * d8 + d9 * d9;
                                if (d10 < 1.0D) {
                                    booleans[(i3 * 16 + i4) * 8 + i6] = true;
                                }
                            }
                        }
                    }
                }

                int i7, i6;
                boolean boolean_2;

                for (i5 = 0; i5 < 16; ++i5) {
                    for (i6 = 0; i6 < 16; ++i6) {
                        for (i7 = 0; i7 < 8; ++i7) {
                            if (booleans[(i5 * 16 + i6) * 8 + i7]) {
                                world.setBlockState(pos.add(i5, i7, i6), i7 >= 4 ? Blocks.AIR.getDefaultState() : Blocks.AIR.getDefaultState(), 2);
                            }
                        }
                    }
                }

                BlockPos blockPos_3;
                for (i5 = 0; i5 < 16; ++i5) {
                    for (i6 = 0; i6 < 16; ++i6) {
                        for (i7 = 4; i7 < 8; ++i7) {
                            if (booleans[(i5 * 16 + i6) * 8 + i7]) {
                                blockPos_3 = pos.add(i5, i7 - 1, i6);
                                if (Block.isNaturalDirt(world.getBlockState(blockPos_3).getBlock()) && world.getLightLevel(LightType.SKY, pos.add(i5, i7, i6)) > 0) {
                                    world.setBlockState(blockPos_3, GalacticraftBlocks.MOON_TURF_BLOCK.getDefaultState(), 2);
                                }
                            }
                        }
                    }
                }

                for (i5 = 0; i5 < 16; ++i5) {
                    for (i6 = 0; i6 < 16; ++i6) {
                        for (i7 = 0; i7 < 8; ++i7) {
                            boolean_2 = !booleans[(i5 * 16 + i6) * 8 + i7] && (i5 < 15 && booleans[((i5 + 1) * 16 + i6) * 8 + i7] || i5 > 0 && booleans[((i5 - 1) * 16 + i6) * 8 + i7] || i6 < 15 && booleans[(i5 * 16 + i6 + 1) * 8 + i7] || i6 > 0 && booleans[(i5 * 16 + (i6 - 1)) * 8 + i7] || i7 < 7 && booleans[(i5 * 16 + i6) * 8 + i7 + 1] || i7 > 0 && booleans[(i5 * 16 + i6) * 8 + (i7 - 1)]);
                            if (boolean_2 && (i7 < 4 || rand.nextInt(2) != 0) && world.getBlockState(pos.add(i5, i7, i6)).getMaterial().isSolid()) {
                                world.setBlockState(pos.add(i5, i7, i6), GalacticraftBlocks.MOON_TURF_BLOCK.getDefaultState(), 2);
                            }
                        }
                    }
                }


                return true;
            }
        }
    }
}
