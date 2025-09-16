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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.misc.cape.CapeMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record CapeAssignmentsPayload(List<Entry> entries) implements CustomPacketPayload {
    public static final ResourceLocation ID = Constant.id("cape_assignments");
    public static final CustomPacketPayload.Type<CapeAssignmentsPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final class Entry {
        public final String uuid;
        public final CapeMode mode;
        public final String gcCapeId;

        public Entry(String uuid, CapeMode mode, String gcCapeId) {
            this.uuid = uuid;
            this.mode = mode;
            this.gcCapeId = gcCapeId;
        }
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, CapeAssignmentsPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> {
                        buf.writeVarInt(msg.entries.size());
                        for (Entry e : msg.entries) {
                            buf.writeUtf(e.uuid);
                            buf.writeEnum(e.mode);
                            buf.writeBoolean(e.gcCapeId != null);
                            if (e.gcCapeId != null) buf.writeUtf(e.gcCapeId);
                        }
                    },
                    buf -> {
                        int n = buf.readVarInt();
                        List<Entry> list = new ArrayList<>(n);
                        for (int i = 0; i < n; i++) {
                            String uuid = buf.readUtf();
                            CapeMode mode = buf.readEnum(CapeMode.class);
                            String id = buf.readBoolean() ? buf.readUtf() : null;
                            list.add(new Entry(uuid, mode, id));
                        }
                        return new CapeAssignmentsPayload(list);
                    }
            );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}