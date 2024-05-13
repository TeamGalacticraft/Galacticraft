/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.impl.internal.accessor.ChunkOxygenAccessor;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenSyncer;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess implements ChunkOxygenAccessor, ChunkOxygenSyncer {
    @Shadow @Final Level level;
    private @Unique short dirtySections = 0b0;

    private LevelChunkMixin(ChunkPos pos, UpgradeData upgradeData, LevelHeightAccessor heightLimitView, Registry<Biome> biome, long inhabitedTime, @Nullable LevelChunkSection[] sectionArrayInitializer, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biome, inhabitedTime, sectionArrayInitializer, blendingData);
    }

    @Override
    public boolean galacticraft$isInverted(int x, int y, int z) {
        return ((ChunkSectionOxygenAccessor) this.sections[this.getSectionIndex(y)]).galacticraft$isInverted(x, y & 15, z);
    }

    @Override
    public void galacticraft$setInverted(int x, int y, int z, boolean inverted) {
        var accessor = ((ChunkSectionOxygenAccessor) this.sections[this.getSectionIndex(y)]);
        if (inverted != accessor.galacticraft$isInverted(x, y & 15, z)) {
            if (!this.level.isClientSide) {
                this.unsaved = true;
                this.dirtySections |= (0b1 << this.getSectionIndex(y));
            }
            accessor.galacticraft$setInverted(x, y & 15, z, inverted);
        }
    }

    @Override
    public @Nullable FriendlyByteBuf galacticraft$syncOxygenPacketsToClient() {
        assert !this.level.isClientSide;
        if (this.dirtySections != 0) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer(Integer.BYTES * 2 + Short.BYTES + (Short.BYTES + 1)));
            buf.writeInt(this.getPos().x);
            buf.writeInt(this.getPos().z);
            buf.writeShort(this.dirtySections);
            for (int i = 0; i < this.sections.length; i++) {
                if ((this.dirtySections & (0b1 << i++)) != 0) {
                    ((ChunkSectionOxygenAccessor) this.sections[i]).galacticraft$writeOxygenPacket(buf);
                }
            }
            this.dirtySections = 0;
            return buf;
        }
        return null;
    }

    @Override
    public void galacticraft$readOxygenUpdate(@NotNull FriendlyByteBuf buf) {
        short dirty = buf.readShort();
        for (int i = 0; i < this.sections.length; i++) {
            if ((dirty & (0b1 << i++)) != 0) {
                ((ChunkSectionOxygenAccessor) this.sections[i]).galacticraft$readOxygenPacket(buf);
            }
        }
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 0))
    private void resetAirOnBlockChange(BlockPos pos, BlockState blockState, boolean bl, CallbackInfoReturnable<BlockState> cir) {
        this.galacticraft$setInverted(pos.getX() & 15, pos.getY(), pos.getZ() & 15, false);
    }
}
