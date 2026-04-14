/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.world.gen;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

import net.minecraftforge.event.ForgeEventFactory;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon;
import micdoodle8.mods.galacticraft.core.perlin.generator.GradientNoise;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.DungeonConfiguration;
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon;
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomBoss;
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomTreasure;

public class ChunkProviderMoon extends ChunkProviderBase
{

    public static final IBlockState BLOCK_TOP = GCBlocks.blockMoon.getDefaultState().withProperty(BlockBasicMoon.BASIC_TYPE_MOON, BlockBasicMoon.EnumBlockBasicMoon.MOON_TURF);
    public static final IBlockState BLOCK_FILL = GCBlocks.blockMoon.getDefaultState().withProperty(BlockBasicMoon.BASIC_TYPE_MOON, BlockBasicMoon.EnumBlockBasicMoon.MOON_DIRT);
    public static final IBlockState BLOCK_LOWER = GCBlocks.blockMoon.getDefaultState().withProperty(BlockBasicMoon.BASIC_TYPE_MOON, BlockBasicMoon.EnumBlockBasicMoon.MOON_STONE);

    private final Random rand;

    private final GradientNoise noiseGen1;
    private final GradientNoise noiseGen2;
    private final GradientNoise noiseGen3;
    private final GradientNoise noiseGen4;

    private final World world;
    private final MapGenVillageMoon villageGenerator = new MapGenVillageMoon();

    private final MapGenDungeon dungeonGeneratorMoon =
        new MapGenDungeon(new DungeonConfiguration(GCBlocks.blockMoon.getDefaultState().withProperty(BlockBasicMoon.BASIC_TYPE_MOON, BlockBasicMoon.EnumBlockBasicMoon.MOON_DUNGEON_BRICK), 25, 8, 16,
            5, 6, RoomBoss.class, RoomTreasure.class));

    private final MapGenBaseMeta caveGenerator = new MapGenCavesMoon();

    private static final int CRATER_PROB = 300;

    // DO NOT CHANGE
    private static final int MID_HEIGHT = 63;
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 128;
    private static final int CHUNK_SIZE_Z = 16;

    public ChunkProviderMoon(World worldIn, long seed, boolean mapFeaturesEnabled)
    {
        this.world = worldIn;
        this.rand = new Random(seed);
        this.noiseGen1 = new GradientNoise(this.rand.nextLong(), 4, 0.25D);
        this.noiseGen2 = new GradientNoise(this.rand.nextLong(), 4, 0.25D);
        this.noiseGen3 = new GradientNoise(this.rand.nextLong(), 1, 0.25D);
        this.noiseGen4 = new GradientNoise(this.rand.nextLong(), 1, 0.25D);
    }

