/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.network;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.accessor.ServerPlayerEntityAccessor;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.api.rocket.LaunchStage;
import com.hrznstudio.galacticraft.block.entity.BubbleDistributorBlockEntity;
import com.hrznstudio.galacticraft.block.entity.RocketAssemblerBlockEntity;
import com.hrznstudio.galacticraft.block.entity.RocketDesignerBlockEntity;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.hrznstudio.galacticraft.screen.PlayerInventoryGCScreenHandler;
import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftPackets {
    public static void register() {

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "dimension_teleport"), ((context, buf) -> {
            RegistryKey<World> dimension = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
            context.getTaskQueue().execute(() -> {
                context.getPlayer().setWorld(context.getPlayer().getServer().getWorld(dimension));
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "redstone"), ((context, buf) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            context.getTaskQueue().execute(() -> {
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), context, false);
                if (blockEntity != null) {
                    blockEntity.setRedstone(buffer.readEnumConstant(ConfigurableMachineBlockEntity.RedstoneState.class));
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "security"), ((context, buf) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            context.getTaskQueue().execute(() -> {
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), context, true);
                if (blockEntity != null) {
                    ConfigurableMachineBlockEntity.SecurityInfo.Publicity publicity = buffer.readEnumConstant(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.class);
                    blockEntity.getSecurity().setPublicity(publicity);
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "side_config"), ((context, buf) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            context.getTaskQueue().execute(() -> {
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), context, false);
                if (blockEntity != null) {
                    if (buffer.readBoolean()) {
                        blockEntity.getSideConfigInfo().set(buffer.readEnumConstant(BlockFace.class), buffer.readEnumConstant(SideOption.class));
                    } else {
                        if (buffer.readBoolean()) {
                            blockEntity.getSideConfigInfo().increment(buffer.readEnumConstant(BlockFace.class));
                        } else {
                            blockEntity.getSideConfigInfo().decrement(buffer.readEnumConstant(BlockFace.class));
                        }
                    }
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "open_gc_inv"), ((context, buf) -> context.getTaskQueue().execute(() -> context.getPlayer().openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player) -> new PlayerInventoryGCScreenHandler(inv, player), new TranslatableText(""))))));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "bubble_max"), (context, packetByteBuf) -> {
            PacketByteBuf buf = new PacketByteBuf(packetByteBuf.copy());
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                context.getPlayer().getServer().execute(() -> {
                    byte max = buf.readByte();
                    BlockPos pos = buf.readBlockPos();
                    if (context.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        if (context.getPlayer().world.getBlockEntity(pos) instanceof BubbleDistributorBlockEntity) {
                            if (max > 0) {
                                ((BubbleDistributorBlockEntity) context.getPlayer().world.getBlockEntity(pos)).setTargetSize(max);
                            }
                        }
                    }
                });
            }
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "bubble_visible"), (context, packetByteBuf) -> {
            PacketByteBuf buf = new PacketByteBuf(packetByteBuf.copy());
            if (context.getPlayer() instanceof ServerPlayerEntity) {
                context.getPlayer().getServer().execute(() -> {
                    boolean visible = buf.readBoolean();
                    BlockPos pos = buf.readBlockPos();
                    if (context.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        if (context.getPlayer().world.getBlockEntity(pos) instanceof BubbleDistributorBlockEntity) {
                            ((BubbleDistributorBlockEntity) context.getPlayer().world.getBlockEntity(pos)).bubbleVisible = visible;
                        }
                    }
                });
            }
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_red"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            packetContext.getTaskQueue().execute(() -> {
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
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_green"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            packetContext.getTaskQueue().execute(() -> {
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
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_blue"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            packetContext.getTaskQueue().execute(() -> {
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
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_alpha"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            packetContext.getTaskQueue().execute(() -> {
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
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "designer_part"), (packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            BlockPos pos = buf.readBlockPos();
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                        RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos);
                        assert blockEntity != null;
                        Identifier id = buf.readIdentifier();
                        if (packetContext.getPlayer() instanceof ServerPlayerEntityAccessor
                                && ((ServerPlayerEntityAccessor) packetContext.getPlayer()).getResearchTracker().isUnlocked(id)) {
                            blockEntity.setPartServer(Objects.requireNonNull(Galacticraft.ROCKET_PARTS.get(id)));
                            blockEntity.updateSchematic();
                        }
                    }
                }
            });
        });

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_jump"), ((context, buff) -> {
            context.getTaskQueue().execute(() -> {
                if (context.getPlayer().hasVehicle()) {
                    if (context.getPlayer().getVehicle() instanceof RocketEntity && ((RocketEntity) context.getPlayer().getVehicle()).getStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                        ((RocketEntity) context.getPlayer().getVehicle()).onJump();
                    }
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_yaw_press"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer().getVehicle() instanceof RocketEntity && ((RocketEntity) packetContext.getPlayer().getVehicle()).getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    packetContext.getPlayer().getVehicle().prevYaw = packetContext.getPlayer().getVehicle().yaw;
                    packetContext.getPlayer().getVehicle().yaw += buf.readBoolean() ? 2.0F : -2.0F;
                    packetContext.getPlayer().getVehicle().yaw %= 360.0F;
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "rocket_pitch_press"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer().getVehicle() instanceof RocketEntity && ((RocketEntity) packetContext.getPlayer().getVehicle()).getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    packetContext.getPlayer().getVehicle().prevPitch = packetContext.getPlayer().getVehicle().pitch;
                    packetContext.getPlayer().getVehicle().pitch += buf.readBoolean() ? 2.0F : -2.0F;
                    packetContext.getPlayer().getVehicle().pitch %= 360.0F;
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "assembler_wc"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            packetContext.getTaskQueue().execute(() -> {
                int slot = buf.readInt();
                BlockPos pos = buf.readBlockPos();
                boolean success = false;
                ServerWorld world = ((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld();
                if (((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketAssemblerBlockEntity) {
                        SimpleInventoryComponent inventory = ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory();
                        if (slot < inventory.getSize()) {
                            if (packetContext.getPlayer().inventory.getCursorStack().isEmpty()) {
                                success = true;
                                packetContext.getPlayer().inventory.setCursorStack(inventory.getStack(slot));
                                inventory.setStack(slot, ItemStack.EMPTY);
                            } else {
                                if (inventory.isAcceptableStack(slot, packetContext.getPlayer().inventory.getCursorStack().copy())) {
                                    if (inventory.getStack(slot).isEmpty()) {
                                        if (inventory.getMaxStackSize(slot) >= packetContext.getPlayer().inventory.getCursorStack().getCount()) {
                                            inventory.setStack(slot, packetContext.getPlayer().inventory.getCursorStack().copy());
                                            packetContext.getPlayer().inventory.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            ItemStack stack = packetContext.getPlayer().inventory.getCursorStack().copy();
                                            ItemStack stack1 = packetContext.getPlayer().inventory.getCursorStack().copy();
                                            stack.setCount(inventory.getMaxStackSize(slot));
                                            stack1.setCount(stack1.getCount() - inventory.getMaxStackSize(slot));
                                            inventory.setStack(slot, stack);
                                            packetContext.getPlayer().inventory.setCursorStack(stack1);
                                        }
                                    } else { // IMPOSSIBLE FOR THE 2 STACKS TO BE DIFFERENT AS OF RIGHT NOW. THIS MAY CHANGE.
                                        // SO... IF IT DOES, YOU NEED TO UPDATE THIS.
                                        ItemStack stack = packetContext.getPlayer().inventory.getCursorStack().copy();
                                        int max = inventory.getMaxStackSize(slot);
                                        stack.setCount(stack.getCount() + inventory.getStack(slot).getCount());
                                        if (stack.getCount() <= max) {
                                            packetContext.getPlayer().inventory.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            ItemStack stack1 = stack.copy();
                                            stack.setCount(max);
                                            stack1.setCount(stack1.getCount() - max);
                                            packetContext.getPlayer().inventory.setCursorStack(stack1);
                                        }
                                        inventory.setStack(slot, stack);
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
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "assembler_build"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            packetContext.getTaskQueue().execute(() -> {
                BlockPos pos = buf.readBlockPos();
                if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof RocketAssemblerBlockEntity) {
                        ((RocketAssemblerBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos)).startBuilding();
                    }
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "assembler_build"), ((packetContext, buff) -> packetContext.getTaskQueue().execute(() -> ((ServerPlayerEntity) packetContext.getPlayer()).networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "research_scroll"), new PacketByteBuf(Unpooled.buffer().writeDouble(((ServerPlayerEntityAccessor) packetContext.getPlayer()).getResearchScrollX()).writeDouble(((ServerPlayerEntityAccessor) packetContext.getPlayer()).getResearchScrollY())))))));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "research_scroll"), ((packetContext, buff) -> {
            PacketByteBuf buf = new PacketByteBuf(buff.copy());
            packetContext.getTaskQueue().execute(() -> ((ServerPlayerEntityAccessor) packetContext.getPlayer()).setResearchScroll(buf.readDouble(), buf.readDouble()));
        }));
    }

    private static @Nullable ConfigurableMachineBlockEntity doBasicChecksAndGrabEntity(@NotNull BlockPos pos, @NotNull PacketContext context, boolean strictAccess) {
        if (context.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            BlockEntity entity = context.getPlayer().world.getBlockEntity(pos);
            if (entity instanceof ConfigurableMachineBlockEntity) {
                if (context.getPlayer().getPos().distanceTo(Vec3d.ofCenter(entity.getPos())) < 6.5D) {
                    if (strictAccess) {
                        if (((ConfigurableMachineBlockEntity) entity).getSecurity().isOwner(context.getPlayer())) {
                            return (ConfigurableMachineBlockEntity) entity;
                        } else {
                            Galacticraft.logger.error("Player '" + context.getPlayer().getEntityName() + "' sent critical packet without the proper permissions! ");
                        }
                    } else {
                        if (((ConfigurableMachineBlockEntity) entity).getSecurity().hasAccess(context.getPlayer())) {
                            return (ConfigurableMachineBlockEntity) entity;
                        } else {
                            Galacticraft.logger.error("Player '" + context.getPlayer().getEntityName() + "' sent packet without the proper permissions! ");
                        }
                    }
                } else {
                    Galacticraft.logger.error(context.getPlayer().getEntityName() + " tried to access machine while being more than 6.5 blocks away from the machine! (reach distance is 5 blocks)");
                }
            } else {
                Galacticraft.logger.error("Failed to grab block entity specified by " + context.getPlayer().getEntityName());
            }
        } else {
            Galacticraft.logger.error("Block entity specified by " + context.getPlayer().getEntityName() + " is in an unloaded chunk");
        }
        return null;
    }
}
