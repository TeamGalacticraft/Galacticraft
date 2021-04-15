/*
 * Copyright (c) 2020 Team Galacticraft
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

package dev.galacticraft.mod.network;

import com.mojang.datafixers.util.Either;
import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.AutomationType;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.block.util.BlockFace;
import dev.galacticraft.mod.api.machine.RedstoneInteractionType;
import dev.galacticraft.mod.api.machine.SecurityInfo;
import dev.galacticraft.mod.block.entity.BubbleDistributorBlockEntity;
import dev.galacticraft.mod.screen.GalacticraftPlayerInventoryScreenHandler;
import dev.galacticraft.mod.screen.slot.SlotType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftS2CPacketReceivers {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "open_gc_inv"), (server, player, handler, buf, responseSender) -> server.execute(() -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory(GalacticraftPlayerInventoryScreenHandler::new, LiteralText.EMPTY))));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "side_config"), (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            BlockFace face  = Constants.Misc.BLOCK_FACES[buf.readByte()];
            if (buf.readBoolean()) { //match
                if (buf.readBoolean()) { // int or slottype
                    int i = buf.readInt();
                    server.execute(() -> {
                        MachineBlockEntity machine = doBasicChecksAndGrabEntity(pos, player, false);
                        if (machine != null) {
                            if (i == -1) {
                                machine.getConfiguration().getSideConfiguration().get(face).setMatching(null);
                                return;
                            }
                            machine.getConfiguration().getSideConfiguration().get(face).setMatching(Either.left(i));
                        }
                    });
                } else {
                    int i = buf.readInt();
                    server.execute(() -> {
                        MachineBlockEntity machine = doBasicChecksAndGrabEntity(pos, player, false);
                        if (machine != null) {
                            if (i == -1) {
                                machine.getConfiguration().getSideConfiguration().get(face).setMatching(null);
                                return;
                            }
                            SlotType type = SlotType.SLOT_TYPES.get(i);
                            machine.getConfiguration().getSideConfiguration().get(face).setMatching(Either.right(type));
                        }
                    });
                }
            } else {
                int i = buf.readByte();
                server.execute(() -> {
                    MachineBlockEntity machine = doBasicChecksAndGrabEntity(pos, player, false);
                    if (machine != null) {
                        machine.getConfiguration().getSideConfiguration().get(face).setOption(AutomationType.values()[i]);
                        machine.getConfiguration().getSideConfiguration().get(face).setMatching(null);
                        machine.sync();
                    }
                });
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "redstone_config"), (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            RedstoneInteractionType redstoneInteractionType = RedstoneInteractionType.values()[buf.readByte()];
            server.execute(() -> {
                MachineBlockEntity machine = doBasicChecksAndGrabEntity(pos, player, false);
                if (machine != null) {
                    machine.getConfiguration().setRedstone(redstoneInteractionType);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "security_config"), (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            SecurityInfo.Accessibility accessibility = SecurityInfo.Accessibility.values()[buf.readByte()];
            server.execute(() -> {
                MachineBlockEntity machine = doBasicChecksAndGrabEntity(pos, player, true);
                if (machine != null) {
                    machine.getConfiguration().getSecurity().setAccessibility(accessibility);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "bubble_max"), (server, player, handler, buf, responseSender) -> {
            byte max = buf.readByte();
            BlockPos pos = buf.readBlockPos();
            server.execute(() -> {
                MachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(pos, player, false);
                if (blockEntity instanceof BubbleDistributorBlockEntity) {
                    if (max > 0) {
                        ((BubbleDistributorBlockEntity) blockEntity).setTargetSize(max);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "bubble_visible"), (server, player, handler, buf, responseSender) -> {
            boolean visible = buf.readBoolean();
            BlockPos pos = buf.readBlockPos();
            server.execute(() -> {
                MachineBlockEntity entity = doBasicChecksAndGrabEntity(pos, player, false);

                if (entity instanceof BubbleDistributorBlockEntity) {
                    ((BubbleDistributorBlockEntity) entity).bubbleVisible = visible;
                }
            });
        });
    }

    private static @Nullable MachineBlockEntity doBasicChecksAndGrabEntity(@NotNull BlockPos pos, ServerPlayerEntity player, boolean strictAccess) {
        if (player.getServerWorld().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            BlockEntity entity = player.getServerWorld().getBlockEntity(pos);
            if (entity instanceof MachineBlockEntity) {
                if ((player.getPos().squaredDistanceTo(Vec3d.ofCenter(entity.getPos())) < 64.0 && player.getServerWorld().canPlayerModifyAt(player, pos)) || player.isCreativeLevelTwoOp()) { //todo JamiesWhiteShirt/reach-entity-attributes ?
                    if (strictAccess) {
                        if (((MachineBlockEntity) entity).getSecurity().isOwner(player)) {
                            return (MachineBlockEntity) entity;
                        }
                    } else {
                        if (((MachineBlockEntity) entity).getSecurity().hasAccess(player)) {
                            return (MachineBlockEntity) entity;
                        }
                    }
                } else {
                    Galacticraft.LOGGER.debug("Player sent machine packet outside of allowed reach distance!");
                }
            }
        }
        return null;
    }
}
