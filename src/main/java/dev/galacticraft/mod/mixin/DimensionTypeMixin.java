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

import com.google.common.collect.ImmutableMap;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.structure.GalacticraftStructure;
import dev.galacticraft.mod.world.biome.source.MoonBiomeSource;
import dev.galacticraft.mod.world.gen.chunk.MoonChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(DimensionType.class)
public abstract class DimensionTypeMixin {
    @Inject(method = "createDefaultDimensionOptions", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addGCDimOptions(Registry<DimensionType> registry, Registry<Biome> registry2, Registry<ChunkGeneratorSettings> registry3, long l, CallbackInfoReturnable<SimpleRegistry<DimensionOptions>> cir, SimpleRegistry<DimensionOptions> simpleRegistry) {
        //noinspection Convert2MethodRef
        Registry.register(simpleRegistry, new Identifier(Constant.MOD_ID, "moon"), new DimensionOptions(() -> registry.get(new Identifier(Constant.MOD_ID, "moon")), new MoonChunkGenerator(new MoonBiomeSource(l, 4, registry2), l, () -> createMoonSettings_gc())));
    }

    private static ChunkGeneratorSettings createMoonSettings_gc() {
        return new ChunkGeneratorSettings(
                new StructuresConfig(Optional.empty(), Util.make(() -> {
                    ImmutableMap.Builder<StructureFeature<?>, StructureConfig> builder = ImmutableMap.builder();
                    builder.put(StructureFeature.VILLAGE, new StructureConfig(24, 16, 930573769));
                    builder.put(GalacticraftStructure.MOON_PILLAGER_BASE_FEATURE, new StructureConfig(32, 16, 56836814));
                    builder.put(GalacticraftStructure.MOON_RUINS, new StructureConfig(24, 8, 78473257));
                    return builder.build();
                })),
                GenerationShapeConfig.create(0,
                        256,
                        new NoiseSamplingConfig(1.2, 1.0, 400.0, 300.0),
                        new SlideConfig(-10, 3, 0),
                        new SlideConfig(15, 30, 3),
                        1,
                        2,
                        1.0,
                        -0.46875,
                        true,
                        true,
                        false,
                        false),
                GalacticraftBlock.MOON_ROCKS[0].getDefaultState(),
                Blocks.WATER.getDefaultState(),
                Integer.MIN_VALUE,
                0,
                Integer.MIN_VALUE,
                0,
                false,
                false,
                false,
                false,
                false,
                false);
    }
}
