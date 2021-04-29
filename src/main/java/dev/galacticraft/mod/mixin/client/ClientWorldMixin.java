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

package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.api.atmosphere.AtmosphericGas;
import dev.galacticraft.api.celestialbody.CelestialBodyType;
import dev.galacticraft.mod.accessor.ChunkOxygenAccessor;
import dev.galacticraft.mod.accessor.WorldOxygenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
@Environment(EnvType.CLIENT)
public abstract class ClientWorldMixin implements WorldOxygenAccessor {
    private @Unique boolean breathable = true;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey<World> registryRef, DimensionType dimensionType, int loadDistance, Supplier<Profiler> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
        CelestialBodyType.getByDimType(networkHandler.getRegistryManager(), ((World)(Object)this).getRegistryKey()).ifPresent(celestialBodyType -> this.breathable = celestialBodyType.getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN));
    }

    @Override
    public boolean isBreathable(BlockPos pos) {
        if (breathable) return true;
        if (World.isOutOfBuildLimitVertically(pos)) return false;
        return ((ChunkOxygenAccessor) ((World)(Object)this).getWorldChunk(pos)).isBreathable(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
    }

    @Override
    public void setBreathable(BlockPos pos, boolean value) {
        if (World.isOutOfBuildLimitVertically(pos) || breathable) return;
        ((ChunkOxygenAccessor) ((World)(Object)this).getWorldChunk(pos)).setBreathable(pos.getX() & 15, pos.getY(), pos.getZ() & 15, value);
    }
}