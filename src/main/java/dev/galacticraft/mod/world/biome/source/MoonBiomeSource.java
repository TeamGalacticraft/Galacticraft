/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.ArrayList;
import java.util.Set;

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
        super(new ArrayList<>(4)); // it is a mutable list, as we want to add biomes in later
        // for /locate and other things to work. Will be set in #initialize
        this.biomeSize = biomeSize;
        this.seed = seed;
        this.registry = registry;
        this.sampler = MoonBiomeLayer.build(seed, biomeSize, registry);

        if (MoonBiomeLayer.MOON_HIGHLANDS_ID != -1) {
            this.biomes.clear();
            this.biomes.add(registry.get(GalacticraftBiome.Moon.HIGHLANDS));
            this.biomes.add(registry.get(GalacticraftBiome.Moon.HIGHLANDS_EDGE));
            this.biomes.add(registry.get(GalacticraftBiome.Moon.MARE));
            this.biomes.add(registry.get(GalacticraftBiome.Moon.MARE_EDGE));
            this.structureFeatures.clear();
            this.topMaterials.clear();
            this.initialized = true;
        }
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new MoonBiomeSource(seed, this.biomeSize, registry);
    }

    @Override
    public boolean hasStructureFeature(StructureFeature<?> feature) {
        return super.hasStructureFeature(feature);
    }

    @Override
    public Set<BlockState> getTopMaterials() {
        return super.getTopMaterials();
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.sampler.sample(registry, biomeX, biomeZ);
    }
}
