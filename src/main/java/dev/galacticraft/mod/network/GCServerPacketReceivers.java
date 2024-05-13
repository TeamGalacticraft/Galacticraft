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

package dev.galacticraft.mod.network;

import dev.galacticraft.api.accessor.SatelliteAccessor;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.events.GCEventHandlers;
import dev.galacticraft.mod.network.packets.*;
import dev.galacticraft.mod.screen.GCPlayerInventoryMenu;
import dev.galacticraft.mod.screen.RocketMenu;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Objects;

/**
 * Handles server-bound (C2S) packets.
 */
public class GCServerPacketReceivers {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(Constant.Packet.OPEN_GC_INVENTORY, (server, player, handler, buf, responseSender) -> server.execute(() -> player.openMenu(new SimpleMenuProvider(GCPlayerInventoryMenu::new, Component.empty()) {
            @Override
            public boolean shouldCloseCurrentScreen() {
                return false;
            }
        })));
        ServerPlayNetworking.registerGlobalReceiver(Constant.Packet.OPEN_GC_ROCKET, (server, player, handler, buf, responseSender) -> server.execute(() -> player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
                return new RocketMenu(syncId, inventory, player, (RocketEntity) player.getVehicle());
            }

            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                assert player.getVehicle() instanceof RocketEntity;
                RocketEntity rocket = (RocketEntity) player.getVehicle();
                buf.writeInt(rocket.getId());
            }

            @Override
            public boolean shouldCloseCurrentScreen() {
                return false;
            }
        })));

        registerPacket(BubbleMaxPacket.TYPE);
        registerPacket(SelectPartPacket.TYPE);
        registerPacket(ToggleBubbleVisibilityPacket.TYPE);
        registerPacket(ControlEntityPacket.TYPE);

        ServerPlayNetworking.registerGlobalReceiver(Constant.Packet.PLANET_TP, ((server, player, handler, buf, responseSender) -> {
            FriendlyByteBuf buffer = new FriendlyByteBuf(buf.copy());
            if (player.galacticraft$isCelestialScreenActive()) {
                server.execute(() -> {
                    ResourceLocation id = buffer.readResourceLocation();
                    CelestialBody<?, ?> body = ((SatelliteAccessor) server).galacticraft$getSatellites().get(id);
                    CelestialBody<?, ?> fromBody = CelestialBody.getByDimension(player.level()).orElseThrow();
                    if (body == null) body = server.registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY).get(id);
                    GCEventHandlers.onPlayerChangePlanets(server, player, body, fromBody);
                });
            } else {
                player.connection.disconnect(Component.literal("Invalid planet teleport packet received."));
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(Constant.Packet.CREATE_SATELLITE, (server, player, handler, buf, responseSender) -> {
            SatelliteType.registerSatellite(server, player, Objects.requireNonNull(server.registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY).get(buf.readResourceLocation())), server.getStructureManager().get(Constant.Structure.SPACE_STATION).orElseThrow());
        });
    }

    public static <Packet extends GCPacket> void registerPacket(PacketType<Packet> type) {
        ServerPlayNetworking.registerGlobalReceiver(type, GCPacket::handle);
    }
}
