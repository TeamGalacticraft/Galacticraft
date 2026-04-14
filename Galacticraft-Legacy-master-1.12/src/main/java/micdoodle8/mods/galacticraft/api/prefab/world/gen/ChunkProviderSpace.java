/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.prefab.world.gen;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

import net.minecraftforge.event.ForgeEventFactory;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.perlin.generator.GradientNoise;
import micdoodle8.mods.galacticraft.core.world.gen.EnumCraterSize;

/**
 * Do not include this prefab class in your released mod download.
 */
public abstract class ChunkProviderSpace extends ChunkProviderBase
{

    protected final Random rand;

    private final GradientNoise noiseGen1;
    private final GradientNoise noiseGen2;
    private final GradientNoise noiseGen3;
    private final GradientNoise noiseGen4;
    private final GradientNoise noiseGen5;
    private final GradientNoise noiseGen6;
    private final GradientNoise noiseGen7;

    protected final World world;

    private Biome[] biomesForGeneration = this.getBiomesForGeneration();

    private final double TERRAIN_HEIGHT_MOD = this.getHeightModifier();
    private final double SMALL_FEATURE_HEIGHT_MOD = this.getSmallFeatureHeightModifier();
    private final double MOUNTAIN_HEIGHT_MOD = this.getMountainHeightModifier();
    private final double VALLEY_HEIGHT_MOD = this.getValleyHeightModifier();
    private final int CRATER_PROB = this.getCraterProbability();

    // DO NOT CHANGE
    private final int MID_HEIGHT = this.getSeaLevel();
    private static final int CHUNK_SIZE = 16;
    private static final double MAIN_FEATURE_FILTER_MOD = 4;
    private static final double LARGE_FEATURE_FILTER_MOD = 8;
    private static final double SMALL_FEATURE_FILTER_MOD = 8;

    private List<MapGenBaseMeta> worldGenerators;

    
    
    public ChunkProviderSpace(World world, long seed, boolean mapFeaturesEnabled)
    {
        this.world = world;
        this.rand = new Random(seed);
        this.noiseGen1 = new GradientNoise(this.rand.nextLong(), 4, 0.25D);
        this.noiseGen2 = new GradientNoise(this.rand.nextLong(), 4, 0.25D);
        this.noiseGen3 = new GradientNoise(this.rand.nextLong(), 4, 0.25D);
        this.noiseGen4 = new GradientNoise(this.rand.nextLong(), 2, 0.25D);
        this.noiseGen5 = new GradientNoise(this.rand.nextLong(), 1, 0.25D);
        this.noiseGen6 = new GradientNoise(this.rand.nextLong(), 1, 0.25D);
        this.noiseGen7 = new GradientNoise(this.rand.nextLong(), 1, 0.25D);
    }

    public void generateTerrain(int chunkX, int chunkZ, ChunkPrimer primer)
    {
        this.noiseGen1.setFrequencyAll(0.014999999664723873D);
        this.noiseGen2.setFrequencyAll(0.0030000000474999913D);
        this.noiseGen3.setFrequencyAll(0.01D);
        this.noiseGen4.setFrequencyAll(0.01D);
        this.noiseGen5.setFrequencyAll(0.01D);
        this.noiseGen6.setFrequencyAll(0.0010000000474974513D);
        this.noiseGen7.setFrequencyAll(0.004999999888241291D);

        for (int x = 0; x < CHUNK_SIZE; x++)
        {
            for (int z = 0; z < CHUNK_SIZE; z++)
            {
                final double baseHeight = this.noiseGen1.evalNoise(chunkX * 16 + x, chunkZ * 16 + z) * this.TERRAIN_HEIGHT_MOD;
                final double smallHillHeight = this.noiseGen2.evalNoise(chunkX * 16 + x, chunkZ * 16 + z) * this.SMALL_FEATURE_HEIGHT_MOD;
                double mountainHeight = Math.abs(this.noiseGen3.evalNoise(chunkX * 16 + x, chunkZ * 16 + z));
                double valleyHeight = Math.abs(this.noiseGen4.evalNoise(chunkX * 16 + x, chunkZ * 16 + z));
                final double featureFilter = this.noiseGen5.evalNoise(chunkX * 16 + x, chunkZ * 16 + z) * MAIN_FEATURE_FILTER_MOD;
                final double largeFilter = this.noiseGen6.evalNoise(chunkX * 16 + x, chunkZ * 16 + z) * LARGE_FEATURE_FILTER_MOD;
                final double smallFilter = this.noiseGen7.evalNoise(chunkX * 16 + x, chunkZ * 16 + z) * SMALL_FEATURE_FILTER_MOD - 0.5;
                mountainHeight = this.lerp(smallHillHeight, mountainHeight * this.MOUNTAIN_HEIGHT_MOD, this.fade(this.clamp(mountainHeight * 2, 0, 1)));
                valleyHeight = this.lerp(smallHillHeight, valleyHeight * this.VALLEY_HEIGHT_MOD - this.VALLEY_HEIGHT_MOD + 9, this.fade(this.clamp((valleyHeight + 2) * 4, 0, 1)));

                double yDev = this.lerp(valleyHeight, mountainHeight, this.fade(largeFilter));
                yDev = this.lerp(smallHillHeight, yDev, smallFilter);
                yDev = this.lerp(baseHeight, yDev, featureFilter);

                for (int y = 0; y < 255; y++)
                {
                    if (y < this.MID_HEIGHT + yDev)
                    {
                        primer.setBlockState(x, y, z, this.getStoneBlock().getBlock().getStateFromMeta(this.getStoneBlock().getMetadata()));
                    }
                }
            }
        }
    }

