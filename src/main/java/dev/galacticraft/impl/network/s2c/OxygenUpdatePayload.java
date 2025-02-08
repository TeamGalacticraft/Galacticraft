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

package dev.galacticraft.impl.network.s2c;

import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.StreamCodecs;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public record OxygenUpdatePayload(long chunk, OxygenData[] data) implements S2CPayload {
    public static final ResourceLocation ID = Constant.id("oxygen_update");
    public static final Type<OxygenUpdatePayload> TYPE = new Type<>(ID);
    public static final StreamCodec<ByteBuf, OxygenUpdatePayload> CODEC = StreamCodec.composite(
            StreamCodecs.LONG,
            d -> d.chunk,
            StreamCodecs.array(OxygenData.CODEC, OxygenData[]::new),
            d -> d.data,
            OxygenUpdatePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return () -> {
            LevelChunk chunk = context.client().level.getChunk(ChunkPos.getX(this.chunk), ChunkPos.getZ(this.chunk));
            for (OxygenData datum : this.data) {
                ChunkSectionOxygenAccessor accessor = (ChunkSectionOxygenAccessor) chunk.getSection(datum.section);
                accessor.galacticraft$setBits(datum.data);
            }
        };
    }

    public record OxygenData(byte section, @NotNull BitSet data) {
        private static final StreamCodec<ByteBuf, BitSet> BIT_SET_CODEC = ByteBufCodecs.BYTE_ARRAY.map(BitSet::valueOf, BitSet::toByteArray);
        public static final StreamCodec<ByteBuf, OxygenData> CODEC = StreamCodec.composite(
                ByteBufCodecs.BYTE,
                d -> d.section,
                BIT_SET_CODEC,
                d -> d.data,
                OxygenData::new
        );
    }
}
