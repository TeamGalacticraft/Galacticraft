/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.cave.feature;

import dev.galacticraft.mod.world.gen.cave.CaveFeature;
import dev.galacticraft.mod.world.gen.cave.CaveFeatureContext;
import dev.galacticraft.mod.world.gen.cave.CaveSampleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class SurfaceSpikeFeature implements CaveFeature {
    private final BlockState block;
    private final int minY;
    private final int chance;
    private final int minHeight;
    private final int maxHeight;
    private final boolean floorOnly;

    public SurfaceSpikeFeature(
            BlockState block,
            int minY,
            int chance,
            int minHeight,
            int maxHeight,
            boolean floorOnly
    ) {
        this.block = block;
        this.minY = minY;
        this.chance = chance;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.floorOnly = floorOnly;
    }

    @Override
    public void decorate(CaveFeatureContext context, BlockPos pos, CaveSampleType type, int hash) {
        if (this.floorOnly && type != CaveSampleType.FLOOR) {
            return;
        }

        if (pos.getY() < this.minY || Math.floorMod(hash, this.chance) != 0) {
            return;
        }

        int heightRange = Math.max(1, this.maxHeight - this.minHeight + 1);
        int height = this.minHeight + Math.floorMod(hash >> 4, heightRange);

        placeSpike(context.chunk(), context.chunkPos(), pos, height);
    }

    private void placeSpike(ChunkAccess chunk, ChunkPos chunkPos, BlockPos start, int height) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int i = 0; i < height; i++) {
            int radius = radiusForLayer(i, height);
            int y = start.getY() + i;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) {
                        continue;
                    }

                    mutable.set(start.getX() + dx, y, start.getZ() + dz);

                    if (insideChunk(chunkPos, mutable) && chunk.getBlockState(mutable).isAir()) {
                        chunk.setBlockState(mutable, this.block, false);
                    }
                }
            }
        }
    }

    private static int radiusForLayer(int layer, int height) {
        double progress = layer / (double) Math.max(1, height - 1);

        if (progress < 0.22D && height >= 9) {
            return 2;
        }

        if (progress < 0.55D && height >= 5) {
            return 1;
        }

        return 0;
    }

    private static boolean insideChunk(ChunkPos chunkPos, BlockPos pos) {
        return pos.getX() >= chunkPos.getMinBlockX()
                && pos.getX() <= chunkPos.getMaxBlockX()
                && pos.getZ() >= chunkPos.getMinBlockZ()
                && pos.getZ() <= chunkPos.getMaxBlockZ();
    }
}