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

package dev.galacticraft.mod.mixin;

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.biome.source.GalacticraftBiomeParameters;
import dev.galacticraft.mod.world.dimension.GalacticraftDimensionType;
import dev.galacticraft.mod.world.gen.chunk.GalacticraftChunkGeneratorSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(DimensionType.class)
public abstract class DimensionTypeMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature") // MCDev doesn't seem to capture the locals correctly
    @Inject(method = "createDefaultDimensionOptions(Lnet/minecraft/util/registry/DynamicRegistryManager;JZ)Lnet/minecraft/util/registry/SimpleRegistry;", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addGCDimOptions(DynamicRegistryManager registryManager, long seed, boolean bl, CallbackInfoReturnable<SimpleRegistry<DimensionOptions>> cir, SimpleRegistry<DimensionOptions> dimOptionsRegistry, Registry<DimensionType> dimTypeRegistry, Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGenSettingsRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry) {
        dimOptionsRegistry.add(RegistryKey.of(Registry.DIMENSION_KEY, new Identifier(Constant.MOD_ID, "moon")), new DimensionOptions(() -> dimTypeRegistry.getOrThrow(GalacticraftDimensionType.MOON_DIMENSION_TYPE_KEY), new NoiseChunkGenerator(noiseRegistry, GalacticraftBiomeParameters.MOON.getBiomeSource(biomeRegistry), seed, () -> chunkGenSettingsRegistry.getOrThrow(GalacticraftChunkGeneratorSettings.MOON))), Lifecycle.stable());
    }

    @Inject(method = "addRegistryDefaults", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addGCDimOptions(DynamicRegistryManager registryManager, CallbackInfoReturnable<DynamicRegistryManager> cir, MutableRegistry<DimensionType> registry) {
        GalacticraftDimensionType.register(registry);
    }
}
