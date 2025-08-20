package dev.galacticraft.mod.network.s2c;

import dev.galacticraft.impl.network.s2c.S2CPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.misc.cape.CapeMode;
import dev.galacticraft.mod.misc.cape.CapesClientState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record CapeAssignmentsPacket(List<Entry> entries) implements S2CPayload {
    public static final ResourceLocation ID = Constant.id("cape_assignments");
    public static final Type<CapeAssignmentsPacket> TYPE = new Type<>(ID);

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

    public static final StreamCodec<RegistryFriendlyByteBuf, CapeAssignmentsPacket> STREAM_CODEC =
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
                        return new CapeAssignmentsPacket(list);
                    }
            );

    @Override
    public Runnable handle(@NotNull ClientPlayNetworking.Context context) {
        return () -> {
            var map = new HashMap<String, CapesClientState.Entry>(entries.size());
            for (var e : entries) {
                map.put(e.uuid.toLowerCase(), new CapesClientState.Entry(e.mode, e.gcCapeId));
            }
            CapesClientState.apply(map);
        };
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}