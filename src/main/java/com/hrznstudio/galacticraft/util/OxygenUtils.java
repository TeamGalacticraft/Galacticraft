package com.hrznstudio.galacticraft.util;

import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.item.ItemStack;

public class OxygenUtils {
    public static boolean isOxygenItem(ItemStack stack) {
        return ComponentProvider.fromItemStack(stack).hasComponent(UniversalComponents.TANK_COMPONENT) && ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).contains(GalacticraftFluids.OXYGEN);
    }

    public static Fraction getOxygen(ItemStack stack) {
        assert isOxygenItem(stack);
        return ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).amountOf(GalacticraftFluids.OXYGEN);
    }

    /**
     * @param stack  The battery/energy item to extract energy from
     * @param amount The amount of energy, in Galacticraft Joules to extract from the battery
     * @param action The action
     * @return The amount of energy that was extracted
     */
    public static Fraction extractOxygen(ItemStack stack, Fraction amount, ActionType action) {
        assert isOxygenItem(stack);
        TankComponent component = ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT);
        Fraction total = Fraction.ZERO;
        for (int i = 0; i < component.getTanks(); i++) {
            if (component.getContents(i).getFluid().equals(GalacticraftFluids.OXYGEN)) {
                Fraction taken = component.takeFluid(i, amount, ActionType.PERFORM).getAmount();
                amount = amount.subtract(taken);
                total = total.add(taken);
                if (amount.equals(Fraction.ZERO) || amount.intValue() == 0) {
                    break;
                }
            }
        }
        return total;
    }

    /**
     * @param stack  The battery/energy item to insert energy into
     * @param amount The amount of energy, in Galacticraft Joules, to inset into the battery
     * @param action The action
     * @return The amount of energy that could not be inserted
     */
    public static Fraction insertOxygen(ItemStack stack, Fraction amount, ActionType action) {
        assert isOxygenItem(stack);
        TankComponent component = ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT);
        for (int i = 0; i < component.getTanks(); i++) {
            if (component.getContents(i).getFluid().equals(GalacticraftFluids.OXYGEN) || component.getContents(i).isEmpty()) {
                amount = component.insertFluid(i, new FluidVolume(GalacticraftFluids.OXYGEN, amount), ActionType.PERFORM).getAmount();
                if (amount.equals(Fraction.ZERO) || amount.intValue() == 0) {
                    break;
                }
            }
        }
        return amount;
    }

    public static Fraction getMaxOxygen(ItemStack stack) {
        TankComponent component = ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT);
        Fraction cap = Fraction.ZERO;
        for (int i = 0; i < component.getTanks(); i++) {
            if (component.canInsert(i)) {
                cap.add(component.getMaxCapacity(i));
            }
        }
        return cap;
    }
}
