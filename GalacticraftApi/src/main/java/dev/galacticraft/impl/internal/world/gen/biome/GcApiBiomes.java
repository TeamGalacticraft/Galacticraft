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

package dev.galacticraft.impl.internal.world.gen.biome;

import dev.galacticraft.impl.Constant;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GcApiBiomes {
    public static final Biome SPACE = createSpaceBiome();

    private static Biome createSpaceBiome() {
        Biome.BiomeBuilder builder = new Biome.BiomeBuilder();
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder genSettings = new BiomeGenerationSettings.Builder();
        BiomeSpecialEffects.Builder effects = new BiomeSpecialEffects.Builder();
        effects.fogColor(0).waterColor(4159204).waterFogColor(329011).skyColor(0);
        return builder
                .downfall(0)
                .temperature(1)
                .specialEffects(effects.build())
                .mobSpawnSettings(spawns.build())
                .precipitation(Biome.Precipitation.NONE)
                .generationSettings(genSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE).build();
    }

    public static void register() {
        Registry.register(BuiltinRegistries.BIOME, new ResourceLocation(Constant.MOD_ID, "space"), SPACE);
    }
}
