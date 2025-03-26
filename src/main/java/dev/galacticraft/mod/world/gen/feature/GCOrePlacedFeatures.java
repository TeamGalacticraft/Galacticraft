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
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class GCOrePlacedFeatures {
    // OVERWORLD
    public static final ResourceKey<PlacedFeature> ORE_SILICON = key("ore_silicon"); //todo actual ore numbers
    public static final ResourceKey<PlacedFeature> ORE_SILICON_LARGE = key("ore_silicon_large");
    
    public static final ResourceKey<PlacedFeature> ORE_TIN_UPPER = key("ore_tin_upper");
    public static final ResourceKey<PlacedFeature> ORE_TIN_MIDDLE = key("ore_tin_middle");
    public static final ResourceKey<PlacedFeature> ORE_TIN_SMALL = key("ore_tin_small");

    public static final ResourceKey<PlacedFeature> ORE_ALUMINUM_MIDDLE = key("ore_aluminum_middle");
    public static final ResourceKey<PlacedFeature> ORE_ALUMINUM_SMALL = key("ore_aluminum_small");

    // MOON
    public static final ResourceKey<PlacedFeature> BASALT_DISK_MOON = key("disk_basalt_moon");

    public static final ResourceKey<PlacedFeature> ORE_COPPER_MOON = key("ore_copper_moon");
    public static final ResourceKey<PlacedFeature> ORE_COPPER_LARGE_MOON = key("ore_copper_large_moon");

    public static final ResourceKey<PlacedFeature> ORE_TIN_UPPER_MOON = key("ore_tin_upper_moon");
    public static final ResourceKey<PlacedFeature> ORE_TIN_MIDDLE_MOON = key("ore_tin_middle_moon");
    public static final ResourceKey<PlacedFeature> ORE_TIN_SMALL_MOON = key("ore_tin_small_moon");

    public static final ResourceKey<PlacedFeature> ORE_CHEESE_MOON = key("ore_cheese_moon");
    public static final ResourceKey<PlacedFeature> ORE_CHEESE_LARGE_MOON = key("ore_cheese_large_moon");

    public static final ResourceKey<PlacedFeature> ORE_LUNAR_SAPPHIRE_MOON = key("ore_lunar_sapphire_moon");

    public static void register() {
        BiomeModifications.create(Constant.id("ores")).add(ModificationPhase.ADDITIONS, BiomeSelectors.foundInOverworld(), context -> {
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_SILICON);
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_SILICON_LARGE);
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_TIN_UPPER);
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_TIN_MIDDLE);
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_TIN_SMALL);
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_ALUMINUM_MIDDLE);
            context.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_ALUMINUM_SMALL);
        });
    }

    public static void bootstrapRegistries(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> lookup = context.lookup(Registries.CONFIGURED_FEATURE);

        // OVERWORLD
        context.register(ORE_SILICON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_SILICON_SMALL), modifiersWithCount(5, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
        context.register(ORE_SILICON_LARGE, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_SILICON_LARGE), modifiersWithRarity(11, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
        context.register(ORE_TIN_UPPER, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_TIN), modifiersWithCount(99, HeightRangePlacement.triangle(VerticalAnchor.absolute(90), VerticalAnchor.absolute(384)))));
        context.register(ORE_TIN_MIDDLE, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_TIN), modifiersWithCount(11, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(56)))));
        context.register(ORE_TIN_SMALL, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_TIN_SMALL), modifiersWithCount(11, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(80)))));
        context.register(ORE_ALUMINUM_MIDDLE, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_ALUMINUM), modifiersWithCount(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-96), VerticalAnchor.absolute(0)))));
        context.register(ORE_ALUMINUM_SMALL, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_ALUMINUM_SMALL), modifiersWithCount(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(16)))));

        // MOON
        context.register(BASALT_DISK_MOON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.BASALT_DISK_MOON), modifiersWithCount(14, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()))));
        context.register(ORE_COPPER_MOON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_COPPER_SMALL_MOON), modifiersWithCount(18, HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.absolute(112)))));
        context.register(ORE_COPPER_LARGE_MOON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_COPPER_LARGE_MOON), modifiersWithCount(17, HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.absolute(112)))));
        context.register(ORE_TIN_UPPER_MOON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_TIN_MOON), modifiersWithCount(54, HeightRangePlacement.triangle(VerticalAnchor.absolute(90), VerticalAnchor.absolute(384)))));
        context.register(ORE_TIN_MIDDLE_MOON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_TIN_MOON), modifiersWithCount(6, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(56)))));
        context.register(ORE_TIN_SMALL_MOON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_TIN_SMALL_MOON), modifiersWithCount(6, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(80)))));
        context.register(ORE_CHEESE_MOON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_CHEESE_SMALL_MOON), modifiersWithCount(10, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(112)))));
        context.register(ORE_CHEESE_LARGE_MOON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_CHEESE_LARGE_MOON), modifiersWithCount(5, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(112)))));
        context.register(ORE_LUNAR_SAPPHIRE_MOON, new PlacedFeature(lookup.getOrThrow(GCOreConfiguredFeature.ORE_LUNAR_SAPPHIRE_MOON), modifiersWithCount(18, HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(112)))));
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<PlacedFeature> key(String id) {
        return Constant.key(Registries.PLACED_FEATURE, id);
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
