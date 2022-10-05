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
import dev.galacticraft.api.machine.MachineStatus;
import dev.galacticraft.api.machine.MachineStatus.Type;
import dev.galacticraft.api.machine.storage.MachineFluidStorage;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.api.machine.storage.display.TankDisplay;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GCBlocks;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.entity.RocketEntity;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GCSlotTypes;
import dev.galacticraft.mod.screen.FuelLoaderScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FuelLoaderBlockEntity extends MachineBlockEntity {
    private static final int CHARGE_SLOT = 0;
    private static final int FUEL_INPUT_SLOT = 1;
    private static final int FUEL = 0;
    private BlockPos connectionPos = BlockPos.ZERO;
    private Direction check = null;

    public FuelLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.FUEL_LOADER_TYPE, pos, state);
    }

    @NotNull
    public BlockPos getConnectionPos() {
        return connectionPos;
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.builder()
                .addSlot(GCSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 61))
                .addSlot(GCSlotTypes.FUEL_IN, new ItemSlotDisplay(80, 61))
                .build();
    }

    @Override
    protected @NotNull MachineFluidStorage createFluidStorage() {
        return MachineFluidStorage.Builder.create()
                .addTank(GCSlotTypes.FUEL_IN, 0 , new TankDisplay(0, 0, 0))
                .build();
    }

//    @Override
//    public boolean canInsertEnergy() {
//        return true;
//    }
//
//    @Override
//    public void updateComponents() {
//        super.updateComponents();
//        this.attemptChargeFromStack(CHARGE_SLOT);
//    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.connectionPos == BlockPos.ZERO) return GCMachineStatus.NO_ROCKET;
        BlockEntity be = this.getLevel().getBlockEntity(connectionPos);
        Entity entity;
        if (be instanceof RocketLaunchPadBlockEntity launchPad) {
            if (!launchPad.hasRocket()) return GCMachineStatus.NO_ROCKET;
            entity = level.getEntity(launchPad.getRocketEntityId());
            if (!(entity instanceof RocketEntity)) return GCMachineStatus.NO_ROCKET;
        } else {
            return GCMachineStatus.NO_ROCKET;
        }
//        if (((RocketEntity) entity).getTank().getAmount().compareTo(((RocketEntity) entity).getTank().getMaxAmount_F(0)) >= 0) return GCMachineStatus.ROCKET_IS_FULL;

//        if (this.fluidInv().extractFluid(0, key -> GalacticraftTag.FUEL.contains(key.getRawFluid()), FluidVolumeUtil.EMPTY, FluidAmount.ONE, Simulation.SIMULATE).isEmpty()) return Status.NOT_ENOUGH_FUEL;
//        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        return GCMachineStatus.LOADING;
    }

    @Override
    public void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (check != null) {
            BlockPos launchPad = this.worldPosition.relative(check);
            if (level.getBlockState(launchPad).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD) {
                launchPad = launchPad.offset(RocketLaunchPadBlock.partToCenterPos(level.getBlockState(launchPad).getValue(RocketLaunchPadBlock.PART)));
                if (level.getBlockState(launchPad).getBlock() instanceof RocketLaunchPadBlock
                        && level.getBlockState(launchPad).getValue(RocketLaunchPadBlock.PART) == RocketLaunchPadBlock.Part.CENTER
                        && level.getBlockEntity(launchPad) instanceof RocketLaunchPadBlockEntity) {
                    connectionPos = launchPad;
                }
            }
            check = null;
        }

//        if (!this.isTankFull(0)) {
//            FluidExtractable extractable = FluidAttributes.EXTRACTABLE.getFirstOrNull(this.itemInv().getSlot(FUEL_INPUT_SLOT));
//            if (extractable != null) {
//                if (!extractable.attemptExtraction(key -> GalacticraftTag.FUEL.contains(key.getRawFluid()), FluidAmount.of(1, 20), Simulation.ACTION).isEmpty()) {
//                    this.fluidInv().insertFluid(0, extractable.extract(key -> GalacticraftTag.FUEL.contains(key.getRawFluid()), FluidAmount.of(1, 20)), Simulation.ACTION);
//                }
//            }
//        }
//
//        if (this.getStatus().type().isActive()) {
//            SimpleFixedFluidInv inv = ((RocketEntity) this.world.getEntityById(((RocketLaunchPadBlockEntity) world.getBlockEntity(connectionPos)).getRocketEntityId())).getTank();
//            this.fluidInv().insertFluid(0, inv.insertFluid(0, this.fluidInv().extractFluid(0, key -> GalacticraftTag.FUEL.contains(key.getRawFluid()), FluidVolumeUtil.EMPTY, FluidAmount.of(1, 50), Simulation.ACTION), Simulation.ACTION), Simulation.ACTION);
//        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (connectionPos != BlockPos.ZERO) {
            tag.putBoolean("has_connection" , true);
            tag.putLong("connection_pos", connectionPos.asLong());
        }
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.getBoolean("has_connection")) {
            connectionPos = BlockPos.of(tag.getLong("connection_pos"));
        } else {
            connectionPos = BlockPos.ZERO;
        }
    }

    public void updateConnections(Direction direction) {
        this.check = direction;
    }

    @Environment(EnvType.CLIENT)
    public void setConnectionPos(@NotNull BlockPos connectionPos) {
        this.connectionPos = connectionPos;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new FuelLoaderScreenHandler(syncId, player, this);
    }
}