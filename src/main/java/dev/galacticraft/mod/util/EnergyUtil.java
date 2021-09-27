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

import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.misc.Ref;
import alexiil.mc.lib.attributes.misc.Reference;
import dev.galacticraft.energy.GalacticraftEnergy;
import dev.galacticraft.energy.api.*;
import dev.galacticraft.energy.impl.DefaultEnergyType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class EnergyUtil {
    private EnergyUtil() {}

    public static final ItemFilter IS_EXTRACTABLE = EnergyUtil::isEnergyExtractable;
    public static final ItemFilter IS_INSERTABLE = EnergyUtil::isEnergyInsertable;

    public static boolean isEnergyExtractable(Reference<ItemStack> reference) {
        return GalacticraftEnergy.EXTRACTABLE.getFirstOrNull(reference) != null;
    }

    public static boolean isEnergyInsertable(Reference<ItemStack> reference) {
        return GalacticraftEnergy.INSERTABLE.getFirstOrNull(reference) != null;
    }

    public static boolean isCapacitorView(Reference<ItemStack> reference) {
        return GalacticraftEnergy.CAPACITOR_VIEW.getFirstOrNull(reference) != null;
    }

    public static boolean isCapacitor(Reference<ItemStack> reference) {
        return GalacticraftEnergy.CAPACITOR.getFirstOrNull(reference) != null;
    }

    public static boolean isEnergyExtractable(ItemStack stack) {
        return isEnergyExtractable(new Ref<>(stack));
    }

    public static boolean isEnergyInsertable(ItemStack stack) {
        return isEnergyInsertable(new Ref<>(stack));
    }

    public static boolean isCapacitorView(ItemStack stack) {
        return isCapacitorView(new Ref<>(stack));
    }

    public static boolean isCapacitor(ItemStack stack) {
        return isCapacitor(new Ref<>(stack));
    }

    public static EnergyExtractable getEnergyExtractable(Reference<ItemStack> reference) {
        return GalacticraftEnergy.EXTRACTABLE.getFirst(reference);
    }

    public static EnergyInsertable getEnergyInsertable(Reference<ItemStack> reference) {
        return GalacticraftEnergy.INSERTABLE.getFirst(reference);
    }

    public static CapacitorView getCapacitorView(Reference<ItemStack> reference) {
        return GalacticraftEnergy.CAPACITOR_VIEW.getFirst(reference);
    }

    public static Capacitor getCapacitor(Reference<ItemStack> reference) {
        return GalacticraftEnergy.CAPACITOR.getFirst(reference);
    }

    public static EnergyExtractable getEnergyExtractable(ItemStack stack) {
        return getEnergyExtractable(new Ref<>(stack));
    }

    public static EnergyInsertable getEnergyInsertable(ItemStack stack) {
        return getEnergyInsertable(new Ref<>(stack));
    }

    public static CapacitorView getCapacitorView(ItemStack stack) {
        return getCapacitorView(new Ref<>(stack));
    }

    public static Capacitor getCapacitor(ItemStack stack) {
        return getCapacitor(new Ref<>(stack));
    }

    public static EnergyExtractable getEnergyExtractable(World world, BlockPos pos, Direction direction) {
        return GalacticraftEnergy.EXTRACTABLE.getFirst(world, pos, SearchOptions.inDirection(direction));
    }

    public static EnergyInsertable getEnergyInsertable(World world, BlockPos pos, Direction direction) {
        return GalacticraftEnergy.INSERTABLE.getFirst(world, pos, SearchOptions.inDirection(direction));
    }

    public static CapacitorView getCapacitorView(World world, BlockPos pos, Direction direction) {
        return GalacticraftEnergy.CAPACITOR_VIEW.getFirst(world, pos, SearchOptions.inDirection(direction));
    }

    public static Capacitor getCapacitor(World world, BlockPos pos, Direction direction) {
        return GalacticraftEnergy.CAPACITOR.getFirst(world, pos, SearchOptions.inDirection(direction));
    }

    public static boolean canAccessEnergy(World world, BlockPos pos, Direction direction) {
        EnergyInsertable insertable = GalacticraftEnergy.INSERTABLE.getFirstOrNull(world, pos, SearchOptions.inDirection(direction));
        EnergyExtractable extractable = GalacticraftEnergy.EXTRACTABLE.getFirstOrNull(world, pos, SearchOptions.inDirection(direction));
        return insertable != null || extractable != null;
    }

    public static int getEnergy(ItemStack stack) {
        assert isCapacitor(stack);
        return getCapacitorView(stack).getEnergy();
    }

    /**
     * @param stack  The battery/energy item to extract energy from
     * @param amount The amount of energy, in Galacticraft Joules to extract from the battery
     * @param simulation Whether to actually change values or not.
     * @return The amount of energy that was extracted
     */
    public static int extractEnergy(Reference<ItemStack> stack, int amount, Simulation simulation) {
        return getEnergyHandler(stack).extract(amount, simulation);
    }

    public static int extractEnergy(Reference<ItemStack> stack, int amount) {
        return extractEnergy(stack, amount, Simulation.ACTION);
    }

    /**
     * @param stack  The battery/energy item to insert energy into
     * @param amount The amount of energy, in Galacticraft Joules, to inset into the battery
     * @param simulation Whether to actually change values or not
     * @return The amount of energy that could not be inserted
     */
    public static int insert(Reference<ItemStack> stack, int amount, Simulation simulation) {
        return getEnergyHandler(stack).insert(amount, simulation);
    }

    public static int insert(Reference<ItemStack> stack, int amount) {
        return insert(stack, amount, Simulation.ACTION);
    }

    /**
     * @param stack The battery/energy item in question
     * @return The max amount of energy the battery can hold
     */
    public static int getMaxCapacity(ItemStack stack) {
        assert isCapacitor(stack);
        return getEnergyHandler(new Ref<>(stack)).getMaxCapacity();
    }

    public static void setEnergy(ItemStack stack, int amount) {
        assert isCapacitor(stack);
        getEnergyHandler(new Ref<>(stack)).setEnergy(amount);
    }

    public static Capacitor getEnergyHandler(Reference<ItemStack> stackReference) {
        return GalacticraftEnergy.CAPACITOR.getFirst(stackReference);
    }

    public static Capacitor getEnergyHandler(World world, BlockPos pos, @Nullable Direction direction) {
        return GalacticraftEnergy.CAPACITOR.getFirst(world, pos, SearchOptions.inDirection(direction));
    }

    public static MutableText getDisplay(int value) {
        return DefaultEnergyType.INSTANCE.display(value);
    }

    public static EnergyTransferable getTransferable(EnergyInsertable insertable, EnergyExtractable extractable) {
        if (insertable instanceof EnergyTransferable transferable) return transferable;
        if (extractable instanceof EnergyTransferable transferable) return transferable;
        return new EnergyTransferable() {
            @Override
            public int attemptExtraction(EnergyType type, int amount, Simulation simulation) {
                return extractable.attemptExtraction(type, amount, simulation);
            }

            @Override
            public int attemptInsertion(EnergyType type, int amount, Simulation simulation) {
                return insertable.attemptInsertion(type, amount, simulation);
            }

            @Override
            public EnergyExtractable getPureExtractable() {
                return extractable.getPureExtractable();
            }

            @Override
            public EnergyInsertable getPureInsertable() {
                return insertable.getPureInsertable();
            }
        };
    }

    public static class Values {
        public static final int T1_MACHINE_ENERGY_USAGE = 30;
        public static final int T2_MACHINE_ENERGY_USAGE = 60;
    }
}
