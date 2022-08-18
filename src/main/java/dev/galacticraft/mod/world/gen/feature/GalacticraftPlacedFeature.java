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
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.List;

public class GalacticraftPlacedFeature {
    public static final ResourceKey<PlacedFeature> OIL_LAKE_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(Constant.MOD_ID, "oil_lake"));

    public static final Holder<PlacedFeature> OIL_LAKE = register(OIL_LAKE_KEY, new PlacedFeature(GalacticraftConfiguredFeature.OIL_LAKE, List.of(
            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
            RarityFilter.onAverageOnceEvery(70),
            InSquarePlacement.spread(),
            BiomeFilter.biome()
    )));

    public static Holder<PlacedFeature> register(ResourceKey<PlacedFeature> id, PlacedFeature placedFeature) {
        return BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, id, placedFeature);
    }

    public static void register() {
        BiomeModifications.addFeature(context -> context.hasFeature(ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation("lake_lava"))), GenerationStep.Decoration.LAKES, ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, GalacticraftConfiguredFeature.OIL_LAKE_KEY.location()));
    }
}
