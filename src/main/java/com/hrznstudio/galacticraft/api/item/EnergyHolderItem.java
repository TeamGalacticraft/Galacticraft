/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.api.item;

import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface EnergyHolderItem {

    /**
     * @return The maximum energy that the given {@link ItemStack} can store.
     */
    int getMaxEnergy(ItemStack battery);

    /**
     * If this is overridden to return true, energy will always be extracted and inserting of energy will be denied.
     *
     * @return If the battery is infinite.
     */
    default boolean isInfinite() {
        return false;
    }

    /**
     * Attempt to extract energy from this battery.
     *
     * @param battery        The battery that energy should be removed from.
     * @param energyToRemove The amount of energy to remove from the battery.
     * @return The amount of energy that could not be removed from this battery
     */
    default int extract(ItemStack battery, int energyToRemove) {
        if (isInfinite()) {
            // Allow extracting any amount.
            return 0;
        }

        int batteryEnergy = GalacticraftEnergy.getBatteryEnergy(battery);
        if (energyToRemove > batteryEnergy) {
            GalacticraftEnergy.decrementEnergy(battery, batteryEnergy);
            return energyToRemove - batteryEnergy;
        }

        GalacticraftEnergy.decrementEnergy(battery, energyToRemove);
        return 0;
    }

    /**
     * Attempt to insert energy into this battery.
     *
     * @param battery     The battery that energy should be added to.
     * @param energyToAdd The amount of energy to add to the battery.
     * @return The amount of energy that could not be added from this battery
     */
    default int insert(ItemStack battery, int energyToAdd) {
        if (isInfinite()) {
            // Don't insert any energy.
            return energyToAdd;
        }

        int batteryEnergy = GalacticraftEnergy.getBatteryEnergy(battery);
        int maxEnergy = GalacticraftEnergy.getMaxBatteryEnergy(battery);

        if (batteryEnergy + energyToAdd > maxEnergy) {
            // Could not add all the energy to the battery. Add what we can and return the leftover.

            GalacticraftEnergy.setEnergy(battery, maxEnergy);
            return (batteryEnergy + energyToAdd) - maxEnergy;
        }

        GalacticraftEnergy.incrementEnergy(battery, energyToAdd);
        return 0;
    }
}