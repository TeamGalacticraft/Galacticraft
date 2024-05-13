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

import dev.galacticraft.mod.Constant;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class GCBiomes {
    public static final class Moon {
        public static final ResourceKey<Biome> COMET_TUNDRA = key("comet_tundra");
        public static final ResourceKey<Biome> BASALTIC_MARE = key("basaltic_mare");
        public static final ResourceKey<Biome> LUNAR_HIGHLANDS = key("lunar_highlands");
        public static final ResourceKey<Biome> LUNAR_LOWLANDS = key("lunar_lowlands");
        public static final ResourceKey<Biome> OLIVINE_SPIKES = key("olivine_spikes");
    }

    public static final class Venus {
        public static final ResourceKey<Biome> VENUS_VALLEY = key("venus_valley");
        public static final ResourceKey<Biome> VENUS_FLAT = key("venus_flat");
        public static final ResourceKey<Biome> VENUS_MOUNTAIN = key("venus_mountain");
    }

    public static final ResourceKey<Biome> SPACE = ResourceKey.create(Registries.BIOME, Constant.id("space"));

    public static Biome createSpaceBiome(HolderGetter<PlacedFeature> holderGetter, HolderGetter<ConfiguredWorldCarver<?>> holderGetter2) {
        Biome.BiomeBuilder builder = new Biome.BiomeBuilder();
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder genSettings = new BiomeGenerationSettings.Builder(holderGetter, holderGetter2);
        BiomeSpecialEffects.Builder effects = new BiomeSpecialEffects.Builder();
        effects.fogColor(0).waterColor(4159204).waterFogColor(329011).skyColor(0);
        return builder
                .downfall(0)
                .temperature(1)
                .specialEffects(effects.build())
                .mobSpawnSettings(spawns.build())
                .hasPrecipitation(false)
                .generationSettings(genSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE).build();
    }

    public static void bootstrapRegistries(BootstapContext<Biome> context) { // moj-map typo :(
        HolderGetter<PlacedFeature> featureLookup = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverLookup = context.lookup(Registries.CONFIGURED_CARVER);
        context.register(SPACE, createSpaceBiome(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER)));
        context.register(Moon.COMET_TUNDRA, MoonBiomes.createCometTundra(featureLookup, carverLookup));
        context.register(Moon.BASALTIC_MARE, MoonBiomes.createBasalticMare(featureLookup, carverLookup));
        context.register(Moon.LUNAR_HIGHLANDS, MoonBiomes.createLunarHighlands(featureLookup, carverLookup));
        context.register(Moon.LUNAR_LOWLANDS, MoonBiomes.createLunarLowlands(featureLookup, carverLookup));
        context.register(Moon.OLIVINE_SPIKES, MoonBiomes.createOlivineSpikes(featureLookup, carverLookup));

        context.register(Venus.VENUS_VALLEY, VenusBiomes.venus(featureLookup, carverLookup));
        context.register(Venus.VENUS_FLAT, VenusBiomes.venus(featureLookup, carverLookup));
        context.register(Venus.VENUS_MOUNTAIN, VenusBiomes.venus(featureLookup, carverLookup));
    }

    @Contract(pure = true)
    public static @NotNull ResourceKey<Biome> key(String id) {
        return Constant.key(Registries.BIOME, id);
    }
}
