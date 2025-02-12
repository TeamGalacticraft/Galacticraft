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

package dev.galacticraft.mod.network;

import dev.galacticraft.mod.network.c2s.*;
import dev.galacticraft.mod.network.s2c.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class GCPackets {
    public static void register() {
        PayloadTypeRegistry.playS2C().register(BubbleSizePayload.TYPE, BubbleSizePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(BubbleUpdatePayload.TYPE, BubbleUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(FootprintPacket.TYPE, FootprintPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(FootprintRemovedPacket.TYPE, FootprintRemovedPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OpenCelestialScreenPayload.TYPE, OpenCelestialScreenPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ResetPerspectivePacket.TYPE, ResetPerspectivePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(RocketSpawnPacket.TYPE, RocketSpawnPacket.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(ControlEntityPayload.TYPE, ControlEntityPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(OpenRocketPayload.TYPE, OpenRocketPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SatelliteCreationPayload.TYPE, SatelliteCreationPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(BubbleMaxPayload.TYPE, BubbleMaxPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(BubbleVisibilityPayload.TYPE, BubbleVisibilityPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PlanetTeleportPayload.TYPE, PlanetTeleportPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(OpenGcInventoryPayload.TYPE, OpenGcInventoryPayload.STREAM_CODEC);
    }
}
