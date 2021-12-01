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
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public enum MoonBiomeLevelLayer implements ParentedLayer, IdentityCoordinateTransformer {
    INSTANCE;

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        double sample = context.getNoiseSampler().sample(x / 32.0, 0, z / 32.0); //~ 0.7 - -0.7
        if (parent.sample(x, z) == MoonBiomeLayer.MOON_MARE_ID) {
            if (sample >= 0.5) {
                return MoonBiomeLayer.MOON_MARE_HILLS_ID;
            } else if (sample <= -0.45) {
                return MoonBiomeLayer.MOON_MARE_FLAT_ID;
            } else { //+0.49 - -0.44
                return MoonBiomeLayer.MOON_MARE_ID;
            }
        } else {
            if (sample >= 0.5) {
                return MoonBiomeLayer.MOON_HIGHLANDS_HILLS_ID;
            } else if (sample <= -0.45) {
                return MoonBiomeLayer.MOON_HIGHLANDS_FLAT_ID;
            } else { //+0.49 - -0.44
                return MoonBiomeLayer.MOON_HIGHLANDS_ID;
            }
        }
    }
}
