package com.hrznstudio.galacticraft.component.impl;

import com.hrznstudio.galacticraft.util.OxygenUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;

public class SimpleOxygenTankComponent extends SimpleTankComponent {
    public SimpleOxygenTankComponent(int size, Fraction maxCapacity) {
        super(size, maxCapacity);
    }

    @Override
    public FluidVolume insertFluid(FluidVolume fluid, ActionType action) {
        if (OxygenUtils.isOxygen(fluid)) {
            return super.insertFluid(fluid, action);
        }
        return fluid;
    }

    @Override
    public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
        if (OxygenUtils.isOxygen(fluid)) {
            return super.insertFluid(tank, fluid, action);
        }
        return fluid;
    }

    @Override
    public void setFluid(int slot, FluidVolume stack) {
        if (OxygenUtils.isOxygen(stack)) super.setFluid(slot, stack);
    }

}
