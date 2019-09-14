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
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
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
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonChunkGenerator extends SurfaceChunkGenerator<MoonChunkGeneratorConfig> {

    private static final float[] BIOME_WEIGHT_TABLE = SystemUtil.consume(new float[25], (floats) -> {
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
        this.noiseSampler = new OctavePerlinNoiseSampler(this.random, 16);
        this.amplified = iWorld.getLevelProperties().getGeneratorType() == LevelGeneratorType.AMPLIFIED;
    }

    @Override
    public void populateEntities(ChunkRegion chunkRegion) {
        int chunkX = chunkRegion.getCenterChunkX();
        int chunkZ = chunkRegion.getCenterChunkZ();
        Biome biome = chunkRegion.getChunk(chunkX, chunkZ).getBiomeArray()[0];
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setSeed(chunkRegion.getSeed(), chunkX << 4, chunkZ << 4);
        SpawnHelper.populateEntities(chunkRegion, biome, chunkX, chunkZ, chunkRandom);
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
    protected double[] computeNoiseRange(int i, int i2) {
        double[] doubles = new double[2];
        float f1 = 0.0F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        float f4 = this.biomeSource.getBiomeForNoiseGen(i, i2).getDepth();

        for (int i4 = -2; i4 <= 2; ++i4) {
            for (int i5 = -2; i5 <= 2; ++i5) {
                Biome biome = this.biomeSource.getBiomeForNoiseGen(i + i4, i2 + i5);
                float f5 = biome.getDepth();
                float f6 = biome.getScale();
                if (this.amplified && f5 > 0.0F) {
                    f5 = 1.0F + f5 * 2.0F;
                    f6 = 1.0F + f6 * 4.0F;
                }

                float float_7 = BIOME_WEIGHT_TABLE[i4 + 2 + (i5 + 2) * 5] / (f5 + 2.0F);
                if (biome.getDepth() > f4) {
                    float_7 /= 2.0F;
                }

                f1 += f6 * float_7;
                f2 += f5 * float_7;
                f3 += float_7;
            }
        }

        f1 /= f3;
        f2 /= f3;
        f1 = f1 * 0.9F + 0.1F;
        f2 = (f2 * 4.0F - 1.0F) / 8.0F;
        doubles[0] = (double) f2 + this.method_16414(i, i2);
        doubles[1] = f1;
        return doubles;
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
    public List<SpawnEntry> getEntitySpawnList(EntityCategory entityCategory_1, BlockPos blockPos_1) {
        return super.getEntitySpawnList(entityCategory_1, blockPos_1);
    }

    public void spawnEntities(ServerWorld world, boolean boolean_1, boolean boolean_2) {
        this.phantomSpawner.spawn(world, boolean_1, boolean_2);
        this.pillagerSpawner.spawn(world, boolean_1, boolean_2);
    }

    public int getSpawnHeight() {
        return this.world.getSeaLevel() + 1;
    }

    public int getSeaLevel() {
        return super.getSeaLevel();
    }
}
