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

package dev.galacticraft.impl.network;

import dev.galacticraft.impl.network.c2s.FlagDataPayload;
import dev.galacticraft.impl.network.c2s.TeamNamePayload;
import dev.galacticraft.impl.network.s2c.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class GCApiPackets {
    public static void register() {
        PayloadTypeRegistry.playS2C().register(AddSatellitePayload.TYPE, AddSatellitePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GearInvPayload.TYPE, GearInvPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OxygenUpdatePayload.TYPE, OxygenUpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RemoveSatellitePayload.TYPE, RemoveSatellitePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ResearchUpdatePayload.TYPE, ResearchUpdatePayload.CODEC);

        PayloadTypeRegistry.playC2S().register(FlagDataPayload.TYPE, FlagDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(TeamNamePayload.TYPE, TeamNamePayload.CODEC);
    }
}
