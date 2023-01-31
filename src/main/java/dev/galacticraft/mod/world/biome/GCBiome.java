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

package dev.galacticraft.mod.world.biome;

import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.world.gen.carver.GCConfiguredCarver;
import dev.galacticraft.mod.world.gen.feature.GCOrePlacedFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class GCBiome {
    private static class Moon {
        private static final Music MUSIC = new Music(GCSounds.MUSIC_MOON, 1200, 3600, true);

        private enum BiomeType {
            HIGHLANDS(GCConfiguredCarver.MOON_HIGHLANDS_CAVE_CARVER, new BiomeSpecialEffects.Builder()
                    .skyColor(0)
                    .fogColor(1447445)
                    .waterColor(4013374)
                    .waterFogColor(4802890)
                    .backgroundMusic(MUSIC)
                    .build(),
                    HolderSet.direct(
                            GCOrePlacedFeature.ORE_TIN_SMALL_MOON,
                            GCOrePlacedFeature.ORE_TIN_MIDDLE_MOON,
                            GCOrePlacedFeature.ORE_TIN_UPPER_MOON,
                            GCOrePlacedFeature.ORE_COPPER_MOON,
                            GCOrePlacedFeature.ORE_COPPER_LARGE_MOON,
                            GCOrePlacedFeature.BASALT_DISK_MOON
                    ),
                    new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_ZOMBIE, 95, 4, 5))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_CREEPER, 100, 4, 4))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SKELETON, 100, 4, 4))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SPIDER, 100, 4, 4))
                            .build()
            ),

            MARE(GCConfiguredCarver.MOON_MARE_CAVE_CARVER, new BiomeSpecialEffects.Builder()
                    .skyColor(0)
                    .fogColor(1447445)
                    .waterColor(2170913)
                    .waterFogColor(2828843)
                    .backgroundMusic(MUSIC)
                    .build(),
                    HolderSet.direct(
                            GCOrePlacedFeature.ORE_TIN_SMALL_MOON,
                            GCOrePlacedFeature.ORE_TIN_MIDDLE_MOON,
                            GCOrePlacedFeature.ORE_TIN_UPPER_MOON,
                            GCOrePlacedFeature.ORE_COPPER_MOON,
                            GCOrePlacedFeature.ORE_COPPER_LARGE_MOON,
                            GCOrePlacedFeature.BASALT_DISK_MOON
                    ),
                    new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_ZOMBIE, 95, 4, 5))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_CREEPER, 100, 4, 5))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SKELETON, 100, 4, 5))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SPIDER, 100, 4, 5))
                            .build()
            );

            private final Holder<ConfiguredWorldCarver<?>> caves;
            private final BiomeSpecialEffects biomeEffects;
            private final HolderSet<PlacedFeature> ores;
            private final MobSpawnSettings spawnSettings;

            BiomeType(Holder<ConfiguredWorldCarver<?>> caves, BiomeSpecialEffects biomeEffects, HolderSet<PlacedFeature> ores, MobSpawnSettings spawnSettings) {
                this.caves = caves;
                this.biomeEffects = biomeEffects;
                this.ores = ores;
                this.spawnSettings = spawnSettings;
            }

            public Holder<ConfiguredWorldCarver<?>> getCaves() {
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
                    .addCarver(GenerationStep.Carving.AIR, GCConfiguredCarver.MOON_CRATER_CARVER)
                    .addCarver(GenerationStep.Carving.AIR, GCConfiguredCarver.MOON_CANYON_CARVER)
                    .addCarver(GenerationStep.Carving.AIR, type.getCaves());

            for (Holder<PlacedFeature> ore : type.getOres()) {
                builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ore);
            }

            return Moon.createBuilder()
                    .generationSettings(builder.build())
                    .specialEffects(type.getBiomeEffects())
                    .mobSpawnSettings(type.getSpawnSettings()).build();
        }

        public static void register() {
            registerBiome(GCBiomeKey.Moon.HIGHLANDS, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
            registerBiome(GCBiomeKey.Moon.HIGHLANDS_EDGE, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
            registerBiome(GCBiomeKey.Moon.HIGHLANDS_FLAT, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
            registerBiome(GCBiomeKey.Moon.HIGHLANDS_HILLS, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
            registerBiome(GCBiomeKey.Moon.HIGHLANDS_VALLEY, Moon.createMoon(Moon.BiomeType.HIGHLANDS));

            registerBiome(GCBiomeKey.Moon.MARE, Moon.createMoon(Moon.BiomeType.MARE));
            registerBiome(GCBiomeKey.Moon.MARE_EDGE, Moon.createMoon(Moon.BiomeType.MARE));
            registerBiome(GCBiomeKey.Moon.MARE_FLAT, Moon.createMoon(Moon.BiomeType.MARE));
            registerBiome(GCBiomeKey.Moon.MARE_HILLS, Moon.createMoon(Moon.BiomeType.MARE));
            registerBiome(GCBiomeKey.Moon.MARE_VALLEY, Moon.createMoon(Moon.BiomeType.MARE));
        }
    }

    public static void register() {
        Moon.register();
    }

    private static void registerBiome(ResourceKey<Biome> key, Biome biome) {
        BuiltinRegistries.register(BuiltinRegistries.BIOME, key, biome);
    }
}
