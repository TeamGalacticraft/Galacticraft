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

package com.hrznstudio.galacticraft.world.biome.layer.moon;

import com.hrznstudio.galacticraft.world.biome.moon.mare.MoonMareBiome;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonValleyLayer implements CrossSamplingLayer {
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        if (n != s && e != w) {
            int mare = 0;
            if (BuiltinRegistries.BIOME.get(n) instanceof MoonMareBiome) {
                mare++;
            }
            if (BuiltinRegistries.BIOME.get(s) instanceof MoonMareBiome) {
                mare++;
            }
            if (BuiltinRegistries.BIOME.get(e) instanceof MoonMareBiome) {
                mare++;
            }
            if (BuiltinRegistries.BIOME.get(w) instanceof MoonMareBiome) {
                mare++;
            }
            if (mare == 2) return context.nextInt(2) == 0 ? MoonBiomeLayers.MOON_MARE_VALLEY_ID : MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID;
            return mare < 2 ? MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID : MoonBiomeLayers.MOON_MARE_VALLEY_ID;
        }

        if (n == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID || n == MoonBiomeLayers.MOON_MARE_VALLEY_ID && context.nextInt(3) == 0) {
            return n;
        }

        if (s == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID || s == MoonBiomeLayers.MOON_MARE_VALLEY_ID && context.nextInt(3) == 0) {
            return s;
        }

        if (e == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID || e == MoonBiomeLayers.MOON_MARE_VALLEY_ID && context.nextInt(3) == 0) {
            return e;
        }

        if (w == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID || w == MoonBiomeLayers.MOON_MARE_VALLEY_ID && context.nextInt(3) == 0) {
            return w;
        }

        switch (context.nextInt(3)) {
            case 0:
                return n;
            case 1:
                return e;
            case 2:
                return s;
            case 3:
                return w;
        }
        return center;
    }
}
