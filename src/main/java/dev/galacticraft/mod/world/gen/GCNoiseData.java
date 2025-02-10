/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.world.gen;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class GCNoiseData {
    public static final ResourceKey<NormalNoise.NoiseParameters> EROSION = createKey("moon/erosion");
    public static final ResourceKey<NormalNoise.NoiseParameters> BASALT_MARE = createKey("moon/basalt_mare");
    public static final ResourceKey<NormalNoise.NoiseParameters> BASALT_MARE_HEIGHT = createKey("moon/basalt_mare_height");

    private static ResourceKey<NormalNoise.NoiseParameters> createKey(String id) {
        return ResourceKey.create(Registries.NOISE, Constant.id(id));
    }

    public static void bootstrapRegistries(BootstrapContext<NormalNoise.NoiseParameters> context) {
//        register(context, EROSION, -11, 1, 1, 0, 1, 1);
//        register(context, BASALT_MARE, 5, 0, 0.1, 0.2, 0.1, 0, 0, 0, 0);
//        register(context, BASALT_MARE_HEIGHT, -12, 0.3);
    }

    private static void register(
            BootstrapContext<NormalNoise.NoiseParameters> context,
            ResourceKey<NormalNoise.NoiseParameters> key,
            int firstOctave,
            double amplitude,
            double... otherAmplitudes
    ) {
        context.register(key, new NormalNoise.NoiseParameters(firstOctave, amplitude, otherAmplitudes));
    }
}
