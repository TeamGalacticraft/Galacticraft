/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.GCBiomePropertyAccessor;
import com.hrznstudio.galacticraft.api.biome.GalacticraftBiomeProperties;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.sounds.GalacticraftSounds;
import com.hrznstudio.galacticraft.structure.moon_village.MoonVillageData;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftStructureFeatures;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.BlockStateWithChance;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.MultiBlockSurfaceConfig;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.MusicSound;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftDefaultBiomeCreators {
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MOON_HIGHLANDS_CONFIGURED_SURFACE_BUILDER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier(Constants.MOD_ID, "moon_highlands"), GalacticraftSurfaceBuilders.MOON.method_30478(new TernarySurfaceConfig(GalacticraftBlocks.MOON_TURF.getDefaultState(), GalacticraftBlocks.MOON_DIRT.getDefaultState(), GalacticraftBlocks.MOON_TURF.getDefaultState())));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MOON_HIGHLANDS_ROCK_CONFIGURED_SURFACE_BUILDER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier(Constants.MOD_ID, "moon_highlands_rock"), GalacticraftSurfaceBuilders.MOON.method_30478(new TernarySurfaceConfig(GalacticraftBlocks.MOON_ROCK.getDefaultState(), GalacticraftBlocks.MOON_ROCK.getDefaultState(), GalacticraftBlocks.MOON_ROCK.getDefaultState())));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MOON_MARE_CONFIGURED_SURFACE_BUILDER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier(Constants.MOD_ID, "moon_mare"), GalacticraftSurfaceBuilders.MOON.method_30478(new MultiBlockSurfaceConfig(
            new BlockStateWithChance[]{
                    new BlockStateWithChance(GalacticraftBlocks.MOON_BASALT.getDefaultState(), -1),
                    new BlockStateWithChance(GalacticraftBlocks.MOON_TURF.getDefaultState(), 100)
            }, //DISABLED for now. Need to find a good ratio
            new BlockStateWithChance[]{
                    new BlockStateWithChance(GalacticraftBlocks.MOON_BASALT.getDefaultState(), -1),
                    new BlockStateWithChance(GalacticraftBlocks.MOON_DIRT.getDefaultState(), 100)
            },
            new BlockStateWithChance[]{
                    new BlockStateWithChance(GalacticraftBlocks.MOON_BASALT.getDefaultState(), -1),
                    new BlockStateWithChance(GalacticraftBlocks.MOON_ROCK.getDefaultState(), 100)
            }
    )));

    private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> MOON_VILLAGE_FEATURE = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, new Identifier(Constants.MOD_ID, "moon_village"), GalacticraftStructureFeatures.MOON_VILLAGE.configure(new StructurePoolFeatureConfig(() -> MoonVillageData.BASE_POOL, 6)));
    private static final ConfiguredCarver<ProbabilityConfig> MOON_HIGHLANDS_CAVE_CARVER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, new Identifier(Constants.MOD_ID, "moon_highlands_caves"), Carver.CAVE.method_28614(new ProbabilityConfig(0.1F)));
    private static final ConfiguredCarver<ProbabilityConfig> MOON_MARE_CAVE_CARVER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, new Identifier(Constants.MOD_ID, "moon_mare_caves"), Carver.CAVE.method_28614(new ProbabilityConfig(0.15F)));
    private static final ConfiguredFeature<?, ?> MOON_BASALT_CLUSTER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Constants.MOD_ID, "moon_basalt_cluster"), Feature.DISK.configure(new DiskFeatureConfig(GalacticraftBlocks.MOON_BASALT.getDefaultState(), UniformIntDistribution.of(2, 3), 2, ImmutableList.of(GalacticraftBlocks.MOON_ROCK.getDefaultState()))).decorate(ConfiguredFeatures.Decorators.field_26167));
    private static final ConfiguredFeature<?, ?> MOON_COPPER_ORE = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Constants.MOD_ID, "moon_copper_ore"), Feature.ORE.configure(new OreFeatureConfig(new TagMatchRuleTest(GalacticraftTags.MOON_STONE), GalacticraftBlocks.MOON_COPPER_ORE.getDefaultState(), 8)).method_30377(64).spreadHorizontally().repeat(12));
    private static final ConfiguredFeature<?, ?> MOON_TIN_ORE = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Constants.MOD_ID, "moon_tin_ore"), Feature.ORE.configure(new OreFeatureConfig(new TagMatchRuleTest(GalacticraftTags.MOON_STONE), GalacticraftBlocks.MOON_TIN_ORE.getDefaultState(), 8)).method_30377(64).spreadHorizontally().repeat(26));
    private static final ConfiguredFeature<?, ?> MOON_CHEESE_ORE = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Constants.MOD_ID, "moon_cheese_ore"), Feature.ORE.configure(new OreFeatureConfig(new TagMatchRuleTest(GalacticraftTags.MOON_STONE), GalacticraftBlocks.MOON_CHEESE_ORE.getDefaultState(), 4)).method_30377(128).spreadHorizontally().repeat(12));

    private static final Biome.SpawnEntry MOON_HIGHLANDS_ZOMBIE_SPAWNS = new Biome.SpawnEntry(GalacticraftEntityTypes.EVOLVED_ZOMBIE, 100, 4, 5);
    private static final Biome.SpawnEntry MOON_HIGHLANDS_CREEPER_SPAWNS = new Biome.SpawnEntry(GalacticraftEntityTypes.EVOLVED_CREEPER, 100, 4, 5);
    private static final Biome.SpawnEntry MOON_MARE_ZOMBIE_SPAWNS = new Biome.SpawnEntry(GalacticraftEntityTypes.EVOLVED_ZOMBIE, 100, 5, 6);
    private static final Biome.SpawnEntry MOON_MARE_CREEPER_SPAWNS = new Biome.SpawnEntry(GalacticraftEntityTypes.EVOLVED_CREEPER, 100, 5, 6);

    private static final MusicSound MOON_MUSIC = new MusicSound(GalacticraftSounds.MUSIC_MOON, 1200, 3600, true);

    public static Biome createMoonHighlandsBiome(ConfiguredSurfaceBuilder<?> surfaceBuilder, float depth, float scale, float downfall) {
        Biome biome = new Biome(
                new Biome.Settings()
                        .precipitation(Biome.Precipitation.NONE)
                        .surfaceBuilder(surfaceBuilder)
                        .category(Biome.Category.NONE)
                        .temperature(-173.0F)
                        .downfall(downfall)
                        .method_30637(0)
                        .depth(depth)
                        .scale(scale)
                        .effects(new BiomeEffects.Builder()
                                .waterColor(4013374)
                                .waterFogColor(4802890)
                                .fogColor(1447446)
                                .music(MOON_MUSIC)
                                .build())
        ) {
            @Override
            public boolean canSetSnow(WorldView world, BlockPos blockPos) {
                return false;
            }
        };
        biome.addCarver(GenerationStep.Carver.AIR, MOON_HIGHLANDS_CAVE_CARVER);
        biome.addStructureFeature(MOON_VILLAGE_FEATURE);
        biome.addSpawn(SpawnGroup.MONSTER, MOON_HIGHLANDS_ZOMBIE_SPAWNS);
        biome.addSpawn(SpawnGroup.MONSTER, MOON_HIGHLANDS_CREEPER_SPAWNS);
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, MOON_BASALT_CLUSTER);
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, MOON_COPPER_ORE);
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, MOON_TIN_ORE);
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, MOON_CHEESE_ORE);
        ((GCBiomePropertyAccessor) biome).setProperty(GalacticraftBiomeProperties.IS_SPACE_BIOME, true);
        return biome;
    }

    public static Biome createMoonMareBiome(ConfiguredSurfaceBuilder<?> surfaceBuilder, float depth, float scale, float downfall) {
        Biome biome = new Biome(
                new Biome.Settings()
                        .precipitation(Biome.Precipitation.NONE)
                        .surfaceBuilder(surfaceBuilder)
                        .category(Biome.Category.NONE)
                        .temperature(-173.0F)
                        .downfall(downfall)
                        .method_30637(0)
                        .depth(depth)
                        .scale(scale)
                        .effects(new BiomeEffects.Builder()
                                .waterColor(2170913)
                                .waterFogColor(2828843)
                                .fogColor(1447446)
                                .music(MOON_MUSIC)
                                .build())
        ) {
            @Override
            public boolean canSetSnow(WorldView world, BlockPos blockPos) {
                return false;
            }

            @Override
            protected float computeTemperature(BlockPos blockPos) {
                return super.computeTemperature(blockPos);
            }
        };
        biome.addCarver(GenerationStep.Carver.AIR, MOON_MARE_CAVE_CARVER);
        biome.addSpawn(SpawnGroup.MONSTER, MOON_MARE_ZOMBIE_SPAWNS);
        biome.addSpawn(SpawnGroup.MONSTER, MOON_MARE_CREEPER_SPAWNS);
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, MOON_COPPER_ORE);
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, MOON_TIN_ORE);
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, MOON_CHEESE_ORE);
        ((GCBiomePropertyAccessor) biome).setProperty(GalacticraftBiomeProperties.IS_SPACE_BIOME, true);
        ((GCBiomePropertyAccessor) biome).setProperty(GalacticraftBiomeProperties.IS_MARE, true);
        return biome;
    }
}
