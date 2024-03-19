/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.world.dimension;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.biome.source.GCMultiNoiseBiomeSourceParameterLists;
import dev.galacticraft.mod.world.gen.GCNoiseGeneratorSettings;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GCLevelStems {
    public static final ResourceKey<LevelStem> MOON = key("moon");
    public static final ResourceKey<LevelStem> VENUS = key("venus");

    public static void bootstrapRegistries(@NotNull BootstapContext<LevelStem> context) {
        HolderGetter<DimensionType> typeLookup = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
        HolderGetter<NoiseGeneratorSettings> noiseLookup = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<MultiNoiseBiomeSourceParameterList> biomeNoiseLookup = context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);

        // the returned reference may be null
//        context.register(MOON, new LevelStem(typeLookup.getOrThrow(GCDimensionTypes.MOON), new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(biomeNoiseLookup.getOrThrow(GCMultiNoiseBiomeSourceParameterLists.MOON)), noiseLookup.getOrThrow(GCNoiseGeneratorSettings.MOON))));
        context.register(VENUS, new LevelStem(typeLookup.getOrThrow(GCDimensionTypes.VENUS), new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(biomeNoiseLookup.getOrThrow(GCMultiNoiseBiomeSourceParameterLists.VENUS)), noiseLookup.getOrThrow(GCNoiseGeneratorSettings.VENUS))));
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull ResourceKey<LevelStem> key(@NotNull String id) {
        return Constant.key(Registries.LEVEL_STEM, id);
    }
}
