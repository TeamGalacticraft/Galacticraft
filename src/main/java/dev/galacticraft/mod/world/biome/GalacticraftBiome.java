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

package dev.galacticraft.mod.world.biome;

import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.sound.GalacticraftSound;
import dev.galacticraft.mod.world.gen.carver.GalacticraftCarver;
import dev.galacticraft.mod.world.gen.carver.config.CraterCarverConfig;
import dev.galacticraft.mod.world.gen.feature.GalacticraftOrePlacedFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class GalacticraftBiome {
    private static class Moon {
        // TODO Actually register these?
        private static final Holder<ConfiguredWorldCarver<CanyonCarverConfiguration>> CANYON = Holder.direct(WorldCarver.CANYON.configured(new CanyonCarverConfiguration(
                0.05f,
                UniformHeight.of(VerticalAnchor.absolute(10), VerticalAnchor.absolute(67)),
                ConstantFloat.of(3.0f),
                VerticalAnchor.aboveBottom(8),
                CarverDebugSettings.DEFAULT,
                Registry.BLOCK.getOrCreateTag(BlockTags.OVERWORLD_CARVER_REPLACEABLES), // TODO: REPLACEABLES tag
                UniformFloat.of(-0.125f, 0.125f),
                new CanyonCarverConfiguration.CanyonShapeConfiguration(
                        UniformFloat.of(0.75f, 1.0f),
                        TrapezoidFloat.of(0, 6, 2),
                        3,
                        UniformFloat.of(0.75f, 1.0f),
                        1.0f,
                        0.0f)
        )));

        private static final Holder<ConfiguredWorldCarver<CraterCarverConfig>> CRATER = Holder.direct(GalacticraftCarver.CRATERS.configured(new CraterCarverConfig(
                0.05f,
                ConstantHeight.of(VerticalAnchor.absolute(128)),
                UniformFloat.of(0.4f, 0.6f),
                CarverDebugSettings.DEFAULT,
                27,
                8,
                8
        )));

        private static final Music MUSIC = new Music(GalacticraftSound.MUSIC_MOON, 1200, 3600, true);

        private enum BiomeType {
            HIGHLANDS(GalacticraftCarver.LUNAR_CAVE.configured(
                    new CaveCarverConfiguration(
                    0.15f,
                    UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
                    UniformFloat.of(0.1f, 0.9f),
                    VerticalAnchor.aboveBottom(-64),
                    HolderSet.direct(),
                    UniformFloat.of(0.7f, 1.4f),
                    UniformFloat.of(0.8f, 1.3f),
                    UniformFloat.of(-1.0f, -0.4f)
            )), new BiomeSpecialEffects.Builder()
                    .skyColor(0)
                    .fogColor(1447445)
                    .waterColor(4013374)
                    .waterFogColor(4802890)
                    .backgroundMusic(MUSIC)
                    .build(),
                    HolderSet.direct(
                            GalacticraftOrePlacedFeature.ORE_TIN_SMALL_MOON,
                            GalacticraftOrePlacedFeature.ORE_TIN_MIDDLE_MOON,
                            GalacticraftOrePlacedFeature.ORE_TIN_UPPER_MOON,
                            GalacticraftOrePlacedFeature.ORE_COPPER_MOON,
                            GalacticraftOrePlacedFeature.ORE_COPPER_LARGE_MOON,
                            GalacticraftOrePlacedFeature.BASALT_DISK_MOON
                    ),
                    new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_ZOMBIE, 95, 4, 5))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_CREEPER, 100, 4, 4))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_SKELETON, 100, 4, 4))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_SPIDER, 100, 4, 4))
                            .build()
            ),

            MARE(GalacticraftCarver.LUNAR_CAVE.configured(new CaveCarverConfiguration(0.18f,
                    UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
                    UniformFloat.of(0.1f, 0.9f),
                    VerticalAnchor.aboveBottom(-64),
                    HolderSet.direct(),
                    UniformFloat.of(0.7f, 1.4f),
                    UniformFloat.of(0.8f, 1.3f),
                    UniformFloat.of(-1.0f, -0.4f)
            )), new BiomeSpecialEffects.Builder()
                    .skyColor(0)
                    .fogColor(1447445)
                    .waterColor(2170913)
                    .waterFogColor(2828843)
                    .backgroundMusic(MUSIC)
                    .build(),
                    HolderSet.direct(
                            GalacticraftOrePlacedFeature.ORE_TIN_SMALL_MOON,
                            GalacticraftOrePlacedFeature.ORE_TIN_MIDDLE_MOON,
                            GalacticraftOrePlacedFeature.ORE_TIN_UPPER_MOON,
                            GalacticraftOrePlacedFeature.ORE_COPPER_MOON,
                            GalacticraftOrePlacedFeature.ORE_COPPER_LARGE_MOON,
                            GalacticraftOrePlacedFeature.BASALT_DISK_MOON
                    ),
                    new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_ZOMBIE, 95, 4, 5))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_CREEPER, 100, 4, 5))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_SKELETON, 100, 4, 5))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_SPIDER, 100, 4, 5))
                            .build()
            );

            private final ConfiguredWorldCarver<CaveCarverConfiguration> caves;
            private final BiomeSpecialEffects biomeEffects;
            private final HolderSet<PlacedFeature> ores;
            private final MobSpawnSettings spawnSettings;

            BiomeType(ConfiguredWorldCarver<CaveCarverConfiguration> caves, BiomeSpecialEffects biomeEffects, HolderSet<PlacedFeature> ores, MobSpawnSettings spawnSettings) {
                this.caves = caves;
                this.biomeEffects = biomeEffects;
                this.ores = ores;
                this.spawnSettings = spawnSettings;
            }

            public ConfiguredWorldCarver<CaveCarverConfiguration> getCaves() {
                return this.caves;
            }

            public BiomeSpecialEffects getBiomeEffects() {
                return biomeEffects;
            }

            public HolderSet<PlacedFeature> getOres() {
                return this.ores;
            }

            public MobSpawnSettings getSpawnSettings() {
                return spawnSettings;
            }
        }

        private static Biome.BiomeBuilder createBuilder() {
            return new Biome.BiomeBuilder()
                    .temperature(2)
                    .downfall(0)
                    .precipitation(Biome.Precipitation.NONE);
        }

        private static Biome createMoon(BiomeType type) {
            BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder()
                    .addCarver(GenerationStep.Carving.AIR, CRATER)
                    .addCarver(GenerationStep.Carving.AIR, CANYON)
                    .addCarver(GenerationStep.Carving.AIR, Holder.direct(type.getCaves())); // TODO: getCaves should provide a RegisteyEntry

            for (Holder<PlacedFeature> ore : type.getOres()) {
                builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ore);
            }

            return Moon.createBuilder()
                    .generationSettings(builder.build())
                    .specialEffects(type.getBiomeEffects())
                    .mobSpawnSettings(type.getSpawnSettings()).build();
        }
    }

    public static void register() {
        register(GalacticraftBiomeKey.Moon.HIGHLANDS, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
        register(GalacticraftBiomeKey.Moon.HIGHLANDS_EDGE, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
        register(GalacticraftBiomeKey.Moon.HIGHLANDS_FLAT, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
        register(GalacticraftBiomeKey.Moon.HIGHLANDS_HILLS, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
        register(GalacticraftBiomeKey.Moon.HIGHLANDS_VALLEY, Moon.createMoon(Moon.BiomeType.HIGHLANDS));

        register(GalacticraftBiomeKey.Moon.MARE, Moon.createMoon(Moon.BiomeType.MARE));
        register(GalacticraftBiomeKey.Moon.MARE_EDGE, Moon.createMoon(Moon.BiomeType.MARE));
        register(GalacticraftBiomeKey.Moon.MARE_FLAT, Moon.createMoon(Moon.BiomeType.MARE));
        register(GalacticraftBiomeKey.Moon.MARE_HILLS, Moon.createMoon(Moon.BiomeType.MARE));
        register(GalacticraftBiomeKey.Moon.MARE_VALLEY, Moon.createMoon(Moon.BiomeType.MARE));
    }

    private static void register(ResourceKey<Biome> key, Biome biome) {
        BuiltinRegistries.register(BuiltinRegistries.BIOME, key, biome);
    }
}
