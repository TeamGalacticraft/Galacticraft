package dev.galacticraft.mod.network.c2s;

import dev.galacticraft.impl.network.c2s.C2SPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.misc.cape.CapeMode;
import dev.galacticraft.mod.misc.cape.ServerCapeManager;
import dev.galacticraft.mod.network.s2c.CapeAssignmentsPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public record CapeSelectionPayload(CapeMode mode, String gcCapeId) implements C2SPayload {
    public static final ResourceLocation ID = Constant.id("cape_select");
    public static final Type<CapeSelectionPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, CapeSelectionPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> {
                        buf.writeEnum(msg.mode);
                        buf.writeBoolean(msg.gcCapeId != null);
                        if (msg.gcCapeId != null) buf.writeUtf(msg.gcCapeId);
                    },
                    buf -> {
                        CapeMode mode = buf.readEnum(CapeMode.class);
                        String id = buf.readBoolean() ? buf.readUtf() : null;
                        return new CapeSelectionPayload(mode, id);
                    }
            );

    @Override
    public void handle(ServerPlayNetworking.@NotNull Context context) {
        var player = context.player();
        CapeMode m = this.mode;
        String id = this.gcCapeId;

        if (!ServerCapeManager.validateSelection(player, m, id)) {
            m = CapeMode.VANILLA;
            id = null;
        }
        ServerCapeManager.set(player, m, id);

        var snap = ServerCapeManager.snapshot();
        var list = new ArrayList<CapeAssignmentsPacket.Entry>(snap.size());
        for (var e : snap.entrySet()) {
            var a = e.getValue();
            list.add(new CapeAssignmentsPacket.Entry(
                    e.getKey(),
                    a.mode,
                    a.mode == CapeMode.GC ? a.gcCapeId : null
            ));
        }
        var payload = new CapeAssignmentsPacket(list);

        for (var p : context.server().getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(p, payload);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}