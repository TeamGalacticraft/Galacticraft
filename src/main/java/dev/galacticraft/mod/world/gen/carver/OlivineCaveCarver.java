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
import dev.galacticraft.mod.tag.GCBlockTags;
import dev.galacticraft.mod.world.dimension.MoonConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.*;

import java.util.function.Function;

public class OlivineCaveCarver extends CaveWorldCarver {
    public OlivineCaveCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    private static final Block INTERIOR_WALL_BLOCK = GCBlocks.OLIVINE_BLOCK;
    private static final Block EXTERIOR_WALL_BLOCK = GCBlocks.MOON_BASALT;
    private static final Block BUDDING_BLOCK = GCBlocks.BUDDING_OLIVINE;
    private static final Block OLIVINE_BASALT = GCBlocks.OLIVINE_BASALT;
    private static final Block RICH_OLIVINE_BASALT = GCBlocks.RICH_OLIVINE_BASALT;
    private static final Block CLUSTER_BLOCK = GCBlocks.OLIVINE_CLUSTER;

    @Override
    protected boolean carveEllipsoid(CarvingContext context, CaveCarverConfiguration configuration, ChunkAccess chunkAccess, Function<BlockPos, Holder<Biome>> posToBiome, Aquifer aquifer, double x, double y, double z, double width, double height, CarvingMask mask, CarveSkipChecker carveSkipChecker) {
        ChunkPos chunkPos = chunkAccess.getPos();
        RandomSource random = context.randomState().aquiferRandom().at((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
        boolean basaltInterior = random.nextFloat() < MoonConstants.OLIVINE_CAVE_BASALT_INTERIOR_CHANCE;
        double d = (double) chunkPos.getMiddleBlockX();
        double e = (double) chunkPos.getMiddleBlockZ();
        double f = (double) 16.0F + width * (double) 2.0F;
        if (!(Math.abs(x - d) > f) && !(Math.abs(z - e) > f)) {
            int i = chunkPos.getMinBlockX();
            int j = chunkPos.getMinBlockZ();
            int k = Math.max(Mth.floor(x - width) - i - 1, 0);
            int l = Math.min(Mth.floor(x + width) - i, 15);
            int m = Math.max(Mth.floor(y - height) - 1, context.getMinGenY() + 1);
            int n = chunkAccess.isUpgrading() ? 0 : 7;
            int o = Math.min(Mth.floor(y + height) + 1, context.getMinGenY() + context.getGenDepth() - 1 - n);
            int p = Math.max(Mth.floor(z - width) - j - 1, 0);
            int q = Math.min(Mth.floor(z + width) - j, 15);
            boolean bl = false;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for(int r = k; r <= l; ++r) {
                int s = chunkPos.getBlockX(r);
                double g = ((double)s + (double)0.5F - x) / width;

                for(int t = p; t <= q; ++t) {
                    int u = chunkPos.getBlockZ(t);
                    double h = ((double)u + (double)0.5F - z) / width;
                    for (int v = o; v > m; --v) {
                        double w = ((double)v - 0.5D - y) / height;
                        double distance = g * g + h * h + w * w;

                        mutableBlockPos.set(s, v, u);
                        BlockState blockState = chunkAccess.getBlockState(mutableBlockPos);
                        if (distance < 0.88) {
                            if (!carveSkipChecker.shouldSkip(context, g, w, h, v) && (!mask.get(r, v, t) || blockState.is(GCBlockTags.OLIVINE_CAVE_INTERNALS))) {
                                mask.set(r, v, t);
                                if (blockState.is(BUDDING_BLOCK)) {
                                    for (Direction direction : Direction.values()) {
                                        BlockPos crystalPos = mutableBlockPos.relative(direction);

                                        if (crystalPos.getX() >= chunkAccess.getPos().getMinBlockX() && crystalPos.getX() <= chunkAccess.getPos().getMaxBlockX()
                                                && crystalPos.getZ() >= chunkAccess.getPos().getMinBlockZ() && crystalPos.getZ() <= chunkAccess.getPos().getMaxBlockZ()
                                                && crystalPos.getY() >= context.getMinGenY() && crystalPos.getY() < context.getMinGenY() + context.getGenDepth()) {

                                            BlockState neighborState = chunkAccess.getBlockState(crystalPos);
                                            if (neighborState.is(CLUSTER_BLOCK)) {
                                                Direction facing = neighborState.getValue(AmethystClusterBlock.FACING);
                                                if (facing == direction) {
                                                    chunkAccess.setBlockState(crystalPos, Blocks.AIR.defaultBlockState(), false);
                                                }
                                            }
                                        }
                                    }
                                }
                                chunkAccess.setBlockState(mutableBlockPos, Blocks.AIR.defaultBlockState(), true);
                                bl = true;
                            }
                        } else if (distance < 0.9) {
                            if ((blockState.is(GCBlockTags.OLIVINE_CAVE_REPLACEABLES) && !blockState.isAir()) || blockState.is(GCBlockTags.OLIVINE_CAVE_INTERNALS)) {
                                chunkAccess.setBlockState(mutableBlockPos, BUDDING_BLOCK.defaultBlockState(), true);
                                for (Direction direction : Direction.values()) {
                                    BlockPos crystalPos = mutableBlockPos.relative(direction);

                                    int relX = crystalPos.getX() - chunkAccess.getPos().getMinBlockX();
                                    int relZ = crystalPos.getZ() - chunkAccess.getPos().getMinBlockZ();
                                    int crystalPosY = crystalPos.getY();

                                    if (relX >= 0 && relX < 16 && relZ >= 0 && relZ < 16 &&
                                            crystalPosY >= context.getMinGenY() && crystalPosY < context.getMinGenY() + context.getGenDepth()) {

                                        BlockState neighborState = chunkAccess.getBlockState(crystalPos);
                                        if (neighborState.isAir() && random.nextFloat() < 0.75f) {
                                            chunkAccess.setBlockState(
                                                    crystalPos,
                                                    CLUSTER_BLOCK.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction),
                                                    true
                                            );
                                        }
                                    }
                                }
                            }
                        } else if (distance < 1.5) {
                            if ((blockState.is(GCBlockTags.OLIVINE_CAVE_REPLACEABLES) || blockState.is(EXTERIOR_WALL_BLOCK)) && !blockState.isAir()) {
                                if (basaltInterior) {
                                    float pr = random.nextFloat();
                                    if (pr < 0.95f) {
                                        chunkAccess.setBlockState(mutableBlockPos, EXTERIOR_WALL_BLOCK.defaultBlockState(), true);
                                    } else if (pr < 0.995f) {
                                        chunkAccess.setBlockState(mutableBlockPos, OLIVINE_BASALT.defaultBlockState(), true);
                                    } else {
                                        chunkAccess.setBlockState(mutableBlockPos, RICH_OLIVINE_BASALT.defaultBlockState(), true);
                                    }
                                } else {
                                    chunkAccess.setBlockState(mutableBlockPos, INTERIOR_WALL_BLOCK.defaultBlockState(), true);
                                }
                            }
                        } else if (distance < 2) {
                            if (blockState.is(GCBlockTags.OLIVINE_CAVE_REPLACEABLES) && !blockState.isAir()) {
                                chunkAccess.setBlockState(mutableBlockPos, EXTERIOR_WALL_BLOCK.defaultBlockState(), true);
                            }
                        }
                    }
                }
            }
            return bl;
        } else {
            return false;
        }
    }
}