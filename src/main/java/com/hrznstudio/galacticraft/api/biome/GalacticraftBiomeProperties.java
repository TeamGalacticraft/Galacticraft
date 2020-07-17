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

package com.hrznstudio.galacticraft.api.biome;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBiomeProperties {
    public static final BiomePropertyType<Boolean> IS_MARE = Registry.register(Galacticraft.BIOME_PROPERTY_TYPE_REGISTRY, new Identifier(Constants.MOD_ID, "is_mare"),
            new BiomePropertyType.Builder<Boolean>().defaultValue(false).name(new TranslatableText("galacticraft-rewoven.is_moon_mare")).build());

    public static final BiomePropertyType<Boolean> IS_SPACE_BIOME = Registry.register(Galacticraft.BIOME_PROPERTY_TYPE_REGISTRY, new Identifier(Constants.MOD_ID, "is_space"),
            new BiomePropertyType.Builder<Boolean>().defaultValue(false).name(new TranslatableText("galacticraft-rewoven.is_space_biome")).build());

    public static final BiomePropertyType<Boolean> HAS_CRATERS = Registry.register(Galacticraft.BIOME_PROPERTY_TYPE_REGISTRY, new Identifier(Constants.MOD_ID, "has_craters"),
            new BiomePropertyType.Builder<Boolean>().defaultValue(false).name(new TranslatableText("galacticraft-rewoven.has_craters")).build());

    public static final BiomePropertyType<Double> CRATER_CHANCE = Registry.register(Galacticraft.BIOME_PROPERTY_TYPE_REGISTRY, new Identifier(Constants.MOD_ID, "crater_chance"),
            new BiomePropertyType.Builder<Double>().defaultValue(300.0D).name(new TranslatableText("galacticraft-rewoven.crater_chance")).build());

    public static void register() {

    }
}
