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
import dev.galacticraft.mod.world.gen.carver.config.CraterCarverConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.chunk.AquiferSampler;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class CraterCarver extends Carver<CraterCarverConfig> {
    public CraterCarver(Codec<CraterCarverConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean carve(CarverContext context, CraterCarverConfig config, Chunk chunk, Function<BlockPos, Biome> posToBiome, Random random, AquiferSampler aquiferSampler, ChunkPos pos, BitSet carvingMask) {
        int y = 127;//config.y.get(random, context);
        //pos = center chunk pos
        BlockPos craterCenter = pos.getBlockPos(random.nextInt(16), y, random.nextInt(16));
        BlockPos.Mutable mutable = craterCenter.mutableCopy();

        double radius = 8 + (random.nextDouble() * (config.maxRadius - config.minRadius));
        if (random.nextBoolean() && radius < (config.minRadius + config.idealRangeOffset) || radius > (config.maxRadius - config.idealRangeOffset))
            radius = 8 + (random.nextDouble() * (config.maxRadius - config.minRadius));
        double depthMultiplier = 1 - ((random.nextDouble() - 0.5) * 0.3);
        boolean fresh = random.nextInt(16) == 1;
        for (int innerChunkX = 0; innerChunkX < 16; innerChunkX++) { //iterate through positions in chunk
            for (int innerChunkZ = 0; innerChunkZ < 16; innerChunkZ++) {
                double toDig = 0;

                double xDev = Math.abs((chunk.getPos().getOffsetX(innerChunkX)) - craterCenter.getX());
                double zDev = Math.abs((chunk.getPos().getOffsetZ(innerChunkZ)) - craterCenter.getZ());
                if (xDev >= 0 && xDev < 32 && zDev >= 0 && zDev < 32) {
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

                    if (toDig >= 1) {
                        toDig++; // Increase crater depth, but for sum, not each crater
                        if (fresh) toDig++; // Dig one more block, because we're not replacing the top with turf
                    }
                    BlockPos.Mutable copy = new BlockPos.Mutable();
                    mutable.set(innerChunkX, y, innerChunkZ);
                    for (int dug = 0; dug < toDig; dug++) {
                        mutable.move(Direction.DOWN);
                        if (!chunk.getBlockState(mutable).isAir() || carvingMask.get(innerChunkX + (innerChunkZ << 4) + ((mutable.getY() + 128) << 8)) || dug > 0) {
                            chunk.setBlockState(mutable, CAVE_AIR, false);
                            if (dug == 0) {
                                carvingMask.set(innerChunkX + (innerChunkZ << 4) + ((mutable.getY() + 128) << 8), true);
                            }
                            if (!fresh && dug + 1 >= toDig && !chunk.getBlockState(copy.set(mutable).move(Direction.DOWN, 2)).isAir()) {
                                chunk.setBlockState(mutable.move(Direction.DOWN), posToBiome.apply(chunk.getPos().getBlockPos(mutable.getX(), mutable.getY(), mutable.getZ())).getGenerationSettings().getSurfaceConfig().getTopMaterial(), false);
                            }
                        } else {
                            dug--;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldCarve(CraterCarverConfig config, Random random) {
        return random.nextFloat() <= config.probability;
    }
}
