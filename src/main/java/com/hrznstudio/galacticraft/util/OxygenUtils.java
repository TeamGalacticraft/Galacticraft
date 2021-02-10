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

package com.hrznstudio.galacticraft.util;

import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.api.ComponentHelper;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.item.ItemStack;

public class OxygenUtils {
    public static Fraction getOxygen(ItemStack stack) {
        assert isOxygenItem(stack);
        TankComponent component = ComponentHelper.TANK.getComponent(stack);
        if (component != null) {
            return component.amountOf(ImmutableSet.copyOf(GalacticraftTags.OXYGEN.values()));
        }
        return Fraction.ZERO;
    }

    /**
     * @param stack  The tank to extract oxygen from
     * @param amount The amount of oxygen, to extract from the tank
     * @param action The action type
     * @return The amount of oxygen that was extracted
     */
    public static Fraction extractOxygen(ItemStack stack, Fraction amount, ActionType action) {
        assert isOxygenItem(stack);
        TankComponent component = ComponentHelper.TANK.getComponent(stack);
        Fraction extracted = Fraction.ZERO;
        if (component != null) {
            if (component.contains(ImmutableSet.copyOf(GalacticraftTags.OXYGEN.values()))) {
                for (int i = 0; i < component.getTanks(); i++) {
                    FluidVolume volume = component.getContents(i);
                    if (!volume.isEmpty() && volume.getFluid().isIn(GalacticraftTags.OXYGEN)) {
                        Fraction taken = component.takeFluid(i, amount, action).getAmount();
                        extracted = extracted.add(taken);
                        amount = amount.subtract(taken);
                        if (amount.equals(Fraction.ZERO)) return extracted;
                    }
                }
            }
        }
        return extracted;
    }

    /**
     * @param stack  The tank item to insert energy into
     * @param amount The amount of oxygen to inset into the battery
     * @param action The action
     * @return The amount of oxygen that could not be inserted
     */
    public static Fraction insertOxygen(ItemStack stack, Fraction amount, ActionType action) {
        assert isOxygenItem(stack);
        TankComponent component = ComponentHelper.TANK.getComponent(stack);
        if (component != null) {
            return component.insertFluid(new FluidVolume(GalacticraftFluids.OXYGEN, amount), action).getAmount();
        }

        return amount;
    }

    public static Fraction getMaxOxygen(ItemStack stack) {
        assert isOxygenItem(stack);
        TankComponent component = ComponentHelper.TANK.getComponent(stack);
        Fraction amount = Fraction.ZERO;
        if (component != null) {
            for (int i = 0; i < component.getTanks(); i++) {
                if (!component.insertFluid(i, new FluidVolume(GalacticraftFluids.OXYGEN, Fraction.ONE), ActionType.TEST).getAmount().equals(Fraction.ONE)
                        || component.contains(ImmutableSet.copyOf(GalacticraftTags.OXYGEN.values()))) {
                    amount.add(component.getMaxCapacity(i));
                }
            }
        }
        return amount;
    }

    public static boolean isOxygenItem(ItemStack stack) {
        TankComponent component = ComponentHelper.TANK.getComponent(stack);
        if (component != null) {
            for (int i = 0; i < component.getTanks(); i++) {
                FluidVolume volume = component.getContents(i);
                if (volume.isEmpty() || volume.getFluid().isIn(GalacticraftTags.OXYGEN)) return true;
            }
        }

        return false;
    }
}
