/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

public class CraterCarver extends WorldCarver<CraterCarverConfig> {
    public CraterCarver(Codec<CraterCarverConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean carve(CarvingContext context, CraterCarverConfig config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> posToBiome, RandomSource random, Aquifer aquiferSampler, ChunkPos pos, CarvingMask carvingMask) {
        int y = config.y.sample(random, context);
        //pos = center chunk pos
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
        for (int innerChunkX = 0; innerChunkX < 16; innerChunkX++) { //iterate through positions in chunk
            for (int innerChunkZ = 0; innerChunkZ < 16; innerChunkZ++) {
                double toDig = 0;

                double xDev = Math.abs((chunk.getPos().getBlockX(innerChunkX)) - craterCenter.getX());
                double zDev = Math.abs((chunk.getPos().getBlockZ(innerChunkZ)) - craterCenter.getZ());
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
                    BlockPos.MutableBlockPos copy = new BlockPos.MutableBlockPos();
                    mutable.set(innerChunkX, y, innerChunkZ);
                    for (int dug = 0; dug < toDig; dug++) {
                        mutable.move(Direction.DOWN);
                        if (!chunk.getBlockState(mutable).isAir() || carvingMask.get(innerChunkX, mutable.getY() + 64, innerChunkZ) || dug > 0) {
                            chunk.setBlockState(mutable, AIR, false);
                            if (dug == 0) {
                                carvingMask.set(innerChunkX, mutable.getY() + 64, innerChunkZ);
                            }
                            if (!fresh && dug + 1 >= toDig && !chunk.getBlockState(copy.set(mutable).move(Direction.DOWN, 2)).isAir()) {
                                context.topMaterial(posToBiome, chunk, mutable, false).ifPresent(blockStatex -> chunk.setBlockState(mutable.move(Direction.DOWN), blockStatex, false));
                            }
                        } else {
                            dug--;
                        }
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
