/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.accessor.ChunkOxygenAccessor;
import dev.galacticraft.mod.block.entity.BubbleDistributorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

import net.minecraft.entity.EntityType;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class GalacticraftC2SPacketReceivers {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "entity_spawn"), (client, handler, buf, responseSender) -> { //todo(marcus): 1.17?
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            client.execute(() -> {
                int id = buffer.readVarInt();
                UUID uuid = buffer.readUuid();
                Entity entity = Registry.ENTITY_TYPE.get(buffer.readVarInt()).create(MinecraftClient.getInstance().world);
                entity.setEntityId(id);
                entity.setUuid(uuid);
                entity.setPos(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
                entity.yaw = (float) (buffer.readByte() * 360) / 256.0F;
                entity.pitch = (float) (buffer.readByte() * 360) / 256.0F;
                entity.setVelocity(buffer.readShort(), buffer.readShort(), buffer.readShort());
                MinecraftClient.getInstance().world.addEntity(id, entity);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "bubble_size"), (client, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            client.execute(() -> {
                BlockPos pos = buffer.readBlockPos();
                if (client.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    BlockEntity entity = client.world.getBlockEntity(pos);
                    if (entity instanceof BubbleDistributorBlockEntity) {
                        ((BubbleDistributorBlockEntity) entity).setSize(buffer.readDouble());
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "planet_menu_open"), ((client, handler, buf, responseSender) -> {
            int tier = buf.readInt();
            client.execute(() -> {
                MinecraftClient.getInstance().openScreen(new PlanetSelectScreen(false, tier, true));
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "rocket_spawn"), ((client, handler, buf, responseSender) -> {
            EntityType<? extends RocketEntity> type = (EntityType<? extends RocketEntity>) Registry.ENTITY_TYPE.get(buf.readVarInt());

            int entityID = buf.readVarInt();
            UUID entityUUID = buf.readUuid();

            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            float pitch = (buf.readByte() * 360) / 256.0F;
            float yaw = (buf.readByte() * 360) / 256.0F;

            RocketData data = RocketData.fromTag(buf.readCompoundTag());

            client.execute(() -> {
                RocketEntity entity = type.create(client.world);
                assert entity != null;
                entity.updateTrackedPosition(x, y, z);
                entity.setPos(x, y, z);
                entity.pitch = pitch;
                entity.yaw = yaw;
                entity.setEntityId(entityID);
                entity.setUuid(entityUUID);

                entity.setColor(data.getRed(), data.getGreen(), data.getBlue(), data.getAlpha());
                entity.setParts(data.getParts().toArray(new RocketPart[0]));

                MinecraftClient.getInstance().world.addEntity(entityID, entity);
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "oxygen_update"), (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
            byte b = packetByteBuf.readByte();
            ChunkOxygenAccessor accessor = ((ChunkOxygenAccessor) clientPlayNetworkHandler.getWorld().getChunk(packetByteBuf.readInt(), packetByteBuf.readInt()));
            accessor.readOxygenUpdate(b, packetByteBuf);
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "open_screen"), (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {

        });
    }
}
