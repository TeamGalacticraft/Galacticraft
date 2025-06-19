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
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
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
import dev.galacticraft.mod.accessor.GCLevelAccessor;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.machine.SealerManager;
import dev.galacticraft.mod.screen.OxygenSealerMenu;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class OxygenSealerBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_INPUT_SLOT = 1;
    public static final int OXYGEN_TANK = 0;

    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(20);
    public static final int SEAL_CHECK_TIME = 20;
    public static final int SEALER_RANGE = 16;

    private int sealCheckTimer = SEAL_CHECK_TIME;
    private boolean isSealed = false;
    private boolean hasEnergy = false;
    private boolean hasOxygen = false;
    private boolean blocked = false;

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.spec(
                    ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(8, 62)
                            .capacity(1)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.ENERGY)),
                    ItemResourceSlot.builder(TransferType.PROCESSING) // todo: drop for decompressor?
                            .pos(31, 62)
                            .capacity(1)
                            .filter(ResourceFilters.canExtractFluid(Gases.OXYGEN))
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.OXYGEN_TANK))
            ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenSealerEnergyConsumptionRate() * 2,
                    0
            ),
            MachineFluidStorage.spec(
                    FluidResourceSlot.builder(TransferType.STRICT_INPUT)
                            .pos(31, 8)
                            .capacity(OxygenSealerBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
            )
    );

    private boolean oxygenWorld = false;


    public OxygenSealerBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.OXYGEN_SEALER, pos, state, SPEC);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        this.sealCheckTimer = SEAL_CHECK_TIME;
        this.oxygenWorld = level.getDefaultBreathable();
        if (!level.isClientSide && level != null) {
            SealerManager manager = ((GCLevelAccessor) level).getSealerManager();
            manager.addSealer(this, Objects.requireNonNull(Objects.requireNonNull(level.getServer()).getLevel(level.dimension())));
        }
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(level, pos, state, profiler);
        profiler.push("extract_resources");
        this.chargeFromSlot(CHARGE_SLOT);
        this.takeFluidFromSlot(OXYGEN_INPUT_SLOT, OXYGEN_TANK, Gases.OXYGEN);
        profiler.pop();
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        // Check if the machine has enough energy
        SealerManager manager = ((GCLevelAccessor) level).getSealerManager();
        if (!this.energyStorage().canExtract(Galacticraft.CONFIG.oxygenSealerEnergyConsumptionRate())) {
            //recalculate area to avoid issues with fake oxygen
            if (this.hasEnergy) {
                this.hasEnergy = false;
                manager.recalculateSealingStatus(pos, level);
            }
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
        this.hasEnergy = true;

        // Check if the oxygen tank is empty
        if (this.fluidStorage().slot(OXYGEN_TANK).isEmpty()) {
            //recalculate area to avoid issues with fake oxygen
            if (this.hasOxygen) {
                this.hasOxygen = false;
                manager.recalculateSealingStatus(pos, level);
            }
            return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
        }
        this.hasOxygen = true;

        if (!level.getBlockState(pos.offset(0, 1, 0)).isAir()) {
            //recalculate area to avoid issues with fake oxygen
            if (!this.blocked) {
                this.blocked = true;
                manager.recalculateSealingStatus(pos, level);
            }
            return GCMachineStatuses.BLOCKED;
        }
        this.blocked = false;

        // Update sealing status periodically
        if (this.sealCheckTimer-- <= 0) {
            this.sealCheckTimer = SEAL_CHECK_TIME;
            manager.recalculateSealingStatus(pos, level);
        }

        // Consume oxygen if sealed
        if (this.hasOxygen) {
            this.consumeOxygen();
        }
        if (this.hasEnergy) {
            this.consumeEnergy();
        }

        if (this.isSealed) {
            return GCMachineStatuses.SEALED;
        } else {
            return GCMachineStatuses.AREA_TOO_LARGE;
        }
    }

    @Override
    protected void tickDisabled(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickDisabled(level, pos, state, profiler);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide) {
            SealerManager manager = ((GCLevelAccessor) this.level).getSealerManager();
            manager.removeSealer(this, (ServerLevel) this.level);
        }
    }

    @Nullable
    @Override
    public MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return new OxygenSealerMenu(syncId, player, this);
    }

    private void consumeOxygen() {
        this.fluidStorage().slot(OXYGEN_TANK).extract(Galacticraft.CONFIG.oxygenSealerOxygenConsumptionRate());
    }

    private void consumeEnergy() {
        this.energyStorage().extract(Galacticraft.CONFIG.oxygenSealerEnergyConsumptionRate());
    }

    public boolean isSealed() {
        return this.isSealed;
    }

    public void setSealed(boolean sealed) {
        this.isSealed = sealed;
    }

    public boolean hasEnergy() {
        return this.hasEnergy;
    }

    public boolean hasOxygen() {
        return this.hasEnergy;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public int getSealTickTime() {
        return this.sealCheckTimer;
    }
}
