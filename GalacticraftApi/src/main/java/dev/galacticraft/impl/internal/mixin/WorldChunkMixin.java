/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.impl.internal.mixin;

import dev.galacticraft.api.accessor.ChunkOxygenAccessor;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenAccessorInternal;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenSyncer;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessorInternal;
import dev.galacticraft.impl.internal.accessor.WorldOxygenAccessorInternal;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(LevelChunk.class)
public abstract class WorldChunkMixin extends ChunkAccess implements ChunkOxygenAccessor, ChunkOxygenSyncer, ChunkOxygenAccessorInternal {
    @Shadow @Final Level level;
    private @Unique boolean /*@NotNull*/ [] sectionDirty;
    private @Unique boolean defaultBreathable = false;
    private @Unique byte dirty = 0;

    private WorldChunkMixin(ChunkPos pos, UpgradeData upgradeData, LevelHeightAccessor heightLimitView, Registry<Biome> biome, long inhabitedTime, @Nullable LevelChunkSection[] sectionArrayInitializer, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biome, inhabitedTime, sectionArrayInitializer, blendingData);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/UpgradeData;Lnet/minecraft/world/ticks/LevelChunkTicks;Lnet/minecraft/world/ticks/LevelChunkTicks;J[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V", at = @At("RETURN"))
    private void galacticraft_init(@NotNull Level world, ChunkPos pos, UpgradeData upgradeData, LevelChunkTicks<Block> blockTickScheduler, LevelChunkTicks<Fluid> fluidTickScheduler, long inhabitedTime, LevelChunkSection[] sectionArrayInitializer, LevelChunk.PostLoadProcessor entityLoader, BlendingData blendingData, CallbackInfo ci) {
        this.sectionDirty = new boolean[world.getSectionsCount()];
        this.defaultBreathable = ((WorldOxygenAccessorInternal) world).getDefaultBreathable();
        for (LevelChunkSection section : this.getSections()) {
            assert section != null;
            ((ChunkSectionOxygenAccessorInternal) section).setDefaultBreathable(this.defaultBreathable);
        }
    }


    @Override
    public boolean isBreathable(int x, int y, int z) {
        if (this.isOutsideBuildHeight(y)) return this.defaultBreathable;
        LevelChunkSection section = this.getSection(this.getSectionIndex(y));
        if (!section.hasOnlyAir()) {
            return section.isBreathable(x & 15, y & 15, z & 15);
        }
        return this.defaultBreathable;
    }

    @Override
    public void setBreathable(int x, int y, int z, boolean value) {
        if (this.isOutsideBuildHeight(y)) return;
        LevelChunkSection section = this.getSection(this.getSectionIndex(y));
        assert section != null;
        if (value != section.isBreathable(x & 15, y & 15, z & 15)) {
            if (!this.level.isClientSide) {
                this.unsaved = true;
                if (!this.sectionDirty[this.getSectionIndex(y)]) {
                    this.sectionDirty[this.getSectionIndex(y)] = true;
                    this.dirty++;
                }
            }
            section.setBreathable(x & 15, y & 15, z & 15, value);
        }
    }

    @Override
    public @Nullable FriendlyByteBuf syncOxygenPacketsToClient() {
        assert !this.level.isClientSide;
        if (this.dirty != 0) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer(8 + this.dirty * 4));
            buf.writeInt(this.getPos().x);
            buf.writeInt(this.getPos().z);
            buf.writeByte(this.dirty);
            for (int i = 0; i < this.sectionDirty.length; i++) {
                if (this.sectionDirty[i]) {
                    this.sectionDirty[i] = false;
                    buf.writeByte(i);
                    ((ChunkSectionOxygenAccessorInternal) this.getSection(i)).writeOxygenPacket(buf);
                }
            }
            this.dirty = 0;
            return buf;
        }
        return null;
    }

    @Override
    public boolean getDefaultBreathable() {
        return this.defaultBreathable;
    }

    @Override
    public void setDefaultBreathable(boolean defaultBreathable) {
        this.defaultBreathable = defaultBreathable;
    }

    @Override
    public void readOxygenUpdate(byte b, @NotNull FriendlyByteBuf buf) {
        LevelChunkSection section = this.getSection(b);
        assert section != null;
        ((ChunkSectionOxygenAccessorInternal) section).readOxygenPacket(buf);
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;hasOnlyAir()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void galacticraft_passDefaultValue(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir, int i, LevelChunkSection chunkSection) {
        this.setBreathable(pos.getX(), pos.getY(), pos.getZ(), this.defaultBreathable);
    }
}
