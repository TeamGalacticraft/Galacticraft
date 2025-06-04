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

package dev.galacticraft.mod.world.gen.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.perlin.NoiseModule;
import dev.galacticraft.api.perlin.generator.Billowed;
import dev.galacticraft.api.perlin.generator.Gradient;
import dev.galacticraft.api.vector.BlockVec3;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.gen.base.MapGenAbandonedBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static dev.galacticraft.impl.internal.fabric.GalacticraftAPI.currentWorldSaveDirectory;
import static dev.galacticraft.mod.world.gen.custom.AsteroidSaveData.saveDataID;


public class AsteroidChunkGenerator extends ChunkGenerator {

    //from WorldAsteroidProvider
    private HashSet<AsteroidData> asteroids = new HashSet<>();
    private boolean dataNotLoaded = true;
    private AsteroidSaveData datafile;
    private double solarMultiplier = -1D;

    private int largeCount = 0;
    private final Random rand;
    private final NoiseModule asteroidDensity;
    private final NoiseModule asteroidTurbulance;
    private final ResourceKey<Level> dimensionKey;

    private final NoiseModule asteroidSkewX;
    private final NoiseModule asteroidSkewY;
    private final NoiseModule asteroidSkewZ;

    private final SpecialAsteroidBlockHandler coreHandler;
    private final SpecialAsteroidBlockHandler shellHandler;
    //micdoodle8 says do not change but I did :)
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 384;
    private static final int CHUNK_SIZE_Z = 16;

    private static final int MAX_ASTEROID_RADIUS = 25;
    private static final int MIN_ASTEROID_RADIUS = 5;

    private static final int MAX_ASTEROID_SKEW = 8;

    //MIN_ASTEROID_Y is -64 + 48
    private static final int MIN_ASTEROID_Y = -16;
    //MAX_ASTEROID_Y is 384 - 64 - 48
    private static final int MAX_ASTEROID_Y = AsteroidChunkGenerator.CHUNK_SIZE_Y - 64 - 48;
    //default is 800
    private static final int ASTEROID_CHANCE = 800;
    private static final int ASTEROID_CORE_CHANCE = 2; //1 / n chance per asteroid
    private static final int ASTEROID_SHELL_CHANCE = 2; //1 / n chance per asteroid

    private static final int MIN_BLOCKS_PER_CHUNK = 50;
    private static final int MAX_BLOCKS_PER_CHUNK = 200;

    private static final int ILMENITE_CHANCE = 400;
    private static final int IRON_CHANCE = 300;
    private static final int ALUMINUM_CHANCE = 250;

    private static final int RANDOM_BLOCK_FADE_SIZE = 32;
    private static final int FADE_BLOCK_CHANCE = 5; //1 / n chance of a block being in the fade zone

    private static final int NOISE_OFFSET_SIZE = 256;

    private static HashSet<BlockVec3> chunksDone = new HashSet<BlockVec3>();
    private int largeAsteroidsLastChunkX;
    private int largeAsteroidsLastChunkZ;
    private final MapGenAbandonedBase dungeonGenerator = new MapGenAbandonedBase();

