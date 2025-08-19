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

package dev.galacticraft.impl.internal.mixin.oxygen;

import dev.galacticraft.api.block.entity.AtmosphereProvider;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenAccessor;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenSyncer;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess implements ChunkOxygenAccessor, ChunkOxygenSyncer {
    @Shadow
    @Final
    Level level;

    public LevelChunkMixin(ChunkPos pos, UpgradeData upgradeData, LevelHeightAccessor heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable LevelChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biomeRegistry, inhabitedTime, sectionArray, blendingData);
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 0))
    private void notifyAPsOnBlockChange(BlockPos pos, BlockState blockState, boolean bl, CallbackInfoReturnable<BlockState> cir) {
        Iterator<BlockPos> iterator = this.galacticraft$getHandlers(pos.getX(), pos.getY(), pos.getZ());
        while (iterator.hasNext()) {
            BlockPos atPos = iterator.next();
            BlockEntity blockEntity = this.level.getBlockEntity(atPos);
            if (blockEntity instanceof AtmosphereProvider provider) {
                provider.notifyStateChange(pos);
            } else {
                for (LevelChunkSection section : this.sections) {
                    ((ChunkSectionOxygenAccessor) section).galacticraft$deallocate(atPos); //todo dirty
                }
            }
        }
    }
}
