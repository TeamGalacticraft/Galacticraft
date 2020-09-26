package com.hrznstudio.galacticraft.util;

import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponentHelper;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.item.ItemStack;

public class EnergyUtils {
    public static boolean isEnergyItem(ItemStack stack) {
        return CapacitorComponentHelper.INSTANCE.hasComponent(stack);
    }

    public static int getEnergy(ItemStack stack) {
        assert isEnergyItem(stack);
        return CapacitorComponentHelper.INSTANCE.getComponent(stack).getCurrentEnergy();
    }

    /**
     * @param stack  The battery/energy item to extract energy from
     * @param amount The amount of energy, in Galacticraft Joules to extract from the battery
     * @param action The action
     * @return The amount of energy that was extracted
     */
    public static int extractEnergy(ItemStack stack, int amount, ActionType action) {
        assert isEnergyItem(stack);
        return CapacitorComponentHelper.INSTANCE.getComponent(stack).extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amount, action);
    }

    /**
     * @param stack  The battery/energy item to insert energy into
     * @param amount The amount of energy, in Galacticraft Joules, to inset into the battery
     * @param action The action
     * @return The amount of energy that could not be inserted
     */
    public static int insertEnergy(ItemStack stack, int amount, ActionType action) {
        assert isEnergyItem(stack);
        return CapacitorComponentHelper.INSTANCE.getComponent(stack).insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amount, action);
    }

    /**
     * @param stack The battery/energy item in question
     * @return The max amount of energy the battery can hold
     */
    public static int getMaxEnergy(ItemStack stack) {
        assert isEnergyItem(stack);
        return CapacitorComponentHelper.INSTANCE.getComponent(stack).getMaxEnergy();
    }

    public static void setEnergy(ItemStack stack, int amount) {
        assert isEnergyItem(stack);
        ((SimpleCapacitorComponent) CapacitorComponentHelper.INSTANCE.getComponent(stack)).setCurrentEnergy(amount);
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
