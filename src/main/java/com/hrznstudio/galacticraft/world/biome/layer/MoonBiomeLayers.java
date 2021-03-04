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

package com.hrznstudio.galacticraft.world.biome.layer;

import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.biome.layer.moon.MoonHighlandsBiomeLayer;
import com.hrznstudio.galacticraft.world.biome.layer.moon.MoonMareBiomeLayer;
import com.hrznstudio.galacticraft.world.biome.layer.moon.MoonMergeLayer;
import com.hrznstudio.galacticraft.world.biome.layer.moon.ValleyCrossSamplingLayer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.*;
import net.minecraft.world.biome.source.BiomeLayerSampler;

import java.util.function.LongFunction;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonBiomeLayers {
    public static int MOON_HIGHLANDS_PLAINS_ID = -1;
    public static int MOON_HIGHLANDS_ROCKS_ID = -1;
    public static int MOON_HIGHLANDS_VALLEY_ID = -1;

    public static int MOON_MARE_PLAINS_ID = -1;
    public static int MOON_MARE_ROCKS_ID = -1;
    public static int MOON_MARE_EDGE_ID = -1;

    public static Registry<Biome> registry;

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(int biomeSize, LongFunction<C> contextProvider, Registry<Biome> registry) {
        LayerFactory<T> baseLayer = MoonHighlandsBiomeLayer.INSTANCE.create(contextProvider.apply(4415L));

        baseLayer = MoonMergeLayer.INSTANCE.create(contextProvider.apply(1703L), baseLayer, MoonMareBiomeLayer.INSTANCE.create(contextProvider.apply(6521L))); //layer merge

        for (int i = 1; i < biomeSize - 1; ++i) {
            baseLayer = ScaleLayer.NORMAL.create(contextProvider.apply(3891L + i * 3L), baseLayer);
        }
        baseLayer = ValleyCrossSamplingLayer.INSTANCE.create(contextProvider.apply(9241L), baseLayer); //add valleys (separators, like rivers)
        baseLayer = ScaleLayer.FUZZY.create(contextProvider.apply(6214L), baseLayer);
        baseLayer = ScaleLayer.NORMAL.create(contextProvider.apply(7834L), baseLayer);

        return baseLayer;
    }

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = parent;

        for (int i = 0; i < count; ++i) {
            layerFactory = layer.create(contextProvider.apply(seed + (long) i), layerFactory);
        }

        return layerFactory;
    }

    public static BiomeLayerSampler build(long seed, int biomeSize, Registry<Biome> registry) {
        if (MOON_HIGHLANDS_PLAINS_ID == -1 || MOON_HIGHLANDS_PLAINS_ID != registry.getRawId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS))) {
            MOON_HIGHLANDS_PLAINS_ID = registry.getRawId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS));
            MOON_HIGHLANDS_ROCKS_ID = registry.getRawId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_ROCKS));
            MOON_HIGHLANDS_VALLEY_ID = registry.getRawId(registry.get(GalacticraftBiomes.Moon.HIGHLANDS_VALLEY));
            MOON_MARE_PLAINS_ID = registry.getRawId(registry.get(GalacticraftBiomes.Moon.MARE_PLAINS));
            MOON_MARE_ROCKS_ID = registry.getRawId(registry.get(GalacticraftBiomes.Moon.MARE_ROCKS));
            MOON_MARE_EDGE_ID = registry.getRawId(registry.get(GalacticraftBiomes.Moon.MARE_EDGE));
        }

        LayerFactory<CachingLayerSampler> layerFactory = build(biomeSize, (salt) -> new CachingLayerContext(25, seed, salt), registry);
        MoonBiomeLayers.registry = registry;
        return new BiomeLayerSampler(layerFactory);
    }
}
