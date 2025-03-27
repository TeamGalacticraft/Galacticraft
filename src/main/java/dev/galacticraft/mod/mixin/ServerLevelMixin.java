/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.mod.accessor.GCLevelAccessor;
import dev.galacticraft.mod.machine.SealerManager;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import dev.galacticraft.mod.misc.footprint.ServerFootprintManager;
import dev.galacticraft.mod.world.dimension.GCDimensions;
import dev.galacticraft.mod.world.gen.spawner.EvolvedPillagerSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements GCLevelAccessor {
    @Shadow
    @Final
    @Mutable
    private List<CustomSpawner> customSpawners;

    @Shadow
    public abstract ServerLevel getLevel();

    private final @Unique FootprintManager footprintManager = new ServerFootprintManager();

    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void setSpawnersGC(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess session, ServerLevelData properties, ResourceKey<Level> worldKey, LevelStem dimensionOptions, ChunkProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, @Nullable RandomSequences randomSequences, CallbackInfo ci) {
        if (worldKey.equals(GCDimensions.MOON)) {
            this.customSpawners = ImmutableList.<CustomSpawner>builder().add(new EvolvedPillagerSpawner()).build();
        }
    }

    @Inject(method = "sendBlockUpdated", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", remap = false))
    private void onBlockChanges(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        // Skip if both old and new states are solid blocks
        if (oldState.isSolid() && newState.isSolid()) {
            return;
        }

        // Check if the block is a leaf (for oxygen collection logic)
        if (newState.is(BlockTags.LEAVES)) {
            // Oxygen collector code update (if needed)
        }

        // Notify the SealerManager about the block change
        SealerManager manager = ((GCLevelAccessor) getLevel()).getSealerManager();
        manager.onBlockChange(pos, newState, this.getLevel());
    }

    @Inject(method = "tickChunk", at = @At("HEAD"))
    private void tickFootprints(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        var profiler = getProfiler();
        profiler.push("footprints");
        footprintManager.tick((ServerLevel) (Object) this, chunk.getPos().toLong());
        profiler.pop();
    }

    @Override
    public FootprintManager galacticraft$getFootprintManager() {
        return footprintManager;
    }
}