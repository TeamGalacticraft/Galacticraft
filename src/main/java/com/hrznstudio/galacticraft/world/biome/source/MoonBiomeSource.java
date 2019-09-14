/*
 * Copyright (c) 2018-2019 Horizon Studio
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
