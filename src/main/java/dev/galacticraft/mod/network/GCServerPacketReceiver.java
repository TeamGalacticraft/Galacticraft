/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import dev.galacticraft.api.entity.Rocket;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.screen.BubbleDistributorMenu;
import dev.galacticraft.mod.screen.GCPlayerInventoryMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;

/**
 * Handles server-bound (C2S) packets.
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GCServerPacketReceiver {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(Constant.Packet.OPEN_GC_INVENTORY, (server, player, handler, buf, responseSender) -> server.execute(() -> player.openMenu(new SimpleMenuProvider(GCPlayerInventoryMenu::new, Component.empty()) {
            @Override
            public boolean shouldCloseCurrentScreen() {
                return false;
            }
        })));

        ServerPlayNetworking.registerGlobalReceiver(Constant.Packet.BUBBLE_MAX, (server, player, handler, buf, responseSender) -> {
            byte max = buf.readByte();
            server.execute(() -> {
                if (player.containerMenu instanceof BubbleDistributorMenu sHandler) {
                    OxygenBubbleDistributorBlockEntity machine = sHandler.machine;
                    if (machine.getSecurity().hasAccess(player)) {
                        if (max > 0) {
                            machine.setTargetSize(max);
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(Constant.Packet.BUBBLE_VISIBLE, (server, player, handler, buf, responseSender) -> {
            boolean visible = buf.readBoolean();
            server.execute(() -> {
                if (player.containerMenu instanceof BubbleDistributorMenu sHandler) {
                    OxygenBubbleDistributorBlockEntity machine = sHandler.machine;
                    if (machine.getSecurity().hasAccess(player)) {
                        machine.bubbleVisible = visible;
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constant.MOD_ID, "rocket_jump"), ((server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player.isPassenger()) {
                    if (player.getVehicle() instanceof Rocket && ((Rocket) player.getVehicle()).getStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                        ((Rocket) player.getVehicle()).onJump();
                    }
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constant.MOD_ID, "planet_tp"), ((server, player, handler, buf, responseSender) -> {
            FriendlyByteBuf buffer = new FriendlyByteBuf(buf.copy());
            if (player.getCelestialScreenState() != null) {
                server.execute(() -> {
                    ResourceLocation id = buffer.readResourceLocation();
                    CelestialBody<?, ?> body = ((SatelliteAccessor) server).getSatellites().get(id);
                    if (body == null) body = server.registryAccess().registryOrThrow(AddonRegistry.CELESTIAL_BODY_KEY).get(id);
                    if (body.type() instanceof Landable landable && (player.getCelestialScreenState().canTravelTo(server.registryAccess(), body) || player.getCelestialScreenState() == RocketData.empty())) {
                        player.setCelestialScreenState(null);
                        player.teleportTo(server.getLevel(landable.world(body.config())), player.getX(), 500, player.getZ(), player.getYRot(), player.getXRot());
                    } else {
                        player.connection.disconnect(Component.literal("Invalid planet teleport packet received."));
                    }
                });
            } else {
                player.connection.disconnect(Component.literal("Invalid planet teleport packet received."));
            }
        }));
    }
}
