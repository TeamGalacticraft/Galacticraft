/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.carver;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CraterCarver extends Carver<ProbabilityConfig> {
    public CraterCarver(int heightLimit) {
        this(ProbabilityConfig.CODEC, heightLimit);
    }

    public CraterCarver(Codec<ProbabilityConfig> codec, int heightLimit) {
        super(codec, heightLimit);
    }

    @Override
    public boolean carve(Chunk chunk, Function<BlockPos, Biome> posToBiome, Random random, int seaLevel, int chunkX, int chunkZ, int mainChunkX, int mainChunkZ, BitSet carvingMask, ProbabilityConfig carverConfig) {
        boolean carved = false;
        for (int cX = -1; cX < 2; cX++) {
            for (int cZ = -1; cZ < 2; cZ++) {
                if (carvingMask.get((cX + 1) | (cZ + 1) << 4)) continue;
                Random random1 = new ChunkRandom(ChunkPos.toLong(chunk.getPos().x + cX, chunk.getPos().z + cZ));
                if (random1.nextFloat() < carverConfig.probability) {
//                    BlockState block = Registry.BLOCK.get(random1.nextInt(10)).getDefaultState();
                    BlockPos craterCenter = new BlockPos(cX * 16 + random1.nextInt(15), 75, cZ * 16 + random1.nextInt(15));
                    BlockPos.Mutable mutable = craterCenter.mutableCopy();
//                    if (cX == 0 && cZ == 0) chunk.setBlockState(mutable, block, false);
                    double radius = skewRandom(4, 15, random1.nextGaussian(), 1.2, -0.5);
                    double depthMultiplier = skewRandom(0.4, 1.4, random1.nextGaussian(), 2, 0);
                    boolean fresh = random1.nextInt(15) == 1;
                    for (int innerChunkX = 0; innerChunkX < 16; innerChunkX++) { //iterate through positions in chunk
                        for (int innerChunkZ = 0; innerChunkZ < 16; innerChunkZ++) {
                            double toDig = 0;

                            double xDev = Math.abs(innerChunkX - craterCenter.getX());
                            double zDev = Math.abs(innerChunkZ - craterCenter.getZ());
                            if (xDev >= 0 && xDev < 16 && zDev >= 0 && zDev < 16) {
                                if (xDev * xDev + zDev * zDev < radius * radius) { //distance to crater and depth
                                    xDev /= radius;
                                    zDev /= radius;
                                    final double sqrtY = xDev * xDev + zDev * zDev;
                                    double yDev = sqrtY * sqrtY * 6;
                                    double craterDepth = 5 - yDev;
                                    craterDepth *= depthMultiplier;
                                    if (craterDepth > 0.0) {
                                        toDig = craterDepth;
                                    }
                                }

                                if (toDig > 0) carved = true;
                                else continue;

                                if (toDig > 0) toDig++; // Increase crater depth, but for sum, not each crater
                                if (fresh) toDig++; // Dig one more block, because we're not replacing the top with turf

                                mutable.set(innerChunkX, this.heightLimit, innerChunkZ);
                                carvingMask.set((cX + 1) | (cZ + 1) << 4);
                                for (int dug = 0; dug < toDig; dug++) {
                                    mutable.move(Direction.DOWN);
                                    if (!chunk.getBlockState(mutable).isAir() || dug > 0) {
//                                        Galacticraft.LOGGER.info("X: " + mutable.getX() + " Y: " + mutable.getY() + " Z: " + mutable.getZ() + " Dug: " + dug + " Max: " + toDig + "Block: " + chunk.getBlockState(mutable));
                                        chunk.setBlockState(mutable, CAVE_AIR, false);
                                        if (!fresh && dug + 1 >= toDig)
                                            chunk.setBlockState(mutable.move(Direction.DOWN), GalacticraftBlock.MOON_TURF.getDefaultState(), false);
                                    } else {
                                        dug--;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return carved;
    }

    @Override
    public boolean shouldCarve(Random random, int chunkX, int chunkZ, ProbabilityConfig config) {
        return true;// random.nextFloat() < config.probability && random.nextBoolean() && random.nextBoolean() && random.nextBoolean() && random.nextBoolean();
    }

    @Override
    protected boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y) {
        return false;
    }

    private static double skewRandom(double min, double max, double gaussian, double skew, double bias) {
        double range = max - min;
        double biasFactor = Math.exp(bias);
        return (min + range / 2.0) + (range * (biasFactor / (biasFactor + Math.exp(-gaussian / skew)) - 0.5));
    }
}
