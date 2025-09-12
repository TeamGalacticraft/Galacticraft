package dev.galacticraft.mod.network.s2c;

import dev.galacticraft.impl.network.s2c.S2CPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.trance.ClientTranceState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record HallucinationStatePacket(boolean active) implements S2CPayload {
    public static final ResourceLocation ID = Constant.id("hallucination_state");
    public static final Type<HallucinationStatePacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, HallucinationStatePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> buf.writeBoolean(msg.active),
                    buf -> new HallucinationStatePacket(buf.readBoolean())
            );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    /** Client handler registration */
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (packet, ctx) -> {

        });
    }

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return () -> {
            ClientTranceState.setHallucinating(this.active());
        };
    }
}