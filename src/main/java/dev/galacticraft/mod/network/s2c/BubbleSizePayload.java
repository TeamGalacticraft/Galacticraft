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
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record BubbleSizePayload(BlockPos pos, double size) implements S2CPayload {
    public static final StreamCodec<ByteBuf, BubbleSizePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            p -> p.pos,
            ByteBufCodecs.DOUBLE,
            p -> p.size,
            BubbleSizePayload::new
    );

    public static final ResourceLocation ID = Constant.id("bubble_size");
    public static final CustomPacketPayload.Type<BubbleSizePayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public BubbleSizePayload {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }
    }

    @Override
    public Runnable handle(@NotNull ClientPlayNetworking.Context context) {
        return () -> {
            ClientLevel level = context.client().level;
            if (level != null && level.hasChunk(SectionPos.blockToSectionCoord(this.pos().getX()), SectionPos.blockToSectionCoord(this.pos().getZ()))) {
                BlockEntity entity = level.getBlockEntity(this.pos());
                if (entity instanceof OxygenBubbleDistributorBlockEntity machine) {
                    machine.setSize(this.size());
                }
            }
        };
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
