/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.gas.Gases;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.screen.SimpleMachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineFluidStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.slot.display.ItemSlotDisplay;
import dev.galacticraft.machinelib.api.storage.slot.display.TankDisplay;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.ServerLevelAccessor;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroups;
import dev.galacticraft.mod.screen.GCMenuTypes;
import dev.galacticraft.mod.util.FluidUtil;
import dev.galacticraft.mod.util.GenericStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenSealerBlockEntity extends MachineBlockEntity {
    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);
    public static final int BATTERY_SLOT = 0;
    public static final int OXYGEN_SLOT = 1;
    public static final int LOX_INPUT = 1;
    public static final int OXYGEN_TANK = 0;
    public static final byte SEAL_CHECK_TIME = 20;

    private final Set<BlockPos> breathablePositions = new HashSet<>();
    private final Set<BlockPos> watching = new HashSet<>();
    private byte sealCheckTime;
    private boolean updateQueued = true;
    private boolean disabled = false;
    private boolean oxygenWorld = false;

    public OxygenSealerBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.OXYGEN_SEALER, pos, state);
    }

    @Override
    public long getEnergyCapacity() {
        return Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize();
    }

    @Override
    public boolean canExposedInsertEnergy() {
        return true;
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GCSlotGroups.ENERGY_CHARGE, Constant.Filter.Item.CAN_EXTRACT_ENERGY, true, ItemSlotDisplay.create(8, 62))
                .addSlot(GCSlotGroups.OXYGEN_TANK_FILL, Constant.Filter.Item.CAN_EXTRACT_LOX, true, ItemSlotDisplay.create(31, 62))
                .build();
    }

    @Override
    protected @NotNull MachineFluidStorage createFluidStorage() {
        return MachineFluidStorage.Builder.create()
                .addTank(GCSlotGroups.OXYGEN_INPUT, MAX_OXYGEN, TankDisplay.create(31, 8), true)
                .build();
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        this.sealCheckTime = SEAL_CHECK_TIME;
        this.oxygenWorld = CelestialBody.getByDimension(world).map(body -> body.atmosphere().breathable()).orElse(true);
        if (!world.isClientSide) ((ServerLevelAccessor) world).addSealer(this);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.attemptChargeFromStack(BATTERY_SLOT);
        this.drainOxygenFromStack(OXYGEN_SLOT);
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        assert world != null;
        if (this.disabled != (this.disabled = false)) {
            ((ServerLevelAccessor) world).addSealer(this);
        }

        try (Transaction transaction = Transaction.openOuter()) {
            if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate(), transaction) == Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate()) {
                if (!this.fluidStorage().isEmpty(OXYGEN_TANK)) {
                    if (this.sealCheckTime > 0) this.sealCheckTime--;
                    if (this.updateQueued && this.sealCheckTime == 0) {
                        profiler.push("check_seal");
                        this.updateQueued = false;
                        this.sealCheckTime = SEAL_CHECK_TIME;
                        BlockPos pos1 = pos.relative(Direction.UP);
                        if (this.oxygenWorld || (this.breathablePositions.isEmpty() && world.isBreathable(pos1))) {
                            profiler.pop();
                            return GCMachineStatus.ALREADY_SEALED;
                        }
                        for (BlockPos pos2 : this.breathablePositions) {
                            world.setBreathable(pos2, false);
                        }
                        this.breathablePositions.clear();
                        this.watching.clear();
                        Queue<Tuple<BlockPos, Direction>> queue = new LinkedList<>();
                        Set<Tuple<BlockPos, Direction>> checked = new HashSet<>();
                        Set<BlockPos> added = new HashSet<>();
                        BlockState state1;
                        Tuple<BlockPos, Direction> pair;
                        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                        queue.add(new Tuple<>(pos1, Direction.UP));
                        checked.add(new Tuple<>(pos1, Direction.UP));
                        while (!queue.isEmpty()) {
                            pair = queue.poll();
                            pos1 = pair.getA();
                            state1 = world.getBlockState(pos1);
                            if (state1.isAir() || (!Block.isFaceFull(state1.getCollisionShape(world, pos1), pair.getB().getOpposite()))) {
                                this.breathablePositions.add(pos1);
                                if (this.breathablePositions.size() > 1000) {
                                    this.breathablePositions.clear();
                                    this.watching.clear();
                                    this.updateQueued = true;
                                    this.sealCheckTime = SEAL_CHECK_TIME * 5;
                                    profiler.pop();
                                    return GCMachineStatus.AREA_TOO_LARGE;
                                }
                                added.add(pos1);
                                final BlockPos finalPos = pos1;
                                queue.removeIf(blockPosDirectionPair -> blockPosDirectionPair.getA().equals(finalPos));
                                for (Direction direction : Constant.Misc.DIRECTIONS) {
                                    final Tuple<BlockPos, Direction> e = new Tuple<>(mutable.set(pos1).move(direction).immutable(), direction);
                                    if (!added.contains(e.getA()) && checked.add(e)) {
                                        if (!Block.isFaceFull(state1.getCollisionShape(world, pos1), e.getB())) {
                                            queue.add(e);
                                        }
                                    }
                                }
                            } else {
                                this.watching.add(pos1);
                            }
                        }
                        for (BlockPos pos2 : this.breathablePositions) {
                            world.setBreathable(pos2, true);
                        }
                        profiler.pop();
                    }
                    profiler.push("extract_oxygen");
                    this.fluidStorage().extract(OXYGEN_TANK, breathablePositions.size() * 2L, transaction);
                    transaction.commit();
                    profiler.pop();
                    return GCMachineStatus.SEALED;
                } else {
                    this.sealCheckTime = 0;
                    return GCMachineStatus.NOT_ENOUGH_OXYGEN;
                }
            } else {
                this.sealCheckTime = 0;
                return MachineStatuses.NOT_ENOUGH_ENERGY;
            }
        }
    }

    @Override
    protected void tickDisabled(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        this.disabled = true;
        ((ServerLevelAccessor) world).removeSealer(this);
        for (BlockPos pos1 : this.breathablePositions) {
            world.setBreathable(pos1, false);
        }
        this.breathablePositions.clear();
        this.watching.clear();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!this.level.isClientSide) {
            ((ServerLevelAccessor) this.level).removeSealer(this);
        }
        for (BlockPos pos : this.breathablePositions) {
            this.level.setBreathable(pos, false);
        }
        this.breathablePositions.clear();
        this.watching.clear();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return SimpleMachineMenu.create(
                    syncId,
                    player,
                    this,
                    GCMenuTypes.OXYGEN_SEALER_HANDLER
            );
        }
        return null;
    }

    public void enqueueUpdate(BlockPos pos, VoxelShape voxelShape2) {
        if ((this.watching.contains(pos) && !Block.isShapeFullBlock(voxelShape2)) || (this.breathablePositions.contains(pos) && !voxelShape2.isEmpty())) {
            this.updateQueued = true;
        }
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
}