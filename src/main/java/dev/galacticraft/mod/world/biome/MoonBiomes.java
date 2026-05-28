/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import dev.galacticraft.mod.world.gen.carver.GCConfiguredCarvers;
import dev.galacticraft.mod.world.gen.feature.GCOrePlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.sounds.Musics;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MoonBiomes {
    public static Biome createCometTundra(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        return MoonBiomes.moon(featureLookup, carverLookup, new BiomeGenerationSettings.Builder(featureLookup, carverLookup));
    }

    public static Biome createBasalticMare(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(featureLookup, carverLookup);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_MARE_CAVE_CARVER);
        return MoonBiomes.moon(featureLookup, carverLookup, generation);
    }

    public static Biome createLunarHighlands(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(featureLookup, carverLookup);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_HIGHLANDS_CAVE_CARVER);
        return MoonBiomes.moon(featureLookup, carverLookup, generation);
    }

    public static Biome createLunarLowlands(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        return MoonBiomes.moon(featureLookup, carverLookup, new BiomeGenerationSettings.Builder(featureLookup, carverLookup));
    }

    public static Biome createCheeseCaves(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(featureLookup, carverLookup);
        MoonBiomes.addPlannedMoonCaveGeneration(generation);
        MoonBiomes.addDefaultMoonOres(generation);
        MoonBiomes.addDefaultSoftDisks(generation);

        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        MoonBiomes.monsters(spawns, 95, 5, 100);

        return new Biome.BiomeBuilder()
                .mobSpawnSettings(spawns.build())
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .specialEffects(MoonBiomes.defaultMoonEffects())
                .generationSettings(generation.build())
                .build();
    }

    public static Biome createOlivineCaves(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(features, carvers);
        MoonBiomes.addPlannedMoonCaveGeneration(generation);
        MoonBiomes.addDefaultMoonOres(generation);

        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.OLIGRUB, 1000, 2, 5));

        return new Biome.BiomeBuilder()
                .mobSpawnSettings(spawns.build())
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .specialEffects(MoonBiomes.defaultMoonEffects())
                .generationSettings(generation.build())
                .build();
    }

    public static Biome createGlacialCaverns(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(features, carvers);
        MoonBiomes.addPlannedMoonCaveGeneration(generation);
        MoonBiomes.addDefaultMoonOres(generation);

        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        MoonBiomes.monsters(spawns, 75, 5, 65);

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .fogColor(0xD6ECFF)
                .waterColor(0xB8E6FF)
                .waterFogColor(0xA8D8F0)
                .skyColor(0)
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .backgroundMusic(Musics.createGameMusic(GCSounds.MUSIC_MOON))
                .build();

        return new Biome.BiomeBuilder()
                .mobSpawnSettings(spawns.build())
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .specialEffects(effects)
                .generationSettings(generation.build())
                .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                .build();
    }

    public static void monsters(MobSpawnSettings.Builder builder, int zombieWeight, int zombieVillagerWeight, int skeletonWeight) {
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SPIDER, 100, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_ZOMBIE, zombieWeight, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SKELETON, skeletonWeight, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_CREEPER, 100, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_ENDERMAN, 10, 1, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_WITCH, 5, 1, 1));
    }

    public static void addDefaultMoonOres(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_COPPER_MOON);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_COPPER_LARGE_MOON);

        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_TIN_SMALL_MOON);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_TIN_MIDDLE_MOON);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_TIN_UPPER_MOON);

        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_CHEESE_MOON);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_CHEESE_LARGE_MOON);

        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_LUNAR_SAPPHIRE_MOON);
    }

    public static void addDefaultSoftDisks(BiomeGenerationSettings.Builder builder) {
        // builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.BASALT_DISK_MOON);
    }

    public static void addDefaultSurfaceMoonCarvers(BiomeGenerationSettings.Builder generation) {
        generation.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_CRATER_CARVER);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_CANYON_CARVER);
    }

    public static void addPlannedMoonCaveGeneration(BiomeGenerationSettings.Builder generation) {
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.PLANNED_MOON_CAVE_CARVER);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_CRATER_CARVER);
    }

    public static Biome moon(
            HolderGetter<PlacedFeature> featureGetter,
            HolderGetter<ConfiguredWorldCarver<?>> carverGetter,
            BiomeGenerationSettings.Builder generation
    ) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        MoonBiomes.addDefaultMoonOres(generation);
        MoonBiomes.addDefaultSoftDisks(generation);
        MoonBiomes.monsters(spawnBuilder, 95, 5, 100);
        MoonBiomes.addDefaultSurfaceMoonCarvers(generation);

        return new Biome.BiomeBuilder()
                .mobSpawnSettings(spawnBuilder.build())
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .specialEffects(MoonBiomes.defaultMoonEffects())
                .generationSettings(generation.build())
                .build();
    }

    private static BiomeSpecialEffects defaultMoonEffects() {
        return new BiomeSpecialEffects.Builder()
                .waterColor(4159204)
                .waterFogColor(329011)
                .fogColor(10518688)
                .skyColor(0)
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .backgroundMusic(Musics.createGameMusic(GCSounds.MUSIC_MOON))
                .build();
    }
}