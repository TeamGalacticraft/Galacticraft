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

package com.hrznstudio.galacticraft.world.biome;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBiomes {

    public static class Moon {
        public static final ResourceKey<Biome> HIGHLANDS_PLAINS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Constants.MOD_ID, Constants.Biomes.Moon.HIGHLANDS_PLAINS));
        public static final ResourceKey<Biome> HIGHLANDS_ROCKS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Constants.MOD_ID, Constants.Biomes.Moon.HIGHLANDS_ROCKS));
        public static final ResourceKey<Biome> HIGHLANDS_VALLEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Constants.MOD_ID, Constants.Biomes.Moon.HIGHLANDS_VALLEY));
        public static final ResourceKey<Biome> MARE_PLAINS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Constants.MOD_ID, Constants.Biomes.Moon.MARE_PLAINS));
        public static final ResourceKey<Biome> MARE_ROCKS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Constants.MOD_ID, Constants.Biomes.Moon.MARE_ROCKS));
        public static final ResourceKey<Biome> MARE_EDGE = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Constants.MOD_ID, Constants.Biomes.Moon.MARE_EDGE));

        private static void init() {
        }
    }

    public static void register() {
        Moon.init();
    }
}
