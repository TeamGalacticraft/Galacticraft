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

package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.accessor.ChunkSectionOxygenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkSection.class)
@Environment(EnvType.CLIENT)
public abstract class ClientChunkSectionMixin implements ChunkSectionOxygenAccessor {
    @Inject(method = "fromPacket", at = @At("RETURN"))
    private void fromPacket(PacketByteBuf packetByteBuf, CallbackInfo ci) {
        this.setTotalOxygen(packetByteBuf.readShort());
        if (this.getTotalOxygen() == 0) return;
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
            oxygen[(i * 8) + 7] = (b & 128) !=0 ;
        }
    }
}
