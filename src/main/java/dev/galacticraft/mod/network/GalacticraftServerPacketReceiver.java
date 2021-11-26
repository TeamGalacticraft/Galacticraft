/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.AutomationType;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.block.util.BlockFace;
import dev.galacticraft.mod.api.machine.RedstoneInteractionType;
import dev.galacticraft.mod.api.machine.SecurityInfo;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.block.entity.BubbleDistributorBlockEntity;
import dev.galacticraft.mod.lookup.storage.MachineFluidStorage;
import dev.galacticraft.mod.screen.BubbleDistributorScreenHandler;
import dev.galacticraft.mod.screen.GalacticraftPlayerInventoryScreenHandler;
import dev.galacticraft.mod.screen.slot.ResourceFlow;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.FluidUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

/**
 * Handles server-bound (C2S) packets.
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftServerPacketReceiver {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "open_gc_inv"), (server, player, handler, buf, responseSender) -> server.execute(() -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory(GalacticraftPlayerInventoryScreenHandler::new, LiteralText.EMPTY))));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "side_config"), (server, player, handler, buf, responseSender) -> {
            BlockFace face = Constant.Misc.BLOCK_FACES[buf.readByte()];
            if (buf.readBoolean()) { //match
                if (buf.readBoolean()) { // int or slottype
                    int i = buf.readInt();
                    server.execute(() -> {
                        if (player.currentScreenHandler instanceof MachineScreenHandler sHandler) {
                            MachineBlockEntity machine = sHandler.machine;
                            if (machine.security().hasAccess(player)) {
                                if (i == -1) {
                                    machine.getConfiguration().getSideConfiguration().get(face).setMatching(null);
                                    return;
                                }
                                machine.getConfiguration().getSideConfiguration().get(face).setMatching(Either.left(i));
                            }
                        }
                    });
                } else {
                    int i = buf.readInt();
                    server.execute(() -> {
                        if (player.currentScreenHandler instanceof MachineScreenHandler sHandler) {
                            MachineBlockEntity machine = sHandler.machine;
                            if (machine.security().hasAccess(player)) {
                                if (i == -1) {
                                    machine.getConfiguration().getSideConfiguration().get(face).setMatching(null);
                                    return;
                                }
                                SlotType<?> type = SlotType.SLOT_TYPES.get(i);
                                machine.getConfiguration().getSideConfiguration().get(face).setMatching(Either.right(type));
                            }
                        }
                    });
                }
            } else {
                byte i = buf.readByte();
                server.execute(() -> {
                    if (player.currentScreenHandler instanceof MachineScreenHandler sHandler) {
                        MachineBlockEntity machine = sHandler.machine;
                        if (machine.security().hasAccess(player)) {
                            machine.getConfiguration().getSideConfiguration().get(face).setOption(AutomationType.getType(i));
                            machine.getConfiguration().getSideConfiguration().get(face).setMatching(null);
                            machine.sync();
                        }
                    }
                });
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "redstone_config"), (server, player, handler, buf, responseSender) -> {
            RedstoneInteractionType redstoneInteractionType = RedstoneInteractionType.values()[buf.readByte()];
            server.execute(() -> {
                if (player.currentScreenHandler instanceof MachineScreenHandler sHandler) {
                    MachineBlockEntity machine = sHandler.machine;
                    if (machine.security().hasAccess(player)) {
                        machine.getConfiguration().setRedstone(redstoneInteractionType);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "security_config"), (server, player, handler, buf, responseSender) -> {
            SecurityInfo.Accessibility accessibility = SecurityInfo.Accessibility.values()[buf.readByte()];
            server.execute(() -> {
                if (player.currentScreenHandler instanceof MachineScreenHandler sHandler) {
                    MachineBlockEntity machine = sHandler.machine;
                    if (machine.security().isOwner(player)) {
                        machine.getConfiguration().getSecurity().setAccessibility(accessibility);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "bubble_max"), (server, player, handler, buf, responseSender) -> {
            byte max = buf.readByte();
            server.execute(() -> {
                if (player.currentScreenHandler instanceof BubbleDistributorScreenHandler sHandler) {
                    BubbleDistributorBlockEntity machine = sHandler.machine;
                    if (machine.security().hasAccess(player)) {
                        if (max > 0) {
                            machine.setTargetSize(max);
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "bubble_visible"), (server, player, handler, buf, responseSender) -> {
            boolean visible = buf.readBoolean();
            server.execute(() -> {
                if (player.currentScreenHandler instanceof BubbleDistributorScreenHandler sHandler) {
                    BubbleDistributorBlockEntity machine = sHandler.machine;
                    if (machine.security().hasAccess(player)) {
                        machine.bubbleVisible = visible;
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "tank_modify"), (server, player, handler, buf, responseSender) -> {
            int index = buf.readInt();
            server.execute(() -> {
                if (player.currentScreenHandler instanceof MachineScreenHandler sHandler) {
                    MachineFluidStorage fluidTank = sHandler.machine.fluidInv();
                    ContainerItemContext context = ContainerItemContext.ofPlayerCursor(player, player.currentScreenHandler);
                    Storage<FluidVariant> itemTank = context.find(FluidStorage.ITEM);
                    if (itemTank != null) {
                        if (itemTank.supportsExtraction()) {
                            if (((Automatable<?>) fluidTank).getTypes()[index].getType().canFlow(ResourceFlow.INPUT)) {
                                try (Transaction transaction = Transaction.openOuter()) {
                                    FluidVariant extractableResource = StorageUtil.findExtractableResource(itemTank, fluidTank.getFilter(index), transaction);
                                    if (extractableResource != null) {
                                        FluidUtil.move(extractableResource, itemTank, fluidTank.getTank(index), Long.MAX_VALUE, transaction);
                                        ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "tank_modify"), new PacketByteBuf(Unpooled.buffer().writeInt(index)));
                                        transaction.commit();
                                        return;
                                    }
                                }
                            }
                        }
                        if (itemTank.supportsInsertion()) {
                            if (((Automatable) fluidTank).getTypes()[index].getType().canFlow(ResourceFlow.OUTPUT)) {
                                try (Transaction transaction = Transaction.openOuter()) {
                                    FluidVariant extractableResource = fluidTank.getFluid(index).fluid();
                                    if (!extractableResource.isBlank()) {
                                        FluidUtil.move(extractableResource, fluidTank.getTank(index), itemTank, Long.MAX_VALUE, transaction);
                                        ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "tank_modify"), new PacketByteBuf(Unpooled.buffer().writeInt(index)));
                                        transaction.commit();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            });
        });
    }
}
