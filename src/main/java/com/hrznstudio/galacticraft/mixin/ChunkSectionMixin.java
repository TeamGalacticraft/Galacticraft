/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.ChunkSectionOxygenAccessor;
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
    private final @Unique boolean[] oxygen = new boolean[16 * 16 * 16];
    private @Unique boolean hasHadOxygen = false;

    @Override
    public boolean isBreathable(int x, int y, int z) {
        return oxygen[x + (y * 16) + (z * 16 * 16)];
    }

    @Override
    public void setBreathable(int x, int y, int z, boolean value) {
        oxygen[x + (y * 16) + (z * 16 * 16)] = value;
        hasHadOxygen |= value;
    }

    @Inject(method = "getPacketSize", at = @At("RETURN"), cancellable = true)
    private void addOxygenSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() + (hasHadOxygen ? 1 : (4096 / 8)));
    }

    @Inject(method = "toPacket", at = @At("RETURN"))
    private void toPacket(PacketByteBuf packetByteBuf, CallbackInfo ci) {
        packetByteBuf.writeBoolean(hasHadOxygen);
        if (hasHadOxygen) {
            byte[] array = new byte[(16 * 16 * 16) / 8];
            boolean[] arr = getArray();
            for (int p = 0; p < arr.length - 8; p += 9) {
                byte b = -128;
                b += arr[p] ? 1 : 0;
                b += arr[p + 1] ? 2 : 0;
                b += arr[p + 2] ? 4 : 0;
                b += arr[p + 3] ? 8 : 0;
                b += arr[p + 4] ? 16 : 0;
                b += arr[p + 5] ? 32 : 0;
                b += arr[p + 6] ? 64 : 0;
                b += arr[p + 7] ? 128 : 0;
                array[p / 8] = b;
            }

            for (byte j : array) {
                packetByteBuf.writeByte(j);
            }
        }
//        if (hasHadOxygen) {
//            short with = 0;
//            short without = 0;
//            boolean init = false;
//            for (boolean o : oxygen) {
//                if (!init) {
//                    init = true;
//                    packetByteBuf.writeBoolean(o);
//                }
//                if (o) {
//                    with++;
//                    if (without > 0) {
//                        packetByteBuf.writeBoolean(without < 255);
//                        if (without < 255) {
//                            packetByteBuf.writeByte(without - 128);
//                        } else {
//                            packetByteBuf.writeShort(without);
//                        }
//                        without = 0;
//                    }
//                } else {
//                    without++;
//                    if (with > 0) {
//                        packetByteBuf.writeBoolean(with < 255);
//                        if (with < 255) {
//                            packetByteBuf.writeByte(with - 128);
//                        } else {
//                            packetByteBuf.writeShort(with);
//                        }
//                        with = 0;
//                    }
//                }
//            }
//        }
//        packetByteBuf.writeBoolean(false).writeShort(Short.MIN_VALUE);
    }

    @Override
    public boolean[] getArray() {
        return this.oxygen;
    }

    @Override
    public boolean hasOxygen() {
        return hasHadOxygen;
    }

    public ChunkSectionMixin(boolean hasHadOxygen) {
        this.hasHadOxygen = hasHadOxygen;
    }
}
