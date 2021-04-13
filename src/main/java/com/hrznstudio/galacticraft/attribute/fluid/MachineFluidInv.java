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

package com.hrznstudio.galacticraft.attribute.fluid;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidTransferable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.GroupedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.GroupedFluidInvFixedWrapper;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.misc.Saveable;
import com.hrznstudio.galacticraft.attribute.Automatable;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import com.hrznstudio.galacticraft.screen.tank.Tank;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class MachineFluidInv implements FixedFluidInv, FluidTransferable, Saveable, Automatable {
    private final List<SlotType> slotTypes = new ArrayList<>();
    private final List<FluidFilter> filters = new ArrayList<>();
    private final List<Vec3i> positions = new ArrayList<>();

    private final List<FluidVolume> tank = new ArrayList<>();
    private final FluidAmount capacity;

    private final GroupedFluidInv groupedInv = new GroupedFluidInvFixedWrapper(this);
    private boolean modifiable = true;

    public MachineFluidInv(FluidAmount capacity) {
        this.capacity = capacity;
    }

    @Override
    public FluidFilter getFilterForTank(int slot) {
        if (slot < 0 || slot >= this.getTankCount()) return ConstantFluidFilter.NOTHING;
        return this.filters.get(slot);
    }

    @Override
    public int getTankCount() {
        return this.getTanks().size();
    }

    @Override
    public FluidVolume getInvFluid(int tank) {
        return this.getTanks().get(tank);
    }

    protected List<FluidVolume> getTanks() {
        this.modifiable = false;
        return this.tank;
    }

    @Override
    public boolean isFluidValidForTank(int tank, FluidKey fluid) {
        return this.getFilterForTank(tank).matches(fluid);
    }

    @Override
    public boolean setInvFluid(int tank, FluidVolume to, Simulation simulation) {
        if (this.isFluidValidForTank(tank, to.getFluidKey()) && !to.amount().isGreaterThan(this.getMaxAmount_F(tank))) {
            if (simulation.isAction()) this.getTanks().set(tank, to);
            return true;
        }
        return false;
    }

    public void addSlot(int index, SlotType type, FluidFilter filter, int x, int y, int s) {
        assert modifiable;
        assert this.getTankCount() == index;
        this.positions.add(index, new Vec3i(x, y, s));
        this.slotTypes.add(index, type);
        this.filters.add(index, filter.or(FluidKey::isEmpty));
        this.getTanks().add(index, FluidVolumeUtil.EMPTY);
        this.modifiable = true;
    }

    public void addSlot(int index, SlotType type, FluidFilter filter) {
        assert modifiable;
        assert this.getTankCount() == index;
        this.positions.add(index, null);
        this.slotTypes.add(index, type);
        this.filters.add(index, filter.or(FluidKey::isEmpty));
        this.getTanks().add(index, FluidVolumeUtil.EMPTY);
        this.modifiable = true;
    }

    public void createTanks(MachineScreenHandler<?> screenHandler) {
        Vec3i vec;
        for (int i = 0; i < getTankCount(); i++) {
            vec = positions.get(i);
            if (vec != null) {
                screenHandler.addTank(new Tank(i, screenHandler.machine.getFluidInv(), vec.getX(), vec.getY(), vec.getZ()));
            }
        }
    }

    @Override
    public List<SlotType> getTypes() {
        return this.slotTypes;
    }

    @Override
    public FluidAmount getMaxAmount_F(int tank) {
        return this.capacity;
    }

    @Override
    public FluidVolume attemptExtraction(FluidFilter filter, FluidAmount maxAmount, Simulation simulation) {
        return this.getGroupedInv().attemptExtraction(filter, maxAmount, simulation);
    }

    @Override
    public GroupedFluidInv getGroupedInv() {
        return this.groupedInv;
    }

    @Override
    public FluidVolume attemptInsertion(FluidVolume fluid, Simulation simulation) {
        return this.getGroupedInv().attemptInsertion(fluid, simulation);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        ListTag tanksTag = new ListTag();
        for (FluidVolume volume : this.getTanks()) {
            tanksTag.add(volume.toTag());
        }
        tag.put("tanks", tanksTag);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        ListTag tanksTag = tag.getList("tanks", new CompoundTag().getType());
        for (int i = 0; i < tanksTag.size() && i < this.getTanks().size(); i++) {
            this.getTanks().set(i, FluidVolume.fromTag(tanksTag.getCompound(i)));
        }
        for (int i = tanksTag.size(); i < this.getTanks().size(); i++) {
            this.getTanks().set(i, FluidVolumeUtil.EMPTY);
        }
    }

    @Override
    public FluidVolume attemptAnyExtraction(FluidAmount maxAmount, Simulation simulation) {
        return this.getGroupedInv().attemptAnyExtraction(maxAmount, simulation);
    }
}