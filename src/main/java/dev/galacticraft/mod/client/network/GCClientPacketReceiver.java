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

package dev.galacticraft.mod.client.network;

import dev.galacticraft.impl.network.s2c.S2CPayload;
import dev.galacticraft.mod.network.s2c.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Handles client-bound (S2C) packets
 */
public class GCClientPacketReceiver {
    public static void register() {
        register(BubbleSizePayload.TYPE);
        register(BubbleUpdatePayload.TYPE);
        register(OpenCelestialScreenPayload.TYPE);
        register(RocketSpawnPacket.TYPE);
        register(FootprintPacket.TYPE);
        register(FootprintRemovedPacket.TYPE);
        register(ResetPerspectivePacket.TYPE);
    }

    public static <P extends S2CPayload> void register(CustomPacketPayload.Type<P> type) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> context.client().execute(payload.handle(context)));
    }
}
