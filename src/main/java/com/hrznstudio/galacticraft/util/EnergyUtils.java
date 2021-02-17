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

import alexiil.mc.lib.attributes.Simulation;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHandler;

import java.util.function.Predicate;

public class EnergyUtils {
    public static final Predicate<ItemStack> ENERGY_HOLDER_ITEM_FILTER = EnergyUtils::isEnergyHolder;

    public static boolean isEnergyHolder(Object object) {
        return Energy.valid(object);
    }

    public static boolean canAccessEnergy(WorldAccess world, BlockPos pos, Direction direction) {
        EnergyHandler handler = Energy.of(world.getBlockEntity(pos)).side(direction).simulate();
        if (handler.getEnergy() < 0) return false;
        if (handler.getMaxStored() <= 0) return false;
        return !(handler.insert(1.0) == 0.0 && handler.extract(1.0) == 0.0);
    }

    public static double getEnergy(ItemStack stack) {
        assert isEnergyHolder(stack);
        return getEnergyHandler(stack).getEnergy();
    }

    /**
     * @param stack  The battery/energy item to extract energy from
     * @param amount The amount of energy, in Galacticraft Joules to extract from the battery
     * @param simulation Whether to actually change values or not.
     * @return The amount of energy that was extracted
     */
    public static double extractEnergy(ItemStack stack, double amount, Simulation simulation) {
        assert isEnergyHolder(stack);
        return getEnergyHandler(stack, simulation).extract(amount);
    }

    public static double extractEnergy(ItemStack stack, double amount) {
        return extractEnergy(stack, amount, Simulation.ACTION);
    }

    /**
     * @param stack  The battery/energy item to insert energy into
     * @param amount The amount of energy, in Galacticraft Joules, to inset into the battery
     * @param simulation Whether to actually change values or not
     * @return The amount of energy that could not be inserted
     */
    public static double insert(ItemStack stack, double amount, Simulation simulation) {
        assert isEnergyHolder(stack);
        return getEnergyHandler(stack, simulation).insert(amount);
    }

    public static double insert(ItemStack stack, double amount) {
        return insert(stack, amount, Simulation.ACTION);
    }

    /**
     * @param stack The battery/energy item in question
     * @return The max amount of energy the battery can hold
     */
    public static double getMaxStored(ItemStack stack) {
        assert isEnergyHolder(stack);
        return getEnergyHandler(stack).getMaxStored();
    }

    public static void setEnergy(ItemStack stack, double amount) {
        assert isEnergyHolder(stack);
        getEnergyHandler(stack).set(amount);
    }

    public static EnergyHandler getEnergyHandler(Object object, Simulation simulation) {
        return simulation.isSimulate() ? Energy.of(object).simulate() : Energy.of(object);
    }

    public static EnergyHandler getEnergyHandler(Object object) {
        return getEnergyHandler(object, Simulation.ACTION);
    }

    public static EnergyHandler getEnergyHandler(WorldAccess world, BlockPos pos, @Nullable Direction direction, Simulation simulation) {
        BlockEntity entity = world.getBlockEntity(pos);
        assert isEnergyHolder(entity);
        return simulation == Simulation.SIMULATE ? getEnergyHandler(entity).side(direction).simulate() : getEnergyHandler(entity).side(direction);
    }

    public static EnergyHandler getEnergyHandler(WorldAccess world, BlockPos pos, @Nullable Direction direction) {
        return getEnergyHandler(world, pos, direction, Simulation.ACTION);
    }

    public static EnergyHandler getEnergyHandler(Object object, @Nullable Direction direction, Simulation simulation) {
        assert isEnergyHolder(object);
        return simulation == Simulation.SIMULATE ? getEnergyHandler(object).side(direction).simulate() : getEnergyHandler(object).side(direction);
    }

    public static EnergyHandler getEnergyHandler(Object object, @Nullable Direction direction) {
        return getEnergyHandler(object, direction, Simulation.ACTION);
    }

    public static EnergyHandler getEnergyHandler(WorldAccess world, BlockPos pos, Simulation simulation) {
        return getEnergyHandler(world, pos, null, simulation);
    }

    public static EnergyHandler getEnergyHandler(WorldAccess world, BlockPos pos) {
        return getEnergyHandler(world, pos, (Direction) null);
    }

    public static MutableText getDisplay(double value) {
        return new LiteralText(String.valueOf(value));
    }

    public static class Values {
        public static final double T1_MACHINE_ENERGY_USAGE = 2.5;
        public static final double T2_MACHINE_ENERGY_USAGE = 5.0;
    }
}
