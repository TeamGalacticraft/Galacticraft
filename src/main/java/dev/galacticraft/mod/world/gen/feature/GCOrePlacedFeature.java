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

package dev.galacticraft.mod.world.gen.feature;

import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class GCOrePlacedFeature {
    // OVERWORLD
    public static final Holder<PlacedFeature> ORE_SILICON = register("ore_silicon", new PlacedFeature(GCOreConfiguredFeature.ORE_SILICON_SMALL, modifiersWithCount(5, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))))); //todo actual ore numbers
    public static final Holder<PlacedFeature> ORE_SILICON_LARGE = register("ore_silicon_large", new PlacedFeature(GCOreConfiguredFeature.ORE_SILICON_LARGE, modifiersWithRarity(11, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
    
    public static final Holder<PlacedFeature> ORE_TIN_UPPER = register("ore_tin_upper", new PlacedFeature(GCOreConfiguredFeature.ORE_TIN, modifiersWithCount(99, HeightRangePlacement.triangle(VerticalAnchor.absolute(90), VerticalAnchor.absolute(384)))));
    public static final Holder<PlacedFeature> ORE_TIN_MIDDLE = register("ore_tin_middle", new PlacedFeature(GCOreConfiguredFeature.ORE_TIN, modifiersWithCount(11, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(56)))));
    public static final Holder<PlacedFeature> ORE_TIN_SMALL = register("ore_tin_small", new PlacedFeature(GCOreConfiguredFeature.ORE_TIN_SMALL, modifiersWithCount(11, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(80)))));

    public static final Holder<PlacedFeature> ORE_ALUMINUM_MIDDLE = register("ore_aluminum_middle", new PlacedFeature(GCOreConfiguredFeature.ORE_ALUMINUM, modifiersWithCount(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-96), VerticalAnchor.absolute(0)))));
    public static final Holder<PlacedFeature> ORE_ALUMINUM_SMALL = register("ore_aluminum_small", new PlacedFeature(GCOreConfiguredFeature.ORE_ALUMINUM_SMALL, modifiersWithCount(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(16)))));

    // MOON
    public static final Holder<PlacedFeature> BASALT_DISK_MOON = register("disk_basalt_moon", new PlacedFeature(GCOreConfiguredFeature.BASALT_DISK_MOON, modifiersWithCount(14, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()))));

    public static final Holder<PlacedFeature> ORE_COPPER_MOON = register("ore_copper_moon", new PlacedFeature(GCOreConfiguredFeature.ORE_COPPER_SMALL_MOON, modifiersWithCount(18, HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(112)))));
    public static final Holder<PlacedFeature> ORE_COPPER_LARGE_MOON = register("ore_copper_large_moon", new PlacedFeature(GCOreConfiguredFeature.ORE_COPPER_LARGE_MOON, modifiersWithCount(17, HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(112)))));

    public static final Holder<PlacedFeature> ORE_TIN_UPPER_MOON = register("ore_tin_upper_moon", new PlacedFeature(GCOreConfiguredFeature.ORE_TIN_MOON, modifiersWithCount(54, HeightRangePlacement.triangle(VerticalAnchor.absolute(90), VerticalAnchor.absolute(384)))));
    public static final Holder<PlacedFeature> ORE_TIN_MIDDLE_MOON = register("ore_tin_middle_moon", new PlacedFeature(GCOreConfiguredFeature.ORE_TIN_MOON, modifiersWithCount(6, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(56)))));
    public static final Holder<PlacedFeature> ORE_TIN_SMALL_MOON = register("ore_tin_small_moon", new PlacedFeature(GCOreConfiguredFeature.ORE_TIN_SMALL_MOON, modifiersWithCount(6, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(80)))));

    public static void register() {
        BiomeModifications.create(Constant.id("ores")).add(ModificationPhase.ADDITIONS, BiomeSelectors.foundInOverworld(), context -> {
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_SILICON.value());
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_SILICON_LARGE.value());
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_TIN_UPPER.value());
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_TIN_MIDDLE.value());
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_TIN_SMALL.value());
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_ALUMINUM_MIDDLE.value());
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_ALUMINUM_SMALL.value());
        });
    }

    private static Holder<PlacedFeature> register(String id, PlacedFeature feature) {
        return BuiltInRegistries.register(BuiltInRegistries.PLACED_FEATURE, Constant.id(id), feature);
    }

    private static @Unmodifiable List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, InSquarePlacement.spread(), heightModifier, BiomeFilter.biome());
    }

    private static @Unmodifiable List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModfier) {
        return modifiers(CountPlacement.of(count), heightModfier);
    }

    private static @Unmodifiable List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
        return modifiers(RarityFilter.onAverageOnceEvery(chance), heightModifier);
    }
}
