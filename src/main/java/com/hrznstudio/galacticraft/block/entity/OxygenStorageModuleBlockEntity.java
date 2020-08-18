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

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.block.machines.OxygenStorageModuleBlock;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.util.OxygenUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenStorageModuleBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {
    private final TankComponent tank = new SimpleTankComponent(1, Fraction.of(1, 100).multiply(Fraction.ofWhole(30_000))) {
        @Override
        public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
            if (fluid.getFluid().isIn(GalacticraftTags.OXYGEN)) {
                return super.insertFluid(tank, fluid, action);
            } else {
                return fluid;
            }
        }

        @Override
        public void setFluid(int slot, FluidVolume stack) {
            if (stack.isEmpty() || stack.getFluid().isIn(GalacticraftTags.OXYGEN)) {
                super.setFluid(slot, stack);
            }
        }
    };

    private Fraction prevOxygen = Fraction.ZERO;

    public OxygenStorageModuleBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_STORAGE_MODULE_TYPE);
    }

    @Override
    protected int getInventorySize() {
        return 0;
    }

    @Override
    protected boolean canExtractEnergy() {
        return false;
    }

    @Override
    protected boolean canInsertEnergy() {
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
    public int getMaxEnergy() {
        return 0;
    }

    public TankComponent getTank() {
        return tank;
    }

    @Override
    public void tick() {
        if (world.isClient || disabled()) {
            return;
        }

        if (prevOxygen.compareTo(getTank().getContents(0).getAmount()) != 0) {
            int level = (int) ((getTank().getContents(0).getAmount().doubleValue() / getTank().getMaxCapacity(0).doubleValue()) * 8.0D);
            world.setBlockState(pos, world.getBlockState(pos).with(OxygenStorageModuleBlock.OXYGEN_LEVEL, level), 0);
            prevOxygen = getTank().getContents(0).getAmount();
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        getTank().fromTag(tag);
        prevOxygen = getTank().getContents(0).getAmount();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        getTank().toTag(tag);
        return tag;
    }

    @Override
    public int getEnergyUsagePerTick() {
        return 0;
    }
}
