package com.hrznstudio.galacticraft.util;

import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.item.ItemStack;

public class OxygenUtils {
    public static boolean isOxygenItem(ItemStack stack) {
        return ComponentProvider.fromItemStack(stack).hasComponent(UniversalComponents.TANK_COMPONENT)
                && (ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getContents(0).getFluid().isIn(GalacticraftTags.OXYGEN)
                || ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getContents(0).isEmpty());
    }

    public static Fraction getOxygen(ItemStack stack) {
        assert isOxygenItem(stack);
        return ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getContents(0).getFluid().isIn(GalacticraftTags.OXYGEN) ? ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getContents(0).getAmount() : Fraction.ZERO;
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
        if (component.canExtract(0)) {
            if (component.getContents(0).getFluid().isIn(GalacticraftTags.OXYGEN)) {
                return component.takeFluid(0, amount, action).getAmount();
            }
        }
        return Fraction.ZERO;
    }

    public static boolean isOxygen(FluidVolume volume) {
        return volume.getFluid().isIn(GalacticraftTags.OXYGEN);
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
        if (component.canInsert(0)) {
            if (component.getContents(0).isEmpty() || component.getContents(0).getFluid().isIn(GalacticraftTags.OXYGEN)) {
                return component.insertFluid(0, new FluidVolume(GalacticraftFluids.OXYGEN, amount), action).getAmount();
            }
        }
        return amount;
    }

    public static Fraction getMaxOxygen(ItemStack stack) {
        assert isOxygenItem(stack);
        return ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getMaxCapacity(0);
    }
}
