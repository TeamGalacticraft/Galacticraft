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

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import static com.hrznstudio.galacticraft.world.biome.GalacticraftDefaultBiomeCreators.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBiomes {

    public static class Moon {
        public static final Biome HIGHLANDS_PLAINS = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.HIGHLANDS_PLAINS), createMoonHighlandsBiome(MOON_HIGHLANDS_CONFIGURED_SURFACE_BUILDER, 0.03F, 0.04F, 0.01F));
        public static final Biome HIGHLANDS_ROCKS = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.HIGHLANDS_ROCKS), createMoonHighlandsBiome(MOON_HIGHLANDS_ROCK_CONFIGURED_SURFACE_BUILDER, 0.6F, 0.007F, 0.0001F));
        public static final Biome HIGHLANDS_VALLEY = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.HIGHLANDS_VALLEY), createMoonHighlandsBiome(MOON_HIGHLANDS_ROCK_CONFIGURED_SURFACE_BUILDER, -0.5F, 0.03F, 0.005F));
        public static final Biome MARE_PLAINS = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.MARE_PLAINS), createMoonMareBiome(MOON_MARE_CONFIGURED_SURFACE_BUILDER, 0.03F, 0.03F, 0.005F));
        public static final Biome MARE_ROCKS = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.MARE_ROCKS), createMoonMareBiome(MOON_MARE_CONFIGURED_SURFACE_BUILDER, 0.7F, 0.01F, 0.003F));
        public static final Biome MARE_VALLEY = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.Moon.MARE_VALLEY), createMoonMareBiome(MOON_MARE_CONFIGURED_SURFACE_BUILDER, -0.6F, 0.03F, 0.004F));

        public static final Biome[] BIOMES = new Biome[]{HIGHLANDS_PLAINS, HIGHLANDS_ROCKS, HIGHLANDS_VALLEY, MARE_PLAINS, MARE_ROCKS, MARE_VALLEY};

        private static void init() {}
    }

    public static void register() {
        Moon.init();
    }
}