    public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer)
    {
        this.noiseGen1.setFrequencyAll(0.012500000186264515D);
        this.noiseGen2.setFrequencyAll(0.014999999664723873D);
        this.noiseGen3.setFrequencyAll(0.0D);
        this.noiseGen4.setFrequencyAll(0.019999999552965164D);

        for (int x = 0; x < ChunkProviderMoon.CHUNK_SIZE_X; x++)
        {
            for (int z = 0; z < ChunkProviderMoon.CHUNK_SIZE_Z; z++)
            {
                final double d = this.noiseGen1.evalNoise(x + chunkX * 16, z + chunkZ * 16) * 8;
                final double d2 = this.noiseGen2.evalNoise(x + chunkX * 16, z + chunkZ * 16) * 24;
                double d3 = this.noiseGen3.evalNoise(x + chunkX * 16, z + chunkZ * 16) - 0.1;
                d3 *= 4;

                double yDev;

                if (d3 < 0.0D)
                {
                    yDev = d;
                } else if (d3 > 1.0D)
                {
                    yDev = d2;
                } else
                {
                    yDev = d + (d2 - d) * d3;
                }

                for (int y = 0; y < ChunkProviderMoon.CHUNK_SIZE_Y; y++)
                {
                    if (y < ChunkProviderMoon.MID_HEIGHT + yDev)
                    {
                        primer.setBlockState(x, y, z, BLOCK_LOWER);
                    }
                }
            }
        }
    }

    public void replaceBlocksForBiome(int x, int z, ChunkPrimer primer, Biome[] biomes)
    {
        final int var5 = 20;
        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                final int var12 = (int) (this.noiseGen4.evalNoise(i + x * 16, j * z * 16) / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int var13 = -1;
                IBlockState state0 = BLOCK_TOP;
                IBlockState state1 = BLOCK_FILL;

                for (int var16 = 127; var16 >= 0; --var16)
                {
                    if (var16 <= this.rand.nextInt(5))
                    {
                        primer.setBlockState(i, var16, j, Blocks.BEDROCK.getDefaultState());
                    } else
                    {
                        IBlockState var18 = primer.getBlockState(i, var16, j);
                        if (Blocks.AIR == var18.getBlock())
                        {
                            var13 = -1;
                        } else if (var18 == BLOCK_LOWER)
                        {
                            if (var13 == -1)
                            {
                                if (var12 <= 0)
                                {
                                    state0 = Blocks.AIR.getDefaultState();
                                    state1 = BLOCK_LOWER;
                                } else if (var16 >= var5 - -16 && var16 <= var5 + 1)
                                {
                                    state0 = BLOCK_FILL;
                                }

                                var13 = var12;

                                if (var16 >= var5 - 1)
                                {
                                    primer.setBlockState(i, var16, j, state0);
                                } else if (var16 < var5 - 1 && var16 >= var5 - 2)
                                {
                                    primer.setBlockState(i, var16, j, state1);
                                }
                            } else if (var13 > 0)
                            {
                                --var13;
                                primer.setBlockState(i, var16, j, state1);
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
        this.rand.setSeed(x * 341873128712L + z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.setBlocksInChunk(x, z, chunkprimer);
        this.createCraters(x, z, chunkprimer);
        this.replaceBlocksForBiome(x, z, chunkprimer, null);

        this.caveGenerator.generate(this.world, x, z, chunkprimer);

        this.dungeonGeneratorMoon.generate(this.world, x, z, chunkprimer);
        this.villageGenerator.generate(this.world, x, z, chunkprimer);

        Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();
        final byte b = (byte) Biome.getIdForBiome(BiomeAdaptive.biomeDefault);
        for (int i = 0; i < abyte.length; ++i)
        {
            abyte[i] = b;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private void createCraters(int chunkX, int chunkZ, ChunkPrimer primer)
    {
        for (int cx = chunkX - 2; cx <= chunkX + 2; cx++)
        {
            for (int cz = chunkZ - 2; cz <= chunkZ + 2; cz++)
            {
                for (int x = 0; x < ChunkProviderMoon.CHUNK_SIZE_X; x++)
                {
                    for (int z = 0; z < ChunkProviderMoon.CHUNK_SIZE_Z; z++)
                    {
                        if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000)) < this.noiseGen4.evalNoise(x * ChunkProviderMoon.CHUNK_SIZE_X + x, cz * ChunkProviderMoon.CHUNK_SIZE_Z + z)
                            / ChunkProviderMoon.CRATER_PROB)
                        {
                            final Random random = new Random(cx * 16 + x + (cz * 16 + z) * 5000);
                            final EnumCraterSize cSize = EnumCraterSize.sizeArray[random.nextInt(EnumCraterSize.sizeArray.length)];
                            final int size = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE;
                            this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, primer);
                        }
                    }
                }
            }
        }
    }

    private void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, ChunkPrimer primer)
    {
        for (int x = 0; x < ChunkProviderMoon.CHUNK_SIZE_X; x++)
        {
            for (int z = 0; z < ChunkProviderMoon.CHUNK_SIZE_Z; z++)
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

    @Override
    public void populate(int x, int z)
    {
        BlockFalling.fallInstantly = true;
        int i = x * 16;
        int j = z * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        Biome biomegenbase = this.world.getBiome(blockpos.add(16, 0, 16));
        this.rand.setSeed(this.world.getSeed());
        long k = this.rand.nextLong() / 2L * 2L + 1L;
        long l = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(x * k + z * l ^ this.world.getSeed());
        boolean flag = false;
        ForgeEventFactory.onChunkPopulate(true, this, this.world, this.rand, x, z, flag);
        if (!ConfigManagerCore.disableMoonVillageGen)
        {
            flag = this.villageGenerator.generateStructure(this.world, this.rand, new ChunkPos(x, z));
        }

        this.dungeonGeneratorMoon.generateStructure(this.world, this.rand, new ChunkPos(x, z));

        biomegenbase.decorate(this.world, this.rand, new BlockPos(i, 0, j));
        ForgeEventFactory.onChunkPopulate(false, this, this.world, this.rand, x, z, flag);
        BlockFalling.fallInstantly = false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        Biome biomegenbase = this.world.getBiome(pos);
        return biomegenbase.getSpawnableList(creatureType);
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z)
    {
        if (!ConfigManagerCore.disableMoonVillageGen)
        {
            this.villageGenerator.generate(this.world, x, z, null);
        }

        this.dungeonGeneratorMoon.generate(this.world, x, z, null);
    }
}
