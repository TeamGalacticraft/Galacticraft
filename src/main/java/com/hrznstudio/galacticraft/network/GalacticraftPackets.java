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
 *
 */

package com.hrznstudio.galacticraft.network;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.screen.PlayerInventoryGCScreenHandler;
import com.hrznstudio.galacticraft.block.entity.BubbleDistributorBlockEntity;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftPackets {
    public static void register() {
        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "redstone"), ((context, buf) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            context.getTaskQueue().execute(() -> {
                BlockEntity blockEntity = context.getPlayer().world.getBlockEntity(buffer.readBlockPos());
                if (blockEntity instanceof ConfigurableMachineBlockEntity) {
                    ((ConfigurableMachineBlockEntity) blockEntity).setRedstoneState(buffer.readEnumConstant(ConfigurableMachineBlockEntity.RedstoneState.class));
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "security"), ((context, buf) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            context.getTaskQueue().execute(() -> {
                BlockPos pos = buffer.readBlockPos();
                if (context.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    BlockEntity blockEntity = ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getBlockEntity(pos);
                    if (blockEntity instanceof ConfigurableMachineBlockEntity) {
                        if (context.getPlayer().getPos().distanceTo(Vec3d.ofCenter(blockEntity.getPos())) < 12.0D) { //Make sure a player isn't just sending packets to insta-claim any machine placed down.
                            if (!((ConfigurableMachineBlockEntity) blockEntity).getSecurity().hasOwner() ||
                                    ((ConfigurableMachineBlockEntity) blockEntity).getSecurity().isOwner(context.getPlayer())) {
                                ConfigurableMachineBlockEntity.SecurityInfo.Publicity publicity = buffer.readEnumConstant(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.class);

                                ((ConfigurableMachineBlockEntity) blockEntity).getSecurity().setOwner(context.getPlayer());
                                ((ConfigurableMachineBlockEntity) blockEntity).getSecurity().setPublicity(publicity);
                            } else {
                                Galacticraft.logger.error("Received invalid (spoofed?) security packet from: " + context.getPlayer().getEntityName());
                            }
                        } else {
                            Galacticraft.logger.warn(context.getPlayer() + " sent a machine packet while not being in range of the machine!");
                        }
                    }
                } else {
                    Galacticraft.logger.warn(context.getPlayer() + " sent a machine packet pointing outside of loaded chunks!");
                }
            });
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "side_config"), ((context, buffer) -> {
            PacketByteBuf buf = new PacketByteBuf(buffer.copy());
            context.getTaskQueue().execute(() -> {
                BlockPos pos = buf.readBlockPos();
                if (context.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    BlockEntity blockEntity = ((ServerPlayerEntity) context.getPlayer()).getServerWorld().getBlockEntity(pos);
                    if (blockEntity instanceof ConfigurableMachineBlockEntity) {
                        if (((ConfigurableMachineBlockEntity) blockEntity).getSecurity().hasAccess(context.getPlayer())) {
                            EnumProperty<SideOption> prop = EnumProperty.of(buf.readEnumConstant(Direction.class).getName(), SideOption.class, ((ConfigurableMachineBlockEntity) blockEntity).validSideOptions());
                            context.getPlayer().world.setBlockState(pos, context.getPlayer().world.getBlockState(pos)
                                    .with(prop, buf.readEnumConstant(SideOption.class)));
                            if (buf.readBoolean()) {
                                if (buf.readBoolean()) {
                                    ((ConfigurableMachineBlockEntity) blockEntity).getSideConfigInfo().increment(ConfigurableMachineBlock.BlockFace.toFace(Direction.NORTH, Direction.byName(prop.getName())));
                                } else {
                                    ((ConfigurableMachineBlockEntity) blockEntity).getSideConfigInfo().decrement(ConfigurableMachineBlock.BlockFace.toFace(Direction.NORTH, Direction.byName(prop.getName())));
                                }
                            }
                        } else {
                            Galacticraft.logger.warn(context.getPlayer() + " sent a machine packet without the proper rights!");
                        }
                    }
                } else {
                    Galacticraft.logger.warn(context.getPlayer() + " sent a machine packet pointing outside of loaded chunks!");
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
    }
}
