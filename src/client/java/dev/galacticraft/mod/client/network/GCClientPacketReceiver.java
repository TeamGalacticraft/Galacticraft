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

import dev.galacticraft.mod.attachments.GCAttachments;
import dev.galacticraft.mod.client.attachments.GCClientPlayer;
import dev.galacticraft.mod.client.gui.screen.ingame.CelestialSelectionScreen;
import dev.galacticraft.mod.client.render.FootprintRenderer;
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.misc.cape.CapesClientState;
import dev.galacticraft.mod.misc.footprint.Footprint;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import dev.galacticraft.mod.network.s2c.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GCClientPacketReceiver {
    public static void register() {
        register(BubbleSizePayload.TYPE, GCClientPacketReceiver::handleBubbleSize);
        register(BubbleUpdatePayload.TYPE, GCClientPacketReceiver::handleBubbleUpdatePayload);
        register(OpenCelestialScreenPayload.TYPE, GCClientPacketReceiver::handleOpenCelestialScreenPayload);
        register(FootprintPayload.TYPE, GCClientPacketReceiver::handleFootprintPayload);
        register(FootprintRemovedPayload.TYPE, GCClientPacketReceiver::handleFootprintRemovedPayload);
        register(ResetPerspectivePayload.TYPE, GCClientPacketReceiver::handleResetPerspectivePayload);
        register(CapeAssignmentsPayload.TYPE, GCClientPacketReceiver::handleCapeAssignmentsPayload);
    }

    public static void handleBubbleSize(BubbleSizePayload payload, ClientPlayNetworking.Context context) {
        ClientLevel level = context.client().level;
        if (level != null && level.hasChunk(SectionPos.blockToSectionCoord(payload.pos().getX()), SectionPos.blockToSectionCoord(payload.pos().getZ()))) {
            BlockEntity entity = level.getBlockEntity(payload.pos());
            if (entity instanceof OxygenBubbleDistributorBlockEntity machine) {
                machine.setSize(payload.size());
            }
        }
    }

    public static void handleBubbleUpdatePayload(BubbleUpdatePayload payload, ClientPlayNetworking.Context context) {
        if (context.player().level().getBlockEntity(payload.pos()) instanceof OxygenBubbleDistributorBlockEntity machine) {
            machine.setTargetSize(payload.maxSize());
            machine.setSize(payload.size());
            machine.setBubbleVisible(payload.visible());
        }
    }

    public static void handleOpenCelestialScreenPayload(OpenCelestialScreenPayload payload, ClientPlayNetworking.Context context) {
        context.client().setScreen(new CelestialSelectionScreen(false, payload.data(), true, payload.celestialBody().value()));
    }

    public static void handleFootprintPayload(FootprintPayload payload, ClientPlayNetworking.Context context) {
        FootprintRenderer.setFootprints(payload.chunk(), payload.footprints());
    }

    public static void handleFootprintRemovedPayload(FootprintRemovedPayload payload, ClientPlayNetworking.Context context) {
        BlockPos pos = payload.pos();
        FootprintManager manager = context.player().level().getAttachedOrThrow(GCAttachments.FOOTPRINT_MANAGER);
        List<Footprint> footprintList = manager.getFootprints().get(payload.chunk());
        List<Footprint> toRemove = new ArrayList<>();

        if (footprintList != null) {
            for (Footprint footprint : footprintList) {
                if (footprint.position.x > pos.getX() && footprint.position.x < pos.getX() + 1 && footprint.position.z > pos.getZ() && footprint.position.z < pos.getZ() + 1) {
                    toRemove.add(footprint);
                }
            }
        }

        if (!toRemove.isEmpty()) {
            footprintList.removeAll(toRemove);
            manager.getFootprints().put(payload.chunk(), footprintList);
        }
    }

    public static void handleResetPerspectivePayload(ResetPerspectivePayload payload, ClientPlayNetworking.Context context) {
        Minecraft.getInstance().options.setCameraType(GCClientPlayer.get(context.player()).getCameraType());
    }

    public static void handleCapeAssignmentsPayload(CapeAssignmentsPayload payload, ClientPlayNetworking.Context context) {
        var map = new HashMap<String, CapesClientState.Entry>(payload.entries().size());
        for (var e : payload.entries()) {
            map.put(e.uuid.toLowerCase(), new CapesClientState.Entry(e.mode, e.gcCapeId));
        }
        CapesClientState.apply(map);
    }

    public static <P extends CustomPacketPayload> void register(CustomPacketPayload.Type<P> type, ClientPlayNetworking.PlayPayloadHandler<P> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> context.client().execute(() -> handler.receive(payload, context)));
    }
}
