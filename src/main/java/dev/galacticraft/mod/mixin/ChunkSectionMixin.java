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

import dev.galacticraft.mod.accessor.ChunkSectionOxygenAccessor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSection.class)
public abstract class ChunkSectionMixin implements ChunkSectionOxygenAccessor {
    private @Unique boolean[] oxygen;
    private @Unique short oxygenated = 0;

    @Override
    public boolean isBreathable(int x, int y, int z) {
        if (oxygenated == 0 || this.oxygen == null) return false;
        return this.oxygen[x + (y * 16) + (z * 16 * 16)];
    }

    @Override
    public void setBreathable(int x, int y, int z, boolean value) {
        if (this.oxygen == null) this.oxygen = new boolean[16 * 16 * 16];
        boolean current = this.oxygen[x + (y * 16) + (z * 16 * 16)];
        if (current != value) {
            if (value) {
                oxygenated++;
            } else {
                oxygenated--;
                if (oxygenated == 0) {
                    this.oxygen = null;
                    return;
                }
            }
        }
        this.oxygen[x + (y * 16) + (z * 16 * 16)] = value;
    }

    @Inject(method = "getPacketSize", at = @At("RETURN"), cancellable = true)
    private void addOxygenSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() + (this.oxygenated == 0 ? 0 : (4096 / 8)) + 2 + 1);
    }

    @Inject(method = "toPacket", at = @At("RETURN"))
    private void toPacket(PacketByteBuf packetByteBuf, CallbackInfo ci) {
        packetByteBuf.writeShort(this.oxygenated);
        if (this.oxygenated > 0) {
            this.writeOxygen(packetByteBuf);
        }
    }

    @Override
    public boolean[] getArray() {
        return this.oxygen;
    }

    @Override
    public short getTotalOxygen() {
        return this.oxygenated;
    }

    @Override
    public void setTotalOxygen(short amount) {
        this.oxygenated = amount;
    }

    @Override
    public void writeOxygen(PacketByteBuf buf) {
        boolean[] arr = this.getArray();
        for (int p = 0; p < (16 * 16 * 16) / 8; p++) {
            byte b = -128;
            b += arr[(p * 8)] ? 1 : 0;
            b += arr[(p * 8) + 1] ? 2 : 0;
            b += arr[(p * 8) + 2] ? 4 : 0;
            b += arr[(p * 8) + 3] ? 8 : 0;
            b += arr[(p * 8) + 4] ? 16 : 0;
            b += arr[(p * 8) + 5] ? 32 : 0;
            b += arr[(p * 8) + 6] ? 64 : 0;
            b += arr[(p * 8) + 7] ? 128 : 0;
            buf.writeByte(b);
        }
    }

    @Override
    public void readOxygen(PacketByteBuf packetByteBuf) {
        boolean[] oxygen = this.getArray();
        for (int i = 0; i < (16 * 16 * 16) / 8; i++) {
            short b = (short) (packetByteBuf.readByte() + 128);
            oxygen[(i * 8)] = (b & 1) != 0;
            oxygen[(i * 8) + 1] = (b & 2) != 0;
            oxygen[(i * 8) + 2] = (b & 4) != 0;
            oxygen[(i * 8) + 3] = (b & 8) != 0;
            oxygen[(i * 8) + 4] = (b & 16) != 0;
            oxygen[(i * 8) + 5] = (b & 32) != 0;
            oxygen[(i * 8) + 6] = (b & 64) != 0;
            oxygen[(i * 8) + 7] = (b & 128) != 0;
        }
    }
}
