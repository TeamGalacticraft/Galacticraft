package com.hrznstudio.galacticraft.screen.property;

import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.PropertyDelegate;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;

public class CapacitorPropertyDelegate implements PropertyDelegate {
    private final EnergyStorage storage;
    private final int[] ints = new int[2];

    public CapacitorPropertyDelegate(EnergyStorage storage) {
        this.storage = storage;
    }

    public static void addTo(MachineScreenHandler<?> handler, EnergyStorage storage) {
        CapacitorPropertyDelegate delegate = new CapacitorPropertyDelegate(storage);
        handler.addProperty(Property.create(delegate, 0));
        handler.addProperty(Property.create(delegate, 1));
    }

    @Override
    public int get(int index) {
        long bits = Double.doubleToLongBits(storage.getStored(EnergySide.UNKNOWN));
        if (index == 0) return (int)(bits >> 32);
        return (int)bits;
    }

    @Override
    public void set(int index, int value) {
        ints[index] = value;
        storage.setStored(Double.longBitsToDouble((long)ints[0] << 32 | ints[1] & 0x1FFFFFFF));
    }

    @Override
    public int size() {
        return 2;
    }
}
