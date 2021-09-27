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

package dev.galacticraft.mod.world.biome.layer;

import dev.galacticraft.mod.world.biome.GalacticraftBiome;
import dev.galacticraft.mod.world.biome.layer.moon.MoonBaseBiomeLayer;
import dev.galacticraft.mod.world.biome.layer.moon.MoonEdgeBiomeLayer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.util.*;
import net.minecraft.world.biome.source.BiomeLayerSampler;

import java.util.function.LongFunction;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MoonBiomeLayer {
    public static int MOON_HIGHLANDS_ID = -1;
    public static int MOON_HIGHLANDS_EDGE_ID = -1;

    public static int MOON_MARE_ID = -1;
    public static int MOON_MARE_EDGE_ID = -1;

    public static Registry<Biome> registry;

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(int biomeSize, LongFunction<C> contextProvider, Registry<Biome> registry) {
        LayerFactory<T> moon = MoonBaseBiomeLayer.INSTANCE.create(contextProvider.apply(4415L));

        for (int i = 0; i < (biomeSize / 2) - 1; i++) {
            moon = ScaleLayer.NORMAL.create(contextProvider.apply(1946L + i * 24L), moon);
        }

        moon = ScaleLayer.FUZZY.create(contextProvider.apply(1283L), moon);
        moon = MoonEdgeBiomeLayer.INSTANCE.create(contextProvider.apply(2378L), moon);

        for (int i = (biomeSize / 2); i < biomeSize - 1; i++) {
            moon = ScaleLayer.NORMAL.create(contextProvider.apply(1472L + i * 37L), moon);
        }
        moon = MoonEdgeBiomeLayer.INSTANCE.create(contextProvider.apply(3782L), moon);
        moon = ScaleLayer.NORMAL.create(contextProvider.apply(2783L), moon);
        moon = ScaleLayer.FUZZY.create(contextProvider.apply(4729L), moon);

        return moon;
    }

    public static BiomeLayerSampler build(long seed, int biomeSize, Registry<Biome> registry) {
        if (MOON_HIGHLANDS_ID != registry.getRawId(registry.get(GalacticraftBiome.Moon.HIGHLANDS_PLAINS))) {
            MOON_HIGHLANDS_ID = registry.getRawId(registry.get(GalacticraftBiome.Moon.HIGHLANDS_PLAINS));
            MOON_HIGHLANDS_EDGE_ID = registry.getRawId(registry.get(GalacticraftBiome.Moon.HIGHLANDS_EDGE));
            MOON_MARE_ID = registry.getRawId(registry.get(GalacticraftBiome.Moon.MARE_PLAINS));
            MOON_MARE_EDGE_ID = registry.getRawId(registry.get(GalacticraftBiome.Moon.MARE_EDGE));
        }
        LayerFactory<CachingLayerSampler> layerFactory = build(biomeSize, (salt) -> new CachingLayerContext(25, seed, salt + 1), registry);
        MoonBiomeLayer.registry = registry;
        return new BiomeLayerSampler(layerFactory);
    }
}
