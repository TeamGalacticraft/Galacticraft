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
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.*;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Function;

public class OlivineCaveCarver extends CaveWorldCarver {
    public OlivineCaveCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean carve(CarvingContext context, CaveCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> posToBiome, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask mask) {
        boolean carved = super.carve(context, config, chunk, posToBiome, random, aquifer, chunkPos, mask);

        if (carved) {
            carveWalls(chunk, mask, context);
        }

        return carved;
    }

    private void carveWalls(ChunkAccess chunk, CarvingMask mask, CarvingContext context) {
        Block olivine = GCBlocks.OLIVINE_BLOCK;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int minY = context.getMinGenY();
        int maxY = minY + context.getGenDepth();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    // For every carved block...
                    if (mask.get(x, y, z)) {
                        int worldX = chunk.getPos().getMinBlockX() + x;
                        int worldZ = chunk.getPos().getMinBlockZ() + z;

                        // Check each neighbor for solid uncarved blocks
                        for (Direction dir : Direction.values()) {
                            int dx = x + dir.getStepX();
                            int dy = y + dir.getStepY();
                            int dz = z + dir.getStepZ();

                            if (dx >= 0 && dx < 16 && dz >= 0 && dz < 16 && dy >= minY && dy < maxY) {
                                if (!mask.get(dx, dy, dz)) {
                                    int neighborWorldX = chunk.getPos().getMinBlockX() + dx;
                                    int neighborWorldZ = chunk.getPos().getMinBlockZ() + dz;
                                    pos.set(neighborWorldX, dy, neighborWorldZ);

                                    BlockState state = chunk.getBlockState(pos);
                                    if (!state.isAir() && state.getFluidState().isEmpty()) {
                                        chunk.setBlockState(pos, olivine.defaultBlockState(), false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasAdjacentCarvedBlock(CarvingMask mask, int x, int y, int z, int minY, int maxY) {
        for (Direction dir : Direction.values()) {
            int dx = x + dir.getStepX();
            int dy = y + dir.getStepY();
            int dz = z + dir.getStepZ();

            if (dy < minY || dy >= maxY) continue;
            if (dx < 0 || dx >= 16 || dz < 0 || dz >= 16) continue; // Don't trust padded areas

            if (mask.get(dx, dy, dz)) {
                return true;
            }
        }
        return false;
    }

}