/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineFluidStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.TransferType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.entity.Dockable;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.block.machine.FuelLoaderBlock;
import dev.galacticraft.mod.content.block.special.launchpad.AbstractLaunchPad;
import dev.galacticraft.mod.content.block.special.launchpad.LaunchPadBlockEntity;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.FuelLoaderMenu;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FuelLoaderBlockEntity extends MachineBlockEntity {
    public static final long TRANSFER_RATE = 500;
    public static final int CHARGE_SLOT = 0;
    public static final int FUEL_INPUT_SLOT = 1;
    public static final int FUEL_TANK = 0;
    public static final int NUM_BUCKETS = 50;
    public static final long MAX_FUEL = FluidUtil.bucketsToDroplets(NUM_BUCKETS);

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.spec(
                    ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(8, 62)
                            .capacity(1)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.ENERGY)),
                    ItemResourceSlot.builder(TransferType.PROCESSING)
                            .pos(44, 35)
                            .capacity(1)
                            .filter(ResourceFilters.canExtractFluid(GCFluids.FUEL)) // fixme: fuel tag?,
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.BUCKET))
            ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    150 * 2, // fixme
                    0
            ),
            MachineFluidStorage.spec(
                    FluidResourceSlot.builder(TransferType.INPUT)
                            .pos(69, 21)
                            .width(38)
                            .height(47)
                            .unmarked()
                            .capacity(FluidConstants.BUCKET * NUM_BUCKETS)
                            .filter(ResourceFilters.ofResource(GCFluids.FUEL)) // fixme: tag?
            )
    );

    private BlockPos connectionPos = BlockPos.ZERO;
    private int amount = 0;
    public Dockable linkedRocket = null;
    private Direction check = null;

    public FuelLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.FUEL_LOADER, pos, state, SPEC);
    }

    @NotNull
    public BlockPos getConnectionPos() {
        return this.connectionPos;
    }

    protected int calculateAmount() {
        return Mth.clamp((int) (this.fluidStorage().slot(FUEL_TANK).getAmount() * 10 / MAX_FUEL), 0, 9);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        int newAmount = this.calculateAmount();
        if (this.amount != newAmount && this.level != null && !this.level.isClientSide) {
            this.amount = newAmount;
            this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(FuelLoaderBlock.AMOUNT, this.amount), 2);
        }
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.fluidStorage().isEmpty()) return GCMachineStatuses.NOT_ENOUGH_FUEL;

        if (this.linkedRocket == null) {
            return GCMachineStatuses.NO_ROCKET;
        }

        FluidResourceSlot slot = this.fluidStorage().slot(FUEL_TANK);

        try (Transaction transaction = Transaction.openOuter()) {
            long insert = this.linkedRocket.getFuelTank().insert(FluidVariant.of(slot.getResource(), slot.getComponents()), Math.min(TRANSFER_RATE, slot.getAmount()), transaction);
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
                launchPad = launchPad.offset(AbstractLaunchPad.partToCenterPos(level.getBlockState(launchPad).getValue(AbstractLaunchPad.PART)));
                if (this.level.getBlockState(launchPad).getBlock() instanceof AbstractLaunchPad
                        && this.level.getBlockState(launchPad).getValue(AbstractLaunchPad.PART) == AbstractLaunchPad.Part.CENTER
                        && this.level.getBlockEntity(launchPad) instanceof LaunchPadBlockEntity) {
                    this.connectionPos = launchPad;
                }
            }
            this.check = null;
        }

        if (this.level.isLoaded(this.connectionPos) && this.level.getBlockEntity(this.connectionPos) instanceof LaunchPadBlockEntity launchPad) {
            this.linkedRocket = launchPad.getDockedEntity();
        } else {
            this.linkedRocket = null;
        }

        this.chargeFromSlot(CHARGE_SLOT);
        this.takeFluidFromSlot(FUEL_INPUT_SLOT, FUEL_TANK, GCFluids.FUEL);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);

        if (this.connectionPos != BlockPos.ZERO) {
            tag.putLong("connection_pos", this.connectionPos.asLong());
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);

        if (tag.contains("connection_pos", Tag.TAG_LONG)) {
            this.connectionPos = BlockPos.of(tag.getLong("connection_pos"));
        } else {
            this.connectionPos = BlockPos.ZERO;
        }
    }

    public void updateConnections(Direction direction) {
        this.check = direction;
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return new FuelLoaderMenu(syncId, (ServerPlayer) player, this);
    }
}