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
import dev.galacticraft.impl.internal.oxygen.TrackingBitSet;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.StreamCodecs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.NotNull;


public record OxygenUpdatePayload(long chunk, OxygenData[] data) implements S2CPayload {
    public static final ResourceLocation ID = Constant.id("oxygen_update");
    public static final Type<OxygenUpdatePayload> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, OxygenUpdatePayload> CODEC = StreamCodec.composite(
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
            ChunkAccess chunk = context.client().level.getChunk(ChunkPos.getX(this.chunk), ChunkPos.getZ(this.chunk), ChunkStatus.FULL, false);
            if (chunk != null) {
                for (OxygenData datum : this.data) {
                    ChunkSectionOxygenAccessor accessor = (ChunkSectionOxygenAccessor) chunk.getSection(datum.section);
                    accessor.galacticraft$loadData(datum.data);
                }
            }
        };
    }

    public record OxygenData(byte section, OxygenSectionData data) {
        public static final StreamCodec<FriendlyByteBuf, OxygenData> CODEC = StreamCodec.composite(
                ByteBufCodecs.BYTE,
                d -> d.section,
                OxygenSectionData.CODEC,
                d -> d.data,
                OxygenData::new
        );
    }

    public record OxygenSectionData(BlockPos[] positions, TrackingBitSet[] data) {
        public static final StreamCodec<FriendlyByteBuf, OxygenSectionData> CODEC = new StreamCodec<>() {
            @Override
            public OxygenSectionData decode(FriendlyByteBuf buf) {
                int count = buf.readVarInt();

                BlockPos[] positions = new BlockPos[count];
                TrackingBitSet[] bits = new TrackingBitSet[count];
                for (int i = 0; i < count; i++) {
                    positions[i] = buf.readBlockPos();
                    bits[i] = TrackingBitSet.read(buf);
                }
                return new OxygenSectionData(positions, bits);
            }

            @Override
            public void encode(FriendlyByteBuf buf, OxygenSectionData section) {
                buf.writeVarInt(section.positions.length);
                for (int i = 0; i < section.positions.length; i++) {
                    buf.writeBlockPos(section.positions[i]);
                    section.data[i].write(buf);
                }
            }
        };
    }
}
