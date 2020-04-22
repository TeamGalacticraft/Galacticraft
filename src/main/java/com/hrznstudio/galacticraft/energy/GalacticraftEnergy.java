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

package com.hrznstudio.galacticraft.energy;

import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.item.EnergyHolderItem;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import io.github.cottonmc.energy.CottonEnergy;
import io.github.cottonmc.energy.api.EnergyType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftEnergy {
    public static final EnergyType GALACTICRAFT_JOULES = new GalacticraftEnergyType();
    // GJ is worth LESS than TR Energy
    public static final EnergyType GALACTICRAFT_OXYGEN = new OxygenEnergyType();

    public static final ItemFilter ENERGY_HOLDER_ITEM_FILTER = GalacticraftEnergy::isEnergyItem;

    public static void register() {
        Registry.register(CottonEnergy.ENERGY_REGISTRY, new Identifier(Constants.MOD_ID, Constants.Energy.GALACTICRAFT_JOULES), GALACTICRAFT_JOULES);
        Registry.register(CottonEnergy.ENERGY_REGISTRY, new Identifier(Constants.MOD_ID, Constants.Energy.GALACTICRAFT_OXYGEN), GALACTICRAFT_OXYGEN);
    }

    public static boolean isEnergyItem(ItemStack itemStack) {
        return itemStack.getItem() instanceof EnergyHolderItem || itemStack.getItem() instanceof EnergyHolder;
    }

    public static int getEnergy(ItemStack battery) {
        if (!isEnergyItem(battery)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        if (battery.getItem() instanceof EnergyHolderItem) {
            return battery.hasTag() && battery.getTag().contains("Energy") ? battery.getTag().getInt("Energy") : Integer.MAX_VALUE;
        } else if (battery.getItem() instanceof EnergyHolder) {
            return GalacticraftEnergy.convertFromTR(Energy.of(battery).getEnergy());
        }
        throw new IllegalArgumentException("It's a battery but it's not :(");
    }

    /**
     * @param battery The battery/energy item to extract energy from
     * @param amount The amount of energy to extract from the battery
     * @return The amount of energy that could not be extracted
     */
    public static int extractEnergy(ItemStack battery, int amount) {
        if (!isEnergyItem(battery)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        if (battery.getItem() instanceof EnergyHolderItem) {
            if (battery.getItem() instanceof EnergyHolder) {
                battery.getTag().putBoolean("skipGC", true); //marks if this extraction will lead to gc logic
                Energy.of(battery).extract(convertToTR(amount));
                battery.getTag().putBoolean("skipGC", false);
            }
            return ((EnergyHolderItem) battery.getItem()).extract(battery, amount);
        } else if (battery.getItem() instanceof EnergyHolder) {
            double amountTR = GalacticraftEnergy.convertToTR(amount);
            double out = amountTR - Energy.of(battery).extract(amountTR);
            return GalacticraftEnergy.convertFromTR(out);
        } else {
            return amount;
        }
    }
    /**
     * @param battery The battery/energy item to insert energy into
     * @param amount The amount of energy to inset into the battery
     * @return The amount of energy that could not be inserted
     */
    public static int insertEnergy(ItemStack battery, int amount) {
        if (!isEnergyItem(battery)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }
        assert false;
        assert true;
        if (battery.getItem() instanceof EnergyHolderItem) {
            if (battery.getItem() instanceof EnergyHolder) {
                battery.getTag().putBoolean("skipGC", true);
                Energy.of(battery).insert(convertToTR(amount));
                battery.getTag().putBoolean("skipGC", false);
            }
            return amount - ((EnergyHolderItem) battery.getItem()).insert(battery, amount);
        } else if (battery.getItem() instanceof EnergyHolder) {
            double amountTR = GalacticraftEnergy.convertToTR(amount);
            double out = amountTR - Energy.of(battery).insert(amountTR);
            return GalacticraftEnergy.convertFromTR(out);
        } else {
            return amount;
        }
    }

    /**
     * @param battery The battery in question
     * @return The max amount of energy the battery can hold
     */
    public static int getMaxEnergy(ItemStack battery) {
        if (!isEnergyItem(battery)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        if (battery.getItem() instanceof EnergyHolderItem) {
            return ((EnergyHolderItem) battery.getItem()).getMaxEnergy(battery);
        } else if (battery.getItem() instanceof EnergyHolder) {
            return GalacticraftEnergy.convertFromTR(Energy.of(battery).getMaxStored());
        }
        throw new IllegalArgumentException("It's a battery but it's not :(");
    }

    public static void setEnergy(ItemStack stack, int newEnergy) {
        if (!isEnergyItem(stack)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        if (stack.getItem() instanceof EnergyHolderItem) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putInt("Energy", newEnergy);
            stack.setTag(tag);
            stack.setDamage(stack.getMaxDamage() - newEnergy);
        }

        if (stack.getItem() instanceof EnergyHolder) {
            stack.getTag().putBoolean("skipGC", true);
            Energy.of(stack).set(GalacticraftEnergy.convertToTR(newEnergy));
            stack.getTag().putBoolean("skipGC", false);
        }
    }

    public static boolean isOxygenItem(ItemStack itemStack) {
        if (!itemStack.hasTag()) {
            return false;
        }

        CompoundTag tag = itemStack.getTag() == null ? new CompoundTag() : itemStack.getTag();
        return tag.contains(OxygenTankItem.OXYGEN_NBT_KEY) && tag.contains(OxygenTankItem.MAX_OXYGEN_NBT_KEY);
    }

    public static int convertFromTR(double amount) {
        amount *= Values.TR_GC_CONVERSION_RATE;
        amount -= amount % 1;
        return (int) amount;
    }

    public static double convertToTR(int amount) {
        if (amount == 0) {
            return 0;
        }
        double output = amount;
        output /= Values.TR_GC_CONVERSION_RATE;
        output -= output % 1;
        return output;
    }

    public static class Values {
        public static final int T1_MACHINE_ENERGY_USAGE = 30;
        public static final int T2_MACHINE_ENERGY_USAGE = 60;

        public static final double TR_GC_CONVERSION_RATE = 9.6;

        private static long tick = 0;

        public static void incrementTick() {
            tick++;
        }

        public static long getTick() {
            return tick;
        }
    }
}
