/*
 * Copyright (c) 2019-2021 HRZN LTD
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
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.block.entity.BubbleDistributorBlockEntity;
import com.hrznstudio.galacticraft.screen.PlayerInventoryGCScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftS2CPacketReceivers {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constants.MOD_ID, "redstone"), (server, player, handler, buf, responseSender) -> {
            FriendlyByteBuf buffer = new FriendlyByteBuf(buf.copy());
            server.execute(() -> {
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getLevel(), player, false);
                if (blockEntity != null) {
                    blockEntity.setRedstone(buffer.readEnum(ConfigurableMachineBlockEntity.RedstoneState.class));
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constants.MOD_ID, "security"), (server, player, handler, buf, responseSender) -> {
            FriendlyByteBuf buffer = new FriendlyByteBuf(buf.copy());
            server.execute(() -> {
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getLevel(), player, true);
                if (blockEntity != null) {
                    ConfigurableMachineBlockEntity.SecurityInfo.Publicity publicity = buffer.readEnum(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.class);
                    blockEntity.getSecurity().setPublicity(publicity);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constants.MOD_ID, "side_config"), (server, player, handler, buf, responseSender) -> {
            FriendlyByteBuf buffer = new FriendlyByteBuf(buf.copy());
            server.execute(() -> {
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getLevel(), player, false);
                if (blockEntity != null) {
                    if (buffer.readBoolean()) {
                        blockEntity.getSideConfiguration().set(buffer.readEnum(BlockFace.class), buffer.readEnum(SideOption.class));
                    } else {
                        if (buffer.readBoolean()) {
                            blockEntity.getSideConfiguration().increment(buffer.readEnum(BlockFace.class));
                        } else {
                            blockEntity.getSideConfiguration().decrement(buffer.readEnum(BlockFace.class));
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constants.MOD_ID, "open_gc_inv"), (server, player, handler, buf, responseSender) -> server.execute(() -> player.openMenu(new SimpleMenuProvider((syncId, inv, pl) -> new PlayerInventoryGCScreenHandler(inv, pl), Constants.Misc.EMPTY_TEXT))));

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constants.MOD_ID, "bubble_max"), (server, player, handler, buf, responseSender) -> {
            FriendlyByteBuf buffer = new FriendlyByteBuf(buf.copy());

            server.execute(() -> {
                byte max = buffer.readByte();
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getLevel(), player, false);
                if (blockEntity instanceof BubbleDistributorBlockEntity) {
                    if (max > 0) {
                        ((BubbleDistributorBlockEntity) blockEntity).setTargetSize(max);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constants.MOD_ID, "bubble_visible"), (server, player, handler, buf, responseSender) -> {
            FriendlyByteBuf buffer = new FriendlyByteBuf(buf.copy());

            server.execute(() -> {
                boolean visible = buffer.readBoolean();
                ConfigurableMachineBlockEntity entity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getLevel(), player, false);

                if (entity instanceof BubbleDistributorBlockEntity) {
                    ((BubbleDistributorBlockEntity) entity).bubbleVisible = visible;
                }
            });
        });
    }

    private static @Nullable ConfigurableMachineBlockEntity doBasicChecksAndGrabEntity(@NotNull BlockPos pos, ServerLevel world, ServerPlayer player, boolean strictAccess) {
        if (world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ConfigurableMachineBlockEntity) {
                if (player.position().distanceTo(Vec3.atCenterOf(entity.getBlockPos())) < 6.5D) {
                    if (strictAccess) {
                        if (((ConfigurableMachineBlockEntity) entity).getSecurity().isOwner(player)) {
                            return (ConfigurableMachineBlockEntity) entity;
                        }
                    } else {
                        if (((ConfigurableMachineBlockEntity) entity).getSecurity().hasAccess(player)) {
                            return (ConfigurableMachineBlockEntity) entity;
                        }
                    }
                }
            }
        }
        return null;
    }
}
