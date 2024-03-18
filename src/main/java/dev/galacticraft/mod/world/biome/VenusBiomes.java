/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.mod.world.gen.feature.GCOrePlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.sounds.Musics;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.awt.*;

public class VenusBiomes {
    public static void monsters(MobSpawnSettings.Builder builder, int zombieWeight, int zombieVillagerWeight, int selektonWeight) {
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SPIDER, 100, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_ZOMBIE, zombieWeight, 4, 4));
//        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_ZOMBIE_VILLAGER, zombieVillagerWeight, 1, 1));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SKELETON, selektonWeight, 4, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_CREEPER, 100, 4, 4));
//        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_SLIME, 100, 4, 4));
//        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_ENDERMAN, 10, 1, 4));
//        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(GCEntityTypes.EVOLVED_WITCH, 5, 1, 1));
    }

    public static void addDefaultVenusOres(BiomeGenerationSettings.Builder builder) {
//        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_COPPER_MOON);
//        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_COPPER_LARGE_MOON);
//
//        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_TIN_SMALL_MOON);
//        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_TIN_MIDDLE_MOON);
//        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, GCOrePlacedFeatures.ORE_TIN_UPPER_MOON);
    }

    public static Biome venus(
            HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter
    ) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);
        BiomeSpecialEffects.Builder specialEffects = new BiomeSpecialEffects.Builder();
        specialEffects.waterColor(4159204)
                .waterFogColor(329011)
                .fogColor(10518688)
                .skyColor(new Color(255, 201, 64).getRGB())
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .backgroundMusic(Musics.createGameMusic(GCSounds.MUSIC_MOON));

        VenusBiomes.addDefaultVenusOres(generation);
//        VenusBiomes.addDefaultSoftDisks(generation);
        VenusBiomes.monsters(spawnBuilder, 95, 5, 100);

        return new Biome.BiomeBuilder()
                .mobSpawnSettings(spawnBuilder.build())
                .hasPrecipitation(true)
                .temperature(2.0F) // temp is hot to prevent snow
                .downfall(0.5F)
                .specialEffects(specialEffects.build())
                .generationSettings(generation.build())
                .build();
    }
}
