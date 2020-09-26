package com.hrznstudio.galacticraft.util;

import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.item.ItemStack;

import java.util.HashSet;

public class OxygenUtils {
    public static Fraction getOxygen(ItemStack stack) {
        assert isOxygenItem(stack);
        TankComponent component = TankComponentHelper.INSTANCE.getComponent(stack);
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
        TankComponent component = TankComponentHelper.INSTANCE.getComponent(stack);
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
        TankComponent component = TankComponentHelper.INSTANCE.getComponent(stack);
        if (component != null) {
            return component.insertFluid(new FluidVolume(GalacticraftFluids.OXYGEN, amount), action).getAmount();
        }

        return amount;
    }

    public static Fraction getMaxOxygen(ItemStack stack) {
        assert isOxygenItem(stack);
        TankComponent component = TankComponentHelper.INSTANCE.getComponent(stack);
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
        TankComponent component = TankComponentHelper.INSTANCE.getComponent(stack);
        if (component != null) {
            for (int i = 0; i < component.getTanks(); i++) {
                FluidVolume volume = component.getContents(i);
                if (volume.isEmpty() || volume.getFluid().isIn(GalacticraftTags.OXYGEN)) return true;
            }
        }

        return false;
    }
}
