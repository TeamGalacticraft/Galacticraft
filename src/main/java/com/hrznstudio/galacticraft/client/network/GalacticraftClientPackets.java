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
import com.hrznstudio.galacticraft.block.entity.BubbleDistributorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class GalacticraftClientPackets {
    public static void register() {
        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "entity_spawn"), (packetContext, packetByteBuf) -> {
            PacketByteBuf buf = new PacketByteBuf(packetByteBuf.copy());
            packetContext.getTaskQueue().execute(() -> {
                int id = buf.readVarInt();
                UUID uuid = buf.readUuid();
                Entity entity = Registry.ENTITY_TYPE.get(buf.readVarInt()).create(MinecraftClient.getInstance().world);
                entity.setEntityId(id);
                entity.setUuid(uuid);
                entity.setPos(buf.readDouble(), buf.readDouble(), buf.readDouble());
                entity.yaw = (float) (buf.readByte() * 360) / 256.0F;
                entity.pitch = (float) (buf.readByte() * 360) / 256.0F;
                entity.setVelocity(buf.readShort(), buf.readShort(), buf.readShort());
                MinecraftClient.getInstance().world.addEntity(id, entity);
            });
        });


        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "bubble_size"), (packetContext, packetByteBuf) -> {
            PacketByteBuf buf = new PacketByteBuf(packetByteBuf.copy());
            packetContext.getTaskQueue().execute(() -> {
                BlockPos pos = buf.readBlockPos();
                if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof BubbleDistributorBlockEntity) {
                        ((BubbleDistributorBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos)).setSize(buf.readDouble());
                    }
                }
            });
        });
    }
}
