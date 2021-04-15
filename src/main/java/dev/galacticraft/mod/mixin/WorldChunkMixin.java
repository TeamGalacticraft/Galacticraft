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

import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.accessor.ChunkOxygenAccessor;
import dev.galacticraft.mod.accessor.ChunkSectionOxygenAccessor;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin implements ChunkOxygenAccessor {
    @Shadow
    @Final
    private ChunkSection[] sections;

    @Shadow public abstract void setShouldSave(boolean shouldSave);

    @Shadow @Final private ChunkPos pos;
    @Shadow @Final private World world;
    private @Unique boolean update = false;
    private final @Unique boolean[] updatable = new boolean[16];

    @Override
    public boolean isBreathable(int x, int y, int z) {
        if (y < 0 || y > 255) return false;
        ChunkSection section = sections[y >> 4];
        if (!ChunkSection.isEmpty(section)) {
            return ((ChunkSectionOxygenAccessor) section).isBreathable(x & 15, y & 15, z & 15);
        }
        return false;
    }

    @Override
    public void setBreathable(int x, int y, int z, boolean value) {
        if (y < 0 || y > 255) return;
        ChunkSection section = sections[y >> 4];
        if (!ChunkSection.isEmpty(section)) {
            if (value != ((ChunkSectionOxygenAccessor) section).isBreathable(x & 15, y & 15, z & 15)) {
                if (!this.world.isClient) {
                    setShouldSave(true);
                    update = true;
                    updatable[y >> 4] = true;
                }
                ((ChunkSectionOxygenAccessor) section).setBreathable(x & 15, y & 15, z & 15, value);
            }
        }
    }

    @Override
    public List<CustomPayloadS2CPacket> syncToClient() {
        if (update && !world.isClient) {
            update = false;
            List<CustomPayloadS2CPacket> list = new LinkedList<>();
            for (int i = 0; i < 16; i++) {
                if (updatable[i]) {
                    updatable[i] = false;
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer(1 + ((16 * 16 * 16) / 8) + (4 * 2), 50 + 1 + ((16 * 16 * 16) / 8) + (4 * 2)).writeByte(i).writeInt(this.pos.x).writeInt(this.pos.z));
                    ((ChunkSectionOxygenAccessor) sections[i]).writeOxygen(buf);
                    list.add(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "oxygen_update"), buf));
                }
            }
            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public void readOxygenUpdate(byte b, PacketByteBuf packetByteBuf) {
        ((ChunkSectionOxygenAccessor) sections[b]).readOxygen(packetByteBuf);
    }
}
