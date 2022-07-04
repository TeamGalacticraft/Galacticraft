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
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.*;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class GalacticraftOrePlacedFeature {
    // OVERWORLD
    public static final RegistryEntry<PlacedFeature> ORE_SILICON = register("ore_silicon", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_SILICON_SMALL, modifiersWithCount(5, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))))); //todo actual ore numbers
    public static final RegistryEntry<PlacedFeature> ORE_SILICON_LARGE = register("ore_silicon_large", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_SILICON_LARGE, modifiersWithRarity(11, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80)))));
    
    public static final RegistryEntry<PlacedFeature> ORE_TIN_UPPER = register("ore_tin_upper", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_TIN, modifiersWithCount(99, HeightRangePlacementModifier.trapezoid(YOffset.fixed(90), YOffset.fixed(384)))));
    public static final RegistryEntry<PlacedFeature> ORE_TIN_MIDDLE = register("ore_tin_middle", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_TIN, modifiersWithCount(11, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(56)))));
    public static final RegistryEntry<PlacedFeature> ORE_TIN_SMALL = register("ore_tin_small", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_TIN_SMALL, modifiersWithCount(11, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(80)))));

    public static final RegistryEntry<PlacedFeature> ORE_ALUMINUM_MIDDLE = register("ore_aluminum_middle", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_ALUMINUM, modifiersWithCount(10, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-96), YOffset.fixed(0)))));
    public static final RegistryEntry<PlacedFeature> ORE_ALUMINUM_SMALL = register("ore_aluminum_small", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_ALUMINUM_SMALL, modifiersWithCount(10, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(16)))));

    // MOON
    public static final RegistryEntry<PlacedFeature> BASALT_DISK_MOON = register("disk_basalt_moon", new PlacedFeature(GalacticraftOreConfiguredFeature.BASALT_DISK_MOON, modifiersWithCount(14, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop()))));

    public static final RegistryEntry<PlacedFeature> ORE_COPPER_MOON = register("ore_copper_moon", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_COPPER_SMALL_MOON, modifiersWithCount(18, HeightRangePlacementModifier.trapezoid(YOffset.fixed(0), YOffset.fixed(112)))));
    public static final RegistryEntry<PlacedFeature> ORE_COPPER_LARGE_MOON = register("ore_copper_large_moon", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_COPPER_LARGE_MOON, modifiersWithCount(17, HeightRangePlacementModifier.trapezoid(YOffset.fixed(0), YOffset.fixed(112)))));

    public static final RegistryEntry<PlacedFeature> ORE_TIN_UPPER_MOON = register("ore_tin_upper_moon", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_TIN_MOON, modifiersWithCount(54, HeightRangePlacementModifier.trapezoid(YOffset.fixed(90), YOffset.fixed(384)))));
    public static final RegistryEntry<PlacedFeature> ORE_TIN_MIDDLE_MOON = register("ore_tin_middle_moon", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_TIN_MOON, modifiersWithCount(6, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(56)))));
    public static final RegistryEntry<PlacedFeature> ORE_TIN_SMALL_MOON = register("ore_tin_small_moon", new PlacedFeature(GalacticraftOreConfiguredFeature.ORE_TIN_SMALL_MOON, modifiersWithCount(6, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(80)))));

    public static void register() {
        BiomeModifications.create(Constant.id("ores")).add(ModificationPhase.ADDITIONS, BiomeSelectors.foundInOverworld(), context -> {
            context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_SILICON.value());
            context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_SILICON_LARGE.value());
            context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_TIN_UPPER.value());
            context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_TIN_MIDDLE.value());
            context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_TIN_SMALL.value());
            context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_ALUMINUM_MIDDLE.value());
            context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.UNDERGROUND_ORES, ORE_ALUMINUM_SMALL.value());
        });
    }

    private static RegistryEntry<PlacedFeature> register(String id, PlacedFeature feature) {
        return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, Constant.id(id), feature);
    }

    private static @Unmodifiable List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
    }

    private static @Unmodifiable List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModfier) {
        return modifiers(CountPlacementModifier.of(count), heightModfier);
    }

    private static @Unmodifiable List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
        return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
    }
}
