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

package dev.galacticraft.mod.network.packets;

import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.mod.Constant.Packet;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record RocketSpawnPacket(EntityType<?> type, int id, UUID uuid, double x, double y, double z, float xRot, float yRot, RocketData data) implements GCPacket {
    public static final PacketType<RocketSpawnPacket> TYPE = PacketType.create(Packet.ROCKET_SPAWN, RocketSpawnPacket::new);
    public RocketSpawnPacket(FriendlyByteBuf buf) {
        this(BuiltInRegistries.ENTITY_TYPE.byId(buf.readVarInt()), buf.readVarInt(), buf.readUUID(), buf.readDouble(), buf.readDouble(), buf.readDouble(), (buf.readByte() * 360) / 256.0F, (buf.readByte() * 360) / 256.0F, RocketData.fromNetwork(buf));
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        RocketEntity entity = (RocketEntity) type.create(player.level());
        assert entity != null;
        entity.syncPacketPositionCodec(x, y, z);
        entity.setPos(x, y, z);
        entity.setXRot(xRot);
        entity.setYRot(yRot);
        entity.setId(id);
        entity.setUUID(uuid);

        entity.setData(data);

        Minecraft.getInstance().level.addEntity(entity);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(BuiltInRegistries.ENTITY_TYPE.getId(type));
        buf.writeVarInt(id);
        buf.writeUUID(uuid);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeByte((int) (xRot / 360F * 256F));
        buf.writeByte((int) (yRot / 360F * 256F));
        data.toNetwork(buf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
