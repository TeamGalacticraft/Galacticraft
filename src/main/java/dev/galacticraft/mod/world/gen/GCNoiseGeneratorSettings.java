/*
 * Copyright (c) 2019-2023 Team Galacticraft
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
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.gen.surfacebuilder.MoonSurfaceRules;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GCNoiseGeneratorSettings {
    public static final ResourceKey<NoiseGeneratorSettings> MOON = key("moon");

    public static void bootstrapRegistries(BootstapContext<NoiseGeneratorSettings> context) {
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noiseLookup = context.lookup(Registries.NOISE);

        DensityFunction barrierNoise = DensityFunctions.constant(10);
        DensityFunction fluidLevelFloodednessNoise = DensityFunctions.constant(10);
        DensityFunction fluidLevelSpreadNoise = DensityFunctions.constant(10);
        DensityFunction lavaNoise = DensityFunctions.constant(10);
        DensityFunction temperature = DensityFunctions.constant(10);
        DensityFunction vegetation = DensityFunctions.constant(10);
        DensityFunction continents = DensityFunctions.constant(10);
        DensityFunction erosion = DensityFunctions.constant(10);
        DensityFunction depth = DensityFunctions.noise(noiseLookup.getOrThrow(Noises.NOODLE_THICKNESS));
        DensityFunction ridges = DensityFunctions.constant(10);
        DensityFunction initialDensityWithoutJaggedness = DensityFunctions.constant(10);
        DensityFunction finalDensity = DensityFunctions.constant(10);
        DensityFunction veinToggle = DensityFunctions.constant(10);
        DensityFunction veinRidged = DensityFunctions.constant(10);
        DensityFunction veinGap = DensityFunctions.constant(10);

        context.register(MOON, new NoiseGeneratorSettings(
                NoiseSettings.create(-64, 384, 1, 2),
                GCBlocks.MOON_ROCK.defaultBlockState(),
                Blocks.AIR.defaultBlockState(),
                new NoiseRouter(
                        barrierNoise,
                        fluidLevelFloodednessNoise,
                        fluidLevelSpreadNoise,
                        lavaNoise,
                        temperature,
                        vegetation,
                        continents,
                        erosion,
                        depth,
                        ridges,
                        initialDensityWithoutJaggedness,
                        finalDensity,
                        veinToggle,
                        veinRidged,
                        veinGap
                ),
                MoonSurfaceRules.MOON,
                new OverworldBiomeBuilder().spawnTarget(),
                63,
                false,
                true,
                true,
                false
        ));
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull ResourceKey<NoiseGeneratorSettings> key(String id) {
        return Constant.key(Registries.NOISE_SETTINGS, id);
    }
}
