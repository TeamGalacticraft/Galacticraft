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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import alexiil.mc.lib.attributes.misc.Reference;
import com.mojang.datafixers.util.Either;
import dev.galacticraft.api.accessor.SatelliteAccessor;
import dev.galacticraft.api.entity.Rocket;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.api.universe.celestialbody.satellite.Orbitable;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.ServerPlayerEntityAccessor;
import dev.galacticraft.mod.api.block.AutomationType;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.block.util.BlockFace;
import dev.galacticraft.mod.api.machine.RedstoneInteractionType;
import dev.galacticraft.mod.api.machine.SecurityInfo;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.attribute.fluid.MachineFluidInv;
import dev.galacticraft.mod.block.entity.BubbleDistributorBlockEntity;
import dev.galacticraft.mod.block.entity.RocketAssemblerBlockEntity;
import dev.galacticraft.mod.block.entity.RocketDesignerBlockEntity;
import dev.galacticraft.mod.screen.BubbleDistributorScreenHandler;
import dev.galacticraft.mod.screen.GalacticraftPlayerInventoryScreenHandler;
import dev.galacticraft.mod.screen.slot.SlotType;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Optional;

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
                        MachineBlockEntity machine = ((MachineScreenHandler<?>) player.currentScreenHandler).machine;
                        if (machine.security().hasAccess(player)) {
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
                        MachineBlockEntity machine = ((MachineScreenHandler<?>) player.currentScreenHandler).machine;
                        if (machine.security().hasAccess(player)) {
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
                    MachineBlockEntity machine = ((MachineScreenHandler<?>) player.currentScreenHandler).machine;
                    if (machine.security().hasAccess(player)) {
                        machine.getConfiguration().getSideConfiguration().get(face).setOption(AutomationType.values()[i]);
                        machine.getConfiguration().getSideConfiguration().get(face).setMatching(null);
                        machine.sync();
                    }
                });
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "redstone_config"), (server, player, handler, buf, responseSender) -> {
            RedstoneInteractionType redstoneInteractionType = RedstoneInteractionType.values()[buf.readByte()];
            server.execute(() -> {
                MachineBlockEntity machine = ((MachineScreenHandler<?>) player.currentScreenHandler).machine;
                if (machine.security().hasAccess(player)) {
                    machine.getConfiguration().setRedstone(redstoneInteractionType);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "security_config"), (server, player, handler, buf, responseSender) -> {
            SecurityInfo.Accessibility accessibility = SecurityInfo.Accessibility.values()[buf.readByte()];
            server.execute(() -> {
                MachineBlockEntity machine = ((MachineScreenHandler<?>) player.currentScreenHandler).machine;
                if (machine.security().isOwner(player)) {
                    machine.getConfiguration().getSecurity().setAccessibility(accessibility);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "bubble_max"), (server, player, handler, buf, responseSender) -> {
            byte max = buf.readByte();
            server.execute(() -> {
                BubbleDistributorBlockEntity machine = ((BubbleDistributorScreenHandler) player.currentScreenHandler).machine;
                if (machine.security().hasAccess(player)) {
                    if (max > 0) {
                        machine.setTargetSize(max);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "bubble_visible"), (server, player, handler, buf, responseSender) -> {
            boolean visible = buf.readBoolean();
            server.execute(() -> {
                BubbleDistributorBlockEntity machine = ((BubbleDistributorScreenHandler) player.currentScreenHandler).machine;
                if (machine.security().hasAccess(player)) {
                    machine.bubbleVisible = visible;
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "tank_modify"), (server, player, handler, buf, responseSender) -> {
            int index = buf.readInt();
            server.execute(() -> {
                MachineFluidInv inv = ((MachineScreenHandler<?>) player.currentScreenHandler).machine.fluidInv();
                ItemInsertable excess = new FixedInventoryVanillaWrapper(player.getInventory()).getInsertable();
                Reference<ItemStack> reference = new Reference<>() {
                    @Override
                    public ItemStack get() {
                        return player.currentScreenHandler.getCursorStack();
                    }

                    @Override
                    public boolean set(ItemStack value) {
                        player.currentScreenHandler.setCursorStack(value);
                        return true;
                    }

                    @Override
                    public boolean isValid(ItemStack value) {
                        return true;
                    }
                };
                FluidExtractable extractable = FluidAttributes.EXTRACTABLE.getFirstOrNull(reference, excess);
                if (extractable != null && !extractable.attemptExtraction(inv.getFilterForTank(index), FluidAmount.MAX_BUCKETS, Simulation.SIMULATE).isEmpty()) {
                    if (((Automatable) inv).getTypes()[index].getType().isInput()) {
                        FluidVolumeUtil.move(extractable, inv.getTank(index));
                        ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "tank_modify"), new PacketByteBuf(Unpooled.buffer().writeInt(index)));
                    }
                } else {
                    FluidInsertable insertable = FluidAttributes.INSERTABLE.getFirstOrNull(reference, excess);
                    if (insertable != null) {
                        if (((Automatable) inv).getTypes()[index].getType().isOutput()) {
                            FluidVolumeUtil.move(inv.getTank(index), insertable);
                            ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "tank_modify"), new PacketByteBuf(Unpooled.buffer().writeInt(index)));
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "dimension_teleport"), ((server, player, handler, buf, responseSender) -> {
            RegistryKey<World> dimension = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
            server.execute(() -> {
                player.setWorld(player.getServer().getWorld(dimension));
            });
        }));
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "designer_red"), (server, player, handler, buf, responseSender) -> {
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

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "designer_green"), (server, player, handler, buf, responseSender) -> {
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

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "designer_blue"), (server, player, handler, buf, responseSender) -> {
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

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "designer_alpha"), (server, player, handler, buf, responseSender) -> {
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

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "designer_part"), (server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            BlockPos pos = buffer.readBlockPos();
            server.execute(() -> {
                if (player.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (player.world.getBlockEntity(pos) instanceof RocketDesignerBlockEntity) {
                        RocketDesignerBlockEntity blockEntity = (RocketDesignerBlockEntity) player.world.getBlockEntity(pos);
                        assert blockEntity != null;
                        RocketPart part = RocketPart.getById(server.getRegistryManager(), buffer.readIdentifier());
                        if (part == null) player.networkHandler.disconnect(new LiteralText("Invalid rocket designer packet received."));
                        if (part.isUnlocked(player)) {
                            blockEntity.setPart(part);
                            blockEntity.updateSchematic();
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "rocket_jump"), ((server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player.hasVehicle()) {
                    if (player.getVehicle() instanceof Rocket && ((Rocket) player.getVehicle()).getStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                        ((Rocket) player.getVehicle()).onJump();
                    }
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "rocket_yaw_press"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                if (player.getVehicle() instanceof Rocket && ((Rocket) player.getVehicle()).getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    player.getVehicle().setYaw((player.getVehicle().getYaw() + (buffer.readBoolean() ? 2.0F : -2.0F)) % 360.0f);
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "rocket_pitch_press"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                if (player.getVehicle() instanceof Rocket && ((Rocket) player.getVehicle()).getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    player.getVehicle().setPitch((player.getVehicle().getPitch() + (buffer.readBoolean() ? 2.0F : -2.0F)) % 360.0f);
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "assembler_wc"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            server.execute(() -> {
                int slot = buffer.readInt();
                BlockPos pos = buffer.readBlockPos();
                boolean success = false;
                ServerWorld world = player.getServerWorld();
                if (player.getServerWorld().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (player.world.getBlockEntity(pos) instanceof RocketAssemblerBlockEntity) {
                        FullFixedItemInv inventory = ((RocketAssemblerBlockEntity) world.getBlockEntity(pos)).getExtendedInv();
                        if (slot < inventory.getSlotCount()) {
                            if (player.currentScreenHandler.getCursorStack().isEmpty()) {
                                success = true;
                                player.currentScreenHandler.setCursorStack(inventory.getInvStack(slot));
                                inventory.setInvStack(slot, ItemStack.EMPTY, Simulation.ACTION);
                            } else {
                                if (inventory.getFilterForSlot(slot).matches(player.currentScreenHandler.getCursorStack().copy())) {
                                    if (inventory.getInvStack(slot).isEmpty()) {
                                        if (inventory.getMaxAmount(slot, player.currentScreenHandler.getCursorStack()) >= player.currentScreenHandler.getCursorStack().getCount()) {
                                            inventory.setInvStack(slot, player.currentScreenHandler.getCursorStack().copy(), Simulation.ACTION);
                                            player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            ItemStack stack = player.currentScreenHandler.getCursorStack().copy();
                                            ItemStack stack1 = player.currentScreenHandler.getCursorStack().copy();
                                            stack.setCount(inventory.getMaxAmount(slot, player.currentScreenHandler.getCursorStack()));
                                            stack1.setCount(stack1.getCount() - inventory.getMaxAmount(slot, player.currentScreenHandler.getCursorStack()));
                                            inventory.setInvStack(slot, stack, Simulation.ACTION);
                                            player.currentScreenHandler.setCursorStack(stack1);
                                        }
                                    } else { // IMPOSSIBLE FOR THE 2 STACKS TO BE DIFFERENT AS OF RIGHT NOW. THIS MAY CHANGE.
                                        // SO... IF IT DOES, YOU NEED TO UPDATE THIS.
                                        ItemStack stack = player.currentScreenHandler.getCursorStack().copy();
                                        int max = inventory.getMaxAmount(slot, player.currentScreenHandler.getCursorStack());
                                        stack.setCount(stack.getCount() + inventory.getInvStack(slot).getCount());
                                        if (stack.getCount() <= max) {
                                            player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            ItemStack stack1 = stack.copy();
                                            stack.setCount(max);
                                            stack1.setCount(stack1.getCount() - max);
                                            player.currentScreenHandler.setCursorStack(stack1);
                                        }
                                        inventory.setInvStack(slot, stack, Simulation.ACTION);
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

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "assembler_build"), ((server, player, handler, buf, responseSender) -> {
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
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "create_satellite"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            if (((ServerPlayerEntityAccessor) player).getCelestialScreenState() != null) {
                server.execute(() -> {
                    CelestialBody<?, ?> parent = server.getRegistryManager().get(AddonRegistry.CELESTIAL_BODY_KEY).get(buffer.readIdentifier());
                    if (parent != null) {
                        if (parent.type() instanceof Orbitable orbitable) {
                            if (((ServerPlayerEntityAccessor) player).getCelestialScreenState().canTravelTo(server.getRegistryManager(), parent) || ((ServerPlayerEntityAccessor) player).getCelestialScreenState() == RocketData.empty()) {
                                if (orbitable.satelliteRecipe(parent.config()) != null) {
                                    SatelliteRecipe recipe = orbitable.satelliteRecipe(parent.config());
                                    if (player.isCreative() || recipe.test(player.getInventory())) {
                                        if (!player.isCreative()) {
                                            Object2IntMap<Ingredient> ingredients = recipe.ingredients();
                                            for (Object2IntMap.Entry<Ingredient> entry : ingredients.object2IntEntrySet()) {
                                                if (entry.getIntValue() != Inventories.remove(player.getInventory(), stack1 -> entry.getKey().test(stack1), entry.getIntValue(), false))
                                                    throw new IllegalStateException("Inventory had enough items, but cannot extract said items?!? Player: " + player.getGameProfile().getName());
                                            }
                                        }
                                        Optional<Structure> structure = server.getStructureManager().getStructure(new Identifier(Constant.MOD_ID, "satellite"));
                                        SatelliteType.registerSatellite(server, player, parent, structure.orElse(new Structure()));
                                    }
                                }
                            }
                        }
                    } else {
                        player.networkHandler.disconnect(new LiteralText("Invalid satellite packet received."));
                    }
                });
            }
        }));
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "planet_tp"), ((server, player, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            if (((ServerPlayerEntityAccessor) player).getCelestialScreenState() != null) {
                server.execute(() -> {
                    Identifier id = buffer.readIdentifier();
                    CelestialBody<?, ?> body = ((SatelliteAccessor) server).satellites().get(id);
                    if (body == null) body = server.getRegistryManager().get(AddonRegistry.CELESTIAL_BODY_KEY).get(id);
                    if (body.type() instanceof Landable landable && (((ServerPlayerEntityAccessor) player).getCelestialScreenState().canTravelTo(server.getRegistryManager(), body) || ((ServerPlayerEntityAccessor) player).getCelestialScreenState() == RocketData.empty())) {
                        ((ServerPlayerEntityAccessor) player).setCelestialScreenState(null);
                        player.teleport(server.getWorld(landable.world(body.config())), player.getX(), 500, player.getZ(), player.getYaw(), player.getPitch());
                    } else {
                        player.networkHandler.disconnect(new LiteralText("Invalid planet teleport packet received."));
                    }
                });
            } else {
                player.networkHandler.disconnect(new LiteralText("Invalid planet teleport packet received."));
            }
        }));
    }
}
