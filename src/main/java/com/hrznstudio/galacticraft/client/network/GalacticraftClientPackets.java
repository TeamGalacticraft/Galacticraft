/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.client.network;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.ClientPlayNetworkHandlerAccessor;
import com.hrznstudio.galacticraft.api.rocket.RocketData;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class GalacticraftClientPackets {
    public static void register() {
        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_spawn"), ((context, buf) -> {
            EntityType<? extends RocketEntity> type = (EntityType<? extends RocketEntity>) Registry.ENTITY_TYPE.get(buf.readVarInt());

            int entityID = buf.readVarInt();
            UUID entityUUID = buf.readUuid();

            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            float pitch = (buf.readByte() * 360) / 256.0F;
            float yaw = (buf.readByte() * 360) / 256.0F;

            RocketData data = RocketData.fromTag(buf.readCompoundTag());

            Runnable spawn = () -> {
                RocketEntity entity = type.create(context.getPlayer().world);
                assert entity != null;
                entity.updateTrackedPosition(x, y, z);
                entity.setPos(x, y, z);
                entity.pitch = pitch;
                entity.yaw = yaw;
                entity.setEntityId(entityID);
                entity.setUuid(entityUUID);

                entity.setColor(data.getRed(), data.getGreen(), data.getBlue(), data.getAlpha());
                entity.setParts(data.getParts());

                MinecraftClient.getInstance().world.addEntity(entityID, entity);
            };
            context.getTaskQueue().execute(spawn);
        }));

        ClientSidePacketRegistry.INSTANCE.register(new Identifier(Constants.MOD_ID, "canister_packet_of_doom"), (packetContext, packetByteBuf) -> {
            PacketByteBuf buf = new PacketByteBuf(packetByteBuf.copy());
            packetContext.getTaskQueue().execute(() -> MinecraftClient.getInstance().player.inventory.setStack(buf.readVarInt(), buf.readItemStack()));
        });

        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "research_update"), ((context, buf) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());

            context.getTaskQueue().execute(() -> {
                ((ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler()).getClientResearchManager().onResearch(buffer);
            });
        }));

//        ClientSidePacketRegistry.INSTANCE.register(new Identifier(Constants.MOD_ID, "research_sync"), (packetContext, packetByteBuf) -> {
//            int size = packetByteBuf.readVarInt();
//            Map<Identifier, ResearchNode.Builder> map = new HashMap<>(size);
//            for (int i =0; i < size; i++) {
//                Identifier id = packetByteBuf.readIdentifier();
//                map.put(id, ResearchNode.Builder.fromPacket(packetByteBuf));
//            }
//            packetContext.getTaskQueue().execute(() -> ((ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler()).getClientResearchManager().getManager().load(map));
//        });
        ClientSidePacketRegistry.INSTANCE.register(new Identifier(Constants.MOD_ID, "research_sync"), (packetContext, packetByteBuf) -> {

        });

    }
}
