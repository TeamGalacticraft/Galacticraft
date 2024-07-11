package dev.galacticraft.mod.network.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public interface C2SPayload extends CustomPacketPayload {
    void handle(@NotNull ServerPlayNetworking.Context context);
}
