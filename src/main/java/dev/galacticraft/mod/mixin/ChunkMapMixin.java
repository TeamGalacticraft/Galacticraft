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

import com.mojang.datafixers.DataFixer;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.PlanetChunkGenerator;
import dev.galacticraft.mod.world.gen.feature.custom.DeferredBlockPlacement;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @Inject(method = "onChunkReadyToSend", at = @At("TAIL"))
    private void flushDeferredBlocks(LevelChunk chunk, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) chunk.getLevel();
        DeferredBlockPlacement.flush(level, chunk.getPos());
    }

    @Shadow
    @Mutable
    private RandomState randomState;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void galacticraft$replaceRandomState(
            ServerLevel level,
            LevelStorageSource.LevelStorageAccess user,
            DataFixer dataFixer,
            StructureTemplateManager structureTemplateManager,
            Executor executor,
            BlockableEventLoop<Runnable> mainThreadExecutor,
            LightChunkGetter lightChunkGetter,
            ChunkGenerator chunkGenerator,
            ChunkProgressListener chunkProgressListener,
            ChunkStatusUpdateListener chunkStatusUpdateListener,
            Supplier<DimensionDataStorage> persistentStateManagerFactory,
            int viewDistance,
            boolean dsync,
            CallbackInfo ci
    ) {
        if (chunkGenerator instanceof PlanetChunkGenerator planetGenerator) {
            this.randomState = RandomState.create(
                    planetGenerator.generatorSettings().value(),
                    level.registryAccess().lookupOrThrow(Registries.NOISE),
                    level.getSeed()
            );
        }
    }
}
