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

import com.hrznstudio.galacticraft.api.biome.SpaceBiome;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.level.LevelGeneratorType;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonChunkGenerator extends SurfaceChunkGenerator<MoonChunkGeneratorConfig> {
    private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], (fs) -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt((float) (i * i + j * j) + 0.2F);
                fs[i + 2 + (j + 2) * 5] = f;
            }
        }

    });
    private final OctavePerlinNoiseSampler noiseSampler;
    private final boolean amplified;
//    private final ZombieSiegeManager zombieSiegeManager = new ZombieSiegeManager();

    public MoonChunkGenerator(IWorld world, BiomeSource biomeSource, MoonChunkGeneratorConfig config) {
        super(world, biomeSource, 4, 8, 256, config, true);
        this.random.consume(2620);
        this.noiseSampler = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.amplified = world.getLevelProperties().getGeneratorType() == LevelGeneratorType.AMPLIFIED;
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
    protected void sampleNoiseColumn(double[] buffer, int x, int z) {
        this.sampleNoiseColumn(buffer, x, z, 684.4119873046875D, 684.4119873046875D, 8.555149841308594D, 4.277574920654297D, 3, -10);
    }

    @Override
    protected double computeNoiseFalloff(double depth, double scale, int y) {
        double e = ((double) y - (8.5D + depth * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / scale;
        if (e < 0.0D) {
            e *= 4.0D;
        }

        return e;
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

    @Override
    public List<Biome.SpawnEntry> getEntitySpawnList(StructureAccessor structureAccessor, EntityCategory entityCategory, BlockPos pos) {
        if (entityCategory == EntityCategory.MONSTER) {
            if (Feature.PILLAGER_OUTPOST.isApproximatelyInsideStructure(this.world, structureAccessor, pos)) {
                return Feature.PILLAGER_OUTPOST.getMonsterSpawns();
            }
        }
        return super.getEntitySpawnList(structureAccessor, entityCategory, pos);
    }

    @Override
    public void spawnEntities(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        super.spawnEntities(world, spawnMonsters, spawnAnimals);
//        this.zombieSiegeManager.spawn(world, spawnMonsters, spawnAnimals);

    }

    public int getSpawnHeight() {
        return this.world.getSeaLevel() + 1;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    @Override
    public void buildSurface(ChunkRegion chunkRegion, Chunk chunk) {
        super.buildSurface(chunkRegion, chunk);
        if (!chunk.getStructureReferences().containsKey(GalacticraftFeatures.MOON_VILLAGE.getName())) {
            createCraters(chunk, chunkRegion);
        }
    }

    private void createCraters(Chunk chunk, ChunkRegion region) {
        for (int cx = chunk.getPos().x - 2; cx <= chunk.getPos().x + 2; cx++) {
            for (int cz = chunk.getPos().z - 2; cz <= chunk.getPos().z + 2; cz++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (Math.abs(this.randFromPoint(cx << 4 + x, (cz << 4 + z) * 1000)) < this.sampleNoise(x << 4 + x, cz << 4 + z) / 300) {
                            Random random = new Random((cx << 4) + x + ((cz << 4) + z) * 5000);

                            int size;

                            int i = random.nextInt(14 + 8 + 2 + 1);
                            if (i < 1) {
                                size = random.nextInt(30 - 26) + 26;
                            } else if (i < 2) {
                                size = random.nextInt(17 - 13) + 13;
                            } else if (i < 8) {
                                size = random.nextInt(25 - 18) + 18;
                            } else {
                                size = random.nextInt(12 - 8) + 8;
                            }

                            if (region.getBiome(new BlockPos(cx << 4, 65, cz << 4)) instanceof SpaceBiome) {
                                if (((SpaceBiome) region.getBiome(new BlockPos(cx << 4, 65, cz << 4))).hasCraters()) {
                                    if (((SpaceBiome) region.getBiome(new BlockPos(cx << 4, 65, cz << 4))).forceSmallCraters()) {
                                        size = random.nextInt(12 - 8) + 8;
                                    } else if (((SpaceBiome) region.getBiome(new BlockPos(cx << 4, 65, cz << 4))).forceMediumCraters()) {
                                        size = random.nextInt(25 - 18) + 18;
                                    } else if (((SpaceBiome) region.getBiome(new BlockPos(cx << 4, 65, cz << 4))).forceLargeCraters()) {
                                        size = random.nextInt(17 - 13) + 13;
                                    }
                                } else {
                                    break;
                                }
                            }

                            this.makeCrater((cx << 4) + x, (cz << 4) + z, chunk.getPos().x << 4, chunk.getPos().z << 4, size, chunk);
                        }
                    }
                }
            }
        }
    }

    private double randFromPoint(int x, int z) {
        int n;
        n = x + z * 57;
        n = n << 13 ^ n;
        return 1.0D - (n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff) / 1073741824.0;
    }

    private void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double xDev = craterX - (chunkX + x);
                double zDev = craterZ - (chunkZ + z);
                if (xDev * xDev + zDev * zDev < size * size) {
                    xDev /= size;
                    zDev /= size;
                    final double sqrtY = xDev * xDev + zDev * zDev;
                    double yDev = sqrtY * sqrtY * 6;
                    yDev = 5 - yDev;
                    int helper = 0;
                    for (int y = 127; y > 0; y--) {
                        if (!chunk.getBlockState(new BlockPos(x, y, z)).isAir() && helper <= yDev) {
                            chunk.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), false);
                            helper++;
                        }
                        if (helper > yDev) {
                            break;
                        }
                    }
                }
            }
        }
    }
}