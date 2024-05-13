/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity.machine;

import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.ServerLevelAccessor;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

public class OxygenSealerBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_INPUT_SLOT = 1;
    public static final int OXYGEN_TANK = 0;

    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);
    public static final int SEAL_CHECK_TIME = 20;

    private final Set<BlockPos> breathablePositions = new HashSet<>();
    private final Set<BlockPos> watching = new HashSet<>();
    private int sealCheckTime;
    private boolean updateQueued = true;
    private boolean disabled = false;
    private boolean oxygenWorld = false;
    private boolean sealed = false;
    private boolean oxygenUnloaded = true;

    public OxygenSealerBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.OXYGEN_SEALER, pos, state);
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
        this.oxygenUnloaded = false;
        profiler.push("extract_resources");
        this.chargeFromStack(CHARGE_SLOT);
        this.takeFluidFromStack(OXYGEN_INPUT_SLOT, OXYGEN_TANK, Gases.OXYGEN);
        profiler.pop();
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        assert level != null;
        // if (this.disabled != (this.disabled = false)) {
        //     ((ServerLevelAccessor) level).addSealer(this);
        // }

        if (this.energyStorage().canExtract(Galacticraft.CONFIG.oxygenCompressorEnergyConsumptionRate())) {
            if (!this.fluidStorage().getSlot(OXYGEN_TANK).isEmpty()) {
                if (this.sealCheckTime > 0) this.sealCheckTime--;
                if (this.updateQueued && this.sealCheckTime == 0) {
                    profiler.push("check_seal");
                    this.updateQueued = false;
                    this.sealCheckTime = SEAL_CHECK_TIME;
                    BlockPos pos1 = pos.relative(Direction.UP);
                    if (this.oxygenWorld || (this.breathablePositions.isEmpty() && level.isBreathable(pos1))) {
                        profiler.pop();
                        return GCMachineStatuses.ALREADY_SEALED;
                    }
                    for (BlockPos pos2 : this.breathablePositions) {
                        level.setBreathable(pos2, false);
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
                        state1 = level.getBlockState(pos1);
                        if (state1.isAir() || (!Block.isFaceFull(state1.getCollisionShape(level, pos1), pair.getB().getOpposite()))) {
                            this.breathablePositions.add(pos1);
                            if (this.breathablePositions.size() > 1024) {
                                this.breathablePositions.clear();
                                this.watching.clear();
                                this.updateQueued = true;
                                this.sealCheckTime = SEAL_CHECK_TIME * 5;
                                this.sealed = false;
                                profiler.pop();
                                return GCMachineStatuses.AREA_TOO_LARGE;
                            }
                            added.add(pos1);
                            final BlockPos finalPos = pos1;
                            queue.removeIf(blockPosDirectionPair -> blockPosDirectionPair.getA().equals(finalPos));
                            for (Direction direction : Constant.Misc.DIRECTIONS) {
                                final Tuple<BlockPos, Direction> e = new Tuple<>(mutable.set(pos1).move(direction).immutable(), direction);
                                if (!added.contains(e.getA()) && checked.add(e)) {
                                    if (!Block.isFaceFull(state1.getCollisionShape(level, pos1), e.getB())) {
                                        queue.add(e);
                                    }
                                }
                            }
                        } else {
                            this.watching.add(pos1);
                        }
                    }
                    this.sealed = true; // if escaped queue then set sealed
                    for (BlockPos pos2 : this.breathablePositions) {
                        level.setBreathable(pos2, true);
                    }
                    profiler.pop();
                }

                if (!this.sealed) {
                    return GCMachineStatuses.AREA_TOO_LARGE;
                }

                profiler.push("extract");
                this.energyStorage().extract(Galacticraft.CONFIG.oxygenCompressorEnergyConsumptionRate());
                this.fluidStorage().getSlot(OXYGEN_TANK).extract(Gases.OXYGEN, breathablePositions.size() * 2L);
                profiler.pop();
                return GCMachineStatuses.SEALED;
            } else {
                this.tryClearSeal(level);
                return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
            }
        } else {
            this.tryClearSeal(level);
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
    }

    private void tryClearSeal(@NotNull ServerLevel world) {
        if (this.sealed) {
            for (BlockPos pos1 : this.breathablePositions) {
                world.setBreathable(pos1, false);
            }
            this.breathablePositions.clear();
            this.watching.clear();

            this.sealed = false;
        }
        this.updateQueued = true;
        this.sealCheckTime = 0;
    }

    @Override
    protected void tickDisabled(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        this.tryClearSeal(world);
        super.tickDisabled(world, pos, state, profiler);
    }

    @Override
    public void setRemoved() {
        if (!this.level.isClientSide) {
            ((ServerLevelAccessor) this.level).removeSealer(this);
        }
        if (!this.oxygenUnloaded) {
            this.oxygenUnloaded = true;
            for (BlockPos pos : this.breathablePositions) {
                this.level.setBreathable(pos, false);
            }
        }
        this.breathablePositions.clear();
        this.watching.clear();

        super.setRemoved();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return new MachineMenu<>(
                    syncId,
                    (ServerPlayer) player,
                    this
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