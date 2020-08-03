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

package com.hrznstudio.galacticraft.world.biome.layer;

import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.biome.layer.moon.MoonBaseBiomeLayer;
import com.hrznstudio.galacticraft.world.biome.layer.moon.MoonBiomeCraterLayer;
import com.hrznstudio.galacticraft.world.biome.layer.moon.MoonBiomeRockLayer;
import com.hrznstudio.galacticraft.world.biome.layer.moon.MoonValleyLayer;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.*;
import net.minecraft.world.biome.source.BiomeLayerSampler;

import java.util.function.LongFunction;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonBiomeLayers {
    public static final int MOON_HIGHLANDS_PLAINS_ID = BuiltinRegistries.BIOME.getRawId(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS);
    public static final int MOON_HIGHLANDS_ROCKS_ID = BuiltinRegistries.BIOME.getRawId(GalacticraftBiomes.Moon.HIGHLANDS_ROCKS);
    public static final int MOON_HIGHLANDS_VALLEY_ID = BuiltinRegistries.BIOME.getRawId(GalacticraftBiomes.Moon.HIGHLANDS_VALLEY);

    public static final int MOON_MARE_PLAINS_ID = BuiltinRegistries.BIOME.getRawId(GalacticraftBiomes.Moon.MARE_PLAINS);
    public static final int MOON_MARE_ROCKS_ID = BuiltinRegistries.BIOME.getRawId(GalacticraftBiomes.Moon.MARE_ROCKS);
    public static final int MOON_MARE_VALLEY_ID = BuiltinRegistries.BIOME.getRawId(GalacticraftBiomes.Moon.MARE_VALLEY);

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(int biomeSize, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = MoonBaseBiomeLayer.INSTANCE.create(contextProvider.apply(1L));
        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2048L), layerFactory);
        layerFactory = MoonBiomeRockLayer.INSTANCE.create(contextProvider.apply(1512L), layerFactory);
        layerFactory = MoonBiomeCraterLayer.INSTANCE.create(contextProvider.apply(2010L), layerFactory);
        layerFactory = ScaleLayer.FUZZY.create(contextProvider.apply(2020L), layerFactory);
        layerFactory = MoonValleyLayer.INSTANCE.create(contextProvider.apply(2030L), layerFactory);

        for (int i = 0; i < biomeSize; ++i) {
            layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(1024L + i), layerFactory);
        }

        layerFactory = stack(2913L, ScaleLayer.NORMAL, layerFactory, 2, contextProvider);
        return layerFactory;
    }

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = parent;

        for (int i = 0; i < count; ++i) {
            layerFactory = layer.create(contextProvider.apply(seed + (long) i), layerFactory);
        }

        return layerFactory;
    }

    public static BiomeLayerSampler build(long seed, int biomeSize) {
        LayerFactory<CachingLayerSampler> layerFactory = build(biomeSize, (salt) -> new CachingLayerContext(25, seed, salt));
        return new BiomeLayerSampler(layerFactory);
    }
}
