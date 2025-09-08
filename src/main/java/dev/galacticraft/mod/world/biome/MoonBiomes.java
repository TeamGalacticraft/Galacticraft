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

package dev.galacticraft.mod.world.biome;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.world.gen.carver.GCConfiguredCarvers;
import dev.galacticraft.mod.world.gen.feature.GCOrePlacedFeatures;
import dev.galacticraft.mod.world.gen.feature.GCPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.sounds.Musics;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MoonBiomes {
    public static Biome createCometTundra(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        return MoonBiomes.moon(featureLookup, carverLookup, new BiomeGenerationSettings.Builder(featureLookup, carverLookup));
    }

    public static Biome createBasalticMare(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        var generation = new BiomeGenerationSettings.Builder(featureLookup, carverLookup);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_MARE_CAVE_CARVER);
        return MoonBiomes.moon(featureLookup, carverLookup, generation);
    }

    public static Biome createLunarHighlands(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        var generation = new BiomeGenerationSettings.Builder(featureLookup, carverLookup);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_HIGHLANDS_CAVE_CARVER);
        return MoonBiomes.moon(featureLookup, carverLookup, generation);
    }

    public static Biome createLunarLowlands(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        return MoonBiomes.moon(featureLookup, carverLookup, new BiomeGenerationSettings.Builder(featureLookup, carverLookup));
    }

    public static void monsters(MobSpawnSettings.Builder builder, int zombieWeight, int zombieVillagerWeight, int skeletonWeight) {
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SPIDER, 100, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_ZOMBIE, zombieWeight, 4, 4));
//        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_ZOMBIE_VILLAGER, zombieVillagerWeight, 1, 1));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SKELETON, skeletonWeight, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_CREEPER, 100, 4, 4));
//        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SLIME, 100, 4, 4));
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

    public static Biome moon(
            HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter, BiomeGenerationSettings.Builder generation
    ) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeSpecialEffects.Builder specialEffects = new BiomeSpecialEffects.Builder();
        specialEffects.waterColor(4159204)
                .waterFogColor(329011)
                .fogColor(10518688)
                .skyColor(0)
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .backgroundMusic(Musics.createGameMusic(GCSounds.MUSIC_MOON));

        MoonBiomes.addDefaultMoonOres(generation);
        MoonBiomes.addDefaultSoftDisks(generation);
        MoonBiomes.monsters(spawnBuilder, 95, 5, 100);

        generation.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_CRATER_CARVER);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_CANYON_CARVER);

        return new Biome.BiomeBuilder()
                .mobSpawnSettings(spawnBuilder.build())
                .hasPrecipitation(false)
                .temperature(2.0F) // temp is hot to prevent snow
                .downfall(0.0F)
                .specialEffects(specialEffects.build())
                .generationSettings(generation.build())
                .build();
    }

    public static Biome createCheeseCaves(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(featureLookup, carverLookup);
        //generation.addCarver(GenerationStep.Carving.AIR, carverLookup.getOrThrow(Constant.key(Registries.CONFIGURED_CARVER, "cheese_cave_carver")));
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_CRATER_CARVER);

        MoonBiomes.addDefaultMoonOres(generation);
        MoonBiomes.addDefaultSoftDisks(generation); // Keep if you plan to add disk features later

        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        MoonBiomes.monsters(spawns, 95, 5, 100);

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .fogColor(10518688)
                .waterColor(4159204)
                .waterFogColor(329011)
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
                .build();
    }

    public static Biome createOlivineCaves(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(features, carvers);
        generation.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Constant.key(Registries.CONFIGURED_CARVER, "olivine_cave_carver")));
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_CRATER_CARVER);

        // Add features
        MoonBiomes.addDefaultMoonOres(generation);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, GCPlacedFeatures.OLIGRUB_EGG);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, GCPlacedFeatures.OLIVINE_PILLAR_SPIKE);

        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.OLIGRUB, 1000, 2, 5));

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .fogColor(10518688)
                .waterColor(4159204)
                .waterFogColor(329011)
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
                .build();
    }

    public static Biome createGlacialCaverns(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(features, carvers);
        //generation.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Constant.key(Registries.CONFIGURED_CARVER, "glacial_cavern_carver")));

        MoonBiomes.addDefaultMoonOres(generation);
        generation.addCarver(GenerationStep.Carving.AIR, GCConfiguredCarvers.MOON_CRATER_CARVER);

        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        MoonBiomes.monsters(spawns, 95, 5, 100);

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .fogColor(10518688)
                .waterColor(4159204)
                .waterFogColor(329011)
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
}
