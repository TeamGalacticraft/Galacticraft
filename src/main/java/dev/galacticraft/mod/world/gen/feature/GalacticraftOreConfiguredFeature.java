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
import dev.galacticraft.mod.tag.GalacticraftTag;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;

import java.util.List;

public class GalacticraftOreConfiguredFeature {
    public static final RuleTest BASE_STONE_MOON = new TagMatchRuleTest(GalacticraftTag.BASE_STONE_MOON);
    public static final RuleTest MOON_STONE_ORE_REPLACEABLES = new TagMatchRuleTest(GalacticraftTag.MOON_STONE_ORE_REPLACABLES);
    public static final RuleTest LUNASLATE_ORE_REPLACEABLES = new TagMatchRuleTest(GalacticraftTag.LUNASLATE_ORE_REPLACABLES);

    public static final List<OreFeatureConfig.Target> TIN_ORES = List.of(OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, GalacticraftBlock.TIN_ORE.getDefaultState()), OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, GalacticraftBlock.DEEPSLATE_TIN_ORE.getDefaultState()));
    public static final List<OreFeatureConfig.Target> ALUMINUM_ORES = List.of(OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, GalacticraftBlock.ALUMINUM_ORE.getDefaultState()), OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, GalacticraftBlock.DEEPSLATE_ALUMINUM_ORE.getDefaultState()));
    public static final List<OreFeatureConfig.Target> SILICON_ORES = List.of(OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, GalacticraftBlock.SILICON_ORE.getDefaultState()), OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, GalacticraftBlock.DEEPSLATE_SILICON_ORE.getDefaultState()));

    public static final List<OreFeatureConfig.Target> COPPER_ORES_MOON = List.of(OreFeatureConfig.createTarget(MOON_STONE_ORE_REPLACEABLES, GalacticraftBlock.MOON_COPPER_ORE.getDefaultState()), OreFeatureConfig.createTarget(LUNASLATE_ORE_REPLACEABLES, GalacticraftBlock.LUNASLATE_COPPER_ORE.getDefaultState()));
    public static final List<OreFeatureConfig.Target> TIN_ORES_MOON = List.of(OreFeatureConfig.createTarget(MOON_STONE_ORE_REPLACEABLES, GalacticraftBlock.TIN_ORE.getDefaultState()), OreFeatureConfig.createTarget(LUNASLATE_ORE_REPLACEABLES, GalacticraftBlock.LUNASLATE_TIN_ORE.getDefaultState()));

    // OVERWORLD
    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_SILICON_SMALL = register("ore_silicon_small", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(SILICON_ORES, 6, 0.5F)));
    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_SILICON_LARGE = register("ore_silicon_large", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(SILICON_ORES, 9, 0.7F)));

    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_TIN = register("ore_tin", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(TIN_ORES, 7)));
    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_TIN_SMALL = register("ore_tin_small", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(TIN_ORES, 3)));

    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_ALUMINUM = register("ore_aluminum", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(ALUMINUM_ORES, 7)));
    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_ALUMINUM_SMALL = register("ore_aluminum_small", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(ALUMINUM_ORES, 3)));

    // MOON
    public static final RegistryEntry<ConfiguredFeature<?, ?>> BASALT_DISK_MOON = register("basalt_disk_moon", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(BASE_STONE_MOON, GalacticraftBlock.MOON_BASALT.getDefaultState(), 33)));

    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_COPPER_SMALL_MOON = register("ore_copper_small_moon", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(COPPER_ORES_MOON, 10)));
    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_COPPER_LARGE_MOON = register("ore_copper_large_moon", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(COPPER_ORES_MOON, 20)));

    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_TIN_MOON = register("ore_tin_moon", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(TIN_ORES_MOON, 6)));
    public static final RegistryEntry<ConfiguredFeature<?, ?>> ORE_TIN_SMALL_MOON = register("ore_tin_small_moon", new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(TIN_ORES_MOON, 4)));

    public static void register() {}

    private static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<?, ?>> register(String id, ConfiguredFeature<FC, F> feature) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, Constant.id(id), feature);
    }
}
