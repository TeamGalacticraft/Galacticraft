/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.ChunkOxygenAccessor;
import com.hrznstudio.galacticraft.accessor.WorldOxygenAccessor;
import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(Level.class)
public abstract class LevelMixin implements WorldOxygenAccessor {
    @Shadow
    public static boolean isOutsideBuildHeight(BlockPos pos) {
        throw new UnsupportedOperationException("Shadowed method was not transformed!");
    }

    @Shadow public abstract LevelChunk getChunkAt(BlockPos pos);

    @Shadow @Final private ResourceKey<Level> dimension;

    private @Unique boolean breathable = true;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(WritableLevelData properties, ResourceKey<Level> registryRef, DimensionType dimensionType, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long seed, CallbackInfo ci) {
        CelestialBodyType.getByDimType(this.dimension).ifPresent(celestialBodyType -> this.breathable = celestialBodyType.getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN));
    }

    @Override
    public boolean isBreathable(BlockPos pos) {
        if (breathable) return true;
        if (isOutsideBuildHeight(pos)) return false;
        return ((ChunkOxygenAccessor) this.getChunkAt(pos)).isBreathable(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
    }

    @Override
    public void setBreathable(BlockPos pos, boolean value) {
        if (isOutsideBuildHeight(pos) || breathable) return;
        ((ChunkOxygenAccessor) this.getChunkAt(pos)).setBreathable(pos.getX() & 15, pos.getY(), pos.getZ() & 15, value);
    }
}