    public static final MapCodec<AsteroidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biomeSource").forGetter(generator -> generator.biomeSource),
                    ServerLevel.RESOURCE_KEY_CODEC.fieldOf("dimensionKey").forGetter(generator -> generator.dimensionKey),
                    Codec.LONG.fieldOf("par2").forGetter(generator -> 1000L)
            ).apply(instance, AsteroidChunkGenerator::new));
    private final Holder<NoiseGeneratorSettings> settings = null;

    public AsteroidChunkGenerator(BiomeSource biomeSource, ResourceKey<Level> dimensionKey, long par2) {
        super(biomeSource);
        this.dimensionKey = dimensionKey;
        this.rand = new Random(par2);

        this.asteroidDensity = new Billowed(this.rand.nextLong(), 2, 0.25F);
        this.asteroidDensity.setFrequency(.009F);
        this.asteroidDensity.amplitude = 0.6F;

        this.asteroidTurbulance = new Gradient(this.rand.nextLong(), 1, 0.2F);
        this.asteroidTurbulance.setFrequency(.08F);
        this.asteroidTurbulance.amplitude = 0.5F;

        this.asteroidSkewX = new Gradient(this.rand.nextLong(), 1, 1);
        this.asteroidSkewX.amplitude = AsteroidChunkGenerator.MAX_ASTEROID_SKEW;
        this.asteroidSkewX.frequencyX = 0.005F;

        this.asteroidSkewY = new Gradient(this.rand.nextLong(), 1, 1);
        this.asteroidSkewY.amplitude = AsteroidChunkGenerator.MAX_ASTEROID_SKEW;
        this.asteroidSkewY.frequencyY = 0.005F;

        this.asteroidSkewZ = new Gradient(this.rand.nextLong(), 1, 1);
        this.asteroidSkewZ.amplitude = AsteroidChunkGenerator.MAX_ASTEROID_SKEW;
        this.asteroidSkewZ.frequencyZ = 0.005F;

        this.coreHandler = new SpecialAsteroidBlockHandler();
        this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ASTEROID_ROCK_2, 5, 0.3));
        this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ASTEROID_ROCK_1, 7, 0.3));
        this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ASTEROID_ROCK, 11, 0.25));

        //!ConfigManagerAst.disableAluminumGen
        if (true)
            this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ASTEROID_ALUMINUM_ORE, 5, 0.2));
        //!ConfigManagerAsteroids.disableIlmeniteGen
        if (true)
            this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ILMENITE_ORE, 4, 0.15));
        //!ConfigManagerAsteroids.disableIronGen
        if (true)
            this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ASTEROID_IRON_ORE, 3, 0.2));
        //ConfigManagerCore.enableSiliconOreGen
        if (true)
            this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ASTEROID_SILICON_ORE, 2, 0.15));
        //Solid Meteoric Iron - has no config to disable
        this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.METEORIC_IRON_BLOCK, 2, 0.13));
        //Diamond ore - has no config to disable
        this.coreHandler.addBlock(new SpecialAsteroidBlock(Blocks.DIAMOND_ORE, 1, 0.1));

        this.shellHandler = new SpecialAsteroidBlockHandler();
        this.shellHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ASTEROID_ROCK, 1, 0.15));
        this.shellHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ASTEROID_ROCK_1, 3, 0.15));
        this.shellHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.ASTEROID_ROCK_2, 1, 0.15));
        this.shellHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.DENSE_ICE, 1, 0.15));
    }

    private ChunkAccess generateChunkData(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        int heightLimit = chunkAccess.getHeight();
        int chunkX = chunkAccess.getPos().x;
        int chunkZ = chunkAccess.getPos().z;

        this.largeCount = 0;
        final Random random = new Random();
        final int asteroidChance = AsteroidChunkGenerator.ASTEROID_CHANCE;
        final int rangeY = AsteroidChunkGenerator.MAX_ASTEROID_Y - AsteroidChunkGenerator.MIN_ASTEROID_Y;
        final int rangeSize = AsteroidChunkGenerator.MAX_ASTEROID_RADIUS - AsteroidChunkGenerator.MIN_ASTEROID_RADIUS;

        //If asteroid centre is nearby might need to generate some asteroid parts in this chunk
        for (int i = chunkX - 3; i < chunkX + 3; i++) {
            int minX = i * 16;
            int maxX = minX + AsteroidChunkGenerator.CHUNK_SIZE_X;
            for (int k = chunkZ - 3; k < chunkZ + 3; k++) {
                int minZ = k * 16;
                int maxZ = minZ + AsteroidChunkGenerator.CHUNK_SIZE_Z;

                //something about redundant code in gc4's code
                for (int x = minX; x < maxX; x += 2) {
                    for (int z = minZ; z < maxZ; z += 2) {
                        //the next line is called 3136 times per chunk generated apparently? saying something about slow getNoise
                        if (this.randFromPointPos(x, z) < (this.asteroidDensity.getNoise(x, z) + 0.4) / asteroidChance) {
                            random.setSeed(x + z * 3067);
                            int y = random.nextInt(rangeY) + AsteroidChunkGenerator.MIN_ASTEROID_Y;
                            int size = random.nextInt(rangeSize) + AsteroidChunkGenerator.MIN_ASTEROID_RADIUS;

                            //generate the parts of the asteroid which are in this chunk
                            this.generateAsteroid(random, x, y, z, chunkX << 4, chunkZ << 4, size, chunkAccess);
                            this.largeCount++;
                        }
                    }
                }
            }
        }

        return chunkAccess;
    }

    public DimensionDataStorage getDimensionDataStorage(MinecraftServer server) {
        ServerLevel level = server.getLevel(dimensionKey);
        if (level == null) {
            throw new IllegalStateException("ServerLevel for the given dimensionKey does not exist.");
        }
        return level.getDataStorage();
    }

    private void generateAsteroid(Random rand, int asteroidX, int asteroidY, int asteroidZ, int chunkX, int chunkZ, int size, ChunkAccess primer) {
        SpecialAsteroidBlock core = this.coreHandler.getBlock(rand, size);

        SpecialAsteroidBlock shell = null;
        if (rand.nextInt(AsteroidChunkGenerator.ASTEROID_SHELL_CHANCE) == 0) {
            shell = this.shellHandler.getBlock(rand, size);
        }

        //Add to the list of asteroids for external use
        this.addAsteroid(asteroidX, asteroidY, asteroidZ, size, core.index);

        final int xMin = Mth.clamp(Math.max(chunkX, asteroidX - size - AsteroidChunkGenerator.MAX_ASTEROID_SKEW - 2) - chunkX, 0, 16);
        final int zMin = Mth.clamp(Math.max(chunkZ, asteroidZ - size - AsteroidChunkGenerator.MAX_ASTEROID_SKEW - 2) - chunkZ, 0, 16);
        final int yMin = asteroidY - size - AsteroidChunkGenerator.MAX_ASTEROID_SKEW - 2;
        final int yMax = asteroidY + size + AsteroidChunkGenerator.MAX_ASTEROID_SKEW + 2;
        final int xMax = Mth.clamp(Math.min(chunkX + 16, asteroidX + size + AsteroidChunkGenerator.MAX_ASTEROID_SKEW + 2) - chunkX, 0, 16);
        final int zMax = Mth.clamp(Math.min(chunkZ + 16, asteroidZ + size + AsteroidChunkGenerator.MAX_ASTEROID_SKEW + 2) - chunkZ, 0, 16);
        final int xSize = xMax - xMin;
        final int ySize = yMax - yMin;
        final int zSize = zMax - zMin;

        if (xSize <= 0 || ySize <= 0 || zSize <= 0) {
            return;
        }

        final float noiseOffsetX = this.randFromPoint(asteroidX, asteroidY, asteroidZ) * AsteroidChunkGenerator.NOISE_OFFSET_SIZE + chunkX;
        final float noiseOffsetY = this.randFromPoint(asteroidX * 7, asteroidY * 11, asteroidZ * 13) * AsteroidChunkGenerator.NOISE_OFFSET_SIZE;
        final float noiseOffsetZ = this.randFromPoint(asteroidX * 17, asteroidY * 23, asteroidZ * 29) * AsteroidChunkGenerator.NOISE_OFFSET_SIZE + chunkZ;
        this.setOtherAxisFrequency(1F / (size * 2F / 2F));

        float[] sizeXArray = new float[ySize * zSize];
        float[] sizeZArray = new float[xSize * ySize];
        float[] sizeYArray = new float[xSize * zSize];

        for (int x = 0; x < xSize; x++) {
            int xx = x * zSize;
            float xxx = x + noiseOffsetX;
            for (int z = 0; z < zSize; z++) {
                sizeYArray[xx + z] = this.asteroidSkewY.getNoise(xxx, z + noiseOffsetZ);
            }
        }

        AsteroidData asteroidData = new AsteroidData(sizeYArray, xMin, zMin, xMax, zMax, zSize, size, asteroidX, asteroidY, asteroidZ);
        this.largeAsteroidsLastChunkX = chunkX;
        this.largeAsteroidsLastChunkZ = chunkZ;

        for (int y = 0; y < ySize; y++) {
            int yy = y * zSize;
            float yyy = y + noiseOffsetY;
            for (int z = 0; z < zSize; z++) {
                sizeXArray[yy + z] = this.asteroidSkewX.getNoise(yyy, z + noiseOffsetZ);
            }
        }

        for (int x = 0; x < xSize; x++) {
            int xx = x * ySize;
            float xxx = x + noiseOffsetX;
            for (int y = 0; y < ySize; y++) {
                sizeZArray[xx + y] = this.asteroidSkewZ.getNoise(xxx, y + noiseOffsetY);
            }
        }

        double shellThickness = 0;
        int terrainY = 0;
        int terrainYY = 0;

        BlockState asteroidShell = null;
        if (shell != null) {
            asteroidShell = shell.block.defaultBlockState();
            shellThickness = 1.0 - shell.thickness;
        }

        BlockState asteroidCore = core.block.defaultBlockState();
        BlockState asteroidRock0 = GCBlocks.ASTEROID_ROCK.defaultBlockState();
        BlockState asteroidRock1 = GCBlocks.ASTEROID_ROCK_1.defaultBlockState();

        for (int x = xMax - 1; x >= xMin; x--) {
            int indexXY = (x - xMin) * ySize - yMin;
            int indexXZ = (x - xMin) * zSize - zMin;
            int distanceX = asteroidX - (x + chunkX);
            int indexBaseX = x * AsteroidChunkGenerator.CHUNK_SIZE_Y << 4;
            float xx = x + chunkX;

            for (int z = zMin; z < zMax; z++) {
                float sizeY = size + sizeYArray[indexXZ + z];
                sizeY *= sizeY;
                int distanceZ = asteroidZ - (z + chunkZ);
                int indexBase = indexBaseX | z * AsteroidChunkGenerator.CHUNK_SIZE_Y;
                float zz = z + chunkZ;

                for (int y = yMin; y < yMax; y++) {
                    float dSizeX = distanceX / (size + sizeXArray[(y - yMin) * zSize + z - zMin]);
                    float dSizeZ = distanceZ / (size + sizeZArray[indexXY + y]);
                    dSizeX *= dSizeX;
                    dSizeZ *= dSizeZ;
                    int distanceY = asteroidY - y;
                    distanceY *= distanceY;
                    float distance = dSizeX + distanceY / sizeY + dSizeZ;
                    float distanceAbove = distance;
                    distance += this.asteroidTurbulance.getNoise(xx, y, zz);

                    if (distance <= 1) {
                        int index = indexBase | y;
                        if (distance <= core.thickness) {
                            if (rand.nextBoolean()) {
                                primer.setBlockState(new BlockPos(x, y, z), asteroidCore, false);
//                              blockArray[index] = core.block;
//                              metaArray[index] = core.meta;
                            } else {
                                primer.setBlockState(new BlockPos(x, y, z), asteroidRock0, false);
//                              blockArray[index] = this.ASTEROID_STONE;
//                              metaArray[index] = this.ASTEROID_STONE_META_0;
                            }
                        } else if (shell != null && distance >= shellThickness) {
                            primer.setBlockState(new BlockPos(x, y, z), asteroidShell, false);
//                            blockArray[index] = shell.block;
//                            metaArray[index] = shell.meta;
                        } else {
                            primer.setBlockState(new BlockPos(x, y, z), asteroidRock1, false);
//                            blockArray[index] = this.ASTEROID_STONE;
//                            metaArray[index] = this.ASTEROID_STONE_META_1;
                        }
                    }
                }
            }
        }
    }

    private final int getTerrainHeightFor(float yMod, int asteroidY, int asteroidSize) {
        return (int) (asteroidY - asteroidSize / 4 + yMod * 1.5F);
    }

    private final int getTerrainHeightAt(int x, int z, float[] yModArray, int xMin, int zMin, int zSize, int asteroidY, int asteroidSize) {
        final int index = (x - xMin) * zSize - zMin;
        if (index < yModArray.length && index >= 0) {
            final float yMod = yModArray[index];
            return this.getTerrainHeightFor(yMod, asteroidY, asteroidSize);
        }
        return 1;
    }

    @Override
    protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) {
        int chunkX = chunkAccess.getPos().x;
        int chunkZ = chunkAccess.getPos().z;

        int x = chunkX << 4;
        int z = chunkZ << 4;
        if (!AsteroidChunkGenerator.chunksDone.add(new BlockVec3(x, 0, z))) {
            return;
        }

//        BlockFalling.fallInstantly = true;
//        this.world.getBiome(new BlockPos(x + 16, 0, z + 16));
//        BlockFalling.fallInstantly = false;

        this.rand.setSeed(seed);
        long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        long var9 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(chunkX * var7 + chunkZ * var9 ^ seed);

        //50:50 chance to include small blocks each chunk
        if (this.rand.nextBoolean()) {
            double density = this.asteroidDensity.getNoise(chunkX * 16, chunkZ * 16) * 0.54;
            double numOfBlocks = Mth.clamp(this.randFromPoint(chunkX, chunkZ), 0.4, 1) * AsteroidChunkGenerator.MAX_BLOCKS_PER_CHUNK * density + AsteroidChunkGenerator.MIN_BLOCKS_PER_CHUNK;
            int y0 = this.rand.nextInt(2);
            Block block;
            int yRange = AsteroidChunkGenerator.MAX_ASTEROID_Y - AsteroidChunkGenerator.MIN_ASTEROID_Y;
            x += 4;
            z += 4;

            for (int i = 0; i < numOfBlocks; i++) {
                int y = this.rand.nextInt(yRange) + AsteroidChunkGenerator.MIN_ASTEROID_Y;

                //50:50 chance vertically as well
                if (y0 == (y / 16) % 2) {
                    int px = x + this.rand.nextInt(AsteroidChunkGenerator.CHUNK_SIZE_X);
                    int pz = z + this.rand.nextInt(AsteroidChunkGenerator.CHUNK_SIZE_Z);

                    block = GCBlocks.ASTEROID_ROCK;

                    if (this.rand.nextInt(ILMENITE_CHANCE) == 0) {
                        block = GCBlocks.ILMENITE_ORE;
                    } else if (this.rand.nextInt(IRON_CHANCE) == 0) {
                        block = GCBlocks.MARS_IRON_ORE;
                    } else if (this.rand.nextInt(ALUMINUM_CHANCE) == 0) {
                        block = GCBlocks.ALUMINUM_ORE;
                    }

                    chunkAccess.setBlockState(new BlockPos(px, y, pz), block.defaultBlockState(), false);
                    int count = 9;
                    if (!(chunkAccess.getBlockState(new BlockPos(px - 1, y, pz)).isAir())) {
                        count = 1;
                    } else if (!(chunkAccess.getBlockState(new BlockPos(px - 2, y, pz)).isAir())) {
                        count = 3;
                    } else if (!(chunkAccess.getBlockState(new BlockPos(px - 3, y, pz)).isAir())) {
                        count = 5;
                    } else if (!(chunkAccess.getBlockState(new BlockPos(px - 4, y, pz)).isAir())) {
                        count = 7;
                    }
                    //world.setLightFor(EnumSkyBlock.BLOCK, new BlockPos(px - (count > 1 ? 1 : 0), y, pz), count);
                }
            }
        }

        if (this.largeAsteroidsLastChunkX != chunkX || this.largeAsteroidsLastChunkZ != chunkZ) {
            this.generateChunkData(null, null, null, chunkAccess);
        }

        this.rand.setSeed(chunkX * var7 + chunkZ * var9 ^ seed);

//         // Update all block lighting
//         for (int xx = 0; xx < 16; xx++) {
//             int xPos = x + xx;
//             for (int zz = 0; zz < 16; zz++) {
//                 int zPos = z + zz;

//                 // Asteroid at min height 48, size 20, can't have lit blocks below 16
//                 for (int y = 16; y < 240; y++) {
// //LIGHTTEMP                    world.checkLightFor(EnumSkyBlock.BLOCK, new BlockPos(xPos, y, zPos));
//                 }
//             }
//         }

        //this.dungeonGenerator.generateStructure(chunkAccess, this.rand, new ChunkPos(chunkX, chunkZ));
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunkAccess) {

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {

    }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState noiseConfig, StructureManager structureAccessor, ChunkAccess chunk) {
        CompletableFuture<ChunkAccess> future = new CompletableFuture<>();

        Minecraft.getInstance().submit(() -> {
            try {
                // This operation will now run on Minecraft's main thread
                ChunkAccess result = generateChunkData(blender, noiseConfig, structureAccessor, chunk);
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public void resetBase() {
        //this.dungeonGenerator.reset();
    }


    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int i, int j, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int i, int j, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return null;
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {

    }

    private float randFromPointPos(int x, int z) {
        int n = x + z * 57;
        n ^= n << 13;
        n = n * (n * n * 15731 + 789221) + 1376312589 & 0x3fffffff;
        return 1.0F - n / 1073741824.0F;
    }

    private final void setOtherAxisFrequency(float frequency) {
        this.asteroidSkewX.frequencyY = frequency;
        this.asteroidSkewX.frequencyZ = frequency;

        this.asteroidSkewY.frequencyX = frequency;
        this.asteroidSkewY.frequencyZ = frequency;

        this.asteroidSkewZ.frequencyX = frequency;
        this.asteroidSkewZ.frequencyY = frequency;
    }

    private float randFromPoint(int x, int y, int z) {
        int n = x + z * 57 + y * 571;
        n ^= n << 13;
        n = n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff;
        return 1.0F - n / 1073741824.0F;
    }

    private float randFromPoint(int x, int z) {
        int n = x + z * 57;
        n ^= n << 13;
        n = n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff;
        return 1.0F - n / 1073741824.0F;
    }

    public void addAsteroid(int x, int y, int z, int size, int core) {
        AsteroidData coords = new AsteroidData(x, y, z, size, core);
        if (!this.asteroids.contains(coords)) {
            if (this.dataNotLoaded) {
                this.loadAsteroidSavedData();
            }
            if (!this.asteroids.contains(coords)) {
                this.addToNBT(this.datafile.datacompound, coords);
                this.asteroids.add(coords);
            }
        }
    }

    private void addToNBT(CompoundTag nbt, AsteroidData coords) {
        ListTag coordList = nbt.getList("coords", 10);
        CompoundTag tag = new CompoundTag();
        coords.writeToNBT(tag);
        coordList.add(tag);
        nbt.put("coords", coordList);
        this.datafile.setDirty();
        Path dataPath = Path.of(currentWorldSaveDirectory.toString(), saveDataID);
        Path dataFile = dataPath.resolve("data.dat");
        this.setData(dataFile, datafile);
    }


    private void writeToNBT(CompoundTag nbt) {
        ListTag coordList = new ListTag();
        for (AsteroidData coords : this.asteroids) {
            CompoundTag tag = new CompoundTag();
            coords.writeToNBT(tag);
            coordList.add(tag);
        }
        nbt.put("coords", coordList);
        this.datafile.setDirty();
        Path dataPath = Path.of(currentWorldSaveDirectory.toString(), saveDataID);
        Path dataFile = dataPath.resolve("data.dat");
        this.setData(dataFile, datafile);
    }

    private static class AsteroidData {
        public float[] sizeYArray;
        public int xMinArray;
        public int zMinArray;
        public int xMax;
        public int zMax;
        public int zSizeArray;
        public int asteroidSizeArray;
        public int asteroidXArray;
        public int asteroidYArray;
        public int asteroidZArray;

        public AsteroidData(float[] sizeYArray2, int xMin, int zMin, int xmax, int zmax, int zSize, int size, int asteroidX, int asteroidY, int asteroidZ) {
            this.sizeYArray = sizeYArray2.clone();
            this.xMinArray = xMin;
            this.zMinArray = zMin;
            this.xMax = xmax;
            this.zMax = zmax;
            this.zSizeArray = zSize;
            this.asteroidSizeArray = size;
            this.asteroidXArray = asteroidX;
            this.asteroidYArray = asteroidY;
            this.asteroidZArray = asteroidZ;
        }

        protected BlockVec3 centre;
        protected int sizeAndLandedFlag = 15;
        protected int coreAndSpawnedFlag = -2;

        public AsteroidData(int x, int y, int z) {
            this.centre = new BlockVec3(x, y, z);
        }

        public AsteroidData(int x, int y, int z, int size, int core) {
            this.centre = new BlockVec3(x, y, z);
            this.sizeAndLandedFlag = size;
            this.coreAndSpawnedFlag = core;
        }

        public AsteroidData(BlockVec3 bv) {
            this.centre = bv;
        }

        @Override
        public int hashCode() {
            if (this.centre != null) {
                return this.centre.hashCode();
            } else {
                return 0;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof AsteroidData) {
                BlockVec3 vector = ((AsteroidData) o).centre;
                return this.centre.x == vector.x && this.centre.y == vector.y && this.centre.z == vector.z;
            }

            if (o instanceof BlockVec3) {
                BlockVec3 vector = (BlockVec3) o;
                return this.centre.x == vector.x && this.centre.y == vector.y && this.centre.z == vector.z;
            }

            return false;
        }

        public CompoundTag writeToNBT(CompoundTag tag) {
            tag.putInt("x", this.centre.x);
            tag.putInt("y", this.centre.y);
            tag.putInt("z", this.centre.z);
            tag.putInt("coreAndFlag", this.coreAndSpawnedFlag);
            tag.putInt("sizeAndFlag", this.sizeAndLandedFlag);

            return tag;
        }

        public static AsteroidData readFromNBT(CompoundTag tag) {
            BlockVec3 tempVector = new BlockVec3();
            tempVector.x = tag.getInt("x");
            tempVector.y = tag.getInt("y");
            tempVector.z = tag.getInt("z");

            AsteroidData roid = new AsteroidData(tempVector);
            if (tag.hasUUID("coreAndFlag")) {
                roid.coreAndSpawnedFlag = tag.getInt("coreAndFlag");
            }
            if (tag.hasUUID("sizeAndFlag")) {
                roid.sizeAndLandedFlag = tag.getInt("sizeAndFlag");
            }

            return roid;
        }
    }

    private int getIndex(int x, int y, int z) {
        return x * AsteroidChunkGenerator.CHUNK_SIZE_Y * 16 | z * AsteroidChunkGenerator.CHUNK_SIZE_Y | y;
    }

    public void loadAsteroidSavedData() {
        Path dataPath = Path.of(currentWorldSaveDirectory.toString(), saveDataID);
        Path dataFile = dataPath.resolve("data.dat");

        this.datafile = this.loadData(dataFile);

        if (this.datafile == null) {
            if (!Files.exists(dataFile)) {
                try {
                    Files.createDirectories(dataPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Files.createFile(dataFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.datafile = new AsteroidSaveData("");
            this.writeToNBT(this.datafile.datacompound);
            this.setData(dataFile, this.datafile);
        } else {
            this.readFromNBT(this.datafile.datacompound);
        }

        this.dataNotLoaded = false;

    }

    public void setData(Path filePath, AsteroidSaveData data) {
        try {
            NbtIo.write(data.datacompound, filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AsteroidSaveData loadData(Path filePath) {
        if (Files.exists(filePath)) {
            CompoundTag tag;
            try {
                tag = NbtIo.read(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            AsteroidSaveData saveData = new AsteroidSaveData("");
            saveData.datacompound = tag;
            return saveData;
        } else {
            return null;
        }
    }


    private void readFromNBT(CompoundTag nbt) {
        ListTag coordList = nbt.getList("coords", 10);
        if (coordList.size() > 0) {
            for (int j = 0; j < coordList.size(); j++) {
                CompoundTag tag1 = coordList.getCompound(j);

                if (tag1 != null) {
                    this.asteroids.add(AsteroidData.readFromNBT(tag1));
                }
            }
        }
    }

    public BlockVec3 isLargeAsteroidAt(int x0, int z0) {
        int xToCheck;
        int zToCheck;
        for (int i0 = 0; i0 <= 32; i0++) {
            for (int i1 = -i0; i1 <= i0; i1++) {
                xToCheck = (x0 >> 4) + i0;
                zToCheck = (z0 >> 4) + i1;

                if (isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return new BlockVec3(xToCheck * 16, 0, zToCheck * 16);
                }

                xToCheck = (x0 >> 4) + i0;
                zToCheck = (z0 >> 4) - i1;

                if (isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return new BlockVec3(xToCheck * 16, 0, zToCheck * 16);
                }

                xToCheck = (x0 >> 4) - i0;
                zToCheck = (z0 >> 4) + i1;

                if (isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return new BlockVec3(xToCheck * 16, 0, zToCheck * 16);
                }

                xToCheck = (x0 >> 4) - i0;
                zToCheck = (z0 >> 4) - i1;

                if (isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return new BlockVec3(xToCheck * 16, 0, zToCheck * 16);
                }
            }
        }

        return null;
    }

    private boolean isLargeAsteroidAt0(int x0, int z0) {
        for (int x = x0; x < x0 + AsteroidChunkGenerator.CHUNK_SIZE_X; x += 2) {
            for (int z = z0; z < z0 + AsteroidChunkGenerator.CHUNK_SIZE_Z; z += 2) {
                if ((Math.abs(this.randFromPoint(x, z)) < (this.asteroidDensity.getNoise(x, z) + 0.4) / AsteroidChunkGenerator.ASTEROID_CHANCE)) {
                    return true;
                }
            }
        }

        return false;
    }

}
