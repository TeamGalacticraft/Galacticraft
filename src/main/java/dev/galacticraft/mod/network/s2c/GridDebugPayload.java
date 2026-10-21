/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import dev.galacticraft.mod.client.render.GridDebugRenderer;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * The nodes and edges of the loaded grids of a level, sent to players while grid debugging is on (JVM flag {@code -ea}).
 */
public record GridDebugPayload(List<GridShape> grids) implements S2CPayload {
    public static final StreamCodec<ByteBuf, GridDebugPayload> STREAM_CODEC = GridShape.STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(GridDebugPayload::new, GridDebugPayload::grids);
    public static final CustomPacketPayload.Type<GridDebugPayload> TYPE = new CustomPacketPayload.Type<>(Constant.id("grid_debug"));

    @Override
    public Runnable handle(@NotNull ClientPlayNetworking.Context context) {
        return () -> GridDebugRenderer.update(this.grids);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * @param id    the grid id
     * @param nodes the node positions, as {@link net.minecraft.core.BlockPos#asLong()}
     * @param edges the edges, as pairs of node positions
     */
    public record GridShape(UUID id, List<Long> nodes, List<Long> edges) {
        public static final StreamCodec<ByteBuf, GridShape> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, GridShape::id,
                ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list()), GridShape::nodes,
                ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list()), GridShape::edges,
                GridShape::new
        );
    }
}
