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

package dev.galacticraft.mod.world.gen.feature;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.feature.features.BasaltBeamFeature;
import dev.galacticraft.mod.world.gen.feature.features.OlivineBeamFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GCFeatures {
    public static Feature<NoneFeatureConfiguration> OLIVINE_BEAM;
    public static Feature<NoneFeatureConfiguration> BASALT_BEAM;

    public static final ResourceKey<Feature<?>> OLIVINE_BEAM_KEY = ResourceKey.create(Registries.FEATURE, Constant.id("olivine_beam"));
    public static final ResourceKey<Feature<?>> BASALT_BEAM_KEY = ResourceKey.create(Registries.FEATURE, Constant.id("basalt_beam"));

    public static void register() {
        OLIVINE_BEAM = Registry.register(BuiltInRegistries.FEATURE, OLIVINE_BEAM_KEY, new OlivineBeamFeature(NoneFeatureConfiguration.CODEC));
        BASALT_BEAM = Registry.register(BuiltInRegistries.FEATURE, BASALT_BEAM_KEY, new BasaltBeamFeature(NoneFeatureConfiguration.CODEC));
    }
}
