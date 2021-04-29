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

import com.google.common.collect.ImmutableList;
import dev.galacticraft.api.atmosphere.AtmosphericGas;
import dev.galacticraft.api.celestialbody.CelestialBodyType;
import dev.galacticraft.mod.accessor.ChunkOxygenAccessor;
import dev.galacticraft.mod.accessor.WorldOxygenAccessor;
import dev.galacticraft.mod.world.dimension.GalacticraftDimension;
import dev.galacticraft.mod.world.gen.spawner.EvolvedPillagerSpawner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WorldOxygenAccessor {
    @Shadow @Final @Mutable private List<Spawner> spawners;
    private @Unique boolean breathable = true;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean debugWorld, long l, List<Spawner> list, boolean bl, CallbackInfo ci) {
        CelestialBodyType.getByDimType(server.getRegistryManager(), ((World)(Object)this).getRegistryKey()).ifPresent(celestialBodyType -> this.breathable = celestialBodyType.getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN));
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

    @Inject(method = "<init>", at = @At("RETURN"))
    private void setSpawnersGC(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean debugWorld, long l, List<Spawner> list, boolean bl, CallbackInfo ci) {
        if (registryKey.equals(GalacticraftDimension.MOON)) {
            this.spawners = ImmutableList.<Spawner>builder().add(new EvolvedPillagerSpawner()).build();
        }
    }
}