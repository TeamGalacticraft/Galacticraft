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
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(DimensionTypes.class)
public abstract class DimensionTypeMixin {
//    @SuppressWarnings("InvalidInjectorMethodSignature") // MCDev doesn't seem to capture the locals correctly
//    @Inject(method = "Lnet/minecraft/world/dimension/DimensionType;createDefaultDimensionOptions(Lnet/minecraft/util/registry/DynamicRegistryManager;JZ)Lnet/minecraft/util/registry/Registry;", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
//    private static void addGCDimOptions(DynamicRegistryManager registryManager, long seed, boolean bl, CallbackInfoReturnable<SimpleRegistry<DimensionOptions>> cir, MutableRegistry<DimensionOptions> dimOptionsRegistry, Registry<DimensionType> dimTypeRegistry, Registry<Biome> biomeRegistry, Registry<StructureSet> noiseRegistry, Registry<ChunkGeneratorSettings> chunkGenSettingsRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry) {
//        dimOptionsRegistry.add(RegistryKey.of(Registry.DIMENSION_KEY, new Identifier(Constant.MOD_ID, "moon")), new DimensionOptions(dimTypeRegistry.getOrCreateEntry(GalacticraftDimensionType.MOON_DIMENSION_TYPE_KEY), new NoiseChunkGenerator(noiseRegistry, structuresRegistry, GalacticraftBiomeParameters.MOON.getBiomeSource(biomeRegistry), seed, chunkGenSettingsRegistry.getOrCreateEntry(GalacticraftChunkGeneratorSettings.MOON))), Lifecycle.stable());
//    } TODO: PORT registry is frozen (also why not just use a datapack?)

    @Inject(method = "bootstrap", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addGCDimOptions(Registry<DimensionType> registry, CallbackInfoReturnable<Holder<DimensionType>> cir) {
        GalacticraftDimensionType.register(registry);
    }
}
