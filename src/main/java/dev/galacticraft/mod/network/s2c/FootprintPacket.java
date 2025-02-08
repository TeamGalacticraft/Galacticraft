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
import dev.galacticraft.mod.client.render.FootprintRenderer;
import dev.galacticraft.mod.misc.footprint.Footprint;
import dev.galacticraft.mod.util.StreamCodecs;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record FootprintPacket(long chunk, List<Footprint> footprints) implements S2CPayload {
    public static final StreamCodec<ByteBuf, FootprintPacket> STREAM_CODEC = StreamCodec.composite(
            StreamCodecs.LONG,
            p -> p.chunk,
            ByteBufCodecs.<ByteBuf, Footprint>list().apply(Footprint.STREAM_CODEC),
            p -> p.footprints,
            FootprintPacket::new
    );
    public static final ResourceLocation ID = Constant.id("footprint");
    public static final CustomPacketPayload.Type<FootprintPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return () -> FootprintRenderer.setFootprints(this.chunk, this.footprints);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
