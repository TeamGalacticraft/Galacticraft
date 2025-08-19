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

package dev.galacticraft.mod.network.s2c;

import dev.galacticraft.impl.network.s2c.S2CPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.OxygenSealerBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public record OxygenSealerUpdatePayload(BlockPos pos, BlockPos[] positions, BitSet set) implements S2CPayload {
    public static final ResourceLocation ID = Constant.id("sealer_update");
    public static final Type<OxygenSealerUpdatePayload> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, OxygenSealerUpdatePayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, OxygenSealerUpdatePayload object2) {
            buf.writeBlockPos(object2.pos);
            buf.writeVarInt(object2.positions.length);
            for (BlockPos pos : object2.positions) {
                buf.writeLong(pos.asLong());
            }
            buf.writeLongArray(object2.set.toLongArray());
        }

        @Override
        public OxygenSealerUpdatePayload decode(FriendlyByteBuf buf) {
            BlockPos pos1 = buf.readBlockPos();
            int len = buf.readVarInt();
            BlockPos[] positions = new BlockPos[len];
            for (int i = 0; i < len; i++) {
                positions[i] = BlockPos.of(buf.readLong());
            }
            long[] longs = buf.readLongArray();
            BitSet set = BitSet.valueOf(longs);
            return new OxygenSealerUpdatePayload(pos1, positions, set);
        }
    };

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return () -> {
            if (context.player().level().getBlockEntity(this.pos) instanceof OxygenSealerBlockEntity machine) {
                machine.handleUpdate(this);
            }
        };
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
