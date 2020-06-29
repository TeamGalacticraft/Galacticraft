/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.biome.layer.MoonBiomeLayers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.List;

public class MoonBiomeSource extends BiomeSource {
    public static final Codec<MoonBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.LONG.fieldOf("seed").stable().forGetter((moonBiomeSource) -> moonBiomeSource.seed), Codec.INT.optionalFieldOf("biome_size", 4, Lifecycle.stable()).forGetter((moonBiomeSource) -> moonBiomeSource.biomeSize)).apply(instance, instance.stable(MoonBiomeSource::new)));

    private final BiomeLayerSampler sampler;
    private final long seed;
    private static final List<Biome> BIOMES = ImmutableList.of(GalacticraftBiomes.MOON_HIGHLANDS_PLAINS, GalacticraftBiomes.MOON_HIGHLANDS_CRATERS, GalacticraftBiomes.MOON_HIGHLANDS_ROCKS,
            GalacticraftBiomes.MOON_MARE_PLAINS, GalacticraftBiomes.MOON_MARE_CRATERS, GalacticraftBiomes.MOON_MARE_ROCKS, GalacticraftBiomes.MOON_CHEESE_FOREST);
    private final int biomeSize;

    public MoonBiomeSource(long seed, int biomeSize) {
        super(BIOMES);
        this.biomeSize = biomeSize;
        this.seed = seed;

        this.sampler = MoonBiomeLayers.build(seed, biomeSize, 0);
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return CODEC;
    }

    @Environment(EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new MoonBiomeSource(seed, this.biomeSize);
    }

    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.sampler.sample(biomeX, biomeZ);
    }
}
