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

package dev.galacticraft.mod.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import dev.galacticraft.api.accessor.WorldOxygenAccessor;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.ServerWorldAccessor;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.fluid.MachineFluidInv;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import dev.galacticraft.mod.util.OxygenTankUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
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
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
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
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(BATTERY_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 62);
        builder.addSlot(LOX_INPUT, SlotType.OXYGEN_TANK, OxygenTankUtil.OXYGEN_TANK_EXTRACTABLE, 8, 62);
        return builder;
    }

    @Override
    protected MachineFluidInv.Builder createFluidInv(MachineFluidInv.Builder builder) {
        builder.addLOXTank(OXYGEN_TANK, SlotType.OXYGEN_IN, 30, 8);
        return builder;
    }

    @Override
    public FluidAmount fluidInvCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.sealCheckTime = SEAL_CHECK_TIME;
        this.oxygenWorld = CelestialBody.getByDimension(world).map(body -> body.atmosphere().breathable()).orElse(true);
        if (!world.isClient) ((ServerWorldAccessor) world).addSealer(this);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(BATTERY_SLOT);
        assert this.world != null;
        if (!this.world.isClient && this.getStatus().getType().isActive()) {
            this.fluidInv().extractFluid(OXYGEN_TANK, Constant.Filter.LOX_ONLY, null, FluidAmount.of1620(breathablePositions.size()), Simulation.ACTION);
        }
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.fluidInv().getInvFluid(OXYGEN_TANK).isEmpty()) return Status.NOT_ENOUGH_OXYGEN;
        return Status.SEALED;
    }

    @Override
    public void tickWork() {
        if (this.disabled != (this.disabled = false) && !this.world.isClient) {
            ((ServerWorldAccessor) world).addSealer(this);
        }

        if (!this.getStatus().getType().isActive()) {
            this.sealCheckTime = 0;
            return;
        }

        if (this.sealCheckTime > 0) {
            this.sealCheckTime--;
        }

        if (this.updateQueued && this.sealCheckTime == 0) {
            if (this.getStatus().getType().isActive()) {
                this.world.getProfiler().push("oxygen_sealer");
                this.updateQueued = false;
                this.sealCheckTime = SEAL_CHECK_TIME;
                BlockPos pos = this.pos.toImmutable();
                if (this.oxygenWorld || (this.breathablePositions.isEmpty() && ((WorldOxygenAccessor) world).isBreathable(pos.up()))) {
                    this.setStatus(Status.ALREADY_SEALED);
                    this.world.getProfiler().pop();
                    return;
                }
                for (BlockPos pos2 : this.breathablePositions) {
                    ((WorldOxygenAccessor) this.world).setBreathable(pos2, false);
                }
                this.breathablePositions.clear();
                this.watching.clear();
                Queue<Pair<BlockPos, Direction>> queue = new LinkedList<>();
                Set<Pair<BlockPos, Direction>> checked = new HashSet<>();
                Set<BlockPos> added = new HashSet<>();
                BlockPos pos1 = pos.offset(Direction.UP);
                BlockState state;
                Pair<BlockPos, Direction> pair;
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                queue.add(new Pair<>(pos1, Direction.UP));
                checked.add(new Pair<>(pos1, Direction.UP));
                while (!queue.isEmpty()) {
                    pair = queue.poll();
                    pos1 = pair.getLeft();
                    state = world.getBlockState(pos1);
                    if (state.isAir() || (!Block.isFaceFullSquare(state.getCollisionShape(world, pos1), pair.getRight().getOpposite()))) {
                        this.breathablePositions.add(pos1);
                        added.add(pos1);
                        final BlockPos finalPos = pos1;
                        queue.removeIf(blockPosDirectionPair -> blockPosDirectionPair.getLeft().equals(finalPos));
                        if (this.breathablePositions.size() > 1000) {
                            this.breathablePositions.clear();
                            this.watching.clear();
                            this.updateQueued = true;
                            this.sealCheckTime = SEAL_CHECK_TIME * 5;
                            setStatus(Status.AREA_TOO_LARGE);
                            this.world.getProfiler().pop();
                            return;
                        }
                        for (Direction direction : Constant.Misc.DIRECTIONS) {
                            final Pair<BlockPos, Direction> e = new Pair<>(mutable.set(pos1).move(direction).toImmutable(), direction);
                            if (!added.contains(e.getLeft()) && checked.add(e)) {
                                if (!Block.isFaceFullSquare(state.getCollisionShape(world, pos1), e.getRight())) {
                                    queue.add(e);
                                }
                            }
                        }
                    } else {
                        this.watching.add(pos1);
                    }
                }
                for (BlockPos pos2 : this.breathablePositions) {
                    ((WorldOxygenAccessor) this.world).setBreathable(pos2, true);
                }
                this.setStatus(Status.SEALED);
                this.world.getProfiler().pop();
            }
        }
    }

    @Override
    protected void tickDisabled() {
        this.disabled = true;
        if (!world.isClient) ((ServerWorldAccessor) this.world).removeSealer(this);
        for (BlockPos pos : this.breathablePositions) {
            ((WorldOxygenAccessor) world).setBreathable(pos, false);
        }
        this.breathablePositions.clear();
        this.watching.clear();
    }

    @Override
    protected int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (!this.world.isClient) {
            ((ServerWorldAccessor) this.world).removeSealer(this);
        }
        for (BlockPos pos : this.breathablePositions) {
            ((WorldOxygenAccessor) world).setBreathable(pos, false);
        }
        this.breathablePositions.clear();
        this.watching.clear();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.OXYGEN_SEALER_HANDLER, syncId, inv, this);
        return null;
    }

    public void enqueueUpdate(BlockPos pos, VoxelShape voxelShape2) {
        if ((this.watching.contains(pos) && !Block.isShapeFullCube(voxelShape2)) || (this.breathablePositions.contains(pos) && !voxelShape2.isEmpty())) {
            this.updateQueued = true;
        }
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft.machine.status.not_enough_oxygen"), Formatting.RED, StatusType.MISSING_FLUIDS),
        AREA_TOO_LARGE(new TranslatableText("ui.galacticraft.machine.status.area_too_large"), Formatting.GOLD, StatusType.OTHER),
        ALREADY_SEALED(new TranslatableText("ui.galacticraft.machine.status.already_sealed"), Formatting.GOLD, StatusType.OUTPUT_FULL),
        SEALED(new TranslatableText("ui.galacticraft.machine.status.sealed"), Formatting.GREEN, StatusType.WORKING);

        private final Text text;
        private final StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public @NotNull Text getName() {
            return text;
        }

        @Override
        public @NotNull StatusType getType() {
            return type;
        }

        @Override
        public int getIndex() {
            return ordinal();
        }
    }
}