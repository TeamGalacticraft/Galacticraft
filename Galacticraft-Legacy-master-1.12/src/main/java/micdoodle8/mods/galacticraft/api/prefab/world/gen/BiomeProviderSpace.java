/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.prefab.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.IntCache;

/**
 * Do not include this prefab class in your released mod download. <p> This
 * chunk manager is used for single-biome dimensions, which is common on basic
 * planets.
 */
public abstract class BiomeProviderSpace extends BiomeProvider
{

    private final BiomeCache biomeCache;
    private final List<Biome> biomesToSpawnIn;

    public BiomeProviderSpace()
    {
        this.biomeCache = new BiomeCache(this);
        this.biomesToSpawnIn = new ArrayList<Biome>();
        this.biomesToSpawnIn.add(this.getBiome());
    }

    @Override
    public List<Biome> getBiomesToSpawnIn()
    {
        return this.biomesToSpawnIn;
    }

    @Override
    public Biome getBiome(BlockPos pos, Biome defaultBiome)
    {
        return this.getBiome();
    }

    @Override
    public float getTemperatureAtHeight(float temp, int height)
    {
        return temp;
    }
    
    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
    {
        IntCache.resetIntCache();

        if (biomes == null || biomes.length < width * height)
        {
            biomes = new Biome[width * height];
        }

        for (int var7 = 0; var7 < width * height; ++var7)
        {
            biomes[var7] = this.getBiome();
        }

        return biomes;
    }

    @Override
    public Biome[] getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth)
    {
        return this.getBiomes(oldBiomeList, x, z, width, depth, true);
    }

    @Override
    public Biome[] getBiomes(Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new Biome[width * length];
        }

        if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
        {
            final Biome[] var9 = this.biomeCache.getCachedBiomes(x, z);
            System.arraycopy(var9, 0, listToReuse, 0, width * length);
            return listToReuse;
        } else
        {
            for (int var8 = 0; var8 < width * length; ++var8)
            {
                listToReuse[var8] = this.getBiome();
            }

            return listToReuse;
        }
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed)
    {
        return allowed.contains(this.getBiome());
    }

    @Override
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
    {
        final int i = x - range >> 2;
        final int j = z - range >> 2;
        final int k = x + range >> 2;
        final int l = k - i + 1;

        final int i1 = i + 0 % l << 2;
        final int j1 = j + 0 / l << 2;

        return new BlockPos(i1, 0, j1);
    }

    @Override
    public void cleanupCache()
    {
        this.biomeCache.cleanupCache();
    }

    public abstract Biome getBiome();
}
