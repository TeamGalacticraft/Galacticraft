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

import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.blocks.machines.rocketassembler.RocketAssemblerBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.rocketdesigner.RocketDesignerBlockEntity;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.LiteralText;
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
                        ((ConfigurableElectricMachineBlockEntity) blockEntity).setRedstoneState(buf.readEnumConstant(ConfigurableElectricMachineBlockEntity.RedstoneState.class));
                    }
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "security_update"), ((context, buff) -> {
            PacketByteBuf buffer = new PacketByteBuf(buff.copy());
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                context.getPlayer().getServer().execute(() -> {
                    BlockEntity blockEntity = ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getBlockEntity(buffer.readBlockPos());
                    if (blockEntity instanceof ConfigurableElectricMachineBlockEntity) {
                        if (!((ConfigurableElectricMachineBlockEntity) blockEntity).getSecurity().hasOwner() ||
                                ((ConfigurableElectricMachineBlockEntity) blockEntity).getSecurity().getOwner().equals(context.getPlayer().getUuid())) {
                            ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity publicity = buffer.readEnumConstant(ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity.class);

                            ((ConfigurableElectricMachineBlockEntity) blockEntity).getSecurity().setOwner(context.getPlayer());
                            ((ConfigurableElectricMachineBlockEntity) blockEntity).getSecurity().setPublicity(publicity);
                        } else {
                            Galacticraft.logger.error("Received invaild security packet from: " + context.getPlayer().getEntityName());
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
                entity.setPos(x, y, z);
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

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "assembler_wc"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).server.execute(() -> {
                    int slot = buf.readInt();
                    BlockPos pos = buf.readBlockPos();
                    boolean success = false;
                    ServerWorld world = ((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld();
                    if (((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketAssemblerBlockEntity) {
                            if (slot < ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().getSlotCount()) {
                                if (packetContext.getPlayer().inventory.getCursorStack().isEmpty()) {
                                    success = true;
                                    packetContext.getPlayer().inventory.setCursorStack(((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().getInvStack(slot));
                                    ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().setInvStack(slot, ItemStack.EMPTY, Simulation.ACTION);
                                } else {
                                    if (((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().isItemValidForSlot(slot, packetContext.getPlayer().inventory.getCursorStack().copy())) {
                                        if (((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().getInvStack(slot).isEmpty()) {
                                            if (((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().getMaxAmount(slot, packetContext.getPlayer().inventory.getCursorStack()) >= packetContext.getPlayer().inventory.getCursorStack().getCount()) {
                                                ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().setInvStack(slot, packetContext.getPlayer().inventory.getCursorStack().copy(), Simulation.ACTION);
                                                packetContext.getPlayer().inventory.setCursorStack(ItemStack.EMPTY);
                                            } else {
                                                ItemStack stack = packetContext.getPlayer().inventory.getCursorStack().copy();
                                                ItemStack stack1 = packetContext.getPlayer().inventory.getCursorStack().copy();
                                                stack.setCount(((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().getMaxAmount(slot, packetContext.getPlayer().inventory.getCursorStack()));
                                                stack1.setCount(stack1.getCount() - ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().getMaxAmount(slot, packetContext.getPlayer().inventory.getCursorStack()));
                                                ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().setInvStack(slot, stack, Simulation.ACTION);
                                                packetContext.getPlayer().inventory.setCursorStack(stack1);
                                            }
                                        } else { // IMPOSSIBLE FOR THE 2 STACKS TO BE DIFFERENT AS OF RIGHT NOW. THIS MAY CHANGE.
                                            // SO... IF IT DOES, YOU NEED TO UPDATE THIS.
                                            ItemStack stack = packetContext.getPlayer().inventory.getCursorStack().copy();
                                            int max = ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().getMaxAmount(slot, packetContext.getPlayer().inventory.getCursorStack());
                                            stack.setCount(stack.getCount() + ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().getInvStack(slot).getCount());
                                            if (stack.getCount() <= max) {
                                                packetContext.getPlayer().inventory.setCursorStack(ItemStack.EMPTY);
                                            } else {
                                                ItemStack stack1 = stack.copy();
                                                stack.setCount(max);
                                                stack1.setCount(stack1.getCount() - max);
                                                packetContext.getPlayer().inventory.setCursorStack(stack1);
                                            }
                                            ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory().setInvStack(slot, stack, Simulation.ACTION);
                                        }
                                        success = true;
                                    }
                                }
                            }
                        }
                    }

                    if (!success) {
                        ((ServerPlayerEntity) packetContext.getPlayer()).networkHandler.disconnect(new LiteralText("Bad rocket assembler packet!"));
                    }
                });
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "assembler_build"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            if (packetContext.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) packetContext.getPlayer()).server.execute(() -> {
                    BlockPos pos = buf.readBlockPos();
                    if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketAssemblerBlockEntity) {
                            ((RocketAssemblerBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos)).startBuilding();
                        }
                    }
                });
            }
        }));
    }
}
