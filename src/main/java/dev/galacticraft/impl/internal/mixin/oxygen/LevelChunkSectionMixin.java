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

import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.mod.Constant;
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

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements ChunkSectionOxygenAccessor {
    private @Unique @Nullable BitSet inversionBits = null;
    private @Unique short modifiedBlocks = 0;

    @Override
    public boolean galacticraft$isInverted(int x, int y, int z) {
        if (this.modifiedBlocks == 0) return false;
        assert this.inversionBits != null; // if modifiedBlocks > 0 inverted should not be null.
        return this.inversionBits.get(x + (y << 4) + (z << 8));
    }

    @Override
    public void galacticraft$setInverted(int x, int y, int z, boolean inverted) {
        int bitIndex = x + (y << 4) + (z << 8);
        if (inverted) {
            if (this.inversionBits == null) {
                assert this.modifiedBlocks == 0;
                this.inversionBits = new BitSet(bitIndex); // do not allocate a full bitset if not necessary
                this.inversionBits.set(bitIndex);
                this.modifiedBlocks = 1;
            } else {
                if (!this.inversionBits.get(bitIndex)) {
                    this.inversionBits.set(bitIndex);
                    this.modifiedBlocks++;
                }
            }
        } else if (this.inversionBits != null && this.inversionBits.get(bitIndex)) {
            this.inversionBits.clear(bitIndex);
            if (--this.modifiedBlocks == 0) {
                this.inversionBits = null;
            }
        }
    }

    @Inject(method = "getSerializedSize", at = @At("RETURN"), cancellable = true)
    private void increaseChunkPacketSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() + (this.modifiedBlocks == 0 ? 0 : (Constant.Chunk.CHUNK_SECTION_AREA / Byte.SIZE)) + 2 + 1);
    }

    @Inject(method = "hasOnlyAir()Z", at = @At("RETURN"), cancellable = true)
    private void verifyOxygenEmpty(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() && this.modifiedBlocks == 0);
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeOxygenDataToPacket(FriendlyByteBuf buf, CallbackInfo ci) {
        this.galacticraft$writeOxygenPacket(buf);
    }

    @Inject(method = "read", at = @At("RETURN"))
    private void galacticraft_fromPacket(FriendlyByteBuf buf, CallbackInfo ci) {
        this.galacticraft$readOxygenPacket(buf);
    }

    @Override
    public BitSet galacticraft$inversionBits() {
        return this.inversionBits;
    }

    @Override
    public void galacticraft$setInversionBits(BitSet set) {
        this.inversionBits = set;
    }

    @Override
    public short galacticraft$modifiedBlocks() {
        return this.modifiedBlocks;
    }

    @Override
    public void galacticraft$setModifiedBlocks(short amount) {
        this.modifiedBlocks = amount;
    }

    @Override
    public void galacticraft$writeOxygenPacket(@NotNull FriendlyByteBuf buf) {
        buf.writeShort(this.galacticraft$modifiedBlocks());

        if (this.galacticraft$modifiedBlocks() > 0) {
            assert this.inversionBits != null;
            buf.writeLongArray(this.inversionBits.toLongArray());
        }
    }

    @Override
    public void galacticraft$readOxygenPacket(@NotNull FriendlyByteBuf buf) {
        this.galacticraft$setModifiedBlocks(buf.readShort());
        if (this.galacticraft$modifiedBlocks() > 0) {
            this.galacticraft$setInversionBits(BitSet.valueOf(buf.readLongArray()));
        } else {
            this.galacticraft$setInversionBits(null);
        }
    }
}
