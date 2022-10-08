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
import dev.galacticraft.mod.block.GCBlocks;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import java.util.List;

public class GCOreConfiguredFeature {
    public static final RuleTest BASE_STONE_MOON = new TagMatchTest(GCTags.BASE_STONE_MOON);
    public static final RuleTest MOON_STONE_ORE_REPLACEABLES = new TagMatchTest(GCTags.MOON_STONE_ORE_REPLACABLES);
    public static final RuleTest LUNASLATE_ORE_REPLACEABLES = new TagMatchTest(GCTags.LUNASLATE_ORE_REPLACABLES);

    public static final List<OreConfiguration.TargetBlockState> TIN_ORES = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, GCBlocks.TIN_ORE.defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, GCBlocks.DEEPSLATE_TIN_ORE.defaultBlockState()));
    public static final List<OreConfiguration.TargetBlockState> ALUMINUM_ORES = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, GCBlocks.ALUMINUM_ORE.defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, GCBlocks.DEEPSLATE_ALUMINUM_ORE.defaultBlockState()));
    public static final List<OreConfiguration.TargetBlockState> SILICON_ORES = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, GCBlocks.SILICON_ORE.defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, GCBlocks.DEEPSLATE_SILICON_ORE.defaultBlockState()));

    public static final List<OreConfiguration.TargetBlockState> COPPER_ORES_MOON = List.of(OreConfiguration.target(MOON_STONE_ORE_REPLACEABLES, GCBlocks.MOON_COPPER_ORE.defaultBlockState()), OreConfiguration.target(LUNASLATE_ORE_REPLACEABLES, GCBlocks.LUNASLATE_COPPER_ORE.defaultBlockState()));
    public static final List<OreConfiguration.TargetBlockState> TIN_ORES_MOON = List.of(OreConfiguration.target(MOON_STONE_ORE_REPLACEABLES, GCBlocks.TIN_ORE.defaultBlockState()), OreConfiguration.target(LUNASLATE_ORE_REPLACEABLES, GCBlocks.LUNASLATE_TIN_ORE.defaultBlockState()));

    // OVERWORLD
    public static final Holder<ConfiguredFeature<?, ?>> ORE_SILICON_SMALL = register("ore_silicon_small", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(SILICON_ORES, 6, 0.5F)));
    public static final Holder<ConfiguredFeature<?, ?>> ORE_SILICON_LARGE = register("ore_silicon_large", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(SILICON_ORES, 9, 0.7F)));

    public static final Holder<ConfiguredFeature<?, ?>> ORE_TIN = register("ore_tin", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(TIN_ORES, 7)));
    public static final Holder<ConfiguredFeature<?, ?>> ORE_TIN_SMALL = register("ore_tin_small", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(TIN_ORES, 3)));

    public static final Holder<ConfiguredFeature<?, ?>> ORE_ALUMINUM = register("ore_aluminum", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ALUMINUM_ORES, 7)));
    public static final Holder<ConfiguredFeature<?, ?>> ORE_ALUMINUM_SMALL = register("ore_aluminum_small", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ALUMINUM_ORES, 3)));

    // MOON
    public static final Holder<ConfiguredFeature<?, ?>> BASALT_DISK_MOON = register("basalt_disk_moon", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(BASE_STONE_MOON, GCBlocks.MOON_BASALT.defaultBlockState(), 33)));

    public static final Holder<ConfiguredFeature<?, ?>> ORE_COPPER_SMALL_MOON = register("ore_copper_small_moon", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(COPPER_ORES_MOON, 10)));
    public static final Holder<ConfiguredFeature<?, ?>> ORE_COPPER_LARGE_MOON = register("ore_copper_large_moon", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(COPPER_ORES_MOON, 20)));

    public static final Holder<ConfiguredFeature<?, ?>> ORE_TIN_MOON = register("ore_tin_moon", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(TIN_ORES_MOON, 6)));
    public static final Holder<ConfiguredFeature<?, ?>> ORE_TIN_SMALL_MOON = register("ore_tin_small_moon", new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(TIN_ORES_MOON, 4)));

    public static void register() {}

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(String id, ConfiguredFeature<FC, F> feature) {
        return BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, Constant.id(id), feature);
    }
}
