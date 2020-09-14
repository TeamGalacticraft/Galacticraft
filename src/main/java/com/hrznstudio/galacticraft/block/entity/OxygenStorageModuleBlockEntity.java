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

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.OxygenUtils;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenStorageModuleBlockEntity extends ConfigurableMachineBlockEntity {
    public OxygenStorageModuleBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_STORAGE_MODULE_TYPE);
    }

    @Override
    public Fraction getOxygenTankMaxCapacity() {
        return Fraction.of(1, 100).multiply(Fraction.ofWhole(30_000));
    }

    @Override
    public int getInventorySize() {
        return 0;
    }

    @Override
    public int getOxygenTankSize() {
        return 1;
    }

    @Override
    public int getFluidTankSize() {
        return 0;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.OXYGEN_INPUT, SideOption.OXYGEN_OUTPUT);
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }

    @Override
    public boolean canInsertEnergy() {
        return false;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return OxygenUtils::isOxygenItem;
    }

    @Override
    protected int getBatteryTransferRate() {
        return 0;
    }

    @Override
    public void tick() {
        //todo spread oxygen
    }

    @Override
    public int getMaxEnergy() {
        return 0;
    }

    @Override
    public int getEnergyUsagePerTick() {
        return 0;
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return false;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return false;
    }

    @Override
    public boolean canExtractOxygen(int tank) {
        return true;
    }

    @Override
    public boolean canInsertOxygen(int tank) {
        return true;
    }

    @Override
    public boolean canExtractFluid(int tank) {
        return false;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return false;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return false;
    }
}
