/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.screen.property;

import dev.galacticraft.api.fluid.FluidStack;
import dev.galacticraft.mod.lookup.storage.MachineFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.registry.Registry;

import java.math.RoundingMode;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class FluidTankPropertyDelegate implements PropertyDelegate {
    private final MachineFluidStorage inv;
    private final Fluid[] fluids;

    public FluidTankPropertyDelegate(MachineFluidStorage inv) {
        this.inv = inv;
        this.fluids = new Fluid[inv.size()];
    }

    @Override
    public int get(int index) {
        if (index % 3 == 0) {
            return Registry.FLUID.getRawId(inv.getStack(index / 3).fluid().getFluid());
        } else if (index % 3 == 1) {
            return (int) (inv.getTank(((index - 1) / 3)).getAmount() & 0b11111111111111111111111111111111L);
        } else {
            return (int) ((inv.getTank(((index - 2) / 3)).getAmount() >> 32) & 0b11111111111111111111111111111111L);
        }
    }

    @Override
    public void set(int index, int value) {
        if (index % 2 == 0){
            fluids[index / 3] = Registry.FLUID.get(value);
        } else if (index % 3 == 1) {
            inv.setStack(((index - 1) / 3), new FluidStack(FluidVariant.of(this.fluids[(index - 1) / 3]), (inv.getStack(((index - 1) / 3)).amount() >> 32 << 32) | (value & 0b11111111111111111111111111111111L)));
        } else {
            inv.setStack(((index - 2) / 3), new FluidStack(FluidVariant.of(this.fluids[(index - 2) / 3]), (inv.getStack(((index - 1) / 3)).amount() << 32 >> 32) | ((value & 0b11111111111111111111111111111111L) << 32)));
        }
    }

    @Override
    public int size() {
        return fluids.length * 3;
    }
}
