/*
 * Copyright (c) 2019-2021 HRZN LTD
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
import com.hrznstudio.galacticraft.world.biome.layer.MoonBiomeLayers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.newbiome.layer.Layer;
import java.util.ArrayList;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonBiomeSource extends BiomeSource {
    public static final Codec<MoonBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.LONG.fieldOf("seed").stable().forGetter((moonBiomeSource) -> moonBiomeSource.seed), Codec.INT.optionalFieldOf("biome_size", 4, Lifecycle.stable()).forGetter((moonBiomeSource) -> moonBiomeSource.biomeSize), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((source) -> source.registry)).apply(instance, instance.stable(MoonBiomeSource::new)));

    private final Layer sampler;
    private final long seed;
    private final int biomeSize;
    private final Registry<Biome> registry;
    private boolean initialized = false;

    public MoonBiomeSource(long seed, int biomeSize, Registry<Biome> registry) {
        super(new ArrayList<>()); // its a mutable list, as we want to add biomes in later
                                  // for /locate and other things to work. Will be set in #getBiomeForNoiseGen
        this.biomeSize = biomeSize;
        this.seed = seed;
        this.registry = registry;
        this.sampler = MoonBiomeLayers.build(seed, biomeSize, registry);
        if (!Biomes.TO_NAME.containsValue(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS)) {
            Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS)), GalacticraftBiomes.Moon.HIGHLANDS_PLAINS);
            Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_ROCKS)), GalacticraftBiomes.Moon.HIGHLANDS_ROCKS);
            Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_VALLEY)), GalacticraftBiomes.Moon.HIGHLANDS_VALLEY);
            Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.MARE_PLAINS)), GalacticraftBiomes.Moon.MARE_PLAINS);
            Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.MARE_ROCKS)), GalacticraftBiomes.Moon.MARE_ROCKS);
            Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.MARE_EDGE)), GalacticraftBiomes.Moon.MARE_EDGE);
        }
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public BiomeSource withSeed(long seed) {
        return new MoonBiomeSource(seed, this.biomeSize, registry);
    }

    @Override
    public Biome getNoiseBiome(int biomeX, int biomeY, int biomeZ) {
        if (!this.initialized) {
            if (Biomes.TO_NAME.containsValue(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS)) {
                if (registry.getId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS)) != -1) {
                    Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS)), GalacticraftBiomes.Moon.HIGHLANDS_PLAINS);
                    Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_ROCKS)), GalacticraftBiomes.Moon.HIGHLANDS_ROCKS);
                    Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_VALLEY)), GalacticraftBiomes.Moon.HIGHLANDS_VALLEY);
                    Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.MARE_PLAINS)), GalacticraftBiomes.Moon.MARE_PLAINS);
                    Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.MARE_ROCKS)), GalacticraftBiomes.Moon.MARE_ROCKS);
                    Biomes.TO_NAME.put(registry.getId(registry.get(GalacticraftBiomes.Moon.MARE_EDGE)), GalacticraftBiomes.Moon.MARE_EDGE);

                    this.possibleBiomes.clear();
                    this.possibleBiomes.add(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS));
                    this.possibleBiomes.add(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_ROCKS));
                    this.possibleBiomes.add(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_VALLEY));
                    this.possibleBiomes.add(registry.get(GalacticraftBiomes.Moon.MARE_PLAINS));
                    this.possibleBiomes.add(registry.get(GalacticraftBiomes.Moon.MARE_ROCKS));
                    this.possibleBiomes.add(registry.get(GalacticraftBiomes.Moon.MARE_EDGE));
                    this.supportedStructures.clear();
                    this.surfaceBlocks.clear();
                    this.initialized = true;
                }
            } else {
                throw new RuntimeException();
            }
        }
        return this.sampler.get(registry, biomeX, biomeZ);
    }
}
