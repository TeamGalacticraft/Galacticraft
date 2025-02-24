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

import dev.galacticraft.impl.network.c2s.C2SPayload;
import dev.galacticraft.mod.network.c2s.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Handles server-bound (C2S) packets.
 */
public class GCServerPacketReceivers {
    public static void register() {
        registerPacket(ControlEntityPayload.TYPE);
        registerPacket(OpenRocketPayload.TYPE);
        registerPacket(SatelliteCreationPayload.TYPE);
        registerPacket(BubbleVisibilityPayload.TYPE);
        registerPacket(OpenGcInventoryPayload.TYPE);
        registerPacket(PlanetTeleportPayload.TYPE);
        registerPacket(BubbleMaxPayload.TYPE);
    }

    public static <P extends C2SPayload> void registerPacket(CustomPacketPayload.Type<P> type) {
        ServerPlayNetworking.registerGlobalReceiver(type, C2SPayload::handle);
    }
}
