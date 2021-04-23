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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import net.minecraft.fluid.Fluid;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.registry.Registry;

import java.math.RoundingMode;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class FluidTankPropertyDelegate implements PropertyDelegate {
    private final FixedFluidInv inv;
    private final Fluid[] fluids;

    public FluidTankPropertyDelegate(FixedFluidInv inv) {
        this.inv = inv;
        this.fluids = new Fluid[inv.getTankCount()];
    }

    @Override
    public int get(int index) {
        if (index % 2 == 0) {
            return Registry.FLUID.getRawId(inv.getInvFluid(index / 2).getRawFluid());
        } else {
            return inv.getInvFluid(((index + 1) / 2) - 1).amount().asInt(1000, RoundingMode.HALF_DOWN);
        }
    }

    @Override
    public void set(int index, int value) {
        if (index % 2 == 0) {
            fluids[index / 2] = Registry.FLUID.get(value);
        } else {
            inv.setInvFluid(((index + 1) / 2) - 1, FluidKeys.get(fluids[((index + 1) / 2) - 1]).withAmount(FluidAmount.of(value, 1000)), Simulation.ACTION);
        }
    }

    @Override
    public int size() {
        return fluids.length * 2;
    }
}
