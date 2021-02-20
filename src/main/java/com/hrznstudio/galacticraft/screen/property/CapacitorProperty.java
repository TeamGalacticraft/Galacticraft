package com.hrznstudio.galacticraft.screen.property;

import com.hrznstudio.galacticraft.energy.api.Capacitor;
import net.minecraft.screen.Property;

public class CapacitorProperty extends Property {
    private final Capacitor capacitor;

    public CapacitorProperty(Capacitor capacitor) {
        this.capacitor = capacitor;
    }

    public Capacitor getCapacitor() {
        return capacitor;
    }

    @Override
    public int get() {
        return this.capacitor.getEnergy();
    }

    @Override
    public void set(int value) {
        this.capacitor.setEnergy(value);
    }
}
