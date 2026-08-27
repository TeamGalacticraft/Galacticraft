/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.PlanetChunkGenerator;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    /**
     * Vanilla only uses the generator's actual NoiseGeneratorSettings when the
     * generator is a NoiseBasedChunkGenerator.
     *
     * <p>Galacticraft planet generators extend PlanetChunkGenerator instead, so
     * vanilla would normally construct their RandomState using
     * NoiseGeneratorSettings.dummy().</p>
     *
     * <p>This replaces the dummy settings before the RandomState is created,
     * ensuring both ChunkMap.randomState and ChunkGeneratorStructureState use
     * the planet's actual noise settings.</p>
     */
    @ModifyExpressionValue(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;dummy()Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;"
            )
    )
    private NoiseGeneratorSettings galacticraft$usePlanetNoiseSettings(
            NoiseGeneratorSettings original,
            @Local(argsOnly = true) ChunkGenerator chunkGenerator
    ) {
        if (chunkGenerator instanceof PlanetChunkGenerator planetGenerator) {
            return planetGenerator.generatorSettings().value();
        }

        return original;
    }
}