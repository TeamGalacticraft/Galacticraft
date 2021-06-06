/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.world.biome.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.world.biome.GalacticraftBiome;
import dev.galacticraft.mod.world.biome.layer.MoonBiomeLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.ArrayList;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MoonBiomeSource extends BiomeSource {
    public static final Codec<MoonBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.LONG.fieldOf("seed").stable().forGetter((moonBiomeSource) -> moonBiomeSource.seed), Codec.INT.optionalFieldOf("biome_size", 4, Lifecycle.stable()).forGetter((moonBiomeSource) -> moonBiomeSource.biomeSize), RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter((source) -> source.registry)).apply(instance, instance.stable(MoonBiomeSource::new)));

    private final BiomeLayerSampler sampler;
    private final long seed;
    private final int biomeSize;
    private final Registry<Biome> registry;
    private boolean initialized = false;

    public MoonBiomeSource(long seed, int biomeSize, Registry<Biome> registry) {
        super(new ArrayList<>(4)); // its a mutable list, as we want to add biomes in later
                                  // for /locate and other things to work. Will be set in #getBiomeForNoiseGen
        this.biomeSize = biomeSize;
        this.seed = seed;
        this.registry = registry;
        this.sampler = MoonBiomeLayer.build(seed, biomeSize, registry);
        if (!BuiltinBiomes.BY_RAW_ID.containsValue(GalacticraftBiome.Moon.HIGHLANDS_PLAINS)) {
            BuiltinBiomes.BY_RAW_ID.put(registry.getRawId(registry.get(GalacticraftBiome.Moon.HIGHLANDS_PLAINS)), GalacticraftBiome.Moon.HIGHLANDS_PLAINS);
            BuiltinBiomes.BY_RAW_ID.put(registry.getRawId(registry.get(GalacticraftBiome.Moon.HIGHLANDS_EDGE)), GalacticraftBiome.Moon.HIGHLANDS_EDGE);
            BuiltinBiomes.BY_RAW_ID.put(registry.getRawId(registry.get(GalacticraftBiome.Moon.MARE_PLAINS)), GalacticraftBiome.Moon.MARE_PLAINS);
            BuiltinBiomes.BY_RAW_ID.put(registry.getRawId(registry.get(GalacticraftBiome.Moon.MARE_EDGE)), GalacticraftBiome.Moon.MARE_EDGE);
        }
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public BiomeSource withSeed(long seed) {
        return new MoonBiomeSource(seed, this.biomeSize, registry);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        if (!this.initialized) {
            if (BuiltinBiomes.BY_RAW_ID.containsValue(GalacticraftBiome.Moon.HIGHLANDS_PLAINS)) {
                if (registry.getRawId(registry.get(GalacticraftBiome.Moon.HIGHLANDS_PLAINS)) != -1) {
                    BuiltinBiomes.BY_RAW_ID.put(registry.getRawId(registry.get(GalacticraftBiome.Moon.HIGHLANDS_PLAINS)), GalacticraftBiome.Moon.HIGHLANDS_PLAINS);
                    BuiltinBiomes.BY_RAW_ID.put(registry.getRawId(registry.get(GalacticraftBiome.Moon.HIGHLANDS_EDGE)), GalacticraftBiome.Moon.HIGHLANDS_EDGE);
                    BuiltinBiomes.BY_RAW_ID.put(registry.getRawId(registry.get(GalacticraftBiome.Moon.MARE_PLAINS)), GalacticraftBiome.Moon.MARE_PLAINS);
                    BuiltinBiomes.BY_RAW_ID.put(registry.getRawId(registry.get(GalacticraftBiome.Moon.MARE_EDGE)), GalacticraftBiome.Moon.MARE_EDGE);

                    this.biomes.clear();
                    this.biomes.add(registry.get(GalacticraftBiome.Moon.HIGHLANDS_PLAINS));
                    this.biomes.add(registry.get(GalacticraftBiome.Moon.HIGHLANDS_EDGE));
                    this.biomes.add(registry.get(GalacticraftBiome.Moon.MARE_PLAINS));
                    this.biomes.add(registry.get(GalacticraftBiome.Moon.MARE_EDGE));
                    this.structureFeatures.clear();
                    this.topMaterials.clear();
                    this.initialized = true;
                }
            } else {
                throw new AssertionError("Galacticraft biomes not registered!");
            }
        }
        return this.sampler.sample(registry, biomeX, biomeZ);
    }
}
