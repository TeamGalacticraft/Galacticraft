package com.hrznstudio.galacticraft.attribute.fluid;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.hrznstudio.galacticraft.api.pipe.Pipe;
import org.jetbrains.annotations.Nullable;

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
            Pipe.FluidData data = pipe.getNetwork().insertFluid(pipe.getPos(), null, volume, simulation);
            if (data != null) {
                if (simulation == Simulation.ACTION) {
                    pipe.setFluidData(data);
                }

                return data.getFluid().getFluidKey().withAmount(volume.getAmount_F().sub(data.getFluid().getAmount_F()));
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