    private double lerp(double d1, double d2, double t)
    {
        if (t < 0.0)
        {
            return d1;
        }
        if (t > 1.0)
        {
            return d2;
        } else
        {
            return d1 + (d2 - d1) * t;
        }
    }

    private double fade(double n)
    {
        return n * n * n * (n * (n * 6 - 15) + 10);
    }

    private double clamp(double x, double min, double max)
    {
        if (x < min)
        {
            return min;
        }
        if (x > max)
        {
            return max;
        }
        return x;
    }

    public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn)
    {
        final int var5 = 20;
        this.noiseGen4.setFrequencyAll(0.0625D);
        for (int var8 = 0; var8 < 16; ++var8)
        {
            for (int var9 = 0; var9 < 16; ++var9)
            {
                final int var12 = (int) (this.noiseGen4.evalNoise(x * 16 + var8, z * 16 + var9) / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int var13 = -1;
                Block var14 = this.getGrassBlock().getBlock();
                byte var14m = this.getGrassBlock().getMetadata();
                Block var15 = this.getDirtBlock().getBlock();
                byte var15m = this.getDirtBlock().getMetadata();

                for (int var16 = 255 - 1; var16 >= 0; --var16)
                {
                    if (var16 <= 0 + this.rand.nextInt(5))
                    {
                        primer.setBlockState(var8, var16, var9, Blocks.BEDROCK.getDefaultState());
                    } else
                    {
                        Block var18 = primer.getBlockState(var8, var16, var9).getBlock();

                        if (Blocks.AIR == var18)
                        {
                            var13 = -1;
                        } else if (var18 == this.getStoneBlock().getBlock())
                        {

                            if (var13 == -1)
                            {
                                if (var12 <= 0)
                                {
                                    var14 = Blocks.AIR;
                                    var14m = 0;
                                    var15 = this.getStoneBlock().getBlock();
                                    var15m = this.getStoneBlock().getMetadata();
                                } else if (var16 >= var5 - -16 && var16 <= var5 + 1)
                                {
                                    var14 = this.getGrassBlock().getBlock();
                                    var14m = this.getGrassBlock().getMetadata();
                                    var14 = this.getDirtBlock().getBlock();
                                    var14m = this.getDirtBlock().getMetadata();
                                }

                                var13 = var12;

                                if (var16 >= var5 - 1)
                                {
                                    primer.setBlockState(var8, var16, var9, var14.getStateFromMeta(var14m));
                                } else
                                {
                                    primer.setBlockState(var8, var16, var9, var15.getStateFromMeta(var15m));
                                }
                            } else if (var13 > 0)
                            {
                                --var13;
                                primer.setBlockState(var8, var16, var9, var15.getStateFromMeta(var15m));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Chunk generateChunk(int x, int z)
    {
        ChunkPrimer primer = new ChunkPrimer();
        try
        {
            this.rand.setSeed(x * 341873128712L + z * 132897987541L);
            this.generateTerrain(x, z, primer);
            this.createCraters(x, z, primer);
            this.biomesForGeneration = this.world.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
            this.replaceBiomeBlocks(x, z, primer, this.biomesForGeneration);

            if (this.worldGenerators == null)
            {
                this.worldGenerators = this.getWorldGenerators();
            }

            for (MapGenBaseMeta generator : this.worldGenerators)
            {
                generator.generate(this.world, x, z, primer);
            }

            this.onChunkProvide(x, z, primer);
        } catch (Exception e)
        {
            GalacticraftCore.logger.error("Error caught in planetary worldgen at coords " + x + "," + z + ".  If the next 2 lines are showing an Add-On mod name, please report to that mod's author!");
            e.printStackTrace();
        }

        final Chunk var4 = new Chunk(this.world, primer, x, z);
        final byte[] var5 = var4.getBiomeArray();

        for (int var6 = 0; var6 < var5.length; ++var6)
        {
            var5[var6] = (byte) Biome.getIdForBiome(this.biomesForGeneration[var6]);
        }

        var4.generateSkylightMap();
        return var4;
    }

    public void createCraters(int chunkX, int chunkZ, ChunkPrimer primer)
    {
        this.noiseGen5.setFrequencyAll(0.014999999664723873D);
        for (int cx = chunkX - 2; cx <= chunkX + 2; cx++)
        {
            for (int cz = chunkZ - 2; cz <= chunkZ + 2; cz++)
            {
                for (int x = 0; x < CHUNK_SIZE; x++)
                {
                    for (int z = 0; z < CHUNK_SIZE; z++)
                    {
                        if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000)) < this.noiseGen5.evalNoise(cx * 16 + x, cz * 16 + z) / this.CRATER_PROB)
                        {
                            final Random random = new Random(cx * 16 + x + (cz * 16 + z) * 5000);
                            final EnumCraterSize cSize = EnumCraterSize.sizeArray[random.nextInt(EnumCraterSize.sizeArray.length)];
                            final int size = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE + 15;
                            this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, primer);
                        }
                    }
                }
            }
        }
    }

    public void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, ChunkPrimer primer)
    {
        for (int x = 0; x < CHUNK_SIZE; x++)
        {
            for (int z = 0; z < CHUNK_SIZE; z++)
            {
                double xDev = craterX - (chunkX + x);
                double zDev = craterZ - (chunkZ + z);
                if (xDev * xDev + zDev * zDev < size * size)
                {
                    xDev /= size;
                    zDev /= size;
                    final double sqrtY = xDev * xDev + zDev * zDev;
                    double yDev = sqrtY * sqrtY * 6;
                    yDev = 5 - yDev;
                    int helper = 0;
                    for (int y = 127; y > 0; y--)
                    {
                        if (Blocks.AIR != primer.getBlockState(x, y, z).getBlock() && helper <= yDev)
                        {
                            primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
                            helper++;
                        }

                        if (helper > yDev)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }

    private double randFromPoint(int x, int z)
    {
        int n;
        n = x + z * 57;
        n = n << 13 ^ n;
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff) / 1073741824.0;
    }

    public void decoratePlanet(World world, Random rand, int posX, int posZ)
    {
        this.getBiomeGenerator().decorate(world, rand, posX, posZ);
    }

    @Override
    public void populate(int x, int z)
    {
        BlockFalling.fallInstantly = true;
        int var4 = x * 16;
        int var5 = z * 16;
        this.world.getBiome(new BlockPos(var4 + 16, 0, var5 + 16));
        this.rand.setSeed(this.world.getSeed());
        final long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        final long var9 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(x * var7 + z * var9 ^ this.world.getSeed());
        ForgeEventFactory.onChunkPopulate(true, this, this.world, this.rand, x, z, false);
        this.decoratePlanet(this.world, this.rand, var4, var5);
        this.onPopulate(x, z);
        ForgeEventFactory.onChunkPopulate(false, this, this.world, this.rand, x, z, false);
        BlockFalling.fallInstantly = false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        Biome biomegenbase = this.world.getBiome(pos);
        return biomegenbase.getSpawnableList(creatureType);
    }

    /**
     * Do not return null
     *
     * @return The biome generator for this world, handles ore, flower, etc
     *         generation. See GCBiomeDecoratorBase.
     */
    protected abstract BiomeDecoratorSpace getBiomeGenerator();

    /**
     * Do not return null, have at least one biome for generation
     *
     * @return Biome instance for generation
     */
    protected abstract Biome[] getBiomesForGeneration();

    /**
     * @return The average terrain level. Default is 64.
     */
    protected abstract int getSeaLevel();

    /**
     * List of all world generators to use. Caves, ravines, structures, etc.
     * <p> Return an empty list for no world generators. Do not return null.
     *
     * @return
     */
    protected abstract List<MapGenBaseMeta> getWorldGenerators();

    /**
     * The grass block to be generated. Doesn't have to be grass of course.
     *
     * @return BlockMetaPair instance containing ID and metadata for grass
     *         block.
     */
    protected abstract BlockMetaPair getGrassBlock();

    /**
     * The dirt block to be generated. Doesn't have to be dirt of course.
     *
     * @return BlockMetaPair instance containing ID and metadata for dirt block.
     */
    protected abstract BlockMetaPair getDirtBlock();

    /**
     * The stone block to be generated. Doesn't have to be stone of course.
     *
     * @return BlockMetaPair instance containing ID and metadata for stone
     *         block.
     */
    protected abstract BlockMetaPair getStoneBlock();

    /**
     * @return Base height modifier
     */
    public abstract double getHeightModifier();

    /**
     * @return Height modifier for small hills
     */
    public abstract double getSmallFeatureHeightModifier();

    /**
     * @return Height modifier for mountains
     */
    public abstract double getMountainHeightModifier();

    /**
     * @return Height modifier for valleys
     */
    public abstract double getValleyHeightModifier();

    /**
     * @return Probability that craters will be generated
     */
    public abstract int getCraterProbability();

    public abstract void onChunkProvide(int cX, int cZ, ChunkPrimer primer);

    public abstract void onPopulate(int cX, int cZ);
}
