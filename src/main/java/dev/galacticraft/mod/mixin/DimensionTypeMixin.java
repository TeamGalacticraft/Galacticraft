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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.world.biome.source.MoonBiomeSource;
import dev.galacticraft.mod.world.gen.chunk.MoonChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.Optional;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(DimensionType.class)
public abstract class DimensionTypeMixin {
    @Inject(method = "createDefaultDimensionOptions", at = @At(value = "RETURN", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addGCDimOptions(Registry<DimensionType> registry, Registry<Biome> registry2, Registry<ChunkGeneratorSettings> registry3, long l, CallbackInfoReturnable<SimpleRegistry<DimensionOptions>> cir, SimpleRegistry<DimensionOptions> simpleRegistry) {
        Registry.register(simpleRegistry, new Identifier(Constant.MOD_ID, "moon"), new DimensionOptions(() -> registry.get(new Identifier(Constant.MOD_ID, "moon")), new MoonChunkGenerator(new MoonBiomeSource(l, 4, registry2), l, () -> new ChunkGeneratorSettings(
                new StructuresConfig(Optional.empty(), Collections.emptyMap()), GenerationShapeConfig.create(
                        0, 256,
                new NoiseSamplingConfig(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D),
                new SlideConfig(-10, 3, 0),
                new SlideConfig(15, 3, 0),
                1, 2, 1.0D,
                -0.46875D, true,
                true, false, false),
                GalacticraftBlock.MOON_ROCKS[0].getDefaultState(), Blocks.WATER.getDefaultState(),
                -2147483648, 0, 63,
                0, false, false,
                false, false, false,
                false
        ))));
    }
}
