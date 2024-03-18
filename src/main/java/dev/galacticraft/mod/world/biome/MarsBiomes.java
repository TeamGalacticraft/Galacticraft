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

import dev.galacticraft.mod.content.GCSounds;
import net.minecraft.core.HolderGetter;
import net.minecraft.sounds.Musics;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MarsBiomes {
    public static Biome mars(
            HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter
    ) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);
        BiomeSpecialEffects.Builder specialEffects = new BiomeSpecialEffects.Builder();
        specialEffects.waterColor(4159204)
                .waterFogColor(329011)
                .fogColor(10518688)
                .skyColor(0)
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .backgroundMusic(Musics.createGameMusic(GCSounds.MUSIC_MARS));

//        MoonBiomes.addDefaultMoonOres(generation);
//        MoonBiomes.addDefaultSoftDisks(generation);
//        MoonBiomes.monsters(spawnBuilder, 95, 5, 100);

        return new Biome.BiomeBuilder()
                .mobSpawnSettings(spawnBuilder.build())
                .hasPrecipitation(false)
                .temperature(2.0F) // temp is hot to prevent snow
                .downfall(0.0F)
                .specialEffects(specialEffects.build())
                .generationSettings(generation.build())
                .build();
    }
}
