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
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.util.FluidUtils;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenStorageModuleBlockEntity extends ConfigurableMachineBlockEntity {
    private static final FluidAmount MAX_CAPACITY = FluidAmount.ofWhole(50);
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
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.FLUID_INPUT, SideOption.FLUID_OUTPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return MachineStatus.EMPTY;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return stack -> FluidUtils.canExtractFluids(stack, Constants.Misc.LOX_ONLY);
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.trySpreadFluids(0);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        return MachineStatus.EMPTY;
    }

    @Override
    public void tickWork() {
    }

    @Override
    public int getMaxEnergy() {
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
        return Constants.Misc.LOX_ONLY;
    }
}
