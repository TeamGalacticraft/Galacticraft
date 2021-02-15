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

import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.misc.Reference;
import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.api.ComponentHelper;
import io.github.cottonmc.component.fluid.TankComponent;
import net.minecraft.item.ItemStack;

public class OxygenUtils { //todo: oxygen system that has a difference between oxygen and LOX
    public static FluidAmount getOxygen(ItemStack stack) {
        TankComponent component = ComponentHelper.TANK.getComponent(stack);
        if (component != null) {
            return component.amountOf(GalacticraftTags.OXYGEN.values());
        }
        return FluidAmount.ZERO;
    }

    /**
     * @param stack  The tank to extract oxygen from
     * @param amount The amount of oxygen, to extract from the tank
     * @param action The action type
     * @return The amount of oxygen that was extracted
     */
    public static FluidAmount extractOxygen(Reference<ItemStack> stack, FluidAmount amount, ActionType action) {
        FluidExtractable extractable = FluidAttributes.EXTRACTABLE.get(stack);
        FluidAmount extracted = FluidAmount.ZERO;
        if (extractable.contains(ImmutableSet.copyOf(GalacticraftTags.OXYGEN.values()))) {
            for (int i = 0; i < extractable.getTankCount(); i++) {
                FluidVolume volume = extractable.getContents(i);
                if (!volume.isEmpty() && volume.getFluid().isIn(GalacticraftTags.OXYGEN)) {
                    FluidAmount taken = extractable.takeFluid(i, amount, action).getAmount_F();
                    extracted = extracted.add(taken);
                    amount = amount.subtract(taken);
                    if (amount.equals(FluidAmount.ZERO)) return extracted;
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
    public static FluidAmount insertOxygen(ItemStack stack, FluidAmount amount, ActionType action) {
        assert isOxygenItem(stack);
        TankComponent component = ComponentHelper.TANK.getComponent(stack);
        if (component != null) {
            return component.insertFluid(new FluidVolume(GalacticraftFluids.OXYGEN, amount), action).getAmount_F();
        }

        return amount;
    }

    public static FluidAmount getMaxOxygen(ItemStack stack) {
        assert isOxygenItem(stack);
        TankComponent component = ComponentHelper.TANK.getComponent(stack);
        FluidAmount amount = FluidAmount.ZERO;
        if (component != null) {
            for (int i = 0; i < component.getTankCount(); i++) {
                if (!component.insertFluid(i, new FluidVolume(GalacticraftFluids.OXYGEN, FluidAmount.ONE), Simulation.SIMULATE).getAmount_F().equals(FluidAmount.ONE)
                        || component.contains(ImmutableSet.copyOf(GalacticraftTags.OXYGEN.values()))) {
                    amount.add(component.getMaxAmount_F(i));
                }
            }
        }
        return amount;
    }

    public static boolean isGCOxygen(FluidKey key) {
        return key.getRawFluid() == GalacticraftFluids.OXYGEN;
    }

    public static boolean isOxygen(FluidKey key) {
        return GalacticraftTags.OXYGEN.contains(key.getRawFluid());
    }
}
