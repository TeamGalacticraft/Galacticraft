package dev.galacticraft.mod.network.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public interface S2CPayload extends CustomPacketPayload {
    void handle(@NotNull ClientPlayNetworking.Context context);
}
