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
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.screen.PlayerInventoryGCScreenHandler;
import com.hrznstudio.galacticraft.block.entity.BubbleDistributorBlockEntity;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftPackets {
    public static void register() {
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
