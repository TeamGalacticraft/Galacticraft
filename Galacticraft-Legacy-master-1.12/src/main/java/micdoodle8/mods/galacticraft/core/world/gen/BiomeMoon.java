/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.world.gen;

import java.util.Random;
import micdoodle8.mods.galacticraft.api.world.BiomeData;
import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.ChunkPrimer;

public class BiomeMoon extends BiomeGenBaseGC
{

    public static final Biome moonFlat = new BiomeFlatMoon(BiomeData.builder().biomeName("Moon").baseHeight(1.5F).heightVariation(0.4F).build());

    BiomeMoon(BiomeData properties)
    {
        super(properties, true);
    }

    @Override
    public BiomeDecorator createBiomeDecorator()
    {
        return getModdedBiomeDecorator(new BiomeDecoratorMoon());
    }

    @Override
    public float getSpawningChance()
    {
        return 0.1F;
    }

    @Override
    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        this.fillerBlock = ChunkProviderMoon.BLOCK_LOWER;
        this.topBlock = ChunkProviderMoon.BLOCK_TOP;
        super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
    }
}
