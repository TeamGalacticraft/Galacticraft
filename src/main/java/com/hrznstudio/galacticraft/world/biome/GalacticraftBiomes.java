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

package com.hrznstudio.galacticraft.world.biome;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.biome.moon.MoonBiome;
import com.hrznstudio.galacticraft.world.biome.moon.highlands.MoonHighlandsCratersBiome;
import com.hrznstudio.galacticraft.world.biome.moon.highlands.MoonHighlandsPlainsBiome;
import com.hrznstudio.galacticraft.world.biome.moon.highlands.MoonHighlandsRocksBiome;
import com.hrznstudio.galacticraft.world.biome.moon.mare.MoonMareCratersBiome;
import com.hrznstudio.galacticraft.world.biome.moon.mare.MoonMarePlainsBiome;
import com.hrznstudio.galacticraft.world.biome.moon.mare.MoonMareRocksBiome;
import com.hrznstudio.galacticraft.world.biome.moon.misc.MoonCheeseForestBiome;
import com.hrznstudio.galacticraft.world.biome.moon.misc.MoonValleyBiome;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBiomes {

    public static final Biome MOON_VALLEY = Registry.register(Registry.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON), new MoonValleyBiome());

    public static final Biome MOON_HIGHLANDS_PLAINS = Registry.register(Registry.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_HIGHLANDS_PLAINS), new MoonHighlandsPlainsBiome());
    public static final Biome MOON_HIGHLANDS_CRATERS = Registry.register(Registry.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_HIGHLANDS_CRATERS), new MoonHighlandsCratersBiome());
    public static final Biome MOON_HIGHLANDS_ROCKS = Registry.register(Registry.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_HIGHLANDS_ROCKS), new MoonHighlandsRocksBiome());

    public static final Biome MOON_MARE_PLAINS = Registry.register(Registry.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_MARE_PLAINS), new MoonMarePlainsBiome());
    public static final Biome MOON_MARE_CRATERS = Registry.register(Registry.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_MARE_CRATERS), new MoonMareCratersBiome());
    public static final Biome MOON_MARE_ROCKS = Registry.register(Registry.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_MARE_ROCKS), new MoonMareRocksBiome());

    public static final Biome MOON_CHEESE_FOREST = Registry.register(Registry.BIOME, new Identifier(Constants.MOD_ID, Constants.Biomes.MOON_CHEESE_FOREST), new MoonCheeseForestBiome());

    public static void register() {
    }
}
