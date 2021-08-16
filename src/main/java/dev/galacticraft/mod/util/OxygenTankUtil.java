/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.util;

import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.misc.Reference;
import dev.galacticraft.api.attribute.GcApiAttributes;
import dev.galacticraft.api.attribute.oxygen.EmptyOxygenTank;
import dev.galacticraft.api.attribute.oxygen.OxygenTank;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.tag.GalacticraftTag;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenTankUtil {
    private OxygenTankUtil() {}

    public static final ItemFilter OXYGEN_TANK_EXTRACTABLE = OxygenTankUtil::canExtractLOX;

    private static boolean canExtractLOX(ItemStack stack) {
        return isOxygenTank(stack) && getOxygenTank(stack).getCapacity() > 0;
    }

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
        amount = Math.min(amount, tank.getAmount());
        tank.setAmount(tank.getAmount() - amount);
        return oxygenToLOX(amount);
    }

    public static FluidVolume extractLiquidOxygen(OxygenTank tank, FluidVolume amount) {
        return extractLiquidOxygen(tank, loxToOxygen(amount));
    }

    public static boolean isOxygenTank(ItemStack stack) {
        return GcApiAttributes.OXYGEN_TANK.getFirst(stack) != EmptyOxygenTank.NULL;
    }

    public static OxygenTank getOxygenTank(ItemStack stack) {
        return GcApiAttributes.OXYGEN_TANK.getFirst(stack);
    }

    public static boolean isOxygenTank(Reference<ItemStack> stack) {
        return GcApiAttributes.OXYGEN_TANK.getFirst(stack) != EmptyOxygenTank.NULL;
    }

    public static OxygenTank getOxygenTank(Reference<ItemStack> stack) {
        return GcApiAttributes.OXYGEN_TANK.getFirst(stack);
    }

    public static int loxToOxygen(FluidVolume volume) {
        assert GalacticraftTag.LIQUID_OXYGEN.contains(volume.getFluidKey().getRawFluid());
        return volume.amount().as1620();
    }

    public static FluidVolume oxygenToLOX(int oxygen) {
        return FluidKeys.get(GalacticraftFluid.LIQUID_OXYGEN).withAmount(FluidAmount.of1620(oxygen));
    }

    public static int oxygenToAirTicks(int oxygen) {
        return oxygen / 20;
    }

    public static int loxToAirTicks(FluidVolume volume) {
        return oxygenToAirTicks(loxToOxygen(volume));
    }
}
