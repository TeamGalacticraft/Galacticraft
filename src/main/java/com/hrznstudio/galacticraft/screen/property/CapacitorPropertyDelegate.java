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
