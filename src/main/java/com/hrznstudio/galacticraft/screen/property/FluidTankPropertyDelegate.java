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

package com.hrznstudio.galacticraft.screen.property;

import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.registry.Registry;

public class FluidTankPropertyDelegate implements PropertyDelegate {
    private final TankComponent component;
    private final Fluid[] fluids;

    public FluidTankPropertyDelegate(TankComponent component) {
        this.component = component;
        this.fluids = new Fluid[component.getTanks()];
    }

    @Override
    public int get(int index) {
        if (index % 2 == 0) {
            return Registry.FLUID.getRawId(component.getContents(index / 2).getFluid());
        } else {
            return (int) (component.getContents(((index + 1) / 2) - 1).getAmount().doubleValue() * 1000.0D);
        }
    }

    @Override
    public void set(int index, int value) {
        if (index % 2 == 0) {
            fluids[index / 2] = Registry.FLUID.get(value);
        } else {
            component.setFluid(((index + 1) / 2) - 1, new FluidVolume(fluids[((index + 1) / 2) - 1], Fraction.of(value, 1000)));
        }
    }

    @Override
    public int size() {
        return fluids.length * 2;
    }
}
