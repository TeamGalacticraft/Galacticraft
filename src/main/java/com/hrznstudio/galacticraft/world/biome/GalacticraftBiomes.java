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

package com.hrznstudio.galacticraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBiomes {

    public static class Moon {
        public static final RegistryKey<Biome> HIGHLANDS_PLAINS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.HIGHLANDS_PLAINS));
        public static final RegistryKey<Biome> HIGHLANDS_ROCKS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.HIGHLANDS_ROCKS));
        public static final RegistryKey<Biome> HIGHLANDS_VALLEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.HIGHLANDS_VALLEY));
        public static final RegistryKey<Biome> MARE_PLAINS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.MARE_PLAINS));
        public static final RegistryKey<Biome> MARE_ROCKS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.MARE_ROCKS));
        public static final RegistryKey<Biome> MARE_VALLEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.MARE_VALLEY));

        private static void init() {
//            BuiltinBiomes.register(70, HIGHLANDS_PLAINS, createMoonHighlandsBiome(MOON_HIGHLANDS_CONFIGURED_SURFACE_BUILDER, 0.03F, 0.04F, 0.01F));
//            BuiltinBiomes.register(71, HIGHLANDS_ROCKS, createMoonHighlandsBiome(MOON_HIGHLANDS_ROCK_CONFIGURED_SURFACE_BUILDER, 0.6F, 0.007F, 0.0001F));
//            BuiltinBiomes.register(72, HIGHLANDS_VALLEY, createMoonHighlandsBiome(MOON_HIGHLANDS_ROCK_CONFIGURED_SURFACE_BUILDER, -0.5F, 0.03F, 0.005F));
//            BuiltinBiomes.register(73, MARE_PLAINS, createMoonMareBiome(MOON_MARE_CONFIGURED_SURFACE_BUILDER, 0.03F, 0.03F, 0.005F));
//            BuiltinBiomes.register(74, MARE_ROCKS, createMoonMareBiome(MOON_MARE_CONFIGURED_SURFACE_BUILDER, 0.7F, 0.01F, 0.003F));
//            BuiltinBiomes.register(75, MARE_VALLEY, createMoonMareBiome(MOON_MARE_CONFIGURED_SURFACE_BUILDER, -0.6F, 0.03F, 0.004F));
        }
    }

    public static void register() {
        Moon.init();
    }
}
