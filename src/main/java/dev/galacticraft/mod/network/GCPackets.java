package dev.galacticraft.mod.network;

import dev.galacticraft.mod.network.c2s.*;
import dev.galacticraft.mod.network.s2c.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class GCPackets {
    public static void register() {
        PayloadTypeRegistry.playS2C().register(BubbleSizePayload.TYPE, BubbleSizePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(FootprintPacket.TYPE, FootprintPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(FootprintRemovedPacket.TYPE, FootprintRemovedPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OpenCelestialScreenPayload.TYPE, OpenCelestialScreenPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ResetPerspectivePacket.TYPE, ResetPerspectivePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(RocketSpawnPacket.TYPE, RocketSpawnPacket.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(BubbleMaxPayload.TYPE, BubbleMaxPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(BubbleVisibilityPayload.TYPE, BubbleVisibilityPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ControlEntityPayload.TYPE, ControlEntityPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PlanetTeleportPayload.TYPE, PlanetTeleportPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SelectPartPayload.TYPE, SelectPartPayload.STREAM_CODEC);
    }
}
