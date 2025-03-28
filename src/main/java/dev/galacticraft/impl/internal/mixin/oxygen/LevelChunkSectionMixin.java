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

import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
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
    private @Unique
    @Nullable BitSet bits = null;

    @Override
    public boolean galacticraft$isInverted(int pos) {
        return this.bits != null && this.bits.get(pos);
    }

    @Override
    public void galacticraft$setInverted(int pos, boolean value) {
        if (value) {
            if (this.bits == null) this.bits = new BitSet(pos);
            this.bits.set(pos);
        } else if (this.bits != null) {
            this.bits.clear(pos);
        }
    }

    @Inject(method = "getSerializedSize", at = @At("RETURN"), cancellable = true)
    private void increaseChunkPacketSize(CallbackInfoReturnable<Integer> cir) {
        if (this.bits == null) {
            cir.setReturnValue(cir.getReturnValueI() + VarInt.getByteSize(0));
        } else {
            byte[] byteArray = this.bits.toByteArray();
            cir.setReturnValue(cir.getReturnValueI() + (VarInt.getByteSize(byteArray.length) + byteArray.length));
        }
    }

    @Inject(method = "hasOnlyAir()Z", at = @At("RETURN"), cancellable = true)
    private void verifyOxygenEmpty(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() && this.galacticraft$isEmpty());
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
    public boolean galacticraft$isEmpty() {
        return this.bits == null || this.bits.isEmpty();
    }

    @Override
    public BitSet galacticraft$getBits() {
        return this.bits;
    }

    @Override
    public void galacticraft$setBits(@Nullable BitSet set) {
        this.bits = set;
    }

    @Override
    public void galacticraft$writeOxygenPacket(@NotNull FriendlyByteBuf buf) {
        if (this.bits != null) {
            byte[] bytes = this.bits.toByteArray();
            buf.writeByteArray(bytes);
        } else {
            buf.writeVarInt(0);
        }
    }

    @Override
    public void galacticraft$readOxygenPacket(@NotNull FriendlyByteBuf buf) {
        byte[] bytes = buf.readByteArray();
        if (bytes.length != 0) {
            this.bits = BitSet.valueOf(bytes);
        } else {
            this.bits = null;
        }
    }
}
