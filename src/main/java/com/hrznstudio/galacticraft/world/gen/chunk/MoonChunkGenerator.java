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

package com.hrznstudio.galacticraft.world.gen.chunk;

import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.IWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnEntry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonChunkGenerator extends SurfaceChunkGenerator<MoonChunkGeneratorConfig> {

    private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], (floats) -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt((float) (i * i + j * j) + 0.2F);
                floats[i + 2 + (j + 2) * 5] = f;
            }
        }

    });
    private final OctavePerlinNoiseSampler noiseSampler;
    private final boolean amplified;
    private final PhantomSpawner phantomSpawner = new PhantomSpawner();
    private final PillagerSpawner pillagerSpawner = new PillagerSpawner();

    public MoonChunkGenerator(IWorld iWorld, BiomeSource biomeSource, MoonChunkGeneratorConfig chunkGenConfig) {
        super(iWorld, biomeSource, 4, 8, 256, chunkGenConfig, true);
        this.random.consume(2620);
        this.noiseSampler = new OctavePerlinNoiseSampler(this.random, Arrays.asList(15, 0));
        this.amplified = iWorld.getLevelProperties().getGeneratorType() == LevelGeneratorType.AMPLIFIED;
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        int i = region.getCenterChunkX();
        int j = region.getCenterChunkZ();
        Biome biome = region.getBiome((new ChunkPos(i, j)).getCenterBlockPos());
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setSeed(region.getSeed());
        SpawnHelper.populateEntities(region, biome, i, j, chunkRandom);
    }

    @Override
    protected void sampleNoiseColumn(double[] doubles, int i, int i2) {
        this.sampleNoiseColumn(doubles, i, i2, 684.4119873046875D, 684.4119873046875D, 8.555149841308594D, 4.277574920654297D, 3, -10);
    }

    @Override
    protected double computeNoiseFalloff(double d, double double_2, int i) {
        double d4 = ((double) i - (8.5D + d * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / double_2;
        if (d4 < 0.0D) {
            d4 *= 4.0D;
        }

        return d4;
    }

    @Override
    public MoonChunkGeneratorConfig getConfig() {
        return this.config;
    }

    @Override
    protected double[] computeNoiseRange(int x, int z) {
        double[] ds = new double[2];
        float f = 0.0F;
        float g = 0.0F;
        float h = 0.0F;
        int j = this.getSeaLevel();
        float k = this.biomeSource.getBiomeForNoiseGen(x, j, z).getDepth();

        for (int l = -2; l <= 2; ++l) {
            for (int m = -2; m <= 2; ++m) {
                Biome biome = this.biomeSource.getBiomeForNoiseGen(x + l, j, z + m);
                float n = biome.getDepth();
                float o = biome.getScale();
                if (this.amplified && n > 0.0F) {
                    n = 1.0F + n * 2.0F;
                    o = 1.0F + o * 4.0F;
                }

                float p = BIOME_WEIGHT_TABLE[l + 2 + (m + 2) * 5] / (n + 2.0F);
                if (biome.getDepth() > k) {
                    p /= 2.0F;
                }

                f += o * p;
                g += n * p;
                h += p;
            }
        }

        f /= h;
        g /= h;
        f = f * 0.9F + 0.1F;
        g = (g * 4.0F - 1.0F) / 8.0F;
        ds[0] = (double) g + this.sampleNoise(x, z);
        ds[1] = f;
        return ds;
    }

    private double sampleNoise(int x, int y) {
        double d = this.noiseSampler.sample(x * 200, 10.0D, y * 200, 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
        if (d < 0.0D) {
            d = -d * 0.3D;
        }

        d = d * 3.0D - 2.0D;
        if (d < 0.0D) {
            d /= 28.0D;
        } else {
            if (d > 1.0D) {
                d = 1.0D;
            }

            d /= 40.0D;
        }

        return d;
    }

    private double method_16414(int i, int i2) {
        double d = this.noiseSampler.sample(i * 200, 10.0D, i2 * 200, 1.0D, 0.0D, true) / 8000.0D;
        if (d < 0.0D) {
            d = -d * 0.3D;
        }

        d = d * 3.0D - 2.0D;
        if (d < 0.0D) {
            d /= 28.0D;
        } else {
            if (d > 1.0D) {
                d = 1.0D;
            }

            d /= 40.0D;
        }

        return d;
    }

    @Override
    public List<SpawnEntry> getEntitySpawnList(StructureAccessor accessor, EntityCategory entityCategory_1, BlockPos blockPos_1) {
        return super.getEntitySpawnList(accessor, entityCategory_1, blockPos_1);
    }

    public void spawnEntities(ServerWorld world, boolean boolean_1, boolean boolean_2) {
        this.phantomSpawner.spawn(world, boolean_1, boolean_2);
        this.pillagerSpawner.spawn(world, boolean_1, boolean_2);
    }

    public int getSpawnHeight() {
        return this.world.getSeaLevel() + 1;
    }

}
