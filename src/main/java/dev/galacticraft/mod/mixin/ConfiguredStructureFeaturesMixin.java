/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.mixin;

import com.google.common.collect.ImmutableSet;
import dev.galacticraft.mod.world.gen.feature.GalacticraftConfiguredStructureFeature;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.BiConsumer;

import static dev.galacticraft.mod.world.biome.GalacticraftBiomeKey.*;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(ConfiguredStructureFeatures.class)
public abstract class ConfiguredStructureFeaturesMixin {
    @Shadow private static void register(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, ConfiguredStructureFeature<?, ?> feature, Set<RegistryKey<Biome>> biomes) {}

    @Inject(method = "registerAll", at = @At("RETURN"))
    private static void registerGalacticraftConfiguredStructureFeatures(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, CallbackInfo ci) { //TODO: See if there is an api for structure feature registration
        Set<RegistryKey<Biome>> MOON_HIGHLANDS_BIOMES = ImmutableSet.of(Moon.HIGHLANDS, Moon.HIGHLANDS_EDGE, Moon.HIGHLANDS_FLAT, Moon.HIGHLANDS_HILLS, Moon.HIGHLANDS_VALLEY);
        Set<RegistryKey<Biome>> MOON_MARE_BIOMES = ImmutableSet.of(Moon.MARE, Moon.MARE_EDGE, Moon.MARE_FLAT, Moon.MARE_HILLS, Moon.MARE_VALLEY);

        register(registrar, GalacticraftConfiguredStructureFeature.MOON_RUINS, MOON_MARE_BIOMES);
        register(registrar, GalacticraftConfiguredStructureFeature.MOON_PILLAGER_OUTPOST, MOON_MARE_BIOMES);
        register(registrar, GalacticraftConfiguredStructureFeature.MOON_PILLAGER_OUTPOST, MOON_HIGHLANDS_BIOMES);
        register(registrar, GalacticraftConfiguredStructureFeature.MOON_VILLAGE, MOON_HIGHLANDS_BIOMES);
    }
}
