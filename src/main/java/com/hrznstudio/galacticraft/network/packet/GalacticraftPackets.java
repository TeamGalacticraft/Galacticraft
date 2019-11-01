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

package com.hrznstudio.galacticraft.network.packet;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.api.rocket.LaunchStage;
import com.hrznstudio.galacticraft.api.rocket.PartType;
import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.api.space.Rocket;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftPackets {
    public static void register() {
        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "redstone_update"), ((context, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getServer().execute(() -> {
                    BlockEntity blockEntity = context.getPlayer().world.getBlockEntity(buf.readBlockPos());
                    if (blockEntity instanceof ConfigurableElectricMachineBlockEntity) {
                        ((ConfigurableElectricMachineBlockEntity) blockEntity).redstoneOption = buf.readString();
                    }
                });
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "security_update"), ((context, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getServer().execute(() -> {
                    BlockEntity blockEntity = ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getBlockEntity(buf.readBlockPos());
                    boolean isParty = false;
                    boolean isPublic = false;
                    String owner = buf.readString();
                    if (owner.contains("_Public")) {
                        owner = owner.replace("_Public", "");
                        isPublic = true;
                    } else if (owner.contains("_Party")) {
                        owner = owner.replace("_Party", "");
                        isParty = true;
                    }
                    String username = buf.readString();
                    if (blockEntity instanceof ConfigurableElectricMachineBlockEntity) {
                        ((ConfigurableElectricMachineBlockEntity) blockEntity).owner = owner;
                        ((ConfigurableElectricMachineBlockEntity) blockEntity).username = username;
                        ((ConfigurableElectricMachineBlockEntity) blockEntity).isPublic = isPublic;
                        ((ConfigurableElectricMachineBlockEntity) blockEntity).isParty = isParty;
                    }

                });
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "side_config_update"), ((context, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getServer().execute(() -> {
                    BlockPos pos = buf.readBlockPos();
                    BlockEntity blockEntity = ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getBlockEntity(pos);
                    if (blockEntity != null) {
                        if (blockEntity instanceof ConfigurableElectricMachineBlockEntity) {
                            String data = buf.readString();
                            context.getPlayer().world.setBlockState(pos, context.getPlayer().world.getBlockState(pos)
                                    .with(EnumProperty.of(data.split(",")[0], SideOption.class, SideOption.getApplicableValuesForMachine(context.getPlayer().world.getBlockState(pos).getBlock())),
                                            SideOption.valueOf(data.split(",")[1])));
                        }
                    }
                });
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_jump"), ((context, buf) -> {
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getServer().execute(() -> {
                    if (context.getPlayer().hasVehicle()) {
                        if (context.getPlayer().getVehicle() instanceof RocketEntity) {
                            ((RocketEntity) context.getPlayer().getVehicle()).jump();
                        }
                    }
                });
            }
        }));

        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_spawn"), ((context, buf) -> {
            EntityType<RocketEntity> type = (EntityType<RocketEntity>) Registry.ENTITY_TYPE.get(buf.readVarInt());
            int entityID = buf.readVarInt();
            UUID entityUUID = buf.readUuid();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            float pitch = (buf.readByte() * 360) / 256.0F;
            float yaw = (buf.readByte() * 360) / 256.0F;
            Map<PartType, RocketPart> parts = new HashMap<>();
            for (PartType t : PartType.values()) {
                parts.put(t, Galacticraft.ROCKET_PARTS.get(buf.readInt()));
            }
            Float[] color = {buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()};
            Runnable spawn = () -> {
                RocketEntity entity = new RocketEntity(type, MinecraftClient.getInstance().world);
                entity.updateTrackedPosition(x, y, z);
                entity.x = x;
                entity.y = y;
                entity.z = z;
                entity.pitch = pitch;
                entity.yaw = yaw;
                entity.setEntityId(entityID);
                entity.setUuid(entityUUID);

                entity.parts.clear();
                entity.parts.putAll(parts);
                entity.setColor(color[0], color[1], color[2], color[3]);

                MinecraftClient.getInstance().world.addEntity(entityID, entity);
            };
            context.getTaskQueue().execute(spawn);
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_yaw_update"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).server.execute(() -> {
                    if (packetContext.getPlayer().getVehicle() instanceof RocketEntity) {
                        packetContext.getPlayer().getVehicle().yaw = (buf.readByte() * 360) / 256.0F;
                    }
                });
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_pitch_update"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).server.execute(() -> {
                    if (packetContext.getPlayer().getVehicle() instanceof RocketEntity) {
                        packetContext.getPlayer().getVehicle().pitch = (buf.readByte() * 360) / 256.0F;
                    }
                });
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_velocity_update"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).server.execute(() -> {
                    if (packetContext.getPlayer().getVehicle() instanceof RocketEntity) {
                        packetContext.getPlayer().getVehicle().setVelocity(buf.readDouble(), buf.readDouble(), buf.readDouble());
                    }
                });
            }
        }));
    }
}
