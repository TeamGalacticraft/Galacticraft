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
import dev.galacticraft.api.machine.MachineStatuses;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.api.screen.SimpleMachineScreenHandler;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.SolarPanel;
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotTypes;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class BasicSolarPanelBlockEntity extends MachineBlockEntity implements SolarPanel {
    public static final int CHARGE_SLOT = 0;
    private final boolean[] blockage = new boolean[9];
    private int blocked = 0;
    public long currentEnergyGeneration = 0;

    public BasicSolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.BASIC_SOLAR_PANEL, pos, state);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GalacticraftSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 62))
                .build();
    }

    @Override
    protected void tickClient(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state) {
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                this.blockage[(z + 1) * 3 + (x + 1)] = !world.isSkyVisible(pos.add(x, 2, z));
            }
        }

        double multiplier = this.blocked / 9.0;
        if (world.isRaining() || world.isThundering()) multiplier *= 0.5;
        double time = world.getTimeOfDay() % 24000;
        if (time > 6000) time = 12000L - time;
        this.currentEnergyGeneration = (long)(Galacticraft.CONFIG_MANAGER.get().solarPanelEnergyProductionRate() * (time / 6000.0) * multiplier) * 4L;
    }

    @Override
    public void tickConstant(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state) {
        world.getProfiler().push("charge");
        this.attemptDrainPowerToStack(CHARGE_SLOT);
        world.getProfiler().pop();
        this.blocked = 0;
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                //noinspection AssignmentUsedAsCondition
                if (this.blockage[(z + 1) * 3 + (x + 1)] = !world.isSkyVisible(pos.add(x, 2, z))) {
                    this.blocked++;
                }
            }
        }
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state) {
        world.getProfiler().push("transfer");
        this.trySpreadEnergy();
        world.getProfiler().pop();
        if (this.blocked == 9) return GalacticraftMachineStatus.BLOCKED;
        if (this.energyStorage().isFull()) return MachineStatuses.CAPACITOR_FULL;
        MachineStatus status = null;
        double multiplier = this.blocked / 9.0;
        if (this.blocked > 0) status = GalacticraftMachineStatus.PARTIALLY_BLOCKED;
        if (world.isRaining() || world.isThundering()) {
            if (status == null) status = GalacticraftMachineStatus.RAIN;
            multiplier *= 0.5;
        }
        if (!world.isDay()) status = GalacticraftMachineStatus.NIGHT;
        double time = world.getTimeOfDay() % 24000;
        if (time > 6000) time = 12000L - time;
        world.getProfiler().push("transaction");
        try (Transaction transaction = Transaction.openOuter()) {
            this.energyStorage().insert((long)(Galacticraft.CONFIG_MANAGER.get().solarPanelEnergyProductionRate() * (time / 6000.0) * multiplier) * 4L, transaction);
            transaction.commit();
        }
        world.getProfiler().pop();
        return status == null ? GalacticraftMachineStatus.COLLECTING : status;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) {
            return SimpleMachineScreenHandler.create(
                    syncId,
                    player,
                    this,
                    GalacticraftScreenHandlerType.BASIC_SOLAR_PANEL_HANDLER
            );
        }
        return null;
    }

    @Override
    public boolean @NotNull [] getBlockage() {
        return this.blockage;
    }

    @Override
    public boolean followsSun() {
        return false;
    }

    @Override
    public boolean nightCollection() {
        return false;
    }

    @Override
    public SolarPanelSource getSource() {
        assert this.world != null;
        if (this.world.getDimension().hasCeiling()) return SolarPanelSource.NO_LIGHT_SOURCE;
        if (this.world.isDay()) {
            if (this.world.isRaining()) return SolarPanelSource.OVERCAST;
            return SolarPanelSource.DAY;
        } else {
            return SolarPanelSource.NIGHT;
        }
    }

    @Override
    public long getCurrentEnergyGeneration() {
        return this.currentEnergyGeneration;
    }
}