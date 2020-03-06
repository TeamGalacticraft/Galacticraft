/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.world.gen.feature.cheesetree;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeature;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CheeseTreeFeature extends BranchedTreeFeature<BranchedTreeFeatureConfig> {
    private final Random random = new Random();

    public CheeseTreeFeature(Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig> function) {
        super(function);
    }

    protected static boolean isDirtOrGrass(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, (blockState) -> isDirt(blockState.getBlock()) || blockState.getBlock() == Blocks.FARMLAND);
    }

    protected static boolean isDirt(Block block) {
        return block == GalacticraftBlocks.MOON_TURF;
    }

    @Override
    public Optional<BlockPos> findPositionToGenerate(ModifiableTestableWorld world, int height, int i, int j, BlockPos pos, BranchedTreeFeatureConfig config) {
        BlockPos blockPos2;
        int m;
        int n;
        if (!config.field_21593) {
            m = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR, pos).getY();
            n = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY();
            blockPos2 = new BlockPos(pos.getX(), m, pos.getZ());
            if (n - m > config.maxWaterDepth) {
                return Optional.empty();
            }
        } else {
            blockPos2 = pos;
        }

        blockPos2 = blockPos2.add(random.nextInt(16), 0, random.nextInt(16));

        if (blockPos2.getY() >= 1 && blockPos2.getY() + height + 1 <= 256) {
            for (m = 0; m <= height + 1; ++m) {
                n = method_23447(i, height, j, m);
                BlockPos.Mutable mutable = new BlockPos.Mutable();

                for (int o = -n; o <= n; ++o) {
                    int p = -n;

                    while (p <= n) {
                        if (m + blockPos2.getY() >= 0 && m + blockPos2.getY() < 256) {
                            mutable.set(o + blockPos2.getX(), m + blockPos2.getY(), p + blockPos2.getZ());
                            if (canTreeReplace(world, mutable) && (config.noVines || !isLeaves(world, mutable))) {
                                ++p;
                                continue;
                            }

                            return Optional.empty();
                        }

                        return Optional.empty();
                    }
                }
            }

            if (isDirtOrGrass(world, blockPos2.down()) && blockPos2.getY() < 256 - height - 1) {
                return Optional.of(blockPos2);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public boolean generate(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, BranchedTreeFeatureConfig branchedTreeFeatureConfig) {
        int i = branchedTreeFeatureConfig.baseHeight + random.nextInt(branchedTreeFeatureConfig.heightRandA + 1) + random.nextInt(branchedTreeFeatureConfig.heightRandB + 1);
        int j = branchedTreeFeatureConfig.trunkHeight >= 0 ? branchedTreeFeatureConfig.trunkHeight + random.nextInt(branchedTreeFeatureConfig.trunkHeightRandom + 1) : i - (branchedTreeFeatureConfig.foliageHeight + random.nextInt(branchedTreeFeatureConfig.foliageHeightRandom + 1));
        int k = getRadius(random, j, i, branchedTreeFeatureConfig);
        Optional<BlockPos> optional = this.findPositionToGenerate(modifiableTestableWorld, i, j, k, blockPos, branchedTreeFeatureConfig);
        if (!optional.isPresent()) {
            return false;
        } else {
            BlockPos blockPos2 = optional.get();
            this.setToDirt(modifiableTestableWorld, blockPos2.down());
            Direction direction = Direction.Type.HORIZONTAL.random(random);
            int l = i - random.nextInt(4) - 1;
            int m = 3 - random.nextInt(3);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int n = blockPos2.getX();
            int o = blockPos2.getZ();
            int p = 0;

            int r;
            for (int q = 0; q < i; ++q) {
                r = blockPos2.getY() + q;
                if (q >= l && m > 0) {
                    n += direction.getOffsetX();
                    o += direction.getOffsetZ();
                    --m;
                }

                if (this.setLogBlockState(modifiableTestableWorld, random, mutable.set(n, r, o), set, blockBox, branchedTreeFeatureConfig)) {
                    p = r;
                }
            }

            BlockPos blockPos3 = new BlockPos(n, p, o);
            branchedTreeFeatureConfig.foliagePlacer.generate(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, j, k + 1, blockPos3, set2);
            n = blockPos2.getX();
            o = blockPos2.getZ();
            Direction direction2 = Direction.Type.HORIZONTAL.random(random);
            if (direction2 != direction) {
                r = l - random.nextInt(2) - 1;
                int t = 1 + random.nextInt(3);
                p = 0;

                for (int u = r; u < i && t > 0; --t) {
                    if (u >= 1) {
                        int v = blockPos2.getY() + u;
                        n += direction2.getOffsetX();
                        o += direction2.getOffsetZ();
                        if (this.setLogBlockState(modifiableTestableWorld, random, mutable.set(n, v, o), set, blockBox, branchedTreeFeatureConfig)) {
                            p = v;
                        }
                    }

                    ++u;
                }

                if (p > 0) {
                    BlockPos blockPos4 = new BlockPos(n, p, o);
                    branchedTreeFeatureConfig.foliagePlacer.generate(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, j, k, blockPos4, set2);
                }
            }

            return true;
        }
    }

    public int method_23447(int i, int j, int k, int l) {
        return l == 0 ? 0 : 2;
    }

    public int getRadius(Random random, int i, int j, BranchedTreeFeatureConfig config) {
        return 2 + random.nextInt(1);
    }

    protected boolean method_23451(Random random, int i, int j, int k, int l, int m) {
        return Math.abs(j) == m && Math.abs(l) == m && m > 0;
    }
}
