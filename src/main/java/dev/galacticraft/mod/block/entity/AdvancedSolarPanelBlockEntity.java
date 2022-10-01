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

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.block.face.BlockFace;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.screen.SimpleMachineScreenHandler;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.slot.display.ItemSlotDisplay;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.SolarPanel;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotGroups;
import dev.galacticraft.mod.screen.GCScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class AdvancedSolarPanelBlockEntity extends MachineBlockEntity implements SolarPanel {
    public static final int CHARGE_SLOT = 0;
    private int blocked = 0;
    public long currentEnergyGeneration = 0;
    private final boolean[] blockage = new boolean[9];

    public AdvancedSolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ADVANCED_SOLAR_PANEL, pos, state);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GalacticraftSlotGroups.ENERGY_DRAIN, Constant.Filter.Item.CAN_EXTRACT_ENERGY, true, ItemSlotDisplay.create(8, 62))
                .build();
    }

    @Override
    protected void tickClient(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state) {
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                this.blockage[(z + 1) * 3 + (x + 1)] = !world.canSeeSky(pos.offset(x, 2, z));
            }
        }

        double multiplier = this.blocked / 9.0;
        if (world.isRaining() || world.isThundering()) multiplier *= 0.5;
        double time = world.getDayTime() % 24000;
        if (time > 6000) time = 12000L - time;
        this.currentEnergyGeneration = (long)(Galacticraft.CONFIG_MANAGER.get().solarPanelEnergyProductionRate() * (time / 6000.0) * multiplier) * 4L;
    }

    @Override
    public void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        profiler.push("charge");
        this.attemptDrainPowerToStack(CHARGE_SLOT);
        profiler.popPush("blockage");
        this.blocked = 0;
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                //noinspection AssignmentUsedAsCondition
                if (this.blockage[(z + 1) * 3 + (x + 1)] = !world.canSeeSky(pos.offset(x, 2, z))) {
                    this.blocked++;
                }
            }
        }
        profiler.pop();
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        profiler.push("push_energy");
        this.trySpreadEnergy(world, state);
        profiler.pop();
        if (this.blocked >= 9) return GCMachineStatus.BLOCKED;
        if (this.energyStorage().isFull()) return MachineStatuses.CAPACITOR_FULL;
        MachineStatus status = null;
        double multiplier = blocked == 0 ? 1 : this.blocked / 9.0;
        if (this.blocked > 1) status = GCMachineStatus.PARTIALLY_BLOCKED;
        if (world.isRaining() || world.isThundering()) {
            if (status == null) status = GCMachineStatus.RAIN;
            multiplier *= 0.5;
        }
        if (!world.isDay()) status = GCMachineStatus.NIGHT;
        double time = world.getDayTime() % 24000;
        if (time > 6000) time = 12000L - time;

        profiler.push("transaction");
        try (Transaction transaction = Transaction.openOuter()) {
            this.energyStorage().insert((long)(Galacticraft.CONFIG_MANAGER.get().solarPanelEnergyProductionRate() * (time / 6000.0) * multiplier) * 4L, transaction);
            transaction.commit();
        }
        profiler.pop();
        return status == null ? GCMachineStatus.COLLECTING : status;
    }

    @Override
    public boolean isFaceLocked(BlockFace face) {
        return face == BlockFace.TOP;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return SimpleMachineScreenHandler.create(
                    syncId,
                    player,
                    this,
                    GCScreenHandlerType.ADVANCED_SOLAR_PANEL_HANDLER
            );
        }
        return null;
    }

    @Override
    public long getEnergyCapacity() {
        return Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize();
    }

    @Override
    public boolean @NotNull [] getBlockage() {
        return this.blockage;
    }

    @Override
    public boolean followsSun() {
        return true;
    }

    @Override
    public boolean nightCollection() {
        return false;
    }

    @Override
    public SolarPanelSource getSource() {
        return this.level.dimensionType().hasCeiling() ? SolarPanelSource.NO_LIGHT_SOURCE : this.level.isDay() ? this.level.isRaining() || this.level.isThundering() ? SolarPanelSource.OVERCAST : SolarPanelSource.DAY : SolarPanelSource.NIGHT;
    }

    @Override
    public long getCurrentEnergyGeneration() {
        return this.currentEnergyGeneration;
    }
}