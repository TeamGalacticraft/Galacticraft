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
import dev.galacticraft.mod.world.gen.carver.GalacticraftConfiguredCarver;
import dev.galacticraft.mod.world.gen.feature.GalacticraftOrePlacedFeature;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class GalacticraftBiome {
    private static class Moon {
        private static final MusicSound MUSIC = new MusicSound(GalacticraftSound.MUSIC_MOON, 1200, 3600, true);

        private enum BiomeType {
            HIGHLANDS(GalacticraftConfiguredCarver.MOON_HIGHLANDS_CAVE_CARVER, new BiomeEffects.Builder()
                    .skyColor(0)
                    .fogColor(1447445)
                    .waterColor(4013374)
                    .waterFogColor(4802890)
                    .music(MUSIC)
                    .build(),
                    RegistryEntryList.of(
                            GalacticraftOrePlacedFeature.ORE_TIN_SMALL_MOON,
                            GalacticraftOrePlacedFeature.ORE_TIN_MIDDLE_MOON,
                            GalacticraftOrePlacedFeature.ORE_TIN_UPPER_MOON,
                            GalacticraftOrePlacedFeature.ORE_COPPER_MOON,
                            GalacticraftOrePlacedFeature.ORE_COPPER_LARGE_MOON,
                            GalacticraftOrePlacedFeature.BASALT_DISK_MOON
                    ),
                    new SpawnSettings.Builder()
                            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_ZOMBIE, 95, 4, 5))
                            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_CREEPER, 100, 4, 4))
                            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_SKELETON, 100, 4, 4))
                            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_SPIDER, 100, 4, 4))
                            .build()
            ),

            MARE(GalacticraftConfiguredCarver.MOON_MARE_CAVE_CARVER, new BiomeEffects.Builder()
                    .skyColor(0)
                    .fogColor(1447445)
                    .waterColor(2170913)
                    .waterFogColor(2828843)
                    .music(MUSIC)
                    .build(),
                    RegistryEntryList.of(
                            GalacticraftOrePlacedFeature.ORE_TIN_SMALL_MOON,
                            GalacticraftOrePlacedFeature.ORE_TIN_MIDDLE_MOON,
                            GalacticraftOrePlacedFeature.ORE_TIN_UPPER_MOON,
                            GalacticraftOrePlacedFeature.ORE_COPPER_MOON,
                            GalacticraftOrePlacedFeature.ORE_COPPER_LARGE_MOON,
                            GalacticraftOrePlacedFeature.BASALT_DISK_MOON
                    ),
                    new SpawnSettings.Builder()
                            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_ZOMBIE, 95, 4, 5))
                            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_CREEPER, 100, 4, 5))
                            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_SKELETON, 100, 4, 5))
                            .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_SPIDER, 100, 4, 5))
                            .build()
            );

            private final RegistryEntry<ConfiguredCarver<?>> caves;
            private final BiomeEffects biomeEffects;
            private final RegistryEntryList<PlacedFeature> ores;
            private final SpawnSettings spawnSettings;

            BiomeType(RegistryEntry<ConfiguredCarver<?>> caves, BiomeEffects biomeEffects, RegistryEntryList<PlacedFeature> ores, SpawnSettings spawnSettings) {
                this.caves = caves;
                this.biomeEffects = biomeEffects;
                this.ores = ores;
                this.spawnSettings = spawnSettings;
            }

            public RegistryEntry<ConfiguredCarver<?>> getCaves() {
                return this.caves;
            }

            public BiomeEffects getBiomeEffects() {
                return biomeEffects;
            }

            public RegistryEntryList<PlacedFeature> getOres() {
                return this.ores;
            }

            public SpawnSettings getSpawnSettings() {
                return spawnSettings;
            }
        }

        private static Biome.Builder createBuilder() {
            return new Biome.Builder()
                    .temperature(2)
                    .downfall(0)
                    .precipitation(Biome.Precipitation.NONE);
        }

        private static Biome createMoon(BiomeType type) {
            GenerationSettings.Builder builder = new GenerationSettings.Builder()
                    .carver(GenerationStep.Carver.AIR, GalacticraftConfiguredCarver.MOON_CRATER_CARVER)
                    .carver(GenerationStep.Carver.AIR, GalacticraftConfiguredCarver.MOON_CANYON_CARVER)
                    .carver(GenerationStep.Carver.AIR, type.getCaves());

            for (RegistryEntry<PlacedFeature> ore : type.getOres()) {
                builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ore);
            }

            return Moon.createBuilder()
                    .generationSettings(builder.build())
                    .effects(type.getBiomeEffects())
                    .spawnSettings(type.getSpawnSettings()).build();
        }

        public static void register() {
            registerBiome(GalacticraftBiomeKey.Moon.HIGHLANDS, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
            registerBiome(GalacticraftBiomeKey.Moon.HIGHLANDS_EDGE, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
            registerBiome(GalacticraftBiomeKey.Moon.HIGHLANDS_FLAT, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
            registerBiome(GalacticraftBiomeKey.Moon.HIGHLANDS_HILLS, Moon.createMoon(Moon.BiomeType.HIGHLANDS));
            registerBiome(GalacticraftBiomeKey.Moon.HIGHLANDS_VALLEY, Moon.createMoon(Moon.BiomeType.HIGHLANDS));

            registerBiome(GalacticraftBiomeKey.Moon.MARE, Moon.createMoon(Moon.BiomeType.MARE));
            registerBiome(GalacticraftBiomeKey.Moon.MARE_EDGE, Moon.createMoon(Moon.BiomeType.MARE));
            registerBiome(GalacticraftBiomeKey.Moon.MARE_FLAT, Moon.createMoon(Moon.BiomeType.MARE));
            registerBiome(GalacticraftBiomeKey.Moon.MARE_HILLS, Moon.createMoon(Moon.BiomeType.MARE));
            registerBiome(GalacticraftBiomeKey.Moon.MARE_VALLEY, Moon.createMoon(Moon.BiomeType.MARE));
        }
    }

    public static void register() {
        Moon.register();
    }

    private static void registerBiome(RegistryKey<Biome> key, Biome biome) {
        BuiltinRegistries.add(BuiltinRegistries.BIOME, key, biome);
    }
}
