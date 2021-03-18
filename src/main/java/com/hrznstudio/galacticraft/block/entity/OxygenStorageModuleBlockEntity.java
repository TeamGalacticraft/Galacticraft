/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.block.entity;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.AutomationType;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.api.machine.MachineStatus;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluid.GalacticraftFluids;
import com.hrznstudio.galacticraft.screen.OxygenStorageModuleScreenHandler;
import com.hrznstudio.galacticraft.util.FluidUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenStorageModuleBlockEntity extends MachineBlockEntity {
    private static final FluidAmount MAX_CAPACITY = FluidAmount.ofWhole(50);
    private static final int LOX_INPUT = 0;
    private static final int LOX_OUTPUT = 1;
    private static final ItemFilter[] SLOT_FILTERS = new ItemFilter[2];

    static {
        SLOT_FILTERS[LOX_INPUT] = stack -> FluidUtils.canExtractFluids(stack, Constants.Filter.LOX_ONLY);
        SLOT_FILTERS[LOX_OUTPUT] = stack -> FluidUtils.canInsertFluids(stack, GalacticraftFluids.LIQUID_OXYGEN);
    }

    public OxygenStorageModuleBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_STORAGE_MODULE_TYPE);
    }

    @Override
    public FluidAmount getFluidTankCapacity() {
        return MAX_CAPACITY;
    }

    @Override
    public int getFluidTankSize() {
        return 1;
    }

    @Override
    public List<AutomationType> validSideOptions() {
        return ImmutableList.of(AutomationType.NONE, AutomationType.FLUID_INPUT, AutomationType.FLUID_OUTPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return MachineStatus.NULL;
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot < 2) return SLOT_FILTERS[slot];
        return ConstantItemFilter.NOTHING;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.trySpreadFluids(0);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        return MachineStatus.NULL;
    }

    @Override
    public void tickWork() {
    }

    @Override
    public int getEnergyCapacity() {
        return 0;
    }

    @Override
    public boolean canPipeExtractFluid(int tank) {
        return true;
    }

    @Override
    public boolean canPipeInsertFluid(int tank) {
        return true;
    }

    @Override
    public FluidFilter getFilterForTank(int tank) {
        return Constants.Filter.LOX_ONLY;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) return new OxygenStorageModuleScreenHandler(syncId, player, this);
        return null;
    }
}
