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
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.*;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class GalacticraftBiome {
    private static class Moon {
        // TODO Actually register these?
        private static final RegistryEntry<ConfiguredCarver<RavineCarverConfig>> CANYON = RegistryEntry.of(Carver.RAVINE.configure(new RavineCarverConfig(
                0.05f,
                UniformHeightProvider.create(YOffset.fixed(10), YOffset.fixed(67)),
                ConstantFloatProvider.create(3.0f),
                YOffset.aboveBottom(8),
                CarverDebugConfig.DEFAULT,
                Registry.BLOCK.getOrCreateEntryList(BlockTags.OVERWORLD_CARVER_REPLACEABLES), // TODO: REPLACEABLES tag
                UniformFloatProvider.create(-0.125f, 0.125f),
                new RavineCarverConfig.Shape(
                        UniformFloatProvider.create(0.75f, 1.0f),
                        TrapezoidFloatProvider.create(0, 6, 2),
                        3,
                        UniformFloatProvider.create(0.75f, 1.0f),
                        1.0f,
                        0.0f)
        )));

        private static final RegistryEntry<ConfiguredCarver<CraterCarverConfig>> CRATER = RegistryEntry.of(GalacticraftCarver.CRATERS.configure(new CraterCarverConfig(
                0.05f,
                ConstantHeightProvider.create(YOffset.fixed(128)),
                UniformFloatProvider.create(0.4f, 0.6f),
                CarverDebugConfig.DEFAULT,
                27,
                8,
                8
        )));

        private static final MusicSound MUSIC = new MusicSound(GalacticraftSound.MUSIC_MOON, 1200, 3600, true);

        private enum BiomeType {
            HIGHLANDS(GalacticraftCarver.LUNAR_CAVE.configure(
                    new CaveCarverConfig(
                    0.15f,
                    UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(180)),
                    UniformFloatProvider.create(0.1f, 0.9f),
                    YOffset.aboveBottom(-64),
                    RegistryEntryList.of(),
                    UniformFloatProvider.create(0.7f, 1.4f),
                    UniformFloatProvider.create(0.8f, 1.3f),
                    UniformFloatProvider.create(-1.0f, -0.4f)
            )), new BiomeEffects.Builder()
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

            MARE(GalacticraftCarver.LUNAR_CAVE.configure(new CaveCarverConfig(0.18f,
                    UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(180)),
                    UniformFloatProvider.create(0.1f, 0.9f),
                    YOffset.aboveBottom(-64),
                    RegistryEntryList.of(),
                    UniformFloatProvider.create(0.7f, 1.4f),
                    UniformFloatProvider.create(0.8f, 1.3f),
                    UniformFloatProvider.create(-1.0f, -0.4f)
            )), new BiomeEffects.Builder()
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

            private final ConfiguredCarver<CaveCarverConfig> caves;
            private final BiomeEffects biomeEffects;
            private final RegistryEntryList<PlacedFeature> ores;
            private final SpawnSettings spawnSettings;

            BiomeType(ConfiguredCarver<CaveCarverConfig> caves, BiomeEffects biomeEffects, RegistryEntryList<PlacedFeature> ores, SpawnSettings spawnSettings) {
                this.caves = caves;
                this.biomeEffects = biomeEffects;
                this.ores = ores;
                this.spawnSettings = spawnSettings;
            }

            public ConfiguredCarver<CaveCarverConfig> getCaves() {
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
                    .carver(GenerationStep.Carver.AIR, CRATER)
                    .carver(GenerationStep.Carver.AIR, CANYON)
                    .carver(GenerationStep.Carver.AIR, RegistryEntry.of(type.getCaves())); // TODO: getCaves should provide a RegisteyEntry

            for (RegistryEntry<PlacedFeature> ore : type.getOres()) {
                builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ore);
            }

            return Moon.createBuilder()
                    .generationSettings(builder.build())
                    .effects(type.getBiomeEffects())
                    .spawnSettings(type.getSpawnSettings()).build();
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

    private static void register(RegistryKey<Biome> key, Biome biome) {
        BuiltinRegistries.add(BuiltinRegistries.BIOME, key, biome);
    }
}
