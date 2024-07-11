package dev.galacticraft.mod.network.s2c;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.attachments.GCClientPlayer;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ResetPerspectivePacket() implements S2CPayload {
    public static final ResetPerspectivePacket INSTANCE = new ResetPerspectivePacket();
    public static final StreamCodec<ByteBuf, ResetPerspectivePacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static final ResourceLocation ID = Constant.id("reset_perspective");
    public static final Type<ResetPerspectivePacket> TYPE = new Type<>(ID);

    @Override
    public void handle(@NotNull ClientPlayNetworking.Context context) {
        Minecraft.getInstance().options.setCameraType(GCClientPlayer.get(context.player()).getCameraType());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
