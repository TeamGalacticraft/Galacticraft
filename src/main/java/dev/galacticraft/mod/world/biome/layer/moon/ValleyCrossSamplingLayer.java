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

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public enum ValleyCrossSamplingLayer implements CrossSamplingLayer {
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        int mare = 0;
        int hl = 0;
        if (isMare(n)) {
            mare++;
        } else if (n != MoonBiomeLayer.MOON_MARE_EDGE_ID) {
            hl++;
        }
        if (isMare(s)) {
            mare++;
        } else if (s != MoonBiomeLayer.MOON_MARE_EDGE_ID) {
            hl++;
        }
        if (isMare(e)) {
            mare++;
        } else if (e != MoonBiomeLayer.MOON_MARE_EDGE_ID) {
            hl++;
        }
        if (isMare(w)) {
            mare++;
        } else if (w != MoonBiomeLayer.MOON_MARE_EDGE_ID) {
            hl++;
        }
        if (mare > 2) {
            if (hl > 0) {
                return MoonBiomeLayer.MOON_MARE_EDGE_ID;
            } else {
                return chooseRandom(context, n, e, s, w);
            }
        }
        if (hl == 2 && mare == 2) {
            return MoonBiomeLayer.MOON_HIGHLANDS_VALLEY_ID;
        }
        return chooseRandom(context, n, e, s, w);
    }

    private boolean isMare(int i) {
        return i == MoonBiomeLayer.MOON_MARE_PLAINS_ID || i == MoonBiomeLayer.MOON_MARE_ROCKS_ID;
    }

    private int chooseRandom(LayerRandomnessSource context, int n, int e, int s, int w) {
        int i = context.nextInt(3);
        if (n == MoonBiomeLayer.MOON_MARE_EDGE_ID || n == MoonBiomeLayer.MOON_HIGHLANDS_VALLEY_ID) n -= context.nextInt(1) + 1;
        if (e == MoonBiomeLayer.MOON_MARE_EDGE_ID || e == MoonBiomeLayer.MOON_HIGHLANDS_VALLEY_ID) e -= context.nextInt(1) + 1;
        if (s == MoonBiomeLayer.MOON_MARE_EDGE_ID || s == MoonBiomeLayer.MOON_HIGHLANDS_VALLEY_ID) s -= context.nextInt(1) + 1;
        if (w == MoonBiomeLayer.MOON_MARE_EDGE_ID || w == MoonBiomeLayer.MOON_HIGHLANDS_VALLEY_ID) w -= context.nextInt(1) + 1;
        if (i == 0) {
            return n;
        } else if (i == 1) {
            return e;
        } else if (i == 2) {
            return s;
        } else {
            return w;
        }
    }
}
