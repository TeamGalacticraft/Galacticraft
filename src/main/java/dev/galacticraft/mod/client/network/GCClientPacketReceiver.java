/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Constant.Packet;
import dev.galacticraft.mod.client.gui.screen.ingame.CelestialSelectionScreen;
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.network.GCScreenType;
import dev.galacticraft.mod.network.packets.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

/**
 * Handles client-bound (S2C) packets
= */
@Environment(EnvType.CLIENT)
public class GCClientPacketReceiver {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(Packet.BUBBLE_SIZE, (client, handler, buf, responseSender) -> {
            FriendlyByteBuf buffer = new FriendlyByteBuf(buf.copy());
            client.execute(() -> {
                BlockPos pos = buffer.readBlockPos();
                if (client.level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                    BlockEntity entity = client.level.getBlockEntity(pos);
                    if (entity instanceof OxygenBubbleDistributorBlockEntity machine) {
                        machine.setSize(buffer.readDouble());
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(Packet.OPEN_SCREEN, (client, handler, buf, responseSender) -> {
            var screen = buf.readEnum(GCScreenType.class);
            switch (screen) {
                case CELESTIAL -> {
                    boolean mapMode = buf.readBoolean();
                    client.execute(() -> client.setScreen(new CelestialSelectionScreen(mapMode, RocketData.fromNbt(GCItems.ROCKET.getDefaultInstance().getTag()), true, null)));
                }
                default -> Constant.LOGGER.error("No screen found with id '{}'!", screen);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(Packet.PLANET_MENU_PACKET, (minecraftClient, clientPlayNetworkHandler, buf, packetSender) -> {
            RocketData rocketData = RocketData.fromNbt(Objects.requireNonNull(buf.readNbt()));
            int cBody = buf.readInt();
            minecraftClient.execute(() -> minecraftClient.setScreen(new CelestialSelectionScreen(false, rocketData, true, cBody == -1 ? null : clientPlayNetworkHandler.registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY).getHolder(cBody).orElseThrow().value())));
        });

        registerPacket(RocketSpawnPacket.TYPE);
        registerPacket(FootprintPacket.TYPE);
        registerPacket(FootprintRemovedPacket.TYPE);
        registerPacket(ResetThirdPersonPacket.TYPE);
    }

    public static <Packet extends GCPacket> void registerPacket(PacketType<Packet> type) {
        ClientPlayNetworking.registerGlobalReceiver(type, GCPacket::handle);
    }
}
