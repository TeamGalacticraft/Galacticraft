package com.hrznstudio.galacticraft.world.biome.source;

import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonBiomeSource extends BiomeSource {
    private final Biome[] biomes;

    public MoonBiomeSource(MoonBiomeSourceConfig sourceConfig) {
        this.biomes = new Biome[]{GalacticraftBiomes.MOON, GalacticraftBiomes.MOON_PLAINS};
    }

    @Override
    public Biome getBiome(int var1, int var2) {
        return null;
    }

    @Override
    public Biome[] sampleBiomes(int var1, int var2, int var3, int var4, boolean var5) {
        return biomes;
    }

    @Override
    public Set<Biome> getBiomesInArea(int var1, int var2, int var3) {
        return null;
    }

    @Override
    public BlockPos locateBiome(int var1, int var2, int var3, List<Biome> var4, Random var5) {
        return null;
    }

    @Override
    public boolean hasStructureFeature(StructureFeature<?> var1) {
        return false;
    }

    public Set<BlockState> getTopMaterials() {
        if (this.topMaterials.isEmpty()) {
            for (Biome biome : this.biomes) {
                this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
            }
        }

        return this.topMaterials;
    }
}
