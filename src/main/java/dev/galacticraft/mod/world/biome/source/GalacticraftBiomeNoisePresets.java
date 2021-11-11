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

package dev.galacticraft.mod.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.Constant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.Optional;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftBiomeNoisePresets {
    public static final MultiNoiseBiomeSource.Preset MOON = new MultiNoiseBiomeSource.Preset(Constant.id("moon"), (preset, biomeRegistry) -> new MultiNoiseBiomeSource(new MultiNoiseUtil.Entries<>(
            ImmutableList.of(
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> biomeRegistry.getOrThrow(BiomeKeys.NETHER_WASTES)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> biomeRegistry.getOrThrow(BiomeKeys.SOUL_SAND_VALLEY)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> biomeRegistry.getOrThrow(BiomeKeys.CRIMSON_FOREST)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), () -> biomeRegistry.getOrThrow(BiomeKeys.WARPED_FOREST)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), () -> biomeRegistry.getOrThrow(BiomeKeys.BASALT_DELTAS))
            )), Optional.of(Pair.of(biomeRegistry, preset))));

    public static void register() {
        Registry.register(Registry.BIOME_SOURCE, new Identifier(Constant.MOD_ID, "moon"), MoonBiomeSource.CODEC);
    }
}
