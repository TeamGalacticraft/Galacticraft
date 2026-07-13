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
import dev.galacticraft.mod.misc.footprint.Footprint;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import dev.galacticraft.mod.util.StreamCodecs;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record FootprintExactRemovedPacket(long chunk, Footprint footprint) implements S2CPayload {
    public static final StreamCodec<ByteBuf, FootprintExactRemovedPacket> STREAM_CODEC = StreamCodec.composite(
            StreamCodecs.LONG,
            p -> p.chunk,
            Footprint.STREAM_CODEC,
            p -> p.footprint,
            FootprintExactRemovedPacket::new
    );
    public static final ResourceLocation ID = Constant.id("footprint_exact_removed");
    public static final Type<FootprintExactRemovedPacket> TYPE = new Type<>(ID);

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return () -> {
            FootprintManager manager = context.player().level().galacticraft$getFootprintManager();
            List<Footprint> footprintList = manager.getFootprints().get(this.chunk);
            if (footprintList == null) {
                return;
            }

            Footprint removed = this.footprint;
            footprintList.removeIf(existing -> {
                if (!existing.owner.equals(removed.owner) || existing.type != removed.type || !existing.dimension.equals(removed.dimension)) {
                    return false;
                }

                double dx = existing.position.x - removed.position.x;
                double dy = existing.position.y - removed.position.y;
                double dz = existing.position.z - removed.position.z;
                return dx * dx + dy * dy + dz * dz < 1.0E-6D;
            });

            if (footprintList.isEmpty()) {
                manager.getFootprints().remove(this.chunk);
            } else {
                manager.getFootprints().put(this.chunk, footprintList);
            }
        };
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
