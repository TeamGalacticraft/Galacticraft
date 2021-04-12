/*
 * Copyright (c) 2019-2021 HRZN LTD
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

import com.hrznstudio.galacticraft.accessor.ChunkOxygenAccessor;
import com.hrznstudio.galacticraft.accessor.ChunkSectionOxygenAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin implements ChunkOxygenAccessor {
    @Shadow @Final private LevelChunkSection[] sections;

    @Override
    public boolean isBreathable(int x, int y, int z) {
        if (y < 0 || y > 255) return false;
        LevelChunkSection section = sections[y >> 4];
        if (!LevelChunkSection.isEmpty(section)) {
            return ((ChunkSectionOxygenAccessor) section).isBreathable(x & 15, y & 15, z & 15);
        }
        return false;
    }

    @Override
    public void setBreathable(int x, int y, int z, boolean value) {
        if (y < 0 || y > 255) return;
        LevelChunkSection section = sections[y >> 4];
        if (!LevelChunkSection.isEmpty(section)) {
            ((ChunkSectionOxygenAccessor) section).setBreathable(x & 15, y & 15, z & 15, value);
        }
    }

    @Override
    public List<ClientboundCustomPayloadPacket> syncToClient() {
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public void readOxygenUpdate(byte b, FriendlyByteBuf packetByteBuf) {
        throw new UnsupportedOperationException("NYI");
    }
}
