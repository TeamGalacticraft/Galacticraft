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

package dev.galacticraft.mod.structure;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.feature.GalacticraftConfiguredFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.List;

public class GalacticraftPlacedFeature {
    public static final RegistryEntry<PlacedFeature> OIL_LAKE = BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, new Identifier(Constant.MOD_ID, "oil_lake"), new PlacedFeature(
            RegistryEntry.upcast(GalacticraftConfiguredFeature.OIL_LAKE),
            List.of(RarityFilterPlacementModifier.of(200),
                    SquarePlacementModifier.of(),
                    PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
                    BiomePlacementModifier.of())
    ));

    public static void register() {
        BiomeModifications.addFeature(context -> context.hasFeature(RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier("lake_lava"))), GenerationStep.Feature.LAKES, RegistryKey.of(Registry.PLACED_FEATURE_KEY, GalacticraftConfiguredFeature.OIL_LAKE_KEY.getValue()));
    }
}
