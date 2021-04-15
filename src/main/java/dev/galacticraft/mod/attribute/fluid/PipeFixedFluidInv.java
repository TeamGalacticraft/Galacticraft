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

package dev.galacticraft.mod.attribute.fluid;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.galacticraft.mod.api.pipe.Pipe;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class PipeFixedFluidInv implements FixedFluidInv {
    private final Pipe pipe;

    public PipeFixedFluidInv(Pipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public FluidAmount getMaxAmount_F(int tank) {
        return FluidAmount.of(1, 10);
    }

    @Override
    public int getTankCount() {
        return 1;
    }

    @Override
    public FluidVolume getInvFluid(int i) {
        return FluidVolumeUtil.EMPTY;
    }

    @Override
    public boolean isFluidValidForTank(int i, FluidKey fluidKey) {
        if (pipe.getFluidData() == Pipe.FluidData.EMPTY) {
            Pipe.FluidData data = pipe.getNetwork().insertFluid(pipe.getPos(), null, fluidKey.withAmount(FluidAmount.ONE), Simulation.SIMULATE);
            return data != null;
        }
        return false;
    }

    @Override
    public boolean setInvFluid(int i, FluidVolume fluidVolume, Simulation simulation) {
        if (this.insertFluid(i, fluidVolume, Simulation.SIMULATE).isEmpty()) {
            assert simulation != Simulation.ACTION || this.insertFluid(i, fluidVolume, Simulation.ACTION).isEmpty();
            return true;
        }
        return false;
    }

    @Override
    public FluidVolume insertFluid(int tank, FluidVolume volume, Simulation simulation) {
        if (pipe.getFluidData() == Pipe.FluidData.EMPTY) {
            Pipe.FluidData data = pipe.getNetwork().insertFluid(pipe.getPos(), null, volume.copy(), simulation);
            if (data != null && data != Pipe.FluidData.EMPTY) {
                if (simulation == Simulation.ACTION) {
                    pipe.setFluidData(data);
                }

                return volume.getFluidKey().withAmount(volume.getAmount_F().sub(data.getFluid().getAmount_F()));
            }
        }
        return volume;
    }

    @Override
    public FluidVolume extractFluid(int tank, @Nullable FluidFilter filter, FluidVolume mergeWith, FluidAmount maxAmount, Simulation simulation) {
        if (mergeWith == null) {
            mergeWith = FluidVolumeUtil.EMPTY;
        }
        return mergeWith;
    }
}
