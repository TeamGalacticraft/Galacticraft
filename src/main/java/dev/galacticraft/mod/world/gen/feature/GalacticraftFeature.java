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

package dev.galacticraft.mod.world.gen.feature;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftFeature {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OIL_LAKE_KEY = ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(Constant.MOD_ID, "oil_lake"));
    public static final Holder<ConfiguredFeature<?, ?>> OIL_LAKE = register(OIL_LAKE_KEY, new ConfiguredFeature<>(Feature.LAKE, new LakeFeature.Configuration(
            BlockStateProvider.simple(GalacticraftBlock.CRUDE_OIL),
            BlockStateProvider.simple(Blocks.STONE.defaultBlockState()))
    ));

    public static Holder<ConfiguredFeature<?, ?>> register(ResourceKey<ConfiguredFeature<?, ?>> id, ConfiguredFeature<?, ?> configuredFeature) {
        return BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, id.location(), configuredFeature);
    }

    public static void register() {
        BiomeModifications.addFeature(biomeSelectionContext -> biomeSelectionContext.hasFeature(ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation("lake_lava"))), GenerationStep.Decoration.LAKES, GalacticraftPlacedFeature.OIL_LAKE_KEY);
    }
}
