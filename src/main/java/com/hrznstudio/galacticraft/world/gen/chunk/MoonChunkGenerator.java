package com.hrznstudio.galacticraft.world.gen.chunk;

import com.hrznstudio.galacticraft.api.biome.SpaceBiome;
import com.hrznstudio.galacticraft.world.biome.source.MoonBiomeSource;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class MoonChunkGenerator extends SurfaceChunkGenerator<MoonChunkGeneratorConfig> {
    private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], (array) -> {
        for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
                array[i + 2 + (j + 2) * 5] = f;
            }
        }

    });

    private static final Method SAMPLE_NOISE = getNoiseMethod(); //todo find a better way to do this

    private final OctavePerlinNoiseSampler depthNoiseSampler;
//    private final PhantomSpawner phantomSpawner = new PhantomSpawner();
//    private final PillagerSpawner pillagerSpawner = new PillagerSpawner();
//    private final CatSpawner catSpawner = new CatSpawner();
//    private final ZombieSiegeManager zombieSiegeManager = new ZombieSiegeManager();
    private final MoonChunkGeneratorConfig generatorConfig;

    public MoonChunkGenerator(MoonBiomeSource biomeSource, long seed, MoonChunkGeneratorConfig config) {
        super(biomeSource, seed, config, 4, 8, 256, true);
        this.generatorConfig = config;
        this.random.consume(4822);
        this.depthNoiseSampler = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
    }

    @Environment(EnvType.CLIENT)
    public ChunkGenerator create(long seed) {
        return new MoonChunkGenerator((MoonBiomeSource) this.biomeSource.create(seed), seed, this.generatorConfig);
    }

    public void populateEntities(ChunkRegion region) {
        int i = region.getCenterChunkX();
        int j = region.getCenterChunkZ();
        Biome biome = region.getBiome((new ChunkPos(i, j)).getCenterBlockPos());
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setPopulationSeed(region.getSeed(), i << 4, j << 4);
        SpawnHelper.populateEntities(region, biome, i, j, chunkRandom);
    }

    protected void sampleNoiseColumn(double[] buffer, int x, int z) {
        this.sampleNoiseColumn(buffer, x, z, 684.4119873046875D, 684.4119873046875D, 8.555149841308594D, 4.277574920654297D, 3, -10);
    }

    protected double computeNoiseFalloff(double depth, double scale, int y) {
        double e = ((double)y - (8.5D + depth * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / scale;
        if (e < 0.0D) {
            e *= 4.0D;
        }

        return e;
    }

    protected double[] computeNoiseRange(int x, int z) {
        double[] ds = new double[2];
        float f = 0.0F;
        float g = 0.0F;
        float h = 0.0F;
        int j = this.getSeaLevel();
        float k = this.biomeSource.getBiomeForNoiseGen(x, j, z).getDepth();

        for(int l = -2; l <= 2; ++l) {
            for(int m = -2; m <= 2; ++m) {
                Biome biome = this.biomeSource.getBiomeForNoiseGen(x + l, j, z + m);
                float n = biome.getDepth();
                float o = biome.getScale();

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
        ds[0] = (double)g + this.sampleDepthNoise(x, z);
        ds[1] = f;
        return ds;
    }

    private double sampleDepthNoise(int x, int y) {
        double d = this.depthNoiseSampler.sample(x * 200, 10.0D, y * 200, 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
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

    public List<Biome.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        if (group == SpawnGroup.MONSTER) {
            if (Feature.PILLAGER_OUTPOST.isApproximatelyInsideStructure(accessor, pos)) {
                return Feature.PILLAGER_OUTPOST.getMonsterSpawns();
            }
        }

        return super.getEntitySpawnList(biome, accessor, group, pos);
    }

    public void spawnEntities(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
//        this.phantomSpawner.spawn(world, spawnMonsters, spawnAnimals);
//        this.pillagerSpawner.spawn(world, spawnMonsters, spawnAnimals);
//        this.catSpawner.spawn(world, spawnMonsters, spawnAnimals);
//        this.zombieSiegeManager.spawn(world, spawnMonsters, spawnAnimals);
    }

    @Override
    public void buildSurface(ChunkRegion region, Chunk chunk) {
        super.buildSurface(region, chunk);
        if (!chunk.getStructureReferences().containsKey(GalacticraftFeatures.MOON_VILLAGE.getName())) {
            createCraters(chunk, region);
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

    private static Method getNoiseMethod() {
        try {
            Method method = SurfaceChunkGenerator.class.getDeclaredMethod("sampleNoise", int.class, int.class, int.class, double.class, double.class, double.class, double.class);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            try {
                //noinspection JavaReflectionMemberAccess
                Method method = SurfaceChunkGenerator.class.getDeclaredMethod("method_16411", int.class, int.class, int.class, double.class, double.class, double.class, double.class);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ex) {
                ex.addSuppressed(e);
                RuntimeException exception = new RuntimeException("Failed to get noise method");
                exception.addSuppressed(ex);
                throw exception;
            }
        }
    }

    private double sampleNoise(int x, int y) {
        double d = 0;
        try {
            d = ((double)SAMPLE_NOISE.invoke(this, x * 200, 10.0D, y * 200, 1.0D, 0.0D, true)) * 65535.0D / 8000.0D;
        } catch (IllegalAccessException | InvocationTargetException e) {
            RuntimeException exception = new RuntimeException("Failed to get noise.");
            exception.addSuppressed(e);
        }
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

    public int getSeaLevel() {
        return 0;
    }
}
