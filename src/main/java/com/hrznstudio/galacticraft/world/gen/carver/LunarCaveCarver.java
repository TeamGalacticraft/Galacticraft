package com.hrznstudio.galacticraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CaveCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class LunarCaveCarver extends CaveCarver {
    public LunarCaveCarver(Codec<ProbabilityConfig> codec, int i) {
        super(codec, i);
        this.alwaysCarvableBlocks = ImmutableSet.<Block>builder().addAll(this.alwaysCarvableBlocks)
                .add(GalacticraftBlocks.MOON_ROCK)
                .add(GalacticraftBlocks.MOON_SURFACE_ROCK)
                .add(GalacticraftBlocks.MOON_TURF)
                .add(GalacticraftBlocks.MOON_BASALT)
                .add(GalacticraftBlocks.MOON_DIRT)
                .build();
    }

    @Override
    protected int getMaxCaveCount() {
        return 17; //slightly longer caves
    }

    @Override
    protected float getTunnelSystemWidth(Random random) {
        float f = (random.nextFloat() * 2.1F) + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.1F; //slightly wider caves
        }
        return f;
    }

    @Override
    protected boolean carveAtPoint(Chunk chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutable, BlockPos.Mutable mutable2, BlockPos.Mutable mutable3, int seaLevel, int mainChunkX, int mainChunkZ, int x, int z, int relativeX, int y, int relativeZ, MutableBoolean mutableBoolean) {
        int i = relativeX | relativeZ << 4 | y << 8;
        if (carvingMask.get(i)) {
            return false;
        } else {
            carvingMask.set(i);
            mutable.set(x, y, z);
            BlockState blockState = chunk.getBlockState(mutable);
            BlockState blockState2 = chunk.getBlockState(mutable2.set(mutable, Direction.UP));
            if (blockState.isOf(GalacticraftBlocks.MOON_BASALT) || blockState.isOf(GalacticraftBlocks.MOON_ROCK)) {
                mutableBoolean.setTrue();
            }

            if (!this.canCarveBlock(blockState, blockState2)) {
                return false;
            } else {
                if (y < 11) {
                    chunk.setBlockState(mutable, LAVA.getBlockState(), false); //todo what block (preferably fluid) should cover up the bedrock floor?
                } else {
                    chunk.setBlockState(mutable, CAVE_AIR, false);
                    if (mutableBoolean.isTrue()) {
                        mutable3.set(mutable, Direction.DOWN);
                        if (chunk.getBlockState(mutable3).isOf(GalacticraftBlocks.MOON_DIRT)) {
                            chunk.setBlockState(mutable3, posToBiome.apply(mutable).getGenerationSettings().getSurfaceConfig().getTopMaterial(), false);
                        }
                    }
                }

                return true;
            }
        }
    }
}
