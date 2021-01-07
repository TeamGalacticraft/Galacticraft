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
 */

package com.hrznstudio.galacticraft.component;

import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubTankComponent implements TankComponent {
    private final TankComponent component;
    private final int[] slot;

    public SubTankComponent(TankComponent component, int[] slot) {
        this.component = component;
        this.slot = slot;
    }
    @Override
    public int getTanks() {
        return slot.length;
    }

    @Override
    public Fraction getMaxCapacity(int tank) {
        return component.getMaxCapacity(this.slot[tank]);
    }

    @Override
    public boolean isEmpty() {
        for (int j : this.slot) {
            if (!component.getContents(j).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public List<FluidVolume> getAllContents() {
        List<FluidVolume> list = new ArrayList<>(slot.length);
        for (int j : slot) {
            list.add(component.getContents(j));
        }
        return list;
    }

    @Override
    public FluidVolume getContents(int tank) {
        return component.getContents(this.slot[tank]);
    }

    @Override
    public boolean canInsert(int tank) {
        return component.canInsert(this.slot[tank]);
    }

    @Override
    public boolean canExtract(int tank) {
        return component.canExtract(this.slot[tank]);
    }

    @Override
    public FluidVolume takeFluid(int tank, Fraction amount, ActionType action) {
        return component.takeFluid(this.slot[tank], amount, action);
    }

    @Override
    public FluidVolume removeFluid(int tank, ActionType action) {
        return component.removeFluid(this.slot[tank], action);
    }

    @Override
    public void setFluid(int tank, FluidVolume fluid) {
        component.setFluid(this.slot[tank], fluid);
    }

    @Override
    public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
        return component.insertFluid(this.slot[tank], fluid, action);
    }

    @Override
    public FluidVolume insertFluid(FluidVolume fluid, ActionType action) {
        for (int i : this.slot) {
            fluid = component.insertFluid(i, fluid, action);
            if (fluid.isEmpty()) break;
        }
        return fluid;
    }

    @Override
    public void clear() {
        for (int i : slot) {
            component.setFluid(i, FluidVolume.EMPTY);
        }
    }

    @Override
    public boolean isAcceptableFluid(int tank) {
        return component.isAcceptableFluid(this.slot[tank]);
    }

    @Override
    public void onChanged() {
    }

    @Override
    public void listen(@NotNull Runnable listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Runnable> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        throw new UnsupportedOperationException();
    }
}
