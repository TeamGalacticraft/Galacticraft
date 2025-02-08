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

package dev.galacticraft.mod.network.s2c;

import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.impl.network.s2c.S2CPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.util.StreamCodecs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record RocketSpawnPacket(EntityType<?> eType, int id, UUID uuid, double x, double y, double z, float xRot, float yRot, RocketData data) implements S2CPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, RocketSpawnPacket> STREAM_CODEC = StreamCodecs.composite(
            ByteBufCodecs.registry(Registries.ENTITY_TYPE),
            RocketSpawnPacket::eType,
            ByteBufCodecs.VAR_INT,
            RocketSpawnPacket::id,
            UUIDUtil.STREAM_CODEC,
            RocketSpawnPacket::uuid,
            ByteBufCodecs.DOUBLE,
            RocketSpawnPacket::x,
            ByteBufCodecs.DOUBLE,
            RocketSpawnPacket::y,
            ByteBufCodecs.DOUBLE,
            RocketSpawnPacket::z,
            ByteBufCodecs.FLOAT,
            RocketSpawnPacket::xRot,
            ByteBufCodecs.FLOAT,
            RocketSpawnPacket::yRot,
            RocketData.STREAM_CODEC,
            RocketSpawnPacket::data,
            RocketSpawnPacket::new
    );

    public static final ResourceLocation ID = Constant.id("spawn_rocket");
    public static final CustomPacketPayload.Type<RocketSpawnPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return new Runnable() {
            @Override
            public void run() {
                ClientLevel level = context.client().level;
                assert level != null;

                RocketEntity entity = (RocketEntity) RocketSpawnPacket.this.eType.create(level);
                assert entity != null;
                entity.syncPacketPositionCodec(x, y, z);
                entity.setPos(x, y, z);
                entity.setXRot(xRot);
                entity.setYRot(yRot);
                entity.setId(id);
                entity.setUUID(uuid);

                entity.setData(data);

                level.addEntity(entity);
            }
        };
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
