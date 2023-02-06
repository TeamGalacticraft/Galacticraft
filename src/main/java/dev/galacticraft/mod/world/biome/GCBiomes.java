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

import dev.galacticraft.mod.Constant;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public final class GCBiomes {
    public static final class Moon {
        public static final ResourceKey<Biome> COMET_TUNDRA = key("comet_tundra");
        public static final ResourceKey<Biome> BASALTIC_MARE = key("basaltic_mare");
        public static final ResourceKey<Biome> LUNAR_HIGHLANDS = key("lunar_highlands");
        public static final ResourceKey<Biome> OLIVINE_SPIKES = key("olivine_spikes");
    }

    public static void bootstrapRegistries(BootstapContext<Biome> context) { // moj-map typo :(
        HolderGetter<PlacedFeature> featureLookup = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverLookup = context.lookup(Registries.CONFIGURED_CARVER);
        context.register(Moon.COMET_TUNDRA, MoonBiomes.createCometTundra(featureLookup, carverLookup));
        context.register(Moon.BASALTIC_MARE, MoonBiomes.createBasalticMare(featureLookup, carverLookup));
        context.register(Moon.LUNAR_HIGHLANDS, MoonBiomes.createLunarHighlands(featureLookup, carverLookup));
        context.register(Moon.OLIVINE_SPIKES, MoonBiomes.createOlivineSpikes(featureLookup, carverLookup));
    }

    @Contract(pure = true)
    public static @NotNull ResourceKey<Biome> key(String id) {
        return Constant.key(Registries.BIOME, id);
    }
}
