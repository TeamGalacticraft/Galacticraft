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

package dev.galacticraft.mod.world.gen.feature;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftFeature {
    public static final RegistryKey<ConfiguredFeature<?, ?>> OIL_LAKE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(Constant.MOD_ID, "oil_lake"));
    public static final ConfiguredFeature<?, ?> OIL_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(GalacticraftBlock.CRUDE_OIL.getDefaultState())).decorate(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(70)));

    public static void register() {
        BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, OIL_LAKE_KEY.getValue(), OIL_LAKE);
        BiomeModifications.addFeature(biomeSelectionContext -> biomeSelectionContext.hasFeature(RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier("lake_lava"))), GenerationStep.Feature.LAKES, OIL_LAKE_KEY);
    }
}
