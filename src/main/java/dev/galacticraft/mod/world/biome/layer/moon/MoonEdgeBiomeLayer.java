/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public enum MoonEdgeBiomeLayer implements CrossSamplingLayer {
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        int mare = 0;
        if (isInnerMare(center) || isInnerHighlands(center)) return center;
        if (areAnyInnerMare(n, e, s, w)) {
            if (isMare(n)) {
                return n;
            } else if (isMare(e)) {
                return e;
            } else if (isMare(s)) {
                return s;
            } else if (isMare(w)) {
                return w;
            }
        }
        if (areAnyInnerHighlands(n, e, s, w)) {
            if (isHighlands(n)) {
                return n;
            } else if (isHighlands(e)) {
                return e;
            } else if (isHighlands(s)) {
                return s;
            } else if (isHighlands(w)) {
                return w;
            }
        }
        if (isMare(n)) mare++;
        if (isMare(s)) mare++;
        if (isMare(e)) mare++;
        if (isMare(w)) mare++;
        if (mare > 0 && mare < 4) {
            if (mare > 2) {
                return MoonBiomeLayer.MOON_MARE_EDGE_ID;
            }
            return MoonBiomeLayer.MOON_HIGHLANDS_EDGE_ID;
        }
        return center;
    }

    private static boolean areAnyInnerMare(int n, int e, int s, int w) {
        return isInnerMare(n) || isInnerMare(e) || isInnerMare(s) || isInnerMare(w);
    }

    private static boolean areAnyInnerHighlands(int n, int e, int s, int w) {
        return isInnerHighlands(n) || isInnerHighlands(e) || isInnerHighlands(s) || isInnerHighlands(w);
    }

    private static boolean isMare(int id) {
        return id == MoonBiomeLayer.MOON_MARE_ID || id == MoonBiomeLayer.MOON_MARE_FLAT_ID || id == MoonBiomeLayer.MOON_MARE_HILLS_ID;
    }

    private static boolean isHighlands(int id) {
        return id == MoonBiomeLayer.MOON_HIGHLANDS_ID || id == MoonBiomeLayer.MOON_HIGHLANDS_FLAT_ID || id == MoonBiomeLayer.MOON_HIGHLANDS_HILLS_ID;
    }

    private static boolean isInnerMare(int id) {
        return id == MoonBiomeLayer.MOON_MARE_VALLEY_ID;
    }

    private static boolean isInnerHighlands(int id) {
        return id == MoonBiomeLayer.MOON_HIGHLANDS_VALLEY_ID;
    }
}
