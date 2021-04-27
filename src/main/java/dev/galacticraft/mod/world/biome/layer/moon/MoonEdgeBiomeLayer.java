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

package dev.galacticraft.mod.world.biome.layer.moon;

import dev.galacticraft.mod.world.biome.layer.MoonBiomeLayer;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonEdgeBiomeLayer implements CrossSamplingLayer {
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        int mare = 0;
        int highland = 0;
        if (isMare(n)) mare++;
        else if (isHighland(n)) highland++;
        if (isMare(s)) mare++;
        else if (isHighland(n)) highland++;
        if (isMare(e)) mare++;
        else if (isHighland(n)) highland++;
        if (isMare(w)) mare++;
        else if (isHighland(n)) highland++;
        if (mare > 0 && mare < 4 && highland > 0) {
            if (isMare(center)) {
                return MoonBiomeLayer.MOON_MARE_EDGE_ID;
            }
            return MoonBiomeLayer.MOON_HIGHLANDS_EDGE_ID;
        }
        return center;
    }

    private static boolean isMare(int id) {
        return id == MoonBiomeLayer.MOON_MARE_ID;
    }

    private static boolean isHighland(int id) {
        return id == MoonBiomeLayer.MOON_HIGHLANDS_ID;
    }
}
