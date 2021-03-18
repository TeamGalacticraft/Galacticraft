package com.hrznstudio.galacticraft.attribute.energy;

import alexiil.mc.lib.attributes.ListenerRemovalToken;
import alexiil.mc.lib.attributes.ListenerToken;
import com.hrznstudio.galacticraft.energy.api.Capacitor;
import com.hrznstudio.galacticraft.energy.api.EnergyType;
import com.hrznstudio.galacticraft.energy.impl.DefaultEnergyType;
import org.jetbrains.annotations.Nullable;

public class InfiniteCapacitor implements Capacitor {
    @Override
    public void setEnergy(int amount) {
    }

    @Override
    public EnergyType getEnergyType() {
        return DefaultEnergyType.INSTANCE;
    }

    @Override
    public int getEnergy() {
        return 1_000_000;
    }

    @Override
    public int getMaxCapacity() {
        return 1_000_000;
    }

    @Override
    public @Nullable ListenerToken addListener(CapacitorListener listener, ListenerRemovalToken removalToken) {
        return null;
    }
}
