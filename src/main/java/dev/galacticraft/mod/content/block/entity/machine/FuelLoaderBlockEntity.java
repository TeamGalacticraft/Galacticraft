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

import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.FuelLoaderMenu;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FuelLoaderBlockEntity extends MachineBlockEntity {
    public static final long TRANSFER_RATE = 500;
    public static final int CHARGE_SLOT = 0;
    public static final int FUEL_INPUT_SLOT = 1;
    public static final int FUEL_TANK = 0;
    private BlockPos connectionPos = BlockPos.ZERO;
    public Rocket linkedRocket = null;
    private Direction check = null;

    public FuelLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.FUEL_LOADER, pos, state);
    }

    @NotNull
    public BlockPos getConnectionPos() {
        return this.connectionPos;
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.fluidStorage().isEmpty()) return GCMachineStatuses.NOT_ENOUGH_FUEL;

        if (this.linkedRocket == null) {
            return GCMachineStatuses.NO_ROCKET;
        }

        FluidResourceSlot slot = this.fluidStorage().getSlot(FUEL_TANK);

        try (Transaction transaction = Transaction.openOuter()) {
            long insert = this.linkedRocket.getFuelTank().insert(FluidVariant.of(slot.getResource(), slot.getTag()), Math.min(TRANSFER_RATE, slot.getAmount()), transaction);
            if (insert > 0) {
                slot.extract(insert);
                transaction.commit();
                return GCMachineStatuses.LOADING;
            }
            return GCMachineStatuses.FUEL_TANK_FULL;
        }
    }

    @Override
    public void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.check != null) {
            BlockPos launchPad = this.worldPosition.relative(this.check);
            if (this.level.getBlockState(launchPad).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD) {
                launchPad = launchPad.offset(RocketLaunchPadBlock.partToCenterPos(level.getBlockState(launchPad).getValue(RocketLaunchPadBlock.PART)));
                if (this.level.getBlockState(launchPad).getBlock() instanceof RocketLaunchPadBlock
                        && this.level.getBlockState(launchPad).getValue(RocketLaunchPadBlock.PART) == RocketLaunchPadBlock.Part.CENTER
                        && this.level.getBlockEntity(launchPad) instanceof RocketLaunchPadBlockEntity) {
                    this.connectionPos = launchPad;
                }
            }
            this.check = null;
        }

        if (this.level.isLoaded(this.connectionPos) && this.level.getBlockEntity(this.connectionPos) instanceof RocketLaunchPadBlockEntity launchPad) {
            this.linkedRocket = launchPad.getRocket();
        } else {
            this.linkedRocket = null;
        }

        this.chargeFromStack(CHARGE_SLOT);
        this.takeFluidFromStack(FUEL_INPUT_SLOT, FUEL_TANK, GCFluids.FUEL);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (this.connectionPos != BlockPos.ZERO) {
            tag.putBoolean("has_connection" , true);
            tag.putLong("connection_pos", this.connectionPos.asLong());
        }
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.getBoolean("has_connection")) {
            this.connectionPos = BlockPos.of(tag.getLong("connection_pos"));
        } else {
            this.connectionPos = BlockPos.ZERO;
        }
    }

    public void updateConnections(Direction direction) {
        this.check = direction;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new FuelLoaderMenu(syncId, (ServerPlayer) player, this);
    }
}