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

import dev.galacticraft.api.accessor.WorldOxygenAccessor;
import dev.galacticraft.api.block.entity.MachineBlockEntity;
import dev.galacticraft.api.machine.MachineStatus;
import dev.galacticraft.api.machine.MachineStatuses;
import dev.galacticraft.api.machine.storage.MachineFluidStorage;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.api.machine.storage.display.TankDisplay;
import dev.galacticraft.api.screen.SimpleMachineScreenHandler;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.ServerWorldAccessor;
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotTypes;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
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
        super(GalacticraftBlockEntityType.OXYGEN_SEALER, pos, state);
    }

    @Override
    public long getEnergyCapacity() {
        return Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize();
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GalacticraftSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 62))
                .addSlot(GalacticraftSlotTypes.OXYGEN_TANK_FILL, new ItemSlotDisplay(8, 62))
                .build();
    }

    @Override
    protected @NotNull MachineFluidStorage createFluidStorage() {
        return MachineFluidStorage.Builder.create()
                .addTank(GalacticraftSlotTypes.OXYGEN_INPUT, MAX_OXYGEN, new TankDisplay(31, 8, 48), true)
                .build();
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        this.sealCheckTime = SEAL_CHECK_TIME;
        this.oxygenWorld = CelestialBody.getByDimension(world).map(body -> body.atmosphere().breathable()).orElse(true);
        if (!world.isClientSide) ((ServerWorldAccessor) world).addSealer(this);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.tickConstant(world, pos, state);
        this.attemptChargeFromStack(BATTERY_SLOT);
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        assert world != null;
        if (this.disabled != (this.disabled = false)) {
            ((ServerWorldAccessor) world).addSealer(this);
        }

        try (Transaction transaction = Transaction.openOuter()) {
            if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate(), transaction) == Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate()) {
                if (!this.fluidStorage().isEmpty(OXYGEN_TANK)) {
                    if (this.sealCheckTime > 0) this.sealCheckTime--;
                    if (this.updateQueued && this.sealCheckTime == 0) {
                        world.getProfiler().push("check_seal");
                        this.updateQueued = false;
                        this.sealCheckTime = SEAL_CHECK_TIME;
                        BlockPos pos1 = pos.relative(Direction.UP);
                        if (this.oxygenWorld || (this.breathablePositions.isEmpty() && ((WorldOxygenAccessor) world).isBreathable(pos1))) {
                            world.getProfiler().pop();
                            return GalacticraftMachineStatus.ALREADY_SEALED;
                        }
                        for (BlockPos pos2 : this.breathablePositions) {
                            ((WorldOxygenAccessor) world).setBreathable(pos2, false);
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
                                    world.getProfiler().pop();
                                    return GalacticraftMachineStatus.AREA_TOO_LARGE;
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
                            ((WorldOxygenAccessor) world).setBreathable(pos2, true);
                        }
                        world.getProfiler().pop();
                    }
                    world.getProfiler().push("extract_oxygen");
                    this.fluidStorage().extract(OXYGEN_TANK, breathablePositions.size() * 2L, transaction);
                    transaction.commit();
                    world.getProfiler().pop();
                    return GalacticraftMachineStatus.SEALED;
                } else {
                    this.sealCheckTime = 0;
                    return GalacticraftMachineStatus.NOT_ENOUGH_OXYGEN;
                }
            } else {
                this.sealCheckTime = 0;
                return MachineStatuses.NOT_ENOUGH_ENERGY;
            }
        }
    }

    @Override
    protected void tickDisabled(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        this.disabled = true;
        ((ServerWorldAccessor) world).removeSealer(this);
        for (BlockPos pos1 : this.breathablePositions) {
            ((WorldOxygenAccessor) world).setBreathable(pos1, false);
        }
        this.breathablePositions.clear();
        this.watching.clear();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!this.level.isClientSide) {
            ((ServerWorldAccessor) this.level).removeSealer(this);
        }
        for (BlockPos pos : this.breathablePositions) {
            ((WorldOxygenAccessor) this.level).setBreathable(pos, false);
        }
        this.breathablePositions.clear();
        this.watching.clear();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return SimpleMachineScreenHandler.create(
                    syncId,
                    player,
                    this,
                    GalacticraftScreenHandlerType.OXYGEN_SEALER_HANDLER
            );
        }
        return null;
    }

    public void enqueueUpdate(BlockPos pos, VoxelShape voxelShape2) {
        if ((this.watching.contains(pos) && !Block.isShapeFullBlock(voxelShape2)) || (this.breathablePositions.contains(pos) && !voxelShape2.isEmpty())) {
            this.updateQueued = true;
        }
    }
}