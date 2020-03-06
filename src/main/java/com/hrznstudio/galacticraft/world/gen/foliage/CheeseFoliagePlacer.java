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

package com.hrznstudio.galacticraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

import java.util.Random;
import java.util.Set;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CheeseFoliagePlacer extends FoliagePlacer {
    public CheeseFoliagePlacer(int i, int j) {
        super(i, j, GalacticraftFoliagePlacerTypes.CHEESE_FOLIAGE_PLACER_TYPE);
    }

    public <T> CheeseFoliagePlacer(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
    }

    @Override
    public void generate(ModifiableTestableWorld world, Random random, BranchedTreeFeatureConfig config, int i, int j, int k, BlockPos pos, Set<BlockPos> positions) {
        generate(world, random, config, i, pos, 0, k, positions);
        generate(world, random, config, i, pos, 1, 1, positions);
        BlockPos blockPos = pos.up();

        int n;
        for (n = -1; n <= 1; ++n) {
            for (int m = -1; m <= 1; ++m) {
                this.method_23450(world, random, blockPos.add(n, 0, m), config, positions);
            }
        }

        for (n = 2; n <= k - 1; ++n) {
            this.method_23450(world, random, blockPos.east(n), config, positions);
            this.method_23450(world, random, blockPos.west(n), config, positions);
            this.method_23450(world, random, blockPos.south(n), config, positions);
            this.method_23450(world, random, blockPos.north(n), config, positions);
        }

    }

    @Override
    public void generate(ModifiableTestableWorld modifiableTestableWorld, Random random, BranchedTreeFeatureConfig branchedTreeFeatureConfig, int i, BlockPos blockPos, int j, int k, Set<BlockPos> set) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int l = -k; l <= k; ++l) {
            for (int m = -k; m <= k; ++m) {
                if (!this.method_23451(random, i, l, j, m, k)) {
                    mutable.set(l + blockPos.getX(), j + blockPos.getY(), m + blockPos.getZ());
                    this.method_23450(modifiableTestableWorld, random, mutable, branchedTreeFeatureConfig, set);
                }
            }
        }

    }

    @Override
    public int getRadius(Random random, int i, int j, BranchedTreeFeatureConfig config) {
        return this.radius + random.nextInt(this.randomRadius + 1);
    }

    @Override
    protected boolean method_23451(Random random, int i, int j, int k, int l, int m) {
        return Math.abs(j) == m && Math.abs(l) == m && m > 0;
    }

    @Override
    public int method_23447(int i, int j, int k, int l) {
        return l == 0 ? 0 : 2;
    }
}
