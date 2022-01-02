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
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum MoonInnerLayer implements ParentedLayer, IdentityCoordinateTransformer {
    INSTANCE;

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int sample = parent.sample(x, z);
        double noise = context.getNoiseSampler().sample(x / 4.0, 0, z / 4.0);
        if (Math.abs(noise) >= 0.65) {
            if (isMare(sample)) {
                return MoonBiomeLayer.MOON_MARE_VALLEY_ID;
            } else {
                return MoonBiomeLayer.MOON_HIGHLANDS_VALLEY_ID;
            }
        }
        return sample;
    }

    private static boolean isMare(int id) {
        return id == MoonBiomeLayer.MOON_MARE_ID || id == MoonBiomeLayer.MOON_MARE_FLAT_ID || id == MoonBiomeLayer.MOON_MARE_HILLS_ID || id == MoonBiomeLayer.MOON_MARE_EDGE_ID;
    }
}
