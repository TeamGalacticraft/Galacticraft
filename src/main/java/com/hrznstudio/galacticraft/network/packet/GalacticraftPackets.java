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
import com.hrznstudio.galacticraft.blocks.machines.rocketdesigner.RocketDesignerBlockEntity;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Objects;
import java.util.UUID;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftPackets {
    public static void register() {
        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "redstone_update"), ((context, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getServer().execute(() -> {
                if (context.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    BlockEntity blockEntity = context.getPlayer().world.getBlockEntity(pos);
                    if (blockEntity instanceof ConfigurableElectricMachineBlockEntity) {
                        ((ConfigurableElectricMachineBlockEntity) blockEntity).redstoneOption = buf.readString();
                    }
                }
            });

        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "security_update"), ((context, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getServer().execute(() -> {
                    BlockPos pos = buf.readBlockPos();
                    if (context.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        BlockEntity blockEntity = ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getBlockEntity(pos);
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
                    }
                });
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "side_config_update"), ((context, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getServer().execute(() -> {
                    BlockPos pos = buf.readBlockPos();
                    if (context.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        BlockEntity blockEntity = ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getBlockEntity(pos);
                        if (blockEntity != null) {
                            if (blockEntity instanceof ConfigurableElectricMachineBlockEntity) {
                                String data = buf.readString();
                                context.getPlayer().world.setBlockState(pos, context.getPlayer().world.getBlockState(pos)
                                        .with(EnumProperty.of(data.split(",")[0], SideOption.class, SideOption.getApplicableValuesForMachine(context.getPlayer().world.getBlockState(pos).getBlock())),
                                                SideOption.valueOf(data.split(",")[1])));
                            }
                        }
                    }
                });
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_red"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld().getServer().execute(() -> {
                    if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                            RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos);
                            assert blockEntity != null;
                            byte color = buf.readByte();
                            blockEntity.setRed(color + 128);
                            blockEntity.updateSchematic();
                        }
                    }
                });
            }
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_green"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld().getServer().execute(() -> {
                    if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                            RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos);
                            assert blockEntity != null;
                            byte color = buf.readByte();
                            blockEntity.setGreen(color + 128);
                            blockEntity.updateSchematic();
                        }
                    }
                });
            }
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_blue"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld().getServer().execute(() -> {
                    if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                            RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos);
                            assert blockEntity != null;
                            byte color = buf.readByte();
                            blockEntity.setBlue(color + 128);
                            blockEntity.updateSchematic();
                        }
                    }
                });
            }
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_alpha"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld().getServer().execute(() -> {
                    if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                            RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos);
                            assert blockEntity != null;
                            byte color = buf.readByte();
                            blockEntity.setAlpha(color + 128);
                            blockEntity.updateSchematic();
                        }
                    }
                });
            }
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_part"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld().getServer().execute(() -> {
                    if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                            RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos);
                            assert blockEntity != null;
                            blockEntity.setPart(Objects.requireNonNull(Galacticraft.ROCKET_PARTS.get(buf.readIdentifier())));
                            blockEntity.updateSchematic();
                        }
                    }
                });
            }
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_jump"), ((context, buff) -> {
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getServer().execute(() -> {
                    if (context.getPlayer().hasVehicle()) {
                        if (context.getPlayer().getVehicle() instanceof RocketEntity) {
                            ((RocketEntity) context.getPlayer().getVehicle()).onJump();
                        }
                    }
                });
            }
        }));

        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_spawn"), ((context, buf) -> {
            EntityType<?> type = Registry.ENTITY_TYPE.get(buf.readVarInt());

            int entityID = buf.readVarInt();
            UUID entityUUID = buf.readUuid();

            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            float pitch = (buf.readByte() * 360) / 256.0F;
            float yaw = (buf.readByte() * 360) / 256.0F;

            Runnable spawn = () -> {
                Entity entity = type.create(MinecraftClient.getInstance().world);
                assert entity != null;
                entity.updateTrackedPosition(x, y, z);
                entity.x = x;
                entity.y = y;
                entity.z = z;
                entity.pitch = pitch;
                entity.yaw = yaw;
                entity.setEntityId(entityID);
                entity.setUuid(entityUUID);

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
