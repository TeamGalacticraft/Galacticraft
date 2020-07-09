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
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.world.biome.moon.highlands.MoonHighlandsPlainsBiome;
import com.hrznstudio.galacticraft.world.biome.moon.highlands.MoonHighlandsRocksBiome;
import com.hrznstudio.galacticraft.world.biome.moon.highlands.MoonHighlandsValleyBiome;
import com.hrznstudio.galacticraft.world.biome.moon.mare.MoonMarePlainsBiome;
import com.hrznstudio.galacticraft.world.biome.moon.mare.MoonMareRocksBiome;
import com.hrznstudio.galacticraft.world.biome.moon.mare.MoonMareValleyBiome;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBiomes {
    public static final Biome MOON_HIGHLANDS_PLAINS = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_HIGHLANDS_PLAINS), new MoonHighlandsPlainsBiome());
    public static final Biome MOON_HIGHLANDS_ROCKS = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_HIGHLANDS_ROCKS), new MoonHighlandsRocksBiome());
    public static final Biome MOON_HIGHLANDS_VALLEY = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_HIGHLANDS_VALLEY), new MoonHighlandsValleyBiome());
    public static final Biome MOON_MARE_PLAINS = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_MARE_PLAINS), new MoonMarePlainsBiome());
    public static final Biome MOON_MARE_ROCKS = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_MARE_ROCKS), new MoonMareRocksBiome());
    public static final Biome MOON_MARE_VALLEY = Registry.register(BuiltinRegistries.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_MARE_VALLEY), new MoonMareValleyBiome());

    public static final Biome[] MOON_BIOMES = new Biome[]{MOON_HIGHLANDS_PLAINS, MOON_HIGHLANDS_ROCKS, MOON_HIGHLANDS_VALLEY, MOON_MARE_PLAINS, MOON_MARE_ROCKS, MOON_MARE_VALLEY};

    public static void register() {
    }
}
