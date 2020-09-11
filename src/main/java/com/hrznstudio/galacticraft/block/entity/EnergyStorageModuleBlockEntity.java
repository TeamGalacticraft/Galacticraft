/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.block.entity;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.block.machines.EnergyStorageModuleBlock;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class EnergyStorageModuleBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final int CHARGE_BATTERY_SLOT = 0;
    public static final int DRAIN_BATTERY_SLOT = 1;
    private int prevEnergy = 0;

    public EnergyStorageModuleBlockEntity() {
        super(GalacticraftBlockEntities.ENERGY_STORAGE_MODULE_TYPE);
    }

    @Override
    protected boolean canExtractEnergy() {
        return true;
    }

    @Override
    protected boolean canInsertEnergy() {
        return true;
    }

    @Override
    public int getMaxEnergy() {
        return Galacticraft.configManager.get().energyStorageModuleStorageSize();
    }

    @Override
    protected int getInventorySize() {
        return 2;
    }

    @Override
    protected int getOxygenTankSize() {
        return 0;
    }

    @Override
    protected int getFluidTankSize() {
        return 0;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return Lists.asList(SideOption.DEFAULT, SideOption.POWER_INPUT, new SideOption[]{SideOption.POWER_OUTPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT}); }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
    }

    @Override
    protected int getBatteryTransferRate() {
        return 5;
    }

    @Override
    public void tick() {
        if (world.isClient || disabled()) {
            return;
        }
        attemptChargeFromStack(DRAIN_BATTERY_SLOT);
        attemptDrainPowerToStack(CHARGE_BATTERY_SLOT);
        trySpreadEnergy();

        if (prevEnergy != getCapacitor().getCurrentEnergy()) {
            int level = (int) (((double) getCapacitor().getCurrentEnergy() / (double) getMaxEnergy()) * 8.0D);
            world.setBlockState(pos, world.getBlockState(pos).with(EnergyStorageModuleBlock.ENERGY_LEVEL, level), 0);
            prevEnergy = getCapacitor().getCurrentEnergy();
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        prevEnergy = getCapacitor().getCurrentEnergy();
    }

    @Override
    public int getEnergyUsagePerTick() {
        return 0;
    }

    @Override
    protected boolean canHopperExtractItems(int slot) {
        return true;
    }

    @Override
    protected boolean canHopperInsertItems(int slot) {
        return true;
    }

    @Override
    protected boolean canExtractOxygen(int tank) {
        return false;
    }

    @Override
    protected boolean canInsertOxygen(int tank) {
        return false;
    }

    @Override
    protected boolean canExtractFluid(int tank) {
        return false;
    }

    @Override
    protected boolean canInsertFluid(int tank) {
        return false;
    }

    @Override
    protected boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return false;
    }
}
