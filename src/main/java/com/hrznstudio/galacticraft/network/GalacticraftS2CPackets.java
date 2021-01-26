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
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
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
public class GalacticraftS2CPackets {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "redstone"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getServerWorld(), player, false);
                if (blockEntity != null) {
                    blockEntity.setRedstone(buffer.readEnumConstant(ConfigurableMachineBlockEntity.RedstoneState.class));
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "security"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getServerWorld(), player, true);
                if (blockEntity != null) {
                    ConfigurableMachineBlockEntity.SecurityInfo.Publicity publicity = buffer.readEnumConstant(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.class);
                    blockEntity.getSecurity().setPublicity(publicity);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "side_config"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getServerWorld(), player, false);
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
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "open_gc_inv"), (server, player, handler, buf, responseSender) -> server.execute(() -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, pl) -> new PlayerInventoryGCScreenHandler(inv, pl), Constants.Misc.EMPTY_TEXT))));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "bubble_max"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());

            server.execute(() -> {
                byte max = buffer.readByte();
                ConfigurableMachineBlockEntity blockEntity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getServerWorld(), player, false);
                if (blockEntity instanceof BubbleDistributorBlockEntity) {
                    if (max > 0) {
                        ((BubbleDistributorBlockEntity) blockEntity).setTargetSize(max);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "bubble_visible"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());

            server.execute(() -> {
                boolean visible = buffer.readBoolean();
                ConfigurableMachineBlockEntity entity = doBasicChecksAndGrabEntity(buffer.readBlockPos(), player.getServerWorld(), player, false);

                if (entity instanceof BubbleDistributorBlockEntity) {
                    ((BubbleDistributorBlockEntity) entity).bubbleVisible = visible;
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "dimension_teleport"), ((server, player, handler, buf, responseSender) -> {
            RegistryKey<World> dimension = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
            server.execute(() -> {
                player.setWorld(player.getServer().getWorld(dimension));
            });
        }));
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "designer_red"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            BlockPos pos = buffer.readBlockPos();
            server.execute(() -> {
                if (player.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (player.world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                        RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) player.world.getBlockEntity(pos);
                        assert blockEntity != null;
                        byte color = buffer.readByte();
                        blockEntity.setRed(color + 128);
                        blockEntity.updateSchematic();
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "designer_green"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            BlockPos pos = buffer.readBlockPos();
            server.execute(() -> {
                if (player.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (player.world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                        RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) player.world.getBlockEntity(pos);
                        assert blockEntity != null;
                        byte color = buffer.readByte();
                        blockEntity.setGreen(color + 128);
                        blockEntity.updateSchematic();
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "designer_blue"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            BlockPos pos = buffer.readBlockPos();
            server.execute(() -> {
                if (player.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (player.world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                        RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) player.world.getBlockEntity(pos);
                        assert blockEntity != null;
                        byte color = buffer.readByte();
                        blockEntity.setBlue(color + 128);
                        blockEntity.updateSchematic();
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "designer_alpha"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            BlockPos pos = buffer.readBlockPos();
            server.execute(() -> {
                if (player.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (player.world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                        RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) player.world.getBlockEntity(pos);
                        assert blockEntity != null;
                        byte color = buffer.readByte();
                        blockEntity.setAlpha(color + 128);
                        blockEntity.updateSchematic();
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "designer_part"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            BlockPos pos = buffer.readBlockPos();
            server.execute(() -> {
                if (player.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (player.world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                        RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) player.world.getBlockEntity(pos);
                        assert blockEntity != null;
                        Identifier id = buffer.readIdentifier();
                        if (player instanceof ServerPlayerEntityAccessor
                                && ((ServerPlayerEntityAccessor) player).getResearchTracker().isUnlocked(id)) {
                            blockEntity.setPartServer(Objects.requireNonNull(Galacticraft.ROCKET_PARTS.get(id)));
                            blockEntity.updateSchematic();
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "rocket_jump"), ((server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player.hasVehicle()) {
                    if (player.getVehicle() instanceof RocketEntity && ((RocketEntity) player.getVehicle()).getStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                        ((RocketEntity) player.getVehicle()).onJump();
                    }
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "rocket_yaw_press"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                if (player.getVehicle() instanceof RocketEntity && ((RocketEntity) player.getVehicle()).getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    player.getVehicle().prevYaw = player.getVehicle().yaw;
                    player.getVehicle().yaw += buffer.readBoolean() ? 2.0F : -2.0F;
                    player.getVehicle().yaw %= 360.0F;
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "rocket_pitch_press"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                if (player.getVehicle() instanceof RocketEntity && ((RocketEntity) player.getVehicle()).getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    player.getVehicle().prevPitch = player.getVehicle().pitch;
                    player.getVehicle().pitch += buffer.readBoolean() ? 2.0F : -2.0F;
                    player.getVehicle().pitch %= 360.0F;
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "assembler_wc"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                int slot = buffer.readInt();
                BlockPos pos = buffer.readBlockPos();
                boolean success = false;
                ServerWorld world = ((ServerPlayerEntity) player).getServerWorld();
                if (((ServerPlayerEntity) player).getServerWorld().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (player.world.getBlockEntity(pos) instanceof RocketAssemblerBlockEntity) {
                        SimpleInventoryComponent inventory = ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInventory();
                        if (slot < inventory.getSize()) {
                            if (player.inventory.getCursorStack().isEmpty()) {
                                success = true;
                                player.inventory.setCursorStack(inventory.getStack(slot));
                                inventory.setStack(slot, ItemStack.EMPTY);
                            } else {
                                if (inventory.isAcceptableStack(slot, player.inventory.getCursorStack().copy())) {
                                    if (inventory.getStack(slot).isEmpty()) {
                                        if (inventory.getMaxStackSize(slot) >= player.inventory.getCursorStack().getCount()) {
                                            inventory.setStack(slot, player.inventory.getCursorStack().copy());
                                            player.inventory.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            ItemStack stack = player.inventory.getCursorStack().copy();
                                            ItemStack stack1 = player.inventory.getCursorStack().copy();
                                            stack.setCount(inventory.getMaxStackSize(slot));
                                            stack1.setCount(stack1.getCount() - inventory.getMaxStackSize(slot));
                                            inventory.setStack(slot, stack);
                                            player.inventory.setCursorStack(stack1);
                                        }
                                    } else { // IMPOSSIBLE FOR THE 2 STACKS TO BE DIFFERENT AS OF RIGHT NOW. THIS MAY CHANGE.
                                        // SO... IF IT DOES, YOU NEED TO UPDATE THIS.
                                        ItemStack stack = player.inventory.getCursorStack().copy();
                                        int max = inventory.getMaxStackSize(slot);
                                        stack.setCount(stack.getCount() + inventory.getStack(slot).getCount());
                                        if (stack.getCount() <= max) {
                                            player.inventory.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            ItemStack stack1 = stack.copy();
                                            stack.setCount(max);
                                            stack1.setCount(stack1.getCount() - max);
                                            player.inventory.setCursorStack(stack1);
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
                    player.networkHandler.disconnect(new LiteralText("Bad rocket assembler packet!"));
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "assembler_build"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                BlockPos pos = buffer.readBlockPos();
                if (player.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (player.world.getBlockEntity(pos) instanceof RocketAssemblerBlockEntity) {
                        ((RocketAssemblerBlockEntity) player.world.getBlockEntity(pos)).startBuilding();
                    }
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "assembler_build"), ((server, player, handler, buf, responseSender) -> server.execute(() -> ((ServerPlayerEntity) player).networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "research_scroll"), new PacketByteBuf(Unpooled.buffer().writeDouble(((ServerPlayerEntityAccessor) player).getResearchScrollX()).writeDouble(((ServerPlayerEntityAccessor) player).getResearchScrollY())))))));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constants.MOD_ID, "research_scroll"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> ((ServerPlayerEntityAccessor) player).setResearchScroll(buffer.readDouble(), buffer.readDouble()));
        }));
    }

    private static @Nullable ConfigurableMachineBlockEntity doBasicChecksAndGrabEntity(@NotNull BlockPos pos, ServerWorld world, ServerPlayerEntity player, boolean strictAccess) {
        if (world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ConfigurableMachineBlockEntity) {
                if (player.getPos().distanceTo(Vec3d.ofCenter(entity.getPos())) < 6.5D) {
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
