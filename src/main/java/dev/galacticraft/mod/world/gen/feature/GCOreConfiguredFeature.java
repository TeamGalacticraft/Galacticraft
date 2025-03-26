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
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GCOreConfiguredFeature {
    public static final RuleTest BASE_STONE_MOON = new TagMatchTest(GCTags.BASE_STONE_MOON);
    public static final RuleTest STONE_ORE_REPLACEABLE = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    public static final RuleTest DEEPSLATE_ORE_REPLACEABLE = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
    public static final RuleTest MOON_STONE_ORE_REPLACEABLE = new TagMatchTest(GCTags.MOON_STONE_ORE_REPLACEABLES);
    public static final RuleTest LUNASLATE_ORE_REPLACEABLE = new TagMatchTest(GCTags.LUNASLATE_ORE_REPLACEABLES);

    public static final List<OreConfiguration.TargetBlockState> TIN_ORES = List.of(OreConfiguration.target(STONE_ORE_REPLACEABLE, GCBlocks.TIN_ORE.defaultBlockState()), OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLE, GCBlocks.DEEPSLATE_TIN_ORE.defaultBlockState()));
    public static final List<OreConfiguration.TargetBlockState> ALUMINUM_ORES = List.of(OreConfiguration.target(STONE_ORE_REPLACEABLE, GCBlocks.ALUMINUM_ORE.defaultBlockState()), OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLE, GCBlocks.DEEPSLATE_ALUMINUM_ORE.defaultBlockState()));
    public static final List<OreConfiguration.TargetBlockState> SILICON_ORES = List.of(OreConfiguration.target(STONE_ORE_REPLACEABLE, GCBlocks.SILICON_ORE.defaultBlockState()), OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLE, GCBlocks.DEEPSLATE_SILICON_ORE.defaultBlockState()));

    public static final List<OreConfiguration.TargetBlockState> COPPER_ORES_MOON = List.of(OreConfiguration.target(MOON_STONE_ORE_REPLACEABLE, GCBlocks.MOON_COPPER_ORE.defaultBlockState()), OreConfiguration.target(LUNASLATE_ORE_REPLACEABLE, GCBlocks.LUNASLATE_COPPER_ORE.defaultBlockState()));
    public static final List<OreConfiguration.TargetBlockState> TIN_ORES_MOON = List.of(OreConfiguration.target(MOON_STONE_ORE_REPLACEABLE, GCBlocks.MOON_TIN_ORE.defaultBlockState()), OreConfiguration.target(LUNASLATE_ORE_REPLACEABLE, GCBlocks.LUNASLATE_TIN_ORE.defaultBlockState()));
    public static final List<OreConfiguration.TargetBlockState> CHEESE_ORES_MOON = List.of(OreConfiguration.target(MOON_STONE_ORE_REPLACEABLE, GCBlocks.MOON_CHEESE_ORE.defaultBlockState()));
    public static final List<OreConfiguration.TargetBlockState> LUNAR_SAPPHIRE_ORES_MOON = List.of(OreConfiguration.target(MOON_STONE_ORE_REPLACEABLE, GCBlocks.LUNAR_SAPPHIRE_ORE.defaultBlockState()));

    // OVERWORLD
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_SILICON_SMALL = key("ore_silicon_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_SILICON_LARGE = key("ore_silicon_large");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TIN = key("ore_tin");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TIN_SMALL = key("ore_tin_small");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ALUMINUM = key("ore_aluminum");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ALUMINUM_SMALL = key("ore_aluminum_small");

    // MOON
    public static final ResourceKey<ConfiguredFeature<?, ?>> BASALT_DISK_MOON = key("basalt_disk_moon");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COPPER_SMALL_MOON = key("ore_copper_small_moon");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COPPER_LARGE_MOON = key("ore_copper_large_moon");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TIN_MOON = key("ore_tin_moon");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TIN_SMALL_MOON = key("ore_tin_small_moon");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_CHEESE_SMALL_MOON = key("ore_cheese_small_moon");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_CHEESE_LARGE_MOON = key("ore_cheese_large_moon");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_LUNAR_SAPPHIRE_MOON = key("ore_lunar_sapphire_moon");
    
    @Contract(pure = true)
    private static @NotNull ResourceKey<ConfiguredFeature<?, ?>> key(String s) {
        return Constant.key(Registries.CONFIGURED_FEATURE, s);
    }

    public static void bootstrapRegistries(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // OVERWORLD
        context.register(ORE_SILICON_SMALL, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(SILICON_ORES, 6, 0.5F)));
        context.register(ORE_SILICON_LARGE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(SILICON_ORES, 9, 0.7F)));
        context.register(ORE_TIN, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(TIN_ORES, 7)));
        context.register(ORE_TIN_SMALL, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(TIN_ORES, 3)));
        context.register(ORE_ALUMINUM, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ALUMINUM_ORES, 7)));
        context.register(ORE_ALUMINUM_SMALL, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ALUMINUM_ORES, 3)));

        // MOON
        context.register(BASALT_DISK_MOON, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(BASE_STONE_MOON, GCBlocks.MOON_BASALT.defaultBlockState(), 33)));
        context.register(ORE_COPPER_SMALL_MOON, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(COPPER_ORES_MOON, 10)));
        context.register(ORE_COPPER_LARGE_MOON, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(COPPER_ORES_MOON, 20)));
        context.register(ORE_TIN_MOON, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(TIN_ORES_MOON, 6)));
        context.register(ORE_TIN_SMALL_MOON, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(TIN_ORES_MOON, 4)));
        context.register(ORE_CHEESE_SMALL_MOON, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(CHEESE_ORES_MOON, 4)));
        context.register(ORE_CHEESE_LARGE_MOON, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(CHEESE_ORES_MOON, 9)));
        context.register(ORE_LUNAR_SAPPHIRE_MOON, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(LUNAR_SAPPHIRE_ORES_MOON, 3)));
    }
}
