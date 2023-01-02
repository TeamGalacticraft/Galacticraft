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

import dev.galacticraft.api.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessorInternal;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.BitSet;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(LevelChunkSection.class)
public abstract class ChunkSectionMixin implements ChunkSectionOxygenAccessor, ChunkSectionOxygenAccessorInternal {
    private @Unique @Nullable BitSet inverted = null;
    private @Unique short modifiedBlocks = 0;
    private @Unique boolean defaultBreathable = false;

    @Override
    public boolean isBreathable(int x, int y, int z) {
        if (this.modifiedBlocks == 0) return this.defaultBreathable;
        assert this.inverted != null; // if modifiedBlocks > 0 inverted should not be null.
        boolean b = this.inverted.get(x + (y << 4) + (z << 8));
        return (this.defaultBreathable && !b) || (!this.defaultBreathable && b);
    }

    @Override
    public void setBreathable(int x, int y, int z, boolean breathable) {
        boolean inversion = (this.defaultBreathable && !breathable) || (!this.defaultBreathable && breathable);
        int bitIndex = x + (y << 4) + (z << 8);
        if (inversion) {
            if (this.inverted == null) {
                assert this.modifiedBlocks == 0;
                this.inverted = new BitSet(bitIndex); // do not allocate a full bitset if not necessary
                this.inverted.set(bitIndex);
                this.modifiedBlocks = 1;
            } else {
                if (!this.inverted.get(bitIndex)) {
                    this.inverted.set(bitIndex);
                    this.modifiedBlocks++;
                }
            }
        } else if (this.inverted != null && this.inverted.get(bitIndex)) {
            this.inverted.clear(bitIndex);
            if (--this.modifiedBlocks == 0) {
                this.inverted = null;
            }
        }
    }

    @Inject(method = "getSerializedSize", at = @At("RETURN"), cancellable = true)
    private void galacticraft_increaseChunkPacketSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() + (this.modifiedBlocks == 0 ? 0 : (Constant.Chunk.CHUNK_SECTION_AREA / Byte.SIZE)) + 2 + 1);
    }

    @Inject(method = "hasOnlyAir()Z", at = @At("RETURN"), cancellable = true)
    private void galacticraft_verifyOxygenEmpty(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() && this.modifiedBlocks == 0);
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void galacticraft_writeOxygenDataToPacket(FriendlyByteBuf buf, CallbackInfo ci) {
        this.writeOxygenPacket(buf);
    }

    @Override
    public BitSet getInversion() {
        return this.inverted;
    }

    @Override
    public void setInversion(BitSet inverted) {
        this.inverted = inverted;
    }

    @Override
    public short getModifiedBlocks() {
        return this.modifiedBlocks;
    }

    @Override
    public void setModifiedBlocks(short amount) {
        this.modifiedBlocks = amount;
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
    public void writeOxygenPacket(@NotNull FriendlyByteBuf buf) {
        buf.writeBoolean(this.getDefaultBreathable());
        buf.writeShort(this.getModifiedBlocks());

        if (this.getModifiedBlocks() > 0) {
            assert this.getInversion() != null;
            buf.writeLongArray(this.getInversion().toLongArray());
        }
    }

    @Override
    public void readOxygenPacket(@NotNull FriendlyByteBuf buf) {
        this.setDefaultBreathable(buf.readBoolean());
        this.setModifiedBlocks(buf.readShort());
        if (this.getModifiedBlocks() > 0) {
            this.setInversion(BitSet.valueOf(buf.readLongArray()));
        } else {
            this.setInversion(null);
        }
    }
}
