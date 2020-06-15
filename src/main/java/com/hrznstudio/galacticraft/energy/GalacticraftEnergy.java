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

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftEnergy {
    public static final GalacticraftEnergyType GALACTICRAFT_JOULES = new GalacticraftEnergyType();
    // GJ is worth LESS than TR Energy
    public static final OxygenEnergyType GALACTICRAFT_OXYGEN = new OxygenEnergyType();

    public static final Predicate<ItemStack> ENERGY_HOLDER_ITEM_FILTER = GalacticraftEnergy::isEnergyItem;

    public static void register() {
        Registry.register(UniversalComponents.ENERGY_TYPES, new Identifier(Constants.MOD_ID, Constants.Energy.GALACTICRAFT_JOULES), GALACTICRAFT_JOULES);
        Registry.register(UniversalComponents.ENERGY_TYPES, new Identifier(Constants.MOD_ID, Constants.Energy.GALACTICRAFT_OXYGEN), GALACTICRAFT_OXYGEN);

//        ItemComponentCallback.event(GalacticraftItems.BATTERY).register((stack, container) -> container.put(UniversalComponents.CAPACITOR_COMPONENT, new ItemCapacitorComponent(BatteryItem.getMaxEnergy(), GalacticraftEnergy.GALACTICRAFT_JOULES)));
//        ItemComponentCallback.event(GalacticraftItems.INFINITE_BATTERY).register((stack, container) -> {
//            ItemCapacitorComponent capacitorComponent = new ItemCapacitorComponent(Integer.MAX_VALUE, GalacticraftEnergy.GALACTICRAFT_JOULES);
//            capacitorComponent.copyFrom(GalacticraftEnergy.INFINITE_ENERGY_COMPONENT);
//            container.put(UniversalComponents.CAPACITOR_COMPONENT, capacitorComponent);
//        });
    }

    public static boolean isEnergyItem(ItemStack stack) {
        return ComponentProvider.fromItemStack(stack).hasComponent(UniversalComponents.CAPACITOR_COMPONENT);
    }

    public static int getEnergy(ItemStack stack) {
        if (!isEnergyItem(stack)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        return ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.CAPACITOR_COMPONENT).getCurrentEnergy();
    }

    /**
     * @param stack  The battery/energy item to extract energy from
     * @param amount The amount of energy, in Galacticraft Joules to extract from the battery
     * @param action The action
     * @return The amount of energy that could not be extracted
     */
    public static int extractEnergy(ItemStack stack, int amount, ActionType action) {
        if (!isEnergyItem(stack)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }
        return ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.CAPACITOR_COMPONENT).extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amount, action);
    }

    /**
     * @param stack  The battery/energy item to insert energy into
     * @param amount The amount of energy, in Galacticraft Joules, to inset into the battery
     * @param action The action
     * @return The amount of energy that could not be inserted
     */
    public static int insertEnergy(ItemStack stack, int amount, ActionType action) {
        if (!isEnergyItem(stack)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }
        return ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.CAPACITOR_COMPONENT).insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amount, action);
    }

    /**
     * @param stack The battery/energy item in question
     * @return The max amount of energy the battery can hold
     */
    public static int getMaxEnergy(ItemStack stack) {
        if (!isEnergyItem(stack)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }
        return ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.CAPACITOR_COMPONENT).getMaxEnergy();
    }

    public static void setEnergy(ItemStack stack, int amount) {
        if (!isEnergyItem(stack)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        ((SimpleCapacitorComponent) ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.CAPACITOR_COMPONENT)).setCurrentEnergy(amount);
    }

    public static boolean isOxygenItem(ItemStack itemStack) {
        if (!itemStack.hasTag()) {
            return false;
        }

        CompoundTag tag = itemStack.getTag() == null ? new CompoundTag() : itemStack.getTag();
        return tag.contains(OxygenTankItem.OXYGEN_NBT_KEY) && tag.contains(OxygenTankItem.MAX_OXYGEN_NBT_KEY);
    }

    public static class Values {
        public static final int T1_MACHINE_ENERGY_USAGE = 30;
        public static final int T2_MACHINE_ENERGY_USAGE = 60;

        private static long tick = 0;

        public static void incrementTick() {
            tick++;
        }

        public static long getTick() {
            return tick;
        }
    }
}
