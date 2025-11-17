/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import java.util.function.Function;

public class MarsCraterCarver extends WorldCarver<CraterCarverConfig> {
    public MarsCraterCarver(Codec<CraterCarverConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean carve(CarvingContext context, CraterCarverConfig config, ChunkAccess chunk,
                         Function<BlockPos, Holder<Biome>> posToBiome, RandomSource random,
                         Aquifer aquiferSampler, ChunkPos pos, CarvingMask carvingMask) {

        int y = config.y.sample(random, context);
        BlockPos craterCenter = pos.getBlockAt(random.nextInt(16), y, random.nextInt(16));

        if (!chunk.getReferencesForStructure(context.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(GCStructures.Moon.VILLAGE)).isEmpty()) {
            return false;
        }

        BlockPos.MutableBlockPos mutable = craterCenter.mutable();

        double radius = 8 + (random.nextDouble() * (config.maxRadius - config.minRadius));
        if (random.nextBoolean() && radius < (config.minRadius + config.idealRangeOffset) || radius > (config.maxRadius - config.idealRangeOffset))
            radius = 8 + (random.nextDouble() * (config.maxRadius - config.minRadius));
        double depthMultiplier = 1 - ((random.nextDouble() - 0.5) * 0.3);
        boolean fresh = random.nextInt(16) == 1;

        int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight();

        // Store depth per column
        int[][] digDepth = new int[16][16];

        // Compute crater depth
        for (int innerChunkX = 0; innerChunkX < 16; innerChunkX++) {
            for (int innerChunkZ = 0; innerChunkZ < 16; innerChunkZ++) {
                double toDig = 0.0;

                double xDev = Math.abs(chunk.getPos().getBlockX(innerChunkX) - craterCenter.getX());
                double zDev = Math.abs(chunk.getPos().getBlockZ(innerChunkZ) - craterCenter.getZ());

                double distSq = xDev * xDev + zDev * zDev;
                if (xDev < 32 && zDev < 32 && distSq < radius * radius) {
                    // Normalized distance from center (0 = center, 1 = rim)
                    double distNorm = Math.sqrt(distSq) / radius;

                    // Smooth bowl
                    double craterDepth = (1.0 - distNorm * distNorm) * 5.0;
                    craterDepth *= depthMultiplier;

                    if (distNorm < 0.95 && craterDepth < 1.0) {
                        craterDepth = 1.0;
                    }

                    if (craterDepth > 0.0) {
                        toDig = craterDepth;
                    }
                }

                if (toDig >= 1.0) {
                    toDig++;
                    if (fresh) toDig++;
                }


                digDepth[innerChunkX][innerChunkZ] = (int) Math.floor(toDig);
            }
        }

        // Remove 1-block-wide ridges
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (digDepth[x][z] != 0) continue;

                int nonZero = 0;
                int sum = 0;

                if (x > 0 && digDepth[x - 1][z] > 0) { nonZero++; sum += digDepth[x - 1][z]; }
                if (x < 15 && digDepth[x + 1][z] > 0) { nonZero++; sum += digDepth[x + 1][z]; }
                if (z > 0 && digDepth[x][z - 1] > 0) { nonZero++; sum += digDepth[x][z - 1]; }
                if (z < 15 && digDepth[x][z + 1] > 0) { nonZero++; sum += digDepth[x][z + 1]; }

                // If this column is surrounded by crater on at least two sides, treat it as part of the crater too.
                if (nonZero >= 2) {
                    digDepth[x][z] = Math.max(1, sum / nonZero);
                }
            }
        }

        // Actually dig using cleaned depths
        for (int innerChunkX = 0; innerChunkX < 16; innerChunkX++) {
            for (int innerChunkZ = 0; innerChunkZ < 16; innerChunkZ++) {
                int toDig = digDepth[innerChunkX][innerChunkZ];
                if (toDig <= 0) continue;

                BlockPos.MutableBlockPos copy = new BlockPos.MutableBlockPos();
                mutable.set(innerChunkX, y, innerChunkZ);

                for (int dug = 0; dug < toDig; dug++) {
                    mutable.move(Direction.DOWN);

                    int worldY = mutable.getY();
                    if (worldY < minY) break;
                    if (worldY >= maxY) continue;

                    if (!chunk.getBlockState(mutable).isAir() || carvingMask.get(innerChunkX, worldY, innerChunkZ) || dug > 0) {
                        chunk.setBlockState(mutable, AIR, false);
                        if (dug == 0) {
                            carvingMask.set(innerChunkX, worldY, innerChunkZ);
                        }
                        if (!fresh && dug + 1 >= toDig && !chunk.getBlockState(copy.set(mutable).move(Direction.DOWN, 2)).isAir()) {
                            context.topMaterial(posToBiome, chunk, mutable, false)
                                    .ifPresent(blockStatex -> chunk.setBlockState(mutable.move(Direction.DOWN), blockStatex, false));
                        }
                    } else {
                        dug--;
                    }
                }
            }
        }

        return true;
    }


    @Override
    public boolean isStartChunk(CraterCarverConfig config, RandomSource random) {
        return random.nextFloat() <= config.probability;
    }
}
