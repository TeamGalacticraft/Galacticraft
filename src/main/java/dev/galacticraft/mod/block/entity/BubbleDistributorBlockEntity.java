/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.block.entity;

import dev.galacticraft.api.block.entity.MachineBlockEntity;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.machine.MachineStatus;
import dev.galacticraft.api.machine.MachineStatuses;
import dev.galacticraft.api.machine.storage.MachineFluidStorage;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.api.machine.storage.display.TankDisplay;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.entity.BubbleEntity;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotTypes;
import dev.galacticraft.mod.screen.BubbleDistributorScreenHandler;
import dev.galacticraft.mod.util.FluidUtil;
import dev.galacticraft.mod.util.GenericStorageUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class BubbleDistributorBlockEntity extends MachineBlockEntity {
    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);
    public static final int BATTERY_SLOT = 0;
    public static final int OXYGEN_TANK_SLOT = 1;
    public static final int OXYGEN_TANK = 0;
    public boolean bubbleVisible = true;
    private double size = 0;
    private byte targetSize = 1;
    private int players = 0;
    private int bubbleId = -1;
    private double prevSize;

    public BubbleDistributorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.OXYGEN_BUBBLE_DISTRIBUTOR, pos, state);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GalacticraftSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 62))
                .addSlot(GalacticraftSlotTypes.OXYGEN_TANK_FILL, new ItemSlotDisplay(31, 62))
                .build();
    }

    @Override
    protected @NotNull MachineFluidStorage createFluidStorage() {
        return MachineFluidStorage.Builder.create()
                .addTank(GalacticraftSlotTypes.OXYGEN_INPUT, MAX_OXYGEN, new TankDisplay(31, 8, 48), true)
                .build();
    }

    @Override
    protected void tickConstant(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.tickConstant(world, pos, state);
        world.getProfiler().push("extract_resources");
        this.attemptChargeFromStack(BATTERY_SLOT);
        this.drainOxygenFromStack(OXYGEN_TANK_SLOT);
        world.getProfiler().pop();
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state) {
        world.getProfiler().push("transaction");
        MachineStatus status;
        this.players = world.getPlayers().size();
        this.prevSize = this.size;
        try (Transaction transaction = Transaction.openOuter()) {
            if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate(), transaction) == Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate()) {
                world.getProfiler().push("bubble");
                if (this.size > this.targetSize) {
                    this.setSize(Math.max(this.size - 0.1F, this.targetSize));
                }
                if (this.size > 0.0D && this.bubbleVisible && this.bubbleId == -1) {
                    BubbleEntity entity = new BubbleEntity(GalacticraftEntityType.BUBBLE, world);
                    entity.setPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
                    entity.prevX = this.getPos().getX();
                    entity.prevY = this.getPos().getY();
                    entity.prevZ = this.getPos().getZ();
                    world.spawnEntity(entity);
                    this.bubbleId = entity.getId();
                    for (ServerPlayerEntity player : world.getPlayers()) {
                        player.networkHandler.sendPacket(entity.createSpawnPacket());
                    }
                }
                world.getProfiler().pop();
                if (this.prevSize != this.size || this.players != world.getPlayers().size()) {
                    world.getProfiler().push("network");
                    for (ServerPlayerEntity player : world.getPlayers()) {
                        ServerPlayNetworking.send(player, new Identifier(Constant.MOD_ID, "bubble_size"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeDouble(this.size)));
                    }
                    world.getProfiler().pop();
                }
                world.getProfiler().push("bubbler_distributor_transfer");
                long oxygenRequired = ((long) ((4.0 / 3.0) * Math.PI * this.size * this.size * this.size));
                if (this.fluidStorage().extract(OXYGEN_TANK, oxygenRequired, transaction).getAmount() == oxygenRequired) {
                    if (this.size < this.targetSize) {
                        setSize(this.size + 0.05D);
                    }
                    transaction.commit();
                    world.getProfiler().pop();
                    return GalacticraftMachineStatus.DISTRIBUTING;
                } else {
                    status = GalacticraftMachineStatus.NOT_ENOUGH_OXYGEN;
                    world.getProfiler().pop();
                }
            } else {
                status = MachineStatuses.NOT_ENOUGH_ENERGY;
            }
        } finally {
            world.getProfiler().pop();
        }
        world.getProfiler().push("size");
        if (this.bubbleId != -1 && this.size <= 0) {
            world.getEntityById(bubbleId).remove(Entity.RemovalReason.DISCARDED);
            world.getEntityById(bubbleId).onRemoved();
            this.bubbleId = -1;
        }

        if (this.size > 0) {
            this.setSize(this.size - 0.2D);
        }

        if (this.size < 0) {
            this.setSize(0);
        }
        world.getProfiler().pop();
        return status;
    }

    public byte getTargetSize() {
        return this.targetSize;
    }

    public void setTargetSize(byte targetSize) {
        this.targetSize = targetSize;
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putByte(Constant.Nbt.MAX_SIZE, this.targetSize);
        tag.putDouble(Constant.Nbt.SIZE, this.size);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.size = nbt.getDouble(Constant.Nbt.SIZE);
        if (this.size < 0) this.size = 0;
        this.targetSize = nbt.getByte(Constant.Nbt.MAX_SIZE);
        if (this.targetSize < 1) this.targetSize = 1;
    }

    public double getSize() {
        return this.size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    protected void drainOxygenFromStack(int slot) {
        if (this.fluidStorage().isFull(0)) {
            return;
        }
        ContainerItemContext containerItemContext = ContainerItemContext.ofSingleSlot(this.itemStorage().getSlot(slot));
        Storage<FluidVariant> storage = containerItemContext.find(FluidStorage.ITEM);
        if (storage != null && storage.supportsExtraction()) {
            try (Transaction transaction = Transaction.openOuter()){
                GenericStorageUtil.move(FluidVariant.of(Gases.OXYGEN), storage, this.fluidStorage(), Long.MAX_VALUE, transaction);
                transaction.commit();
            }
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return new BubbleDistributorScreenHandler(syncId, player, this);
        return null;
    }
}