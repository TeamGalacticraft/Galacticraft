package com.hrznstudio.galacticraft.util;

import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.misc.Reference;
import com.hrznstudio.galacticraft.attribute.oxygen.EmptyOxygenTank;
import com.hrznstudio.galacticraft.attribute.GalacticraftAttributes;
import com.hrznstudio.galacticraft.attribute.oxygen.OxygenTank;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import net.minecraft.item.ItemStack;

public class OxygenTankUtils {
    private OxygenTankUtils() {}

    public static FluidVolume insertLiquidOxygen(Reference<ItemStack> stackRef, FluidVolume volume) {
        return insertLiquidOxygen(stackRef, loxToOxygen(volume));
    }

    public static FluidVolume insertLiquidOxygen(Reference<ItemStack> stackRef, int oxygen) {
        return insertLiquidOxygen(getOxygenTank(stackRef), oxygen);
    }

    public static FluidVolume insertLiquidOxygen(OxygenTank tank, int oxygen) {
        tank.setAmount(Math.min(tank.getAmount() + oxygen, tank.getCapacity()));
        return oxygenToLOX(Math.max(0, (tank.getCapacity() - (tank.getAmount() + oxygen)) * -1));
    }

    public static FluidVolume insertLiquidOxygen(OxygenTank tank, FluidVolume volume) {
        return insertLiquidOxygen(tank, loxToOxygen(volume));
    }

    public static FluidVolume extractLiquidOxygen(Reference<ItemStack> stackRef, int amount) {
        return extractLiquidOxygen(getOxygenTank(stackRef), amount);
    }

    public static FluidVolume extractLiquidOxygen(Reference<ItemStack> stackRef, FluidVolume amount) {
        return extractLiquidOxygen(stackRef, loxToOxygen(amount));
    }

    public static FluidVolume extractLiquidOxygen(OxygenTank tank, int amount) {
        if (tank.getAmount() == 0 || amount == 0) return FluidVolumeUtil.EMPTY;
        int exa = tank.getAmount();
        tank.setAmount(Math.max(0, tank.getAmount() - amount));
        assert exa - tank.getAmount() >= 0;
        return oxygenToLOX(exa - tank.getAmount());
    }

    public static FluidVolume extractLiquidOxygen(OxygenTank tank, FluidVolume amount) {
        return extractLiquidOxygen(tank, loxToOxygen(amount));
    }

    public static boolean isOxygenTank(ItemStack stack) {
        return GalacticraftAttributes.OXYGEN_TANK_ATTRIBUTE.getFirst(stack) != EmptyOxygenTank.NULL;
    }

    public static OxygenTank getOxygenTank(ItemStack stack) {
        return GalacticraftAttributes.OXYGEN_TANK_ATTRIBUTE.getFirst(stack);
    }

    public static boolean isOxygenTank(Reference<ItemStack> stack) {
        return GalacticraftAttributes.OXYGEN_TANK_ATTRIBUTE.getFirst(stack) != EmptyOxygenTank.NULL;
    }

    public static OxygenTank getOxygenTank(Reference<ItemStack> stack) {
        return GalacticraftAttributes.OXYGEN_TANK_ATTRIBUTE.getFirst(stack);
    }

    public static int loxToOxygen(FluidVolume volume) {
        assert GalacticraftTags.OXYGEN.contains(volume.getFluidKey().getRawFluid());
        return volume.getAmount_F().as1620();
    }

    public static FluidVolume oxygenToLOX(int oxygen) {
        assert oxygen > 0;
        return FluidKeys.get(GalacticraftFluids.LIQUID_OXYGEN).withAmount(FluidAmount.of1620(oxygen));
    }

    public static int oxygenToAirTicks(int oxygen) {
        return oxygen / 20;
    }

    public static int loxToAirTicks(FluidVolume volume) {
        return oxygenToAirTicks(loxToOxygen(volume));
    }
}
