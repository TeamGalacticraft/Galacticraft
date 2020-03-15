/*
 * Copyright (c) 2019 HRZN LTD
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

import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.biome.layer.MoonBaseBiomesLayer;
import com.hrznstudio.galacticraft.world.gen.chunk.MoonChunkGeneratorConfig;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.*;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.*;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.LevelGeneratorType;

import java.util.Set;
import java.util.function.LongFunction;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonBiomeSource extends BiomeSource {
    private static final Set<Biome> BIOMES = ImmutableSet.of(GalacticraftBiomes.MOON_HIGHLANDS_PLAINS, GalacticraftBiomes.MOON_HIGHLANDS_CRATERS, GalacticraftBiomes.MOON_HIGHLANDS_ROCKS, GalacticraftBiomes.MOON_MARE_PLAINS, GalacticraftBiomes.MOON_MARE_CRATERS, GalacticraftBiomes.MOON_MARE_ROCKS, GalacticraftBiomes.MOON_CHEESE_FOREST);
    private final BiomeLayerSampler biomeSampler;

    public MoonBiomeSource(MoonBiomeSourceConfig config) {
        super(BIOMES);
        this.biomeSampler = build(config.getSeed(), config.getGeneratorType(), config.getGeneratorSettings());
    }

    @Override
    public boolean hasStructureFeature(StructureFeature<?> feature) {
        return this.structureFeatures.computeIfAbsent(feature, (structureFeature) -> this.biomes.stream().anyMatch((biome) -> biome.hasStructureFeature(structureFeature)));
    }

    public static BiomeLayerSampler build(long seed, LevelGeneratorType generatorType, MoonChunkGeneratorConfig settings) {
        LayerFactory<CachingLayerSampler> layerFactory = build(generatorType, settings, (salt) -> new CachingLayerContext(25, seed, salt));
        return new BiomeLayerSampler(layerFactory);
    }

    public static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(LevelGeneratorType generatorType, MoonChunkGeneratorConfig settings, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = ContinentLayer.INSTANCE.create(contextProvider.apply(1L));
        layerFactory = ScaleLayer.FUZZY.create(contextProvider.apply(2000L), layerFactory);
//        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(1L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2001L), layerFactory);
//        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
//        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(50L), layerFactory);
//        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(70L), layerFactory);
//        layerFactory = AddIslandLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
//        LayerFactory<T> layerFactory2 = OceanTemperatureLayer.INSTANCE.create(contextProvider.apply(2L));
//        layerFactory2 = stack(2001L, ScaleLayer.NORMAL, layerFactory2, 6, contextProvider);
//        layerFactory = AddColdClimatesLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
//        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), layerFactory);
//        layerFactory = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
//        layerFactory = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
//        layerFactory = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create(contextProvider.apply(3L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2002L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2003L), layerFactory);
//        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(4L), layerFactory);
        layerFactory = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
        int i = generatorType == LevelGeneratorType.LARGE_BIOMES ? 6 : 4;
//        LayerFactory<T> layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
//        layerFactory3 = SimpleLandNoiseLayer.INSTANCE.create(contextProvider.apply(100L), layerFactory3);
        LayerFactory<T> layerFactory4 = (new MoonBaseBiomesLayer()).create(contextProvider.apply(200L), layerFactory);
        layerFactory4 = stack(1000L, ScaleLayer.NORMAL, layerFactory4, 2, contextProvider);
//        layerFactory4 = EaseBiomeEdgeLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4);
//        LayerFactory<T> layerFactory5 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
//        layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
//        layerFactory3 = SmoothenShorelineLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory3);

        for (int k = 0; k < i; ++k) {
            layerFactory4 = ScaleLayer.NORMAL.create(contextProvider.apply(1000 + k), layerFactory4);
            if (k == 0) {
                layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), layerFactory4);
            }

            if (k == 1 || i == 1) {
                layerFactory4 = AddEdgeBiomesLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4);
            }
        }

        layerFactory4 = SmoothenShorelineLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4);
        return layerFactory4;
    }

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = parent;

        for (int i = 0; i < count; ++i) {
            layerFactory = layer.create(contextProvider.apply(seed + (long) i), layerFactory);
        }

        return layerFactory;
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeSampler.sample(biomeX, biomeZ);
    }
}
