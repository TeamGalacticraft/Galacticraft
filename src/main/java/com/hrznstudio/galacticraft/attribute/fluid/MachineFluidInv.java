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

import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.hrznstudio.galacticraft.accessor.DefaultedListAccessor;
import com.hrznstudio.galacticraft.attribute.Automatable;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import com.hrznstudio.galacticraft.screen.tank.Tank;
import com.hrznstudio.galacticraft.util.collection.ResizableDefaultedList;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class MachineFluidInv extends SimpleFixedFluidInv implements Automatable {
    private final List<SlotType> slotTypes = new ArrayList<>();
    private final List<FluidFilter> filters = new ArrayList<>();
    private final List<Vec3i> positions = new ArrayList<>();

    public MachineFluidInv(FluidAmount tankCapacity) {
        super(0, tankCapacity);
        ((DefaultedListAccessor<FluidVolume>)this).setDefaultedList_gcr(new ResizableDefaultedList<>(new ArrayList<>(), FluidVolumeUtil.EMPTY));
    }

    @Override
    public FluidFilter getFilterForTank(int slot) {
        if (slot < 0 || slot >= this.getTankCount()) return ConstantFluidFilter.NOTHING;
        return this.filters.get(slot);
    }

    @Override
    public boolean isFluidValidForTank(int tank, FluidKey fluid) {
        return this.getFilterForTank(tank).matches(fluid);
    }

    public void addSlot(int index, SlotType type, FluidFilter filter, int x, int y, int s) {
        assert this.getTankCount() == index;
        this.positions.add(index, new Vec3i(x, y, s));
        this.slotTypes.add(index, type);
        this.filters.add(index, filter);
        this.tanks.add(index, FluidVolumeUtil.EMPTY);
    }

    public void addSlot(int index, SlotType type, FluidFilter filter) {
        assert this.getTankCount() == index;
        this.positions.add(index, null);
        this.slotTypes.add(index, type);
        this.filters.add(index, filter);
        this.tanks.add(index, FluidVolumeUtil.EMPTY);
    }

    public void createTanks(MachineScreenHandler<?> screenHandler) {
        Vec3i vec;
        for (int i = 0; i < getTankCount(); i++) {
            vec = positions.get(i);
            if (vec != null) {
                screenHandler.addTank(new Tank(i, screenHandler.machine.getFluidTank(), vec.getX(), vec.getY(), vec.getZ()));
            }
        }
    }

    @Override
    public List<SlotType> getTypes() {
        return this.slotTypes;
    }
}